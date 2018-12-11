## kernel-keymanager-service
[Background & Design](../../design/kernel/kernel-keymanager.md)
 

**Api Documentation**

[API Documentation <TBA>](TBA)

```
mvn javadoc:javadoc
```

**Maven dependency**
  
 ```
    <dependency>
		<groupId>io.mosip.kernel</groupId>
		<artifactId>kernel-keymanager-service</artifactId>
		<version>${project.version}</</version>
	</dependency>
 ```

**Properties to be added in parent Spring Application environment**

[kernel-keymanager-service-dev.properties](../../config/kernel-keymanager-service-dev.properties)

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
  
  








