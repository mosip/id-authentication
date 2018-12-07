## kernel-packetserver-http:
[Background & Design](../../design/kernel/kernel-packetserver-http.md)
 

**Api Documentation**

[API Documentation <TBA>](TBA)

```
mvn javadoc:javadoc
```

**Maven dependency**
  
 ```
    <dependency>
		<groupId>io.mosip.kernel</groupId>
		<artifactId>kernel-packetserver-http</artifactId>
		<version>${project.version}</</version>
	</dependency>
 ```

**Properties to be added in parent Spring Application environment**

[kernel-packetserver-http-dev.properties](../../config/kernel-packetserver-http-dev.properties)

**Usage Sample**


  *Usage 1:*
  
  *Request*
  
  ```
OkHttpClient client = new OkHttpClient();

MediaType mediaType = MediaType.parse("multipart/form-data;boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW");

RequestBody body = RequestBody.create(mediaType, "------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"packet\"; filename=\"C:\\Users\\m1044287\\Downloads\\hsm\\fif.jpg\"\r\nContent-Type: image/jpeg\r\n\r\n\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW--");

Request request = new Request.Builder()
  .url("http://104.211.214.143:8082/")
  .post(body)
  .addHeader("content-type", "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW")

Response response = client.newCall(request).execute();
  ```
  
  *Response*
  
  Status:200
  
  ```
  {
  "fileName": "packet.zip",
  "fileSizeInBytes": 126
  }
  ```
  
  


 *Max Packet Size Exception Scenario*

  Status:400

```
{
   "errors": [
      {
         "code": "KER-FTU-008",
         "message": "packet size should be less than 5 MB and greater than 0"
      }
   ]
}
```
  
  








