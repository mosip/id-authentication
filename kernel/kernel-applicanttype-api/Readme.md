## kernel-applicanttype-api


 [Background & Design](../../docs/design/kernel/kernel-applicanttype.md)

This api can be used to get applicant type code.

[Api Documentation]

```
mvn javadoc:javadoc
```

To use this api, add this to dependency list:

```
		<dependency>
			<groupId>io.mosip.kernel</groupId>
			<artifactId>kernel-applicanttype-api</artifactId>
			<version>1.0.0-SNAPSHOT</version>
		</dependency>
```


**The inputs which have to be provided are:**
We need to provide the Map<String,Object> and the key, value pairs are as follows :
individualTypeCode: mandatory
dateofbirth: mandatory
genderCode: mandatory
biometricAvailable: optional

Valid values for above keys are as follows : 
individualTypeCode: FR,NFR
dateofbirth: must be in this pattern yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
genderCode: MLE,FLE
biometricAvailable: true,false


**Exceptions to be handled while using this functionality:**

1. InvalidApplicantArgumentException ("KER-MSD-147", "Invalid query passed for applicant type")
2. InvalidApplicantArgumentException ("KER-MSD-148", "Date string can not be parsed");


**Usage Sample**
  
*Usage:*
 
 ```
@Autowired
	private ApplicantType applicantCodeService;
	
		AuditRequestBuilder auditRequestBuilder = new AuditRequestBuilder();

		@Override
		public ResponseDTO getApplicantType(RequestDTO dto) {
			//dto contains the key and value pairs to create the map 
			String applicantTypeCode = applicantCodeService.getApplicantType(map);
			//set it to ResponseDTO and return the code
		}

 ```













