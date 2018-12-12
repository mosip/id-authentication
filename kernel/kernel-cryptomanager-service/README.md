## kernel-cryptomanager-service

 

**Api Documentation**

[API Documentation <TBA>](TBA)

```
mvn javadoc:javadoc
```

**The inputs which have to be provided are:**
1. Data provided to encryption should be encoded to BASE64 encoding before requesting to encrypt and decrypt.
2. Data received after encrypting should be decoded from BASE64 encoding.

**Properties to be added in parent Spring Application environment**

[kernel-cryptomanager-service-dev.properties](../../config/kernel-cryptomanager-service-dev.properties)

**Usage Sample**

  *Encrypt Request*
  
  ```
OkHttpClient client = new OkHttpClient();

MediaType mediaType = MediaType.parse("multipart/form-data;boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW");

RequestBody body = RequestBody.create(mediaType, "{\r\n  \"applicationId\": \"REGISTRATION\",\r\n  \"data\": \"VGhpcyBpcyBhIHBsYWluIHRleHQ=\",\r\n  \"referenceId\": \"ref123\",\r\n  \"timeStamp\": \"2018-12-06T12:07:44.403Z\"\r\n}");

Request request = new Request.Builder()
  .url("http://localhost:8087/cryptomanager/v1.0/encrypt")
  .post(body)
  .addHeader("content-type", "application/json")
  .build();

Response response = client.newCall(request).execute();
  ```
  
  *Response*
  
  Status:200
  
  ```
{
"data":"EsGmECXJucN7AH6DHoKzzGs3bwspfOftQHwhpOWHUpptyFU1MYOz_iJxi1dBcLDXKQE_OV1xrY8Jyw0XUcSDbNYW9qHr5Hfbe30kTc-hCVNKItYN0OYOSBvgq9pd6TAatzlADvW6PRbRyHuumRqoD2ZL0tddiZqe6pa_Ya3hlTYsZm-L_65IJnkGDJLmxmMVS-pqqKqqtrXnTdYMjvK2wMkuZIFz4SX6F0jxnHz7XhrKSBzY8b8O4z1ZUterB450kKPzbRsZ3fySdjlpqhwtuVXZV6gkAA_n1iACOksvSyUZ7BN5AgWKnnsUHaNyF6f-e564G6nTN4M3Fyd_Z_KzxCNLRVlfU1BMSVRURVIjcvEHI6pM3H-kRWMRBZJDyte4BHKuUj4PBtU3dJ4kb_Vmd4nFBuguSh_tFHiz62GB"
}
  ```
  
  *Decrypt Request*
  
  ```
OkHttpClient client = new OkHttpClient();

MediaType mediaType = MediaType.parse("application/json");

RequestBody body = RequestBody.create(mediaType, "{\n  \"applicationId\": \"REGISTRATION\",\n  \"data\": \"EsGmECXJucN7AH6DHoKzzGs3bwspfOftQHwhpOWHUpptyFU1MYOz_iJxi1dBcLDXKQE_OV1xrY8Jyw0XUcSDbNYW9qHr5Hfbe30kTc-hCVNKItYN0OYOSBvgq9pd6TAatzlADvW6PRbRyHuumRqoD2ZL0tddiZqe6pa_Ya3hlTYsZm-L_65IJnkGDJLmxmMVS-pqqKqqtrXnTdYMjvK2wMkuZIFz4SX6F0jxnHz7XhrKSBzY8b8O4z1ZUterB450kKPzbRsZ3fySdjlpqhwtuVXZV6gkAA_n1iACOksvSyUZ7BN5AgWKnnsUHaNyF6f-e564G6nTN4M3Fyd_Z_KzxCNLRVlfU1BMSVRURVIjcvEHI6pM3H-kRWMRBZJDyte4BHKuUj4PBtU3dJ4kb_Vmd4nFBuguSh_tFHiz62GB\",\n  \"referenceId\": \"ref123\",\n  \"timeStamp\": \"2018-12-06T12:07:44.403Z\"\n}\n");

Request request = new Request.Builder()
  .url("http://localhost:8087/cryptomanager/v1.0/decrypt")
  .post(body)
  .addHeader("content-type", "application/json")
  .build();

Response response = client.newCall(request).execute();
  ```
  
  *Response*
  
  Status:200
  
  ```
{
 "data": "VGhpcyBpcyBhIHBsYWluIHRleHQ"
}
  ```
  
  








