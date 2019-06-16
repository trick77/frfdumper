package com.trick77.inspector;


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
public class FrfInspector {

    private final static int BUFFER_SIZE = 2048;
    private final static String KEY_RESOURCE_NAME = "the.key";

    private byte[] getEncryptionKey() throws Exception {
        InputStream is = getClass().getClassLoader().getResourceAsStream(KEY_RESOURCE_NAME);
        byte[] key = IOUtils.toByteArray(is);
        is.close();
        return key;
    }

    public void decrypt(final String frfFileName) throws Exception {
        byte[] key = getEncryptionKey();
        File decryptedTmpFile = createDecryptedTmpFile(frfFileName, key);
        File decompressedTmpFile = getDecompressedTmpFile(decryptedTmpFile);
        parseXml(decompressedTmpFile);
        decompressedTmpFile.delete();
    }

    private void parseXml(final File decompressedTmpFile) throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        OdxHandler handler = new OdxHandler();
        saxParser.parse(decompressedTmpFile, handler);
        Container container = handler.container;

        String longName = container.getName();
        String shortName = Container.getNameWithoutRevision(longName, container.getRevision());
        System.out.println("name=" + Container.getNameWithoutSpaces(shortName) + ", revision=" + container.getRevision());
        System.out.println("date=" + container.getDate());

        dumpStringArray("name-idents", container.getNameIdents());
        dumpStringArray("version-idents", container.getVersionIdents());

        for (Security security: container.getSecuritys()) {
            if (security.getMethod().equalsIgnoreCase("sa2")) {
                System.out.println(security.getMethod().toLowerCase() + "=" + security.getSignature().toLowerCase());
            }
        }

        for (Block block: handler.blocks) {
            String id = null;
            String name = null;
            String method = null;
            for (FlashData flashData: handler.flashDatas) {
                if (flashData.getName().equals(block.getName())) {
                    id = flashData.getId();
                    name = flashData.getName();
                    method = flashData.getEncryptCompressMethod();
                    break;
                }
            }

            System.out.print("id=" + id);
            System.out.print(", name=" + name);
            System.out.print(", method=" + method);
            System.out.print(", compressedSize=" + block.getCompressedSize() + " bytes");
            System.out.print(", uncompressedSize=" + block.getUncompressedSize() + " bytes");
            System.out.println();
        }
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
    private File getDecompressedTmpFile(File decryptedFile) throws IOException {
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
        // Deleted the temporary decrypted file
        decryptedFile.delete();
        return decompressedFile;
    }


    /**
     * Creates the decrypted, compressed temporary file.
     */
    private File createDecryptedTmpFile(final String inputFileName, final byte[] key) throws IOException {
        int seed0 = 0;
        int seed1 = 1;
        File tmpFile = File.createTempFile("decrypted-", ".tmp");
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(tmpFile.getPath()));
        try(InputStream is = new FileInputStream(inputFileName)) {
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

    public static void main(final String[] args) throws Exception {
        System.out.println();
        System.out.println("frf inspector v0.1");
        System.out.println("==================");
        if (args.length != 1) {
            System.err.println("Missing argument: filename");
            System.exit(1);
        } else {
            FrfInspector inspector = new FrfInspector();
            inspector.decrypt(args[0]);
        }
    }
}
