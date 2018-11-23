## kernel-dataaccess-hibernate

This folder has kernel-dataaccess-hibernate module which can be used for database operations.
 
 1- [Background & Design](../../design/kernel/kernel-dataaccess.md)
 

 2- [API Documentation ](TBA)
 
 ```
 mvn javadoc:javadoc

 ```

3- Usage Sample
 
Create:
 
 ```
HibernateRepositoryImpl<Person, Integer> repository;

Person baseEntity = new Person("personName");
		repository.create(baseEntity);
 
 ```
 
 Update:
 
 ```
HibernateRepositoryImpl<Person, Integer> repository;

Person baseEntity = new Person("personName");
		repository.update(baseEntity);
 
 ```
 
  FindById:
 
 ```
HibernateRepositoryImpl<Person, Integer> repository;

Person baseEntity = new Person("personName");
		repository.create(baseEntity);
		repository.findById(Person.class, 1);
 
 ```
 
FindAll:
 
 ```
HibernateRepositoryImpl<Person, Integer> repository;

Person baseEntity = new Person("personName");
		repository.create(baseEntity);
		repository.findAll(Person.class);
 
 ```
 
 Delete:
 
 ```
HibernateRepositoryImpl<Person, Integer> repository;

Person baseEntity = new Person("personName");
		repository.create(baseEntity);
		repository.delete(Person.class, 1);
 
 ```
 
 CreateQuery:
 
 ```
 HibernateRepositoryImpl<Person, Integer> repository;
 
		String qlString = "SELECT c FROM Person c WHERE c.name LIKE :personName";
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("personName", "personName");
		Person baseEntity = new Person("personName");
		repository.create(baseEntity);
		repository.createQuerySelect(qlString, parameterMap);
 
 ```
 
  
 CreateNamedQuery:
 
 ```
 HibernateRepositoryImpl<Person, Integer> repository;
 
		String name = "findAllCustomersWithName";
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("personName", "personName");
		Person baseEntity = new Person("personName");
		repository.create(baseEntity);
		repository.createNamedQuerySelect(name, Person.class, parameterMap);
 
 ```








