## kernel-datamapper-orika

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
 


Example1:-

```
   @Bean(name="applicationtoToApplicationDtoDefaultMapper")
	public DataMapper<Application, ApplicationDto> applicationtoToApplicationDtoMapper(){
		return new DataMapperBuilderImpl<>(Application.class, ApplicationDto.class).build();
	}
	
	@Qualifier("applicationtoToApplicationDtoDefaultMapper")
	@Autowired
	private DataMapper<Application,ApplicationDto> applicationtoToApplicationDtoDefaultMapper;
		
		
		
	Application application = new Application("AP001","eng","applicationName","Description");
	ApplicationDTO applicationDTO = applicationtoToApplicationDtoDefaultMapper.map(application);
```

Example2:-

```
   @Bean(name="applicationtoToApplicationDtoMapper")
	public DataMapper<Application, ApplicationDto> applicationtoToApplicationDtoMapper(){
		return new DataMapperBuilderImpl<>(Application.class, ApplicationDto.class).mapNulls(true).byDefault(true).build();
	}
	
	@Qualifier("applicationtoToApplicationDtoMapper")
	@Autowired
	private DataMapper<Application,ApplicationDto> applicationtoToApplicationDtoMapper;
		
		
	Application application = new Application("AP001","eng","applicationName","Description");
	ApplicationDTO applicationDTO = applicationtoToApplicationDtoMapper.map(application);
```

** Usage2: **
 
Example:-
 
 ```
	@Bean(name="applicationtoToApplicationDtoDefaultMapper")
	public DataMapper<Application, ApplicationDto> applicationtoToApplicationDtoMapper(){
		return new DataMapperBuilderImpl<>(Application.class, ApplicationDto.class).build();
	}
	
	@Qualifier("applicationtoToApplicationDtoDefaultMapper")
	@Autowired
	private DataMapper<Application,ApplicationDto> applicationtoToApplicationDtoDefaultMapper;
		
		
		
	Application application = new Application("AP001","eng","applicationName","Description");
	ApplicationDto applicationDTO= new ApplicationDto();
	applicationtoToApplicationDtoDefaultMapper.map(application,applicationDTO);
 ```


** Usage3: **


Example for creating customize converter:-

```
		public class PersonListConverter implements DataConverter<List<Person>, List<PersonDto>> {

		@Override
		public void convert(List<Person> source, List<PersonDto> destination) {
		source.forEach((p) -> {
			PersonDto personne = new PersonDto();
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
		@Bean(name="persontoToPersonDtoMapper")
	public DataMapper<Person, PersonDto> persontoToPersonDtoMapper(){
		return new DataMapperBuilderImpl<>(Person.class, PersonDto.class).build();
	}
	
	@Qualifier("persontoToPersonDtoMapper")
	@Autowired
	private DataMapper<Person,PersonDto> persontoToPersonDtoMapper;
		
	
		
		
	PersonListConverter personListConverter = new PersonListConverter();
	Person person = new Person();
	LocalDate dob = LocalDate.of(1994, Month.JANUARY, 1);
	person.setDob(dob);
	List<Person> personList = new ArrayList<>();
	personList.add(person);
	List<Personne> personneList = new ArrayList<Personne>();
	persontoToPersonDtoMapper.map(personList, personneList, personListConverter);
```