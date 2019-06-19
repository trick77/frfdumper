# FRF Dumper

Unpacks an *.frf binary and displays (some of) its Open Data Exchange (ODX) diagnostics data. The binary ODX flash data
 will **not** be decrypted, since the used encryption methods and keys vary and aren't well known.

```
frf dumper v0.2
==================
* decrypting FL_4G0907589F__0001.frf...
* decompressing /tmp/decrypted-14587290289655052607.tmp...
* dumping odx data:
   name=4G0907589F, revision=0001
   date=2016-02-09T09:21:35
   name-idents=4G0907589B, 4G0907589D, 4G0907589F
   version-idents=0001, 0002, 0003, 0004, 0005
   sa2=6807814a0793acba76126b018284212223244a0787324354656b0181494c
   alfid=013101
   id=EMEM_4G0907589F0001.FD_01ERASEDATA, encrypt-compress-method=00, compressed size=0 bytes, uncompressed size=48896 bytes
   id=EMEM_4G0907589F0001.FD_01DATA, encrypt-compress-method=11, compressed size=42298 bytes, uncompressed size=48896 bytes
   id=EMEM_4G0907589F0001.FD_02ERASEDATA, encrypt-compress-method=00, compressed size=0 bytes, uncompressed size=2490368 bytes
   id=EMEM_4G0907589F0001.FD_02DATA, encrypt-compress-method=11, compressed size=2137078 bytes, uncompressed size=2490368 bytes
   id=EMEM_4G0907589F0001.FD_03ERASEDATA, encrypt-compress-method=00, compressed size=0 bytes, uncompressed size=16384 bytes
   id=EMEM_4G0907589F0001.FD_03DATA, encrypt-compress-method=11, compressed size=2884 bytes, uncompressed size=16384 bytes
   id=EMEM_4G0907589F0001.FD_04ERASEDATA, encrypt-compress-method=00, compressed size=0 bytes, uncompressed size=1036288 bytes
   id=EMEM_4G0907589F0001.FD_04DATA, encrypt-compress-method=11, compressed size=638773 bytes, uncompressed size=1036288 bytes
   id=EMEM_4G0907589F0001.FD_05ERASEDATA, encrypt-compress-method=00, compressed size=0 bytes, uncompressed size=524032 bytes
   id=EMEM_4G0907589F0001.FD_05DATA, encrypt-compress-method=11, compressed size=88468 bytes, uncompressed size=524032 bytes

* exporting odx file to FL_4G0907589F__0001.odx
```
 
## How to build it

mvn package

## How to run it

java -jar target/frfdumper-jar-with-dependencies.jar --frf some.frf --keepodx
