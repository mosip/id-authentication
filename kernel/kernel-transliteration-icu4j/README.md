## kernel-transliteration-icu4j

[Background & Design]
 

 
[API Documentation]
 
 ```
 mvn javadoc:javadoc

 ```
 
**Properties to be added in Spring application environment using this component**


```
#-----------------------------TRANSLITERATION-Properties---------------------------

mosip.kernel.transliteration.arabic-language-code=ara
mosip.kernel.transliteration.franch-language-code=fra

```

 
 **Maven Dependency**
 
 ```
 	<dependency>
			<groupId>io.mosip.kernel</groupId>
			<artifactId>kernel-transliteration-icutext</artifactId>
			<version>${project.version}</version>
		</dependency>

 ```
 
 

**Usage Sample:**

Autowired interface Transliteration

```
	@Autowired
	private Transliteration<String> transliterateImpl;
	
```

Call the method transliterate

Valid transliteration Example:

```
		String frenchToArabic = transliterateImpl.transliterate("fra","ara", "Bienvenue");
		
		System.out.println("ARABIC="+frenchToArabic);
 
 ```
 
 Output:ARABIC= بِِنڤِنُِ
 
 Invalid transliteration Example:
 
 ```
	transliterateImpl.transliterate("dnjksd", "ara", "Bienvenue");
	
	
 ```
 
 Output: 
 

 InvalidTransliterationException:
 
 Language code not supported








