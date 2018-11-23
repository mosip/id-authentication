## Module kernel-idgenerator-uin
Uin generator functionality is to generate, store and provide uins

**Api Documentation**
```
mvn javadoc:javadoc
```


** Properties to be added in parent Spring Application environment **

[kernel-idgenerator-uin-dev.properties](../../config/kernel-idgenerator-uin-dev.properties)




** Database Properties **

Schema : ids

Table : uin

**Usage Sample**

  *Request*
  
```
OkHttpClient client = new OkHttpClient();

Request request = new Request.Builder()
  .url("http://104.211.214.143:8080/idgenerator/uin")
  .get()
  .build();

Response response = client.newCall(request).execute();
```


  *Response*
  
```
Status: 200
{
    "uin": "742192367293"
}
```









