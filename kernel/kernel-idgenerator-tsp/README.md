## kernel-idgenerator-tsp

1- [Background & Design]

2- API Documentation

 ```
localhost:8080/swagger-ui.html

 ```
 
  **Properties to be added in Spring application environment using this component**

[kernel-idgenerator-tsp-dev.properties](../../config/kernel-idgenerator-tsp-dev.properties)


 **Database properties**
 
schema:ids

table:tsp_id 


**Usage Sample:**

  *Request:*
  
```
OkHttpClient client = new OkHttpClient();

Request request = new Request.Builder()
  .url("http://104.211.214.143:8080/idgenerator/tsp")
  .get()
  .build();

Response response = client.newCall(request).execute();
```

 
  *Response:*
  
  HttpStatus: 200 OK
  
```
{
    "tspId": "1000"
}
```
 