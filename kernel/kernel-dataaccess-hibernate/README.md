## kernel-dataaccess-hibernate

This module is used by modules that have to use the DAO layer. There can be multiple databases and multiple DAO framework implementation can be used in various applications in the MOSIP platform.
 
[Background & Design](../../docs/design/kernel/kernel-dataaccess.md)
 

 [API Documentation ]
 
 ```
 mvn javadoc:javadoc

 ```
 
**Maven Dependency**

```
		<dependency>
			<groupId>io.mosip.kernel</groupId>
			<artifactId>kernel-dataaccess-hibernate</artifactId>
			<version>1.0.0-SNAPSHOT</version>
		</dependency>
```


 **Properties to be added in Application environment using this component**

```
javax.persistence.jdbc.driver=org.h2.Driver
javax.persistence.jdbc.url=jdbc:h2\:mem\:testdb
javax.persistence.jdbc.user=sa
javax.persistence.jdbc.password=sp
#jdbc.bootpassword=bootpwd

hibernate.hbm2ddl.auto=create-drop
hibernate.dialect=org.hibernate.dialect.H2Dialect
hibernate.show_sql=false
hibernate.format_sql=false
hibernate.connection.charSet=utf8
hibernate.cache.use_second_level_cache=false
hibernate.cache.use_query_cache=false
hibernate.cache.use_structured_entries=false
hibernate.generate_statistics=false

#property name is case sensitive and required for interceptors configuration
#hibernate.ejb.interceptor=io.mosip.kernel.dataaccess.hibernate.test.CustomInterceptor

```

##Usage Sample

Create repository using BaseRepository

```
public interface TestRepository extends BaseRepository<Person, Long>{

}

```
 
**Create:**
 
```
@Autowired
TestRepository repository;

Person baseEntity = new Person("personName");
		repository.create(baseEntity);
 
 ```
 
 **Update:**
 
```
@Autowired
TestRepository repository;

Person baseEntity = new Person("personName");
		repository.update(baseEntity);
 
 ```
 
**FindById:**
 
```
@Autowired
TestRepository repository;

Person baseEntity = new Person("personName");
		repository.create(baseEntity);
		repository.findById(Person.class, 1);
 
 ```
 
**FindAll:**
 
```
@Autowired
TestRepository repository;

Person baseEntity = new Person("personName");
		repository.create(baseEntity);
		repository.findAll(Person.class);
 
 ```
 
**Delete:**
 
```
@Autowired
TestRepository repository;

Person baseEntity = new Person("personName");
		repository.create(baseEntity);
		repository.delete(Person.class, 1);
 
 ```
 
**CreateQuery:**
 
 ```
 TestRepository repository;
 
		String qlString = "SELECT c FROM Person c WHERE c.name LIKE :personName";
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("personName", "personName");
		Person baseEntity = new Person("personName");
		repository.create(baseEntity);
		repository.createQuerySelect(qlString, parameterMap);
 
 ```
 
  
**CreateNamedQuery:**
 
 ```
 TestRepository repository;
 
		String name = "findAllCustomersWithName";
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("personName", "personName");
		Person baseEntity = new Person("personName");
		repository.create(baseEntity);
		repository.createNamedQuerySelect(name, Person.class, parameterMap);
 
 ```








