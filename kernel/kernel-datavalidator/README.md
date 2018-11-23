## kernel-datavalidator

 
 This folder has Data Validator module which can be used to validate Phone number and Email Id based on policy.
 

 [API Documentation ]
 
 ```
 mvn javadoc:javadoc

 ```
 
### The inputs which have to be provided are:

**Input Format for Phone**

1. Maximum and minimum length of the phone number is as mentioned in the property file

2. Special characters for the phone number is as mentioned in the property file




**Input Format for Email**

1. Maximum and minimum length of the email id is as mentioned in the property file

2. First character of User-Name must be alphabets(a-zA-Z) and digits(0-9) and special characters are not allowed for first character of the          email id

3. Special characters for the User-Name are reading from the property file

4. Domain name should not contain any numerics that is only alphabets (a-zA-Z)

5. Special character for domain-name part of the email Id is as mentioned in the property file

6. Maximum and minimum length of domain extension of the email id is as mentioned in the property file

7. Domain extension should have only alphabets(a-zA-Z)


Properties to be added in Spring application environment using this component

[kernel-datavalidator-dev.properties](../../config/kernel-datavalidator-dev.properties)

 
Usage Sample
 
 Example for Phone Number
 
 ```
	@Autowired
	PhoneValidatorImpl phonevalidator;
	
	boolean return = phonevalidator.validatePhone("+78 789-567");
	
	System.out.println("Validation Result for the given Phone Number = "+return);
 
 ```

  Example for Email ID
 
 ```
	@Autowired
	EmailValidatorImpl emailvalidator;
	
	boolean return = phonevalidator.validatePhone("mosip@gmail.com");
	
	System.out.println("Validation Result for the given Email Id = "+return);
	
	
 
 ```








