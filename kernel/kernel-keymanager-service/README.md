## kernel-keymanager-service

[Background & Design](../../docs/design/kernel/kernel-keymanager.md)

[Api Documentation](https://github.com/mosip/mosip/wiki/Kernel-APIs#1-key-manager)

Default Port and Context Path

```
server.port=8088
server.servlet.path=/keymanager

```

localhost:8088/keymanager/swagger-ui.html


**Application Properties**

[application-dev.properties](../../config/application-dev.properties)

[kernel-keymanager-service-dev.properties](../../config/kernel-keymanager-service-dev.properties)


```
#mosip.kernel.keymanager.softhsm.config-path=/pathto/softhsm.conf
mosip.kernel.keymanager.softhsm.config-path=D\:\\SoftHSM2\\etc\\softhsm2-demo.conf
mosip.kernel.keymanager.softhsm.keystore-type=PKCS11
mosip.kernel.keymanager.softhsm.keystore-pass=pwd

mosip.kernel.keymanager.softhsm.certificate.common-name=www.mosip.io
mosip.kernel.keymanager.softhsm.certificate.organizational-unit=MOSIP
mosip.kernel.keymanager.softhsm.certificate.organization=IITB
mosip.kernel.keymanager.softhsm.certificate.country=IN

mosip.kernel.keygenerator.asymmetric-algorithm-name=RSA
mosip.kernel.keygenerator.asymmetric-algorithm-length=2048
mosip.kernel.keygenerator.symmetric-algorithm-name=AES
mosip.kernel.keygenerator.symmetric-algorithm-length=256

mosip.kernel.crypto.asymmetric-algorithm-name=RSA
mosip.kernel.crypto.symmetric-algorithm-name=AES

mosip.kernel.data-key-splitter=#KEY_SPLITTER#

# DB Properties For Development
--------------------------------------
javax.persistence.jdbc.driver=org.postgresql.Driver
javax.persistence.jdbc.url = jdbc:postgresql://localhost:8888/mosip_kernel
javax.persistence.jdbc.password = dbpwd
javax.persistence.jdbc.user = dbuser

hibernate.hbm2ddl.auto=update
hibernate.dialect=org.hibernate.dialect.PostgreSQL95Dialect
hibernate.jdbc.lob.non_contextual_creation=true
hibernate.show_sql=true
hibernate.format_sql=true
hibernate.connection.charSet=utf8
hibernate.cache.use_second_level_cache=false
hibernate.cache.use_query_cache=false
hibernate.cache.use_structured_entries=false
hibernate.generate_statistics=false
hibernate.current_session_context_class=org.springframework.orm.hibernate5.SpringSessionContext

```

**The inputs which have to be provided are:**
1. Encrypted Key provided to decrypt should be encoded to BASE64.
2. Decrypted Key received after decrypt should be decoded from BASE64 encoding.

**Usage Sample**


  *Get Public Key*
  
  *Request*
  
  ```
OkHttpClient client = new OkHttpClient();

Request request = new Request.Builder()
.url("http://localhost:8088/keymanager/v1.0/publickey/REGISTRATION?referenceId=ref1&timeStamp=2018-12-11T06%3A12%3A52.994Z")
  .get()
  .build();

Response response = client.newCall(request).execute();
  ```
  
  *Response*
  
  Status:200
  
  ```
 {
    "publicKey": "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzaFwykABfN683Mp5SNpBQU2_tIRKILIDBReeuTWQuS-6B8Z7kQmQ0cv2fG8fr8XTx7avyY3su25YFfNuIliBmdC3ZKqWVvsL9EpTCCQolcKo9a0351ieKxe_wCg5DIRLS1CciyK_cr2IqcUwh_Y3zkkZs0cF2R945vA_7RMTUth1_9zdobrxYMrMsIf2L1431vLP0-mUuAonQ9GU34L-SyAP1uscWcbk6Xj_EdZRvqrj2aOXrHy0FbQltrwNuTyX0-ZLBwMH7U50Nrh4BeQBA1ioeFKmdzSEY95Fs2jJGmxDUK77dsHw77jmg125HlEuu-NwIvDlcwCFuGQheUQFvwIDAQAB",
    "issuedAt": "2018-12-11T06:12:52.994",
    "expiryAt": "2019-12-11T06:12:52.994"
}
  ```
 
  *Decrypt Symmetric Key*
  
  *Request*
  
  ```
OkHttpClient client = new OkHttpClient();

MediaType mediaType = MediaType.parse("application/json");

RequestBody body = RequestBody.create(mediaType, "{\n  \"applicationId\": \"REGISTRATION\",\n  \"encryptedSymmetricKey\": \"NuIMhUHds-5SlmcVWob1Kg2PA7mVRbzYLrXwb24JGX767CKdTC67wVYM3wGz9_8vmuNk-Yh_SExT6uJJHZyuY3q7pZ-BbBy-ZRWTEJqxXmnF9EWWADDQCQMQajtU-fyszBzQeIjM6gRcvwjXAuq48bC6LsEEL-9Zm6Cu6iL5oHbE77tCENrvcvdWlXY5SQx8p_w6XFlEoU_0f1ZWqjDYlW5iYHBz4XJsgrJjx7nhywOvqvJkOJZeCXSmbbvHCC6o8nIvzdF0Vd-2S2bGTKlICoLIsj9EUGKFgNLM8chI0QqPILFw2BQQfI3AQMsM2Rc04AoMRT_VYFU5Acs_fuHn3g\",\n  \"referenceId\": \"ref123\",\n  \"timeStamp\": \"2018-12-07T12:07:44.403Z\"\n}");

Request request = new Request.Builder()
  .url("http://localhost:8088/keymanager/v1.0/symmetrickey")
  .post(body)
  .addHeader("content-type", "application/json")
  .build();

Response response = client.newCall(request).execute();
  ```
  
  *Response*
  
  Status:200
  
  ```
 {
    "symmetricKey": "sq9oJCdwV-mHEdxEXRh91WkQcGJ6Q83quNaP9OZa_p0"
 }
  ```
  
  
## Setup steps:

### Linux (Docker)

1. (First time only) Build kernel-keymanager-softhsm docker image using  `softhsm\Dockerfile` with command:

```
docker build --build-arg softhsm_pin=1234 --tag kernel-keymanager-softhsm:1.0 .
```

The pin passed to the variable `softhsm_pin` in docker build command should be same as the value of property `mosip.kernel.keymanager.softhsm.keystore-pass` in properties file.

2. (First time only) Push kernel-keymanager-softhsm docker image to private repository and modify `Dockerfile` with its URI.

3. Build kernel-keymanager-service docker image using `Dockerfile` with command:

```
docker build --tag kernel-keymanager-service:1.0 .
```

4. Run docker container using command:

```
docker run -tid --ulimit memlock=-1  -p 8088:8088 -v softhsm:/softhsm --name kernel-keymanager-service kernel-keymanager-service:1.0
```
Remember to volume mount using `-v softhsm:/softhsm` and do not add bind mount (`-v /softhsm:/softhsm`)

### Windows

1. Download softhsm portable zip archive from https://github.com/disig/SoftHSM2-for-Windows#download
2. Extract it to any location, e.g `D:\SoftHSM2`. SoftHSM2 searches for its configuration file in the following locations:
```
  1. Path specified by SOFTHSM2_CONF environment variable
  2. User specific path %HOMEDRIVE%%HOMEPATH%\softhsm2.conf
  3. File softhsm2.conf in the current working directory
```
3. Modify following in environment variables:
```
> set SOFTHSM2_CONF=D:\SoftHSM2\etc\softhsm2.conf
> set PATH=%PATH%;D:\SoftHSM2\lib\
```
4. Create another conf file at `D:\SoftHSM2\etc\softhsm-application.conf` with below content
```
# Sun PKCS#11 provider configuration file for SoftHSMv2
name = SoftHSM2
library = D:\SoftHSM2\lib\softhsm2-x64.dll 
slotListIndex = 0
```
5. Install JCE With an Unlimited Strength Jurisdiction Policy as shown here:
https://dzone.com/articles/install-java-cryptography-extension-jce-unlimited
6. Go to `D:\SoftHSM2\bin` and run below command:
```
> softhsm2-util.exe --init-token --slot 0 --label "My token 1"
```
Check token is initialized in slot with below command:
```
> softhsm2-util.exe --show-slots
```
The output should be like below:
```
Slot 569035518
    Slot info:
        Description:      SoftHSM slot ID 0x21eacafe
        Manufacturer ID:  SoftHSM project
        Hardware version: 2.4
        Firmware version: 2.4
        Token present:    yes
    Token info:
        Manufacturer ID:  SoftHSM project
        Model:            SoftHSM v2
        Hardware version: 2.4
        Firmware version: 2.4
        Serial number:    b1ee933e21eacafe
        Initialized:      yes
        User PIN init.:   yes
        Label:            My token 1
Slot 1
    Slot info:
        Description:      SoftHSM slot ID 0x1
        Manufacturer ID:  SoftHSM project
        Hardware version: 2.4
        Firmware version: 2.4
        Token present:    yes
    Token info:
        Manufacturer ID:  SoftHSM project
        Model:            SoftHSM v2
        Hardware version: 2.4
        Firmware version: 2.4
        Serial number:
        Initialized:      no
        User PIN init.:   no
        Label:
```
5. Put the newly created conf filepath `D:\SoftHSM2\etc\softhsm-application.conf` in `mosip.kernel.keymanager.softhsm.config-path` property. Softhsm is ready to be used. 

For more information, check https://github.com/opendnssec/SoftHSMv2








