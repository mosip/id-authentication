## kernel-dataaccess-hibernate

This module is used by modules that have to use the DAO layer. There can be multiple databases and multiple DAO framework implementation can be used in various applications in the MOSIP platform.
 
 1- [Background & Design](../../design/kernel/kernel-dataaccess.md)
 

 2- [API Documentation ](TBA)
 
 ```
 mvn javadoc:javadoc

 ```
 
 To use this api, add this to dependency list:

```
		<dependency>
			<groupId>io.mosip.kernel</groupId>
			<artifactId>kernel-dataaccess-hibernate</artifactId>
			<version>1.0.0-SNAPSHOT</version>
		</dependency>
```

3- Usage Sample

Create repository using BaseRepository

```
public interface TestRepository extends BaseRepository<Person, Long>{

}

```
 
Create:
 
```
@Autowired
TestRepository repository;

Person baseEntity = new Person("personName");
		repository.create(baseEntity);
 
 ```
 
 Update:
 
```
@Autowired
TestRepository repository;

Person baseEntity = new Person("personName");
		repository.update(baseEntity);
 
 ```
 
  FindById:
 
```
@Autowired
TestRepository repository;

Person baseEntity = new Person("personName");
		repository.create(baseEntity);
		repository.findById(Person.class, 1);
 
 ```
 
FindAll:
 
```
@Autowired
TestRepository repository;

Person baseEntity = new Person("personName");
		repository.create(baseEntity);
		repository.findAll(Person.class);
 
 ```
 
 Delete:
 
```
@Autowired
TestRepository repository;

Person baseEntity = new Person("personName");
		repository.create(baseEntity);
		repository.delete(Person.class, 1);
 
 ```
 
 CreateQuery:
 
 ```
 TestRepository repository;
 
		String qlString = "SELECT c FROM Person c WHERE c.name LIKE :personName";
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("personName", "personName");
		Person baseEntity = new Person("personName");
		repository.create(baseEntity);
		repository.createQuerySelect(qlString, parameterMap);
 
 ```
 
  
 CreateNamedQuery:
 
 ```
 TestRepository repository;
 
		String name = "findAllCustomersWithName";
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("personName", "personName");
		Person baseEntity = new Person("personName");
		repository.create(baseEntity);
		repository.createNamedQuerySelect(name, Person.class, parameterMap);
 
 ```








