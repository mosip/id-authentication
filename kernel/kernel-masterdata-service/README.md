## kernel-masterdata-service


 [Refer wiki for spec](https://github.com/mosip/mosip/wiki/Master-data-APIs) 
 https://github.com/mosip/mosip/wiki/Master-data-APIs
 
Default Port and Context Path

```
server.port=8086
server.servlet.path=/masterdata

```

localhost:8086/masterdata/swagger-ui.html


**Application Properties**

```
#spring.jackson.serialization.WRITE_DATES_AS_TIMESTAMPS = false


javax.persistence.jdbc.driver=org.postgresql.localhost:8888/mosip_master
javax.persistence.jdbc.user=dbuser
javax.persistence.jdbc.password=dbpwd


hibernate.dialect=org.hibernate.dialect.PostgreSQL95Dialect
hibernate.jdbc.lob.non_contextual_creation=true
hibernate.hbm2ddl.auto=none
hibernate.show_sql=false
hibernate.format_sql=true
hibernate.connection.charSet=utf8
hibernate.cache.use_second_level_cache=false
hibernate.cache.use_query_cache=false
hibernate.cache.use_structured_entries=false
hibernate.generate_statistics=false

#property name is case sensitive and required for interceptors configuration
#hibernate.ejb.interceptor=io.mosip.kernel.masterdata.config.MasterDataInterceptor

mosip.kernel.syncdata-service-globalconfigs-url=https://host/syncdata/v1.0/globalconfigs
mosip.kernel.supported-languages-key=mosip.supported-languages
```

**ERD**
-----------------------------


![ERD](kernel-masterdata-erd.png)

