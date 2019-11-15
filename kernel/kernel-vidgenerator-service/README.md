## kernel-vidgenerator-service

[Background & Design](../../docs/design/kernel/kernel-vidgenerator.md)

[Api Documentation](https://github.com/mosip/mosip/wiki/Kernel-APIs#vid)


Default Port and Context Path

```
server.port=8099
server.servlet.path=v1/vidgenerator

```

** Properties to be added in parent Spring Application environment **

```
javax.persistence.jdbc.driver=org.postgresql.Driver
hibernate.dialect=org.hibernate.dialect.PostgreSQL95Dialect
hibernate.jdbc.lob.non_contextual_creation=true
hibernate.hbm2ddl.auto=none
hibernate.show_sql=false
hibernate.format_sql=false
hibernate.connection.charSet=utf8
hibernate.cache.use_second_level_cache=false
hibernate.cache.use_query_cache=false
hibernate.cache.use_structured_entries=false
hibernate.generate_statistics=false
spring.datasource.initialization-mode=always
hibernate.current_session_context_class=org.springframework.orm.hibernate5.SpringSessionContext




auth.server.validate.url=https://<host>:<port>/v1/authmanager/authorize/validateToken



#-----------------------------VID Properties--------------------------------------
# length of the vid
mosip.kernel.vid.length=16

# Upper bound of number of digits in sequence allowed in id. For example if
# limit is 3, then 12 is allowed but 123 is not allowed in id (in both
# ascending and descending order)
# to disable sequence limit validation assign 0 or negative value
mosip.kernel.vid.length.sequence-limit=3

# Number of digits in repeating block allowed in id. For example if limit is 2,
# then 4xxx4 is allowed but 48xxx48 is not allowed in id (x is any digit)
# to disable repeating block validation assign 0 or negative value
mosip.kernel.vid.length.repeating-block-limit=2


# Lower bound of number of digits allowed in between two repeating digits in
# id. For example if limit is 2, then 11 and 1x1 is not allowed in id (x is any digit)
# to disable repeating limit validation, assign 0  or negative value
mosip.kernel.vid.length.repeating-limit=2

# list of number that id should not be start with
# to disable null
mosip.kernel.vid.not-start-with=0,1

#restricted numbers for vid
mosip.kernel.vid.restricted-numbers=786,666



#----------------------- Crypto --------------------------------------------------
#Crypto asymmetric algorithm name
mosip.kernel.crypto.asymmetric-algorithm-name=RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING
#Crypto symmetric algorithm name
mosip.kernel.crypto.symmetric-algorithm-name=AES/GCM/PKCS5Padding
#Keygenerator asymmetric algorithm name
mosip.kernel.keygenerator.asymmetric-algorithm-name=RSA
#Keygenerator symmetric algorithm name
mosip.kernel.keygenerator.symmetric-algorithm-name=AES
#Asymmetric algorithm key length
mosip.kernel.keygenerator.asymmetric-key-length=2048
#Symmetric algorithm key length
mosip.kernel.keygenerator.symmetric-key-length=256
#Keygenerator symmetric algorithm name
mosip.kernel.keygenerator.symmetric-algorithm-name=AES
# keygenerator asymmetric algorithm name
mosip.kernel.keygenerator.asymmetric-algorithm-name=RSA
#Encrypted data and encrypted symmetric key separator
mosip.kernel.data-key-splitter=#KEY_SPLITTER#
#GCM tag length
mosip.kernel.crypto.gcm-tag-length=128
#Hash algo name
mosip.kernel.crypto.hash-algorithm-name=PBKDF2WithHmacSHA512
#Symmtric key length used in hash
mosip.kernel.crypto.hash-symmetric-key-length=256
#No of iterations in hash
mosip.kernel.crypto.hash-iteration=100000
#Sign algo name
mosip.kernel.crypto.sign-algorithm-name=SHA512withRSA




#minimum threshold of unused vid
mosip.kernel.vid.min-unused-threshold=100000
#number of vids to generate
mosip.kernel.vid.vids-to-generate=200000
#time to renew after expiry(in days)
mosip.kernel.vid.time-to-renew-after-expiry=5
#for genaration on init vids timeout 
mosip.kernel.vid.pool-population-timeout=10000000



kernel.vid.revoke-scheduler-type=cron
#schedular seconds configuration
kernel.vid.revoke-scheduler-seconds=0
#schedular minutes configuration
kernel.vid.revoke-scheduler-minutes=0
#schedular hours configuration
kernel.vid.revoke-scheduler-hours=23
#schedular days configuration
kernel.vid.revoke-scheduler-days_of_month=*
#schedular months configuration
kernel.vid.revoke-scheduler-months=*
#schedular weeks configuration
kernel.vid.revoke-scheduler-days_of_week=*





vid_database_url=jdbc:postgresql://<host>:<port>/mosip_kernel
vid_database_username=<username>
vid_database_password=<password>


```




** Database Properties **

Schema : kernel

Table : uin

**Usage Sample:**

  *GET Request:*
  
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


 *PUT Request:*


```
OkHttpClient client = new OkHttpClient();

Request request = new Request.Builder()
  .url("http://<host>:<port>/v1/vidgenerator/vid?videxpiry=2019-11-09T06:12:52.994Z")
  .get()
  .build();

Response response = client.newCall(request).execute();
```

*Response:*
  
  HttpStatus: 200 OK
  
```
{
 "vid":"2902482016986749"
}
```

