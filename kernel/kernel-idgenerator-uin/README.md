## Module kernel-idgenerator-uin
Uin generator functionality is to generate, store and provide uins.

Rules of UIN generation:
1. UIN generated should be of the length defined by the ADMIN
2. In absence of configured length policy, UIN generated should be of 12 digits
3. UIN should be generated as per the defined logic mentioned below
4. The number should not contain any alphanumeric characters
5. The number should not contain any repeating numbers for 2 or more than 2 digits
6. The number should not contain any sequential number for 3 or more than 3 digits
7 .The numbers should not be generated sequentially
8. The number should not have repeated block of numbers for 2 or more than 2 digits
9. The number should not contain the restricted numbers defined by the ADMIN
10. The last digit in the number should be reserved for a checksum
11. The number should not contain '0' or '1' as the first digit.

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









