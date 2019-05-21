## kernel-signature-service

[Background & Design](../../docs/design/kernel/kernel-cryptography-digitalsignature.md)

[Api Documentation](https://github.com/mosip/mosip/wiki/Kernel-APIs#crypto-manager)

Default Port and Context Path

```
server.port=8092
server.servlet.path=/v1/signature
```
localhost:8092/v1/signature/swagger-ui.html


**Application Properties**

```
#----------------------- signature --------------------------------------------------
auth.server.validate.url=https://dev-int.mosip.io/v1.0/authorize/validateToken

mosip.kernel.keygenerator.asymmetric-algorithm-name=RSA
mosip.kernel.keygenerator.asymmetric-algorithm-length=2048
mosip.kernel.keygenerator.symmetric-algorithm-name=AES
mosip.kernel.keygenerator.symmetric-algorithm-length=256
mosip.kernel.crypto.symmetric-algorithm-name=AES
mosip.kernel.crypto.asymmetric-algorithm-name=RSA
mosip.kernel.signature.signature-request-id=SIGNATURE.REQUEST
mosip.kernel.signature.signature-version-id=v1.0
mosip.sign.applicationid=KERNEL
mosip.sign.refid=KER
mosip.signed.header=response-signature
mosip.kernel.signature.cryptomanager-encrypt-url=https://dev.mosip.io/v1/cryptomanager/private/encrypt
mosip.kernel.keymanager-service-publickey-url=https://dev.mosip.io/v1/keymanager/publickey/{applicationId}
```
**The inputs which have to be provided for sign:**
1. data - Mandatory

**The response will be sign data if request is successful** 

**Exceptions to be handled while using this functionality:**

1. RequestException ("KER-CSS-999", "Invalid request input")

**Usage Sample**
  
  *Request*
  
  ```
OkHttpClient client = new OkHttpClient();

MediaType mediaType = MediaType.parse("application/json");
RequestBody body = RequestBody.create(mediaType, "{ \"id\": \"string\", \"metadata\": {}, \"request\": { \"data\": \"admin\" }, \"requesttime\": \"2018-12-10T06:12:52.994Z\", \"version\": \"string\" }");
Request request = new Request.Builder()
  .url("http://localhost:8092/v1/signature/sign")
  .post(body)
  .addHeader("Content-Type", "application/json")
  .build();

Response response = client.newCall(request).execute();
  ```
  
  *Response*
  
 HTTP Status: 200 OK
  
  ```
{
    "id": null,
    "version": null,
    "responsetime": "2019-05-20T05:59:32.178Z",
    "metadata": null,
    "response": {
        "signature": "ZeNsCOsdgf0UgpXDMry82hrHS6b1ZKvS-tZ_3HBGQHleIu1fZA6LNTtx7XZPFeC8dxsyuYO_iN3mVExM4J2tPlebzsRtuxHigi9o7DI_2xGqFudzlgoH55CP_BBNUDmGm6m-lTMkRx6X61dKfKDNo2NipZdM-a_cHf6Z0aVAU4LdJhV4xWOOm8Pb8sYIc2Nf6kUJRiidEGrxonUCfXX1XlnjMAo75wu99pN8G0mc7JhOehUqbwuXwKo4sQ694ae4F_AYl70sepX24v-0k0ga9esXR4i9rKaoHbzhQFtt2hangQkxHajq9ZTrXWMhd4msTzjHCKdEPXQFsTbKrgKtDQ",
        "timestamp": "2019-05-20T05:59:31.934Z"
    },
    "errors": null
}
  ```
  

 *Invalid data Request*
 
 HTTP Status: 200 

```
{
    "id": null,
    "version": null,
    "responsetime": "2019-05-20T06:02:53.503Z",
    "metadata": null,
    "response": null,
    "errors": [
        {
            "errorCode": "KER-CSS-999",
            "message": "request.data: must not be blank"
        }
    ]
}
```

**The inputs which have to be provided for validate sign response by passing public key along with data and sign response:**
1.signature -Mandatory
2.data - Mandatory
3.publickey -Mandatory


**The response will be Validation Successful  if request is successful, else throw exception Validation Unsuccessful** 

**Exceptions to be handled while using this functionality:**

1. RequestException ("KER-CSS-999", "Invalid request input")
2. SignatureFailureException("KER-CSS-101","Validation Unsuccessful")

**Usage Sample**
  
  *Request*
  
  ```
OkHttpClient client = new OkHttpClient();

MediaType mediaType = MediaType.parse("application/json");


RequestBody body = RequestBody.create(mediaType, "{
  \"id\": \"string\",
  \"metadata\": {},
  \"request\": {
    \"data\": \"test\",
    \"signature\": \"DrgkF2vm4WvBe04UNe-RePRcrg77uQpsH3GENRcglBsid-K0UDReeeZVKwimOdwV7Ht1j-_D1BFf2sCrM8ni7ztE5Xc_3TEaniOAnOgZDRSI0GG-uSqjH51AwTSl1PYdStfXtOn6HEfEU68JG7TdAliDI5C7thJ1YNmPnHusIsZzX6sW_VfvSpLeA_RzCqnUDH_VaEzZt_5zRYiQv9van4wt0P7HTfIBlQ5zaeO3wXOc3Pogct3ssKwqdaMmZdc7QTDOFqDZZVceMTIXKyiH-ZVs_u3QXRysiLVdXoz7d7yXHdWxQtzsfMjY7alMJNgbmu4X26LYNRemn65Mmn6ixA\",
     \"publickey\": \"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnoocJbIeMuAzqSzuJX9CvXmFFka3Fz3C-u9vz6c8RsJSKBCe_SAOi31IvL992kuy1qO4XTS-cUuirx-djuF0E7r5TbQFKlNa-FoPJu8QRIGw2rWVQsc2c0Aqd5cfhr9fgTsM3V3URl1jXY645v9EPE0Ih5E26ld6JQQQ90mpvoa6XlJEf5SUAOuzvr5ws5VoZgEQ6wjO05dZSaEL9vrA5npsNSwLb55FqZb7w9qLZfYbPOBVxUZ-HTddBLP6KvlIHWzsVapjvhUHPgSO0AZDYmx3kkKb7jFuWelPibNyKy619AAnlQX3VR39CKi-6sPLRABs4v-npsFLNz9Wd_VJHwIDAQAB\"
  },
  \"requesttime\": \"2018-12-10T06:12:52.994Z\"}");
  
  
  
Request request = new Request.Builder()
  .url("http://localhost:8092/v1/signature/public/validate")
  .post(body)
  .addHeader("Content-Type", "application/json")
  .build();

Response response = client.newCall(request).execute();
  ```
  
  *Response*
  
 HTTP Status: 200 OK
  
  ```
{
    "id": null,
    "version": null,
    "responsetime": "2019-05-20T07:16:40.794Z",
    "metadata": null,
    "response": {
        "status": "success",
        "message": "Validation Successful"
    },
    "errors": null
}
  ```
  

 *Invalid data Request*
 
 HTTP Status: 200 OK 

```
{
    "id": null,
    "version": null,
    "responsetime": "2019-05-20T07:21:18.136Z",
    "metadata": null,
    "response": null,
    "errors": [
        {
            "errorCode": "KER-CSS-999",
            "message": "request.data: must not be blank"
        },
        {
            "errorCode": "KER-CSS-999",
            "message": "request.publickey: must not be blank"
        },
        {
            "errorCode": "KER-CSS-999",
            "message": "request.signature: must not be blank"
        }
    ]
}
```
*Validation Unsuccessful*
 
 HTTP Status: 200 OK 

```
{
    "id": null,
    "version": null,
    "responsetime": "2019-05-20T10:13:13.470Z",
    "metadata": null,
    "response": null,
    "errors": [
        {
            "errorCode": "KER-CSS-101",
            "message": "Validation Unsuccessful"
        }
    ]
}
```


**The inputs which have to be provided for validate sign response by passing Response Timestamp along with the data and sign response:**
1.signature -Mandatory
2.data - Mandatory
3.timestamp -Mandatory


**The response will be Validation Successful  if request is successful, else throw exception Validation Unsuccessful** 

**Exceptions to be handled while using this functionality:**

1. RequestException ("KER-CSS-999", "Invalid request input")
2. SignatureFailureException("KER-CSS-101","Validation Unsuccessful")

**Usage Sample**
  
  *Request*
  
  ```
OkHttpClient client = new OkHttpClient();

MediaType mediaType = MediaType.parse("application/json");

RequestBody body = RequestBody.create(mediaType, "{ \"id\": \"string\", \"metadata\": {}, \"request\": { \"signature\": \"DrgkF2vm4WvBe04UNe-RePRcrg77uQpsH3GENRcglBsid-K0UDReeeZVKwimOdwV7Ht1j-_D1BFf2sCrM8ni7ztE5Xc_3TEaniOAnOgZDRSI0GG-uSqjH51AwTSl1PYdStfXtOn6HEfEU68JG7TdAliDI5C7thJ1YNmPnHusIsZzX6sW_VfvSpLeA_RzCqnUDH_VaEzZt_5zRYiQv9van4wt0P7HTfIBlQ5zaeO3wXOc3Pogct3ssKwqdaMmZdc7QTDOFqDZZVceMTIXKyiH-ZVs_u3QXRysiLVdXoz7d7yXHdWxQtzsfMjY7alMJNgbmu4X26LYNRemn65Mmn6ixA\", \"data\": \"test\", \"timestamp\": \"2019-05-20T07:28:04.269Z\" }, \"requesttime\": \"2018-12-10T06:12:52.994Z\", \"version\": \"string\" }");



Request request = new Request.Builder()
  .url("http://localhost:8092/v1/signature/validate")
  .post(body)
  .addHeader("Content-Type", "application/json")
  .build();

Response response = client.newCall(request).execute();
  ```
  
  *Response*
  
 HTTP Status: 200 OK
  
  ```
{
    "id": null,
    "version": null,
    "responsetime": "2019-05-20T07:16:40.794Z",
    "metadata": null,
    "response": {
        "status": "success",
        "message": "Validation Successful"
    },
    "errors": null
}
  ```
  

 *Invalid data Request*
 
 HTTP Status: 200 OK 

```
{
    "id": null,
    "version": null,
    "responsetime": "2019-05-20T07:21:18.136Z",
    "metadata": null,
    "response": null,
    "errors": [
        {
            "errorCode": "KER-CSS-999",
            "message": "request.data: must not be blank"
        },
        {
            "errorCode": "KER-CSS-999",
            "message": "request.timestamp: must not be blank"
        },
        {
            "errorCode": "KER-CSS-999",
            "message": "request.signature: must not be blank"
        }
    ]
}
```
*Validation Unsuccessful Request*
 
 HTTP Status: 200 OK 

```
{
    "id": null,
    "version": null,
    "responsetime": "2019-05-20T07:39:02.469Z",
    "metadata": null,
    "response": null,
    "errors": [
        {
            "errorCode": "KER-CSS-101",
            "message": "Validation Unsuccessful"
        }
    ]
}
```

  
  








