## kernel-datamapper-orika

This folder has data mapper module which can be used to do the mapping between two different type of objects.
 
 1- [Background & Design](../../design/kernel/kernel-datamapper.md)
 

 2- [API Documentation <TBA>](TBA)
 
 ```
 mvn javadoc:javadoc

 ```
 
**Maven Dependency**

```
	<dependency>
			<groupId>io.mosip.kernel</groupId>
			<artifactId>kernel-datamapper-orika</artifactId>
			<version>${project.version}</version>
	</dependency>

```
 
**Use cases**

Input Parameters:-
1. source - Source Object
2. destination - Destination Object
3. destinationClass - destination class type
4. mapNull - set to true or false to map null values or not
5. includeDataField - list of data fields to be included for mapping
6. excludeDataField - list of data fields to be excluded from mapping 
7. dataConverter - customize converter need to be created
8. applyDefault - true/false to apply default configuration or not (default configuration will map the rest fields too)
 
** Usage1: **
 
###### public <S, D> D map(S source, Class<D> destinationClass, boolean mapNull, List<IncludeDataField> includeDataField, List<String> excludeDataField, boolean applyDefault);

Example1:-

```
		@Autowired
		DataMapper dataMapper;
		SourceModel sourceObject = new SourceModel("Mosip", 10);
		DestinationModel destinationObject = dataMapper.map(sourceObject, DestinationModel.class, true, null, null, true);
```

Example2:-

```
		@Autowired
		DataMapper dataMapper;
		List<String> excludeField = Arrays.asList("nom");
		List<IncludeDataField> includeField = Arrays.asList(new IncludeDataField("surnom", "nickName", true));
		Personne french = new Personne("Claire", "cla", 2);
		Person2 english = dataMapper.map(french, Person2.class, true, includeField, excludeField, true);
```

** Usage2: **

###### public <S, D> void map(S source, D destination, boolean mapNull, List<IncludeDataField> includeDataField, List<String> excludeDataField, boolean applyDefault);
 
Example:-
 
 ```
		@Autowired
		DataMapper dataMapper;
		SourceModel src = new SourceModel(null, 10);
		DestinationModel dest = new DestinationModel("Neha", 25);
		dataMapper.map(src, dest, true, null, null, true);
 ```

** Usage3: **

###### public <S, D> void map(S source, D destination, DataConverter<S, D> dataConverter);

Example for creating customize converter:-

```
		public class PersonListConverter implements DataConverter<List<Person>, List<Personne>> {

		@Override
		public void convert(List<Person> source, List<Personne> destination) {
		source.forEach((p) -> {
			Personne personne = new Personne();
			LocalDate date=p.getDob();
			LocalDate now= LocalDate.now();
			Period period = Period.between(date, now);
			personne.setAge(period.getYears());
         
			destination.add(personne);
				});
			}
		}
```

```
		@Autowired
		DataMapper dataMapper;
		PersonListConverter personListConverter = new PersonListConverter();
		Person person = new Person();
		LocalDate dob = LocalDate.of(1994, Month.JANUARY, 1);
		person.setDob(dob);
		List<Person> personList = new ArrayList<>();
		personList.add(person);
		List<Personne> personneList = new ArrayList<Personne>();
		dataMapper.map(personList, personneList, personListConverter);
```