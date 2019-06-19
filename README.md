# FRF Inspector

Decrypts an *.frf binary and displays (some of) its Open Data Diagnostics (ODX) data. The containing ODX binary flash data
 will **not** be decrypted, since the used algorithms and encryption keys vary and aren't well known.

```
frf dumper v0.2
==================
name=4G0907589F, revision=0008
date=2018-02-28T15:23:10
name-idents=4G0907589F, 4G0907589D, 4G0907589B
sa2=6807814a0793acba76126b018284212223244a0787324354656b0181494c
id=EMEM_4G0907589F0008.FD_01ERASEDATA, name=01 ERASE DATA, method=00, compressedSize=0 bytes, uncompressedSize=48896 bytes
id=EMEM_4G0907589F0008.FD_01DATA, name=01 DATA, method=11, compressedSize=42298 bytes, uncompressedSize=48896 bytes
id=EMEM_4G0907589F0008.FD_02ERASEDATA, name=02 ERASE DATA, method=00, compressedSize=0 bytes, uncompressedSize=2490368 bytes
id=EMEM_4G0907589F0008.FD_02DATA, name=02 DATA, method=11, compressedSize=2138429 bytes, uncompressedSize=2490368 bytes
id=EMEM_4G0907589F0008.FD_03ERASEDATA, name=03 ERASE DATA, method=00, compressedSize=0 bytes, uncompressedSize=16384 bytes
id=EMEM_4G0907589F0008.FD_03DATA, name=03 DATA, method=11, compressedSize=2884 bytes, uncompressedSize=16384 bytes
id=EMEM_4G0907589F0008.FD_04ERASEDATA, name=04 ERASE DATA, method=00, compressedSize=0 bytes, uncompressedSize=1036288 bytes
id=EMEM_4G0907589F0008.FD_04DATA, name=04 DATA, method=11, compressedSize=642061 bytes, uncompressedSize=1036288 bytes
id=EMEM_4G0907589F0008.FD_05ERASEDATA, name=05 ERASE DATA, method=00, compressedSize=0 bytes, uncompressedSize=524032 bytes
id=EMEM_4G0907589F0008.FD_05DATA, name=05 DATA, method=11, compressedSize=27456 bytes, uncompressedSize=524032 bytes
```
 
## How to build it

mvn package

## How to run it

java -jar target/frfdumper-jar-with-dependencies.jar some.frf
