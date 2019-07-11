## kernel-tokenidgenerator-service

[Background & Design](../../docs/design/kernel/kernel-idgenerator-statictoken.md)

[Api Documentation](https://github.com/mosip/mosip/wiki/Kernel-APIs#static-token-generator)


Default Port and Context Path

```
server.port=8097
server.servlet.path=/tokenidgenerator

```

** Properties to be added in parent Spring Application environment **

```
#-----------------------------TOKENIFGENERATOR Properties--------------------------------------
#uin salt
mosip.kernel.tokenid.uin.salt=zHuDEAbmbxiUbUShgy6pwUhKh9DE0EZn9kQDKPPKbWscGajMwf
#partnercode salt
mosip.kernel.tokenid.partnercode.salt=yS8w5Wb6vhIKdf1msi4LYTJks7mqkbmITk2O63Iq8h0bkRlD0d

#length of the token id
mosip.kernel.tokenid.length=36

```


**Usage Sample:**

  *GET Request:*
  
  
```
OkHttpClient client = new OkHttpClient();

Request request = new Request.Builder()
  .url("http://localhost:8097/v1/tokenidgenerator/7394829283/PC001")
  .get()
  .build();

Response response = client.newCall(request).execute();
```


  *Response:*
  
  HttpStatus: 200 OK
  
```
{
	"id": "mosip.kernel.tokenid.generate",
	"version": "1.0",
	"metadata": {},
	"responsetime": "2019-04-04T05:03:18.287Z",
	"response": {
                  "tokenID": "268177021248100621690339355202974361"
                },
        "errors": []
}
```

