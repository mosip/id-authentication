## kernel-uingenerator-service

[Background & Design](../../docs/design/kernel/kernel-uingenerator.md)

[Api Documentation](https://github.com/mosip/mosip/wiki/Kernel-APIs#4-uin)


Default Port and Context Path

```
server.port=8080
server.servlet.path=/uingenerator

```

** Properties to be added in parent Spring Application environment **

```
#-----------------------------UIN Properties--------------------------------------
#length of the uin
mosip.kernel.uin.length=10
#minimun threshold of uin
mosip.kernel.uin.min-unused-threshold=100000
#number of uins to generate
mosip.kernel.uin.uins-to-generate=200000
#restricted numbers for uin
mosip.kernel.uin.restricted-numbers=786,666
#sequence limit for uin filter
#to disable validation assign zero or negative value
mosip.kernel.uin.length.sequence-limit=3
#repeating block limit for uin filter
#to disable validation assign zero or negative value
mosip.kernel.uin.length.repeating-block-limit=2
#repeating limit for uin filter
#to disable validation assign zero or negative value
mosip.kernel.uin.length.repeating-limit=2
#reverse group digit limit for uin filter
mosip.kernel.uin.length.reverse-digits-limit=5
#group digit limit for uin filter
mosip.kernel.uin.length.digits-limit=5
#should not start with
mosip.kernel.uin.not-start-with=0,1
#adjacent even digit limit for uin filter
mosip.kernel.uin.length.conjugative-even-digits-limit=3



#Database mappings uin
uin_database_url=jdbc:postgresql://localhost:8888/mosip_kernel
uin_database_username=dbusername
uin_database_password=dbpwd
hibernate.current_session_context_class=org.springframework.orm.hibernate5.SpringSessionContext

```




** Database Properties **

Schema : kernel

Table : uin

**Usage Sample:**

  *Request:*
  
```
OkHttpClient client = new OkHttpClient();

Request request = new Request.Builder()
  .url("http://localhost:8080/uingenerator/v1.0/uin")
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









