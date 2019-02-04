## kernel-cbeffutil-api
This api can be used to create/update cbeff xml.This api can be used to validate Cbeff and Search Cbeff based on Type and Subtype data.

**Api Documentation**

```
mvn javadoc:javadoc
```

**Maven dependency**

 ```
    <dependency>
		<groupId>cbeffutil</groupId>
		<artifactId>kernel-cbeffutil-api</artifactId>
		<version>${project.version}</version>
	</dependency>
 ```

1) **Create Cbeff:**

   Creating an CBEFF XML with list of BIR’s which can be dynamically built based on the BIR data.
   
   Class  : CbeffI
   
   Method : byte[] createXML(List<BIR> birList)
   
   Params : List<BIR> birList – List of Bio Metric Block can be added dynamically using BIR Builder.
   
   Return Type : XML file as Byte Array
   
   **BIR Details:**
   
   BIR     - Biometric Information Records (BIRs)\
   BIRInfo - Biometric Information Records Information holds the data of Integrity.\
   BDBInfo - Biometric Data Block Information holds the data of Format Owner,Format Type , Quality , Type ,
   			 Sub-type,Purpose,Processing Level and creation date.
     
   **BIR Creation Sample:**
   
   Each BIR can be created using builder with the types and sub-types.
   
   **BIR Creation using Builder**
   
    ```
   BIR finger = new BIR.BIRBuilder().withBdb(fingerImg)
				.withBirInfo(new BIRInfo.BIRInfoBuilder().withIntegrity(false).build())
				.withBdbInfo(new BDBInfo.BDBInfoBuilder().withFormatOwner(new Long(257)).withFormatType(new Long(7))
						.withQuality(95).withType(Arrays.asList(SingleType.FINGER))
						.withSubtype(Arrays.asList("Right IndexFinger MiddleFinger RingFinger LittleFinger"))
						.withPurpose(PurposeType.ENROLL).withLevel(ProcessedLevelType.RAW).withCreationDate(new Date())
						.build())
				.build();
	 ```	
	 
	 **Usage Sample**
	 ```
	 CbeffImpl cbeffImpl = new CbeffImpl();
	 byte[] createXml = cbeffImpl.createXML(createList);
	 
	 ```
	 
	 **Sample XML Generated :**
	 
	 ```
	 <?xml version="1.0" encoding="UTF-8"?>
	<BIR xmlns="http://docs.oasis-open.org/bias/ns/biaspatronformat-1.0/">
    <Version>
        <Major>2</Major>
        <Minor>1</Minor>
    </Version>
    <CBEFFVersion>
        <Major>2</Major>
        <Minor>1</Minor>
    </CBEFFVersion>
    <BIRInfo>
        <Integrity>false</Integrity>
    </BIRInfo>
    <BIR>
	   <!-- face -->
        <BIRInfo>
            <Integrity>false</Integrity>
        </BIRInfo>
        <BDBInfo>
            <FormatOwner>257</FormatOwner>
            <FormatType>8</FormatType>
            <CreationDate>2018-12-18T12:18:35.662+05:30</CreationDate>
            <Type>Face</Type>
            <Subtype></Subtype>
            <Level>Intermediate</Level>
            <Purpose>Enroll</Purpose>
            <Quality>90</Quality>
        </BDBInfo>
        <BDB>RmFjZQ...==</BDB>
    </BIR>
    <BIR>
	   <!-- left slap -->
        <BIRInfo>
            <Integrity>false</Integrity>
        </BIRInfo>
        <BDBInfo>
            <FormatOwner>257</FormatOwner>
            <FormatType>7</FormatType>
            <CreationDate>2018-12-18T12:18:35.667+05:30</CreationDate>
            <Type>Finger</Type>
            <Subtype>Left IndexFinger MiddleFinger RingFinger LittleFinger</Subtype>
            <Level>Raw</Level>
            <Purpose>Enroll</Purpose>
            <Quality>80</Quality>
        </BDBInfo>
        <BDB>UmlnH5...=</BDB>
    </BIR>
    <BIR>
	  <!-- right slap -->
        <BIRInfo>
            <Integrity>false</Integrity>
        </BIRInfo>
        <BDBInfo>
            <FormatOwner>257</FormatOwner>
            <FormatType>7</FormatType>
            <CreationDate>2018-12-18T12:18:35.667+05:30</CreationDate>
            <Type>Finger</Type>
            <Subtype>Right IndexFinger MiddleFinger RingFinger LittleFinger</Subtype>
            <Level>Raw</Level>
            <Purpose>Enroll</Purpose>
            <Quality>80</Quality>
        </BDBInfo>
        <BDB>TGVdCB...=</BDB>
    </BIR>
    <BIR>
	   <!-- two thumbs -->
        <BIRInfo>
            <Integrity>false</Integrity>
        </BIRInfo>
        <BDBInfo>
            <FormatOwner>257</FormatOwner>
            <FormatType>7</FormatType>
            <CreationDate>2018-12-18T12:18:35.667+05:30</CreationDate>
            <Type>Finger</Type>
            <Subtype>Left Right Thumb</Subtype>
            <Level>Raw</Level>
            <Purpose>Enroll</Purpose>
            <Quality>80</Quality>
        </BDBInfo>
        <BDB>GVmdAC...=</BDB>
    </BIR>
    <BIR>
	  <!-- right iris -->
        <BIRInfo>
            <Integrity>false</Integrity>
        </BIRInfo>
        <BDBInfo>
            <FormatOwner>257</FormatOwner>
            <FormatType>9</FormatType>
            <CreationDate>2018-12-18T12:18:35.667+05:30</CreationDate>
            <Type>Iris</Type>
            <Subtype>Right</Subtype>
            <Level>Raw</Level>
            <Purpose>Enroll</Purpose>
            <Quality>80</Quality>
        </BDBInfo>
        <BDB>UmlnaH...=</BDB>
    </BIR>
    <BIR>
	   <!-- left iris -->
        <BIRInfo>
            <Integrity>false</Integrity>
        </BIRInfo>
        <BDBInfo>
            <FormatOwner>257</FormatOwner>
            <FormatType>9</FormatType>
            <CreationDate>2018-12-18T12:18:35.668+05:30</CreationDate>
            <Type>Iris</Type>
            <Subtype>Left</Subtype>
            <Level>Raw</Level>
            <Purpose>Enroll</Purpose>
            <Quality>80</Quality>
        </BDBInfo>
        <BDB>TGVmdS...=</BDB>
    </BIR>
</BIR> 
 ```
	 
	 
2) **Update Cbeff:**

   Updating the existing CBEFF XML with BIR blocks by passing XML as bytes and List of BIR blocks.
   
   Class       : CbeffI
   
   Method      : byte[] updateXML(List<BIR> birList, byte[] fileBytes)
   
   Params      :\
   				*) List<BIR> birList – List of Bio Metric Block can be added dynamically using BIR Builder.\
   				*) Existing XML File to be updated as Byte Array.
		 
   Return Type : XML file as Byte Array
   
    **Usage Sample**
	 ```
	 CbeffImpl cbeffImpl = new CbeffImpl();
	 byte[] updateXml = cbeffImpl.updateXML(updateList, xmlbytes);
	 
	 ```
   
   
   
3) **Validate XML with XSD:**
	
   Validating the existing XML data with Cbeff XSD.
	
	Class       : CbeffI
	
	Method      : validateXML(byte[] xmlBytes, byte[] xsdBytes)
	
	Params      : Byte Array of XSD and XML to be validated.
	
	Return Type : Boolean
	
	**Usage Sample**
	``` 
	CbeffImpl cbeffImpl = new CbeffImpl();
	cbeffImpl.validateXML(xmlbytes, xsdbytes));
	```
	
	**Sample Response**
	true
	
	
4) **Search Cbeff based on Type and Subtype:**

   Searching an existing Cbeff data based on type and Subtype.
   
   Class       : CbeffI
   
   Method      : Map<String,String> getBDBBasedOnType(byte[] fileBytes,String type,String subType)
   
   Params      :\
   				 *) Existing XML File to be updated as Byte Array.\
                 *) Type as String Example : Finger for Finger based data, FMR for Finger minutiae , Iris etc.\
	             *) Sub Type as String Example : Left , Right etc.\
   Return Type : Map of Type and Subtype with data as String
   
   Incase of only Searching based on Type only , leave the subtype empty or null and vice versa.
   
   **Usage Sample**
	``` 
	CbeffImpl cbeffImpl = new CbeffImpl();
	Map<String,String> testMap = cbeffImpl.getBDBBasedOnType(xmlbytes, "FMR", "Right");
	```
	
	**Sample Response**
	``` 
	{RIGHT FINGER=UklHSFQgRklOR0VS}
	```
   

