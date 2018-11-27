## kernel-datavalidator


 1- [Background & Design](../../design/kernel/kernel-datavalidator.md)
 



 2- [API Documentation ]
 
 
 ```
 mvn javadoc:javadoc

 ```
 
 
 **Properties to be added in Spring application environment using this component**

[kernel-datavalidator-dev.properties](../../config/kernel-datavalidator-dev.properties)
 
 
 
 
  **Maven Dependency**
 
 ```
 	<dependency>
			<groupId>io.mosip.kernel</groupId>
			<artifactId>kernel-datavalidator</artifactId>
			<version>${project.version}</version>
		</dependency>

 ```
 


**Phone**

1. Maximum and minimum length of the phone number is as mentioned in the property file

2. Special characters for the phone number is as mentioned in the property file




**Email**

1. Maximum and minimum length of the email id is as mentioned in the property file

2. First character of User-Name must be alphabets(a-zA-Z) and digits(0-9) and special characters are not allowed for first character of the          email id

3. Special characters for the User-Name are reading from the property file

4. Domain name should not contain any numerics that is only alphabets (a-zA-Z)

5. Special character for domain-name part of the email Id is as mentioned in the property file

6. Maximum and minimum length of domain extension of the email id is as mentioned in the property file

7. Domain extension should have only alphabets(a-zA-Z)





**The response will be true is case if it pass the all validation condition otherwise it will throw respective error message**

 
Usage Sample:
 
 Valid Phone Number Example 
 
 ```
	@Autowired
	PhoneValidator phonevalidatorImpl;
	
	boolean isValid = phonevalidatorImpl.validatePhone("+78 789-567"); // returns true
	
 ```
 
 Invalid Phone Number Example 
 
 ```
	@Autowired
	PhoneValidator phonevalidatorImpl;
	
	boolean isValid = phonevalidatorImpl.validatePhone("+78@789-567"); //Throws Exception "Phone number should not contain any special characters except specified characters."
	
```

Valid Email Id Example 
 
 ```
	@Autowired
	EmailValidator emailvalidatorImpl;
	
	boolean isValid = emailvalidatorImpl.validatePhone("mosip@gmail.com"); // returns true
	
 ```
 
Invalid Email Id Example 
 
 ```
	@Autowired
	EmailValidator emailvalidatorImpl;
	
	boolean isValid = emailvalidatorImpl.validatePhone("mosip@@gmail.com"); //Throws Exception "Invalid Email Id"
	
 ```


