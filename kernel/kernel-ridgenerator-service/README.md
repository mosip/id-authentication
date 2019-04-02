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
HttpResponse<String> response = Unirest.get("http://localhost:8080/ridgenerator/generate/rid/21234/43267")
  .header("content-type", "application/json")
  .header("cache-control", "no-cache")
  .header("postman-token", "16196521-5e9d-7d61-ae46-8fda4e3220ca")
  .asString();
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


