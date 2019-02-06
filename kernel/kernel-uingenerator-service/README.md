## kernel-idgenerator-uin

[Background & Design](../../docs/design/kernel/kernel-idgenerator-uin.md)

Uin generator functionality is to generate, store and provide uins.

Rules of UIN generation:
1. The number should not contain any alphanumeric characters
2. The number should not contain any repeating numbers for 2 or more than 2 digits
3. The number should not contain any sequential number for 3 or more than 3 digits
4. The numbers should not be generated sequentially
5. The number should not have repeated block of numbers for 2 or more than 2 digits
6. The number should not contain the restricted numbers defined by the ADMIN
7. The last digit in the number should be reserved for a checksum
8. The number should not contain '0' or '1' as the first digit
9. First 5 digits should be different from the last 5 digits (E.g. 4345643456)
10. First 5 digits should be different to the last 5 digits reversed (E.g. 4345665434)
11. UIN should not be an ascending or descending cyclic figure (E.g. 4567890123, 6543210987)
12. UIN should be different from the repetition of the first two digits 5 times (E.g. 3434343434)
13. UIN should not contain three even adjacent digits (E.g. 3948613752)
14. UIN should not contain ADMIN defined restricted number

**Api Documentation**


```
mvn javadoc:javadoc
```


** Properties to be added in parent Spring Application environment **

[kernel-uingenerator-service-dev.properties](../../config/kernel-uingenerator-service-dev.properties)




** Database Properties **

Schema : kernel

Table : uin

**Usage Sample:**

  *Request:*
  
```
OkHttpClient client = new OkHttpClient();

Request request = new Request.Builder()
  .url("https://integ.mosip.io/uingenerator/v1.0/uin")
  .get()
  .build();

Response response = client.newCall(request).execute();
```


  *Response:*
  
  HttpStatus: 200 OK
  
```
{
    "uin": "6127460851"
}
```









