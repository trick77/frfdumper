package com.trick77.dumper;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Decrypts an *.frf binary and displays (some of) its Open Data Diagnostics (ODX) data. The containing ODX binary flash data
 * will not be decrypted, since the used algorithms and encryption keys vary and aren't well known.
 *
 */
public class FrfDumper {

    private final static int BUFFER_SIZE = 2048;
    private final static String KEY_RESOURCE_NAME = "the.key";

    public void dump(final String frfFileName, boolean keepOdxFile) throws Exception {
        File frfFile = new File(frfFileName);
        byte[] key = getKey();
        System.out.println("* decrypting " + frfFile.getPath() + "...");
        File decryptedTmpFile = createDecryptedFile(frfFile, key);
        System.out.println("* decompressing " + decryptedTmpFile.getPath() + "...");
        File decompressedTmpFile = getDecompressedFile(decryptedTmpFile);

        System.out.println("* dumping odx data:");
        dumpOdxData(decompressedTmpFile);
        if (keepOdxFile) {
            String odxFileName = FilenameUtils.removeExtension(frfFileName);
            odxFileName += ".odx";
            System.out.println("* exporting odx file to " + odxFileName);
            FileUtils.copyFile(decompressedTmpFile, new File(odxFileName));
        }
        decompressedTmpFile.delete();
    }

    private void dumpOdxData(final File decompressedTmpFile) throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        OdxHandler handler = new OdxHandler();
        saxParser.parse(decompressedTmpFile, handler);
        Container container = handler.getOdxData().getContainer();

        String longName = container.getName();
        String shortName = Container.getNameWithoutRevision(
                Container.getNameWithoutSpaces(longName),
                container.getRevision()
        );
        System.out.println("   name=" + shortName + ", revision=" + container.getRevision());
        System.out.println("   date=" + container.getDate());

        dumpStringArray("   name-idents", container.getNameIdents());
        dumpStringArray("   version-idents", container.getVersionIdents());

        for (Security security: container.getSecuritys()) {
            if (security.getMethod().equalsIgnoreCase("sa2")) {
                System.out.println("   " + security.getMethod().toLowerCase() + "=" + security.getSignature().toLowerCase());
            } else if (security.getMethod().equalsIgnoreCase("alfid")) {
                System.out.println("   " + security.getMethod().toLowerCase() + "=" + security.getSignature().toLowerCase());
            }

        }

        for (Block block: handler.getOdxData().getBlocks()) {
            String id = null;
            String name = null;
            String method = null;
            for (FlashData flashData: handler.getOdxData().getFlashDatas()) {
                if (flashData.getName().equals(block.getName())) {
                    id = flashData.getId();
                    name = flashData.getName();
                    method = flashData.getEncryptCompressMethod();
                    break;
                }
            }

            System.out.print("   id=" + id);
            System.out.print(", encrypt-compress-method=" + method);
            System.out.print(", compressed size=" + block.getCompressedSize() + " bytes");
            System.out.print(", uncompressed size=" + block.getUncompressedSize() + " bytes");
            System.out.println();
        }
        System.out.println();
    }

    private void dumpStringArray(String title, ArrayList<String> list) {
        if (list.size() > 0) {
            System.out.print(title + "=");
            for (int i = 0; i < list.size(); i++) {
                System.out.print(list.get(i));
                if (i < list.size() - 1) {
                    System.out.print(", ");
                }
            }
            System.out.println();
        }
    }

    /**
     * Decompress the contents of the decrypted file and save it to a temporary file.
     */
    private File getDecompressedFile(File decryptedFile) throws IOException {
        ZipInputStream zis = new ZipInputStream(new FileInputStream(decryptedFile.getPath()));
        byte[] buffer = new byte[BUFFER_SIZE];
        File decompressedFile = null;
        try {
            ZipEntry entry;
            while((entry = zis.getNextEntry()) != null) {
                decompressedFile = File.createTempFile(entry.getName() + "-", ".tmp");
                try(FileOutputStream output = new FileOutputStream(decompressedFile.getPath())) {
                    int len = 0;
                    while ((len = zis.read(buffer)) > 0) {
                        output.write(buffer, 0, len);
                    }
                }
            }
        }
        finally {
            zis.close();
        }
        decryptedFile.delete();
        return decompressedFile;
    }


    /**
     * Creates the decrypted, compressed temporary file.
     */
    private File createDecryptedFile(final File decompressedFile, final byte[] key) throws IOException {
        int seed0 = 0;
        int seed1 = 1;
        File tmpFile = File.createTempFile("decrypted-", ".tmp");
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(tmpFile.getPath()));
        try(InputStream is = new FileInputStream(decompressedFile)) {
            BufferedInputStream bis = IOUtils.buffer(is, BUFFER_SIZE);

            // Courtesy of tmbinc
            int keyIndex = 0;
            while(bis.available() > 0) {
                char currentInputChar = (char)bis.read();
                char currentKeyChar = (char)key[keyIndex];
                seed0 = ((seed0 + currentKeyChar) * 3) & 0xff;
                int n = (currentInputChar ^ (seed0 ^ 0xff ^ seed1 ^ currentKeyChar));
                bos.write(n);
                seed1 = ((seed1 + 1) * seed0) & 0xff;
                keyIndex += 1;
                keyIndex %= key.length;
            }
        }
        bos.close();
        return tmpFile;
    }

    private byte[] getKey() throws Exception {
        InputStream is = getClass().getClassLoader().getResourceAsStream(KEY_RESOURCE_NAME);
        byte[] key = IOUtils.toByteArray(is);
        is.close();
        return key;
    }

    private static String getStringArgument(final String[] args, final String argumentName) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equalsIgnoreCase(argumentName)) {
                if (i < args.length) {
                    return args[i + 1];
                }
            }
        }

        return null;
    }

    private static boolean getBooleanArgument(final String[] args, final String argumentName) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equalsIgnoreCase(argumentName)) {
                return true;
            }
        }
        return false;
    }

    public static void main(final String[] args) throws Exception {
        System.out.println();
        System.out.println("frf dumper v0.2");
        System.out.println("==================");
        String fileName = getStringArgument(args, "--frf");
        boolean keepOdxFile = getBooleanArgument(args, "--keepodx");
        boolean decompressFlashData = getBooleanArgument(args,"--decompress-flashdata");
        if (fileName == null || fileName.length() < 1) {
            System.err.println("usage: frfdumper --frf filename [--keepodx] [--decompress-flashdata]");
            System.exit(1);
        } else {
            FrfDumper dumper = new FrfDumper();
            dumper.dump(fileName, keepOdxFile);
        }
    }
}
