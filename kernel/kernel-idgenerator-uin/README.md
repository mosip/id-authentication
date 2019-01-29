## kernel-idgenerator-uin

[Background & Design](../../docs/design/kernel/kernel-idgenerator-uin.md)

[Api Documentation](https://github.com/mosip/mosip/wiki/Kernel-APIs#4-uin)

Default Port and Context Path

```
server.port=8080
server.servlet.path=/uingenerator
```
localhost:8080/uingenerator/swagger-ui.html


**Application Properties**

[kernel-idgenerator-uin-dev.properties](../../config/kernel-idgenerator-uin-dev.properties)

```
#-----------------------------UIN Properties--------------------------------------
#length of the uin
mosip.kernel.uin.length=12
#minimun threshold of uin
mosip.kernel.uin.min-unused-threshold=100000
#number of uins to generate
mosip.kernel.uin.uins-to-generate=200000
#uin generation cron
mosip.kernel.uin.uin-generation-cron=0 * * * * *
#restricted numbers for uin
mosip.kernel.uin.restricted-numbers=786,666
#sequence limit for uin filter
mosip.kernel.uin.length.sequence-limit=3
#repeating block limit for uin filter
mosip.kernel.uin.length.repeating-block-limit=2
#repeating limit for uin filter
mosip.kernel.uin.length.repeating-limit=2


# DB Properties For Development
--------------------------------------
javax.persistence.jdbc.driver=org.postgresql.Driver
javax.persistence.jdbc.url=jdbc:postgresql://locallhost:8888/mosip_kernel
javax.persistence.jdbc.user=dbuser
javax.persistence.jdbc.password=dbpwd

hibernate.hbm2ddl.auto=update
hibernate.dialect=org.hibernate.dialect.PostgreSQL95Dialect
hibernate.jdbc.lob.non_contextual_creation=true
hibernate.show_sql=false
hibernate.format_sql=false
hibernate.connection.charSet=utf8
hibernate.cache.use_second_level_cache=false
hibernate.cache.use_query_cache=false
hibernate.cache.use_structured_entries=false
hibernate.generate_statistics=false
hibernate.current_session_context_class=org.springframework.orm.hibernate5.SpringSessionContext
```


** Database Properties **

Schema : kernel

Table : uin


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



**Usage Sample:**

  *Request:*
  
```
OkHttpClient client = new OkHttpClient();

Request request = new Request.Builder()
  .url("http://104.211.214.143:8080/idgenerator/uin")
  .get()
  .build();

Response response = client.newCall(request).execute();
```


  *Response:*
  
  HttpStatus: 200 OK
  
```
{
    "uin": "742192367293"
}
```









