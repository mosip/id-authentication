## kernel-dataaccess-hibernate

kernel-dataaccess-hibernate module can be used for database operations.
 
 1- [Background & Design](../../design/kernel/kernel-dataaccess.md)
 

 2- [API Documentation ](TBA)
 
 ```
 mvn javadoc:javadoc

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








