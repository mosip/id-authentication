# kernel-ridgenerator-service

[Background & Design -TBA-](../../docs/design/kernel/kernel-ridgenerator.md)

[Api Documentation](https://github.com/mosip/mosip/wiki/Kernel-APIs#7-ridgenerator)

Default Port and Context Path

```
server.port=8096
server.servlet.path=/ridgenerator

```

localhost:8096/ridgenerator/swagger-ui.html

**Application Properties**

[application-dev.properties](../../config/application-dev.properties)


** Usage Sample**
 
 Usage1:
 
 RID Generator Request:
 
 ```
OkHttpClient client = new OkHttpClient();

Request request = new Request.Builder()
  .url("http://localhost:8080/ridgenerator/generate/rid/21234/43267")
  .get()
  .build();

Response response = client.newCall(request).execute();

 ```
 
RID Generator Responses :

Successful Notification :

HttpStatus : 200 Ok

```

{
  "id": null,
  "version": null,
  "responsetime": "2019-03-29T13:23:20.195Z",
  "metadata": null,
  "response": {
    "rid": "21234432670000120190329132320"
  },
  "errors": null
}

```


