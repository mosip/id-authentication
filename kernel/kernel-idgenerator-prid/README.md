## Module kernel-idgenerator-prid

[API Documentation](doc/index.html)



** Properties to be added in parent Spring Application environment **

[kernel-datavalidator-dev.properties](../../config/kernel-datavalidator-dev.properties)




** Database Properties **

Schema : ids

Table : prid

** Description **

1.**ADMIN** _can only configure the length_ 

2.**Logic behind generating prid**
  1. _The Token Id should be generated sequentially._
  2. _cannot not have repeated numbers,cannot contain any repeating numbers for 2 or more than 2 digits._
  3. _cannot have repeated block of numbers for 2 or more than 2 digits._ 
  4._cannot contain any sequential number for 3 or more than 3 digits and cannot contain alphanumeric values._
  5._The last digit of the generated token id should have checksum_  
  6._The number should not contain '0' or '1' as the first digit._
  
####Sample####
   
   _usage1_
     
     STEP-1
     
      Referring value of the prid length from the properties files.
      [@Value("${mosip.kernel.prid.length}")
	   private int pridLength;]
	
	  Step-2
	 
	  Instantiating the class which needs to be called for generating the id  
	  [@Autowired
	  private PridGenerator<String> mosipPridGenerator;]
	
	 Step-3
	 
	  [mosipPridGenerator.generateId());]
	  
	 Sample Output
	  [generatedPrid: 58361782748604]
	
   
   








