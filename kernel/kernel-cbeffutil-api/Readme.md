## kernel-cbeffutil-api

 [Background & Design](../../docs/design/kernel/kernel-cbeffutil.md)

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
   		RegistryIDType format = new RegistryIDType();
		format.setOrganization("257");
		format.setType("7");
		QualityType Qtype = new QualityType();
		Qtype.setScore(new Long(100));
		RegistryIDType algorithm = new RegistryIDType();
		algorithm.setOrganization("HMAC");
		algorithm.setType("SHA-256");
		Qtype.setAlgorithm(algorithm);
		createList = new ArrayList<>();
		BIR rIndexFinger = new BIR.BIRBuilder().withBdb(rindexFinger)
				.withVersion(new BIRVersion.BIRVersionBuilder().withMinor(1).withMajor(1).build())
				.withCbeffversion(new BIRVersion.BIRVersionBuilder().withMinor(1).withMajor(1).build())
				.withBirInfo(new BIRInfo.BIRInfoBuilder().withIntegrity(false).build())
				.withBdbInfo(new BDBInfo.BDBInfoBuilder().withFormat(format)
						.withQuality(Qtype).withType(Arrays.asList(SingleType.FINGER))
						.withSubtype(Arrays.asList("Right IndexFinger"))
						.withPurpose(PurposeType.ENROLL).withLevel(ProcessedLevelType.RAW)
						.withCreationDate(LocalDateTime.now(ZoneId.of("UTC"))).build())
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
        <BIR xmlns="http://standards.iso.org/iso-iec/19785/-3/ed-2/">
        <BIRInfo>
         <Integrity>false</Integrity>
        </BIRInfo>
        <!-- right index finger -->
        <BIR>
        <Version>
        <Major>1</Major>
        <Minor>1</Minor>
        </Version>
        <CBEFFVersion>
         <Major>1</Major>
         <Minor>1</Minor>
        </CBEFFVersion>
        <BIRInfo>
         <Integrity>false</Integrity>
        </BIRInfo>
        <BDBInfo>
         <Format>
            <Organization>Mosip</Organization>
            <Type>257</Type>
         </Format>
         <CreationDate>2019-06-27T13:40:06.209Z</CreationDate>
         <Type>Finger</Type>
         <Subtype>Right IndexFinger</Subtype>
         <Level>Raw</Level>
         <Purpose>Enroll</Purpose>
         <Quality>
            <Algorithm>
               <Organization>HMAC</Organization>
               <Type>SHA-256</Type>
            </Algorithm>
            <Score>100</Score>
         </Quality>
      </BDBInfo>
      <BDB>RklSAD...</BDB>
     </BIR>
    <!-- right middle finger -->
    <BIR>
      <Version>
         <Major>1</Major>
         <Minor>1</Minor>
      </Version>
      <CBEFFVersion>
         <Major>1</Major>
         <Minor>1</Minor>
      </CBEFFVersion>
      <BIRInfo>
         <Integrity>false</Integrity>
      </BIRInfo>
      <BDBInfo>
         <Format>
            <Organization>Mosip</Organization>
            <Type>257</Type>
         </Format>
         <CreationDate>2019-06-27T13:40:06.211Z</CreationDate>
         <Type>Finger</Type>
         <Subtype>Right MiddleFinger</Subtype>
         <Level>Raw</Level>
         <Purpose>Enroll</Purpose>
         <Quality>
            <Algorithm>
               <Organization>HMAC</Organization>
               <Type>SHA-256</Type>
            </Algorithm>
            <Score>100</Score>
         </Quality>
      </BDBInfo>
      <BDB>RklSAD...</BDB>
    </BIR>
    <!-- right ring finger -->
    <BIR>
      <Version>
         <Major>1</Major>
         <Minor>1</Minor>
      </Version>
      <CBEFFVersion>
         <Major>1</Major>
         <Minor>1</Minor>
      </CBEFFVersion>
      <BIRInfo>
         <Integrity>false</Integrity>
      </BIRInfo>
      <BDBInfo>
         <Format>
            <Organization>Mosip</Organization>
            <Type>257</Type>
         </Format>
         <CreationDate>2019-06-27T13:40:06.211Z</CreationDate>
         <Type>Finger</Type>
         <Subtype>Right RingFinger</Subtype>
         <Level>Raw</Level>
         <Purpose>Enroll</Purpose>
         <Quality>
            <Algorithm>
               <Organization>HMAC</Organization>
               <Type>SHA-256</Type>
            </Algorithm>
            <Score>100</Score>
         </Quality>
      </BDBInfo>
      <BDB>RklSAD...</BDB>
     </BIR>
    <!-- right little finger -->
    <BIR>
      <Version>
         <Major>1</Major>
         <Minor>1</Minor>
      </Version>
      <CBEFFVersion>
         <Major>1</Major>
         <Minor>1</Minor>
      </CBEFFVersion>
      <BIRInfo>
         <Integrity>false</Integrity>
      </BIRInfo>
      <BDBInfo>
         <Format>
            <Organization>Mosip</Organization>
            <Type>257</Type>
         </Format>
         <CreationDate>2019-06-27T13:40:06.211Z</CreationDate>
         <Type>Finger</Type>
         <Subtype>Right LittleFinger</Subtype>
         <Level>Raw</Level>
         <Purpose>Enroll</Purpose>
         <Quality>
            <Algorithm>
               <Organization>HMAC</Organization>
               <Type>SHA-256</Type>
            </Algorithm>
            <Score>100</Score>
         </Quality>
      </BDBInfo>
      <BDB>RklSAD...</BDB>
    </BIR>
    <!-- left index finger -->
    <BIR>
      <Version>
         <Major>1</Major>
         <Minor>1</Minor>
      </Version>
      <CBEFFVersion>
         <Major>1</Major>
         <Minor>1</Minor>
      </CBEFFVersion>
      <BIRInfo>
         <Integrity>false</Integrity>
      </BIRInfo>
      <BDBInfo>
         <Format>
            <Organization>Mosip</Organization>
            <Type>257</Type>
         </Format>
         <CreationDate>2019-06-27T13:40:06.211Z</CreationDate>
         <Type>Finger</Type>
         <Subtype>Left IndexFinger</Subtype>
         <Level>Raw</Level>
         <Purpose>Enroll</Purpose>
         <Quality>
            <Algorithm>
               <Organization>HMAC</Organization>
               <Type>SHA-256</Type>
            </Algorithm>
            <Score>100</Score>
         </Quality>
      </BDBInfo>
      <BDB>RklSAD...</BDB>
    </BIR>
    <!-- left middle finger -->
    <BIR>
      <Version>
         <Major>1</Major>
         <Minor>1</Minor>
      </Version>
      <CBEFFVersion>
         <Major>1</Major>
         <Minor>1</Minor>
      </CBEFFVersion>
      <BIRInfo>
         <Integrity>false</Integrity>
      </BIRInfo>
      <BDBInfo>
         <Format>
            <Organization>Mosip</Organization>
            <Type>257</Type>
         </Format>
         <CreationDate>2019-06-27T13:40:06.211Z</CreationDate>
         <Type>Finger</Type>
         <Subtype>Left MiddleFinger</Subtype>
         <Level>Raw</Level>
         <Purpose>Enroll</Purpose>
         <Quality>
            <Algorithm>
               <Organization>HMAC</Organization>
               <Type>SHA-256</Type>
            </Algorithm>
            <Score>100</Score>
         </Quality>
      </BDBInfo>
      <BDB>RklSAD...</BDB>
    </BIR>
     <!-- left ring finger -->
    <BIR>
      <Version>
         <Major>1</Major>
         <Minor>1</Minor>
      </Version>
      <CBEFFVersion>
         <Major>1</Major>
         <Minor>1</Minor>
      </CBEFFVersion>
      <BIRInfo>
         <Integrity>false</Integrity>
      </BIRInfo>
      <BDBInfo>
         <Format>
            <Organization>Mosip</Organization>
            <Type>257</Type>
         </Format>
         <CreationDate>2019-06-27T13:40:06.211Z</CreationDate>
         <Type>Finger</Type>
         <Subtype>Left RingFinger</Subtype>
         <Level>Raw</Level>
         <Purpose>Enroll</Purpose>
         <Quality>
            <Algorithm>
               <Organization>HMAC</Organization>
               <Type>SHA-256</Type>
            </Algorithm>
            <Score>100</Score>
         </Quality>
      </BDBInfo>
      <BDB>RklSAD...</BDB>
    </BIR>
    <!-- left little finger -->
    <BIR>
      <Version>
         <Major>1</Major>
         <Minor>1</Minor>
      </Version>
      <CBEFFVersion>
         <Major>1</Major>
         <Minor>1</Minor>
      </CBEFFVersion>
      <BIRInfo>
         <Integrity>false</Integrity>
      </BIRInfo>
      <BDBInfo>
         <Format>
            <Organization>Mosip</Organization>
            <Type>257</Type>
         </Format>
         <CreationDate>2019-06-27T13:40:06.211Z</CreationDate>
         <Type>Finger</Type>
         <Subtype>Left LittleFinger</Subtype>
         <Level>Raw</Level>
         <Purpose>Enroll</Purpose>
         <Quality>
            <Algorithm>
               <Organization>HMAC</Organization>
               <Type>SHA-256</Type>
            </Algorithm>
            <Score>100</Score>
         </Quality>
      </BDBInfo>
      <BDB>RklSAD...</BDB>
    </BIR>
    <!-- right thumb finger -->
    <BIR>
      <Version>
         <Major>1</Major>
         <Minor>1</Minor>
      </Version>
      <CBEFFVersion>
         <Major>1</Major>
         <Minor>1</Minor>
      </CBEFFVersion>
      <BIRInfo>
         <Integrity>false</Integrity>
      </BIRInfo>
      <BDBInfo>
         <Format>
            <Organization>Mosip</Organization>
            <Type>257</Type>
         </Format>
         <CreationDate>2019-06-27T13:40:06.211Z</CreationDate>
         <Type>Finger</Type>
         <Subtype>Right Thumb</Subtype>
         <Level>Raw</Level>
         <Purpose>Enroll</Purpose>
         <Quality>
            <Algorithm>
               <Organization>HMAC</Organization>
               <Type>SHA-256</Type>
            </Algorithm>
            <Score>100</Score>
         </Quality>
      </BDBInfo>
      <BDB>RklSAD...</BDB>
     </BIR>
    <!-- left thumb finger -->
    <BIR>
      <Version>
         <Major>1</Major>
         <Minor>1</Minor>
      </Version>
      <CBEFFVersion>
         <Major>1</Major>
         <Minor>1</Minor>
      </CBEFFVersion>
      <BIRInfo>
         <Integrity>false</Integrity>
      </BIRInfo>
      <BDBInfo>
         <Format>
            <Organization>Mosip</Organization>
            <Type>257</Type>
         </Format>
         <CreationDate>2019-06-27T13:40:06.211Z</CreationDate>
         <Type>Finger</Type>
         <Subtype>Left Thumb</Subtype>
         <Level>Raw</Level>
         <Purpose>Enroll</Purpose>
         <Quality>
            <Algorithm>
               <Organization>HMAC</Organization>
               <Type>SHA-256</Type>
            </Algorithm>
            <Score>100</Score>
         </Quality>
      </BDBInfo>
      <BDB>RklSAD...</BDB>
    </BIR>
    <!-- face -->
    <BIR>
      <Version>
         <Major>1</Major>
         <Minor>1</Minor>
      </Version>
      <CBEFFVersion>
         <Major>1</Major>
         <Minor>1</Minor>
      </CBEFFVersion>
      <BIRInfo>
         <Integrity>false</Integrity>
      </BIRInfo>
      <BDBInfo>
         <Format>
            <Organization>Mosip</Organization>
            <Type>257</Type>
         </Format>
         <CreationDate>2019-06-27T13:40:06.211Z</CreationDate>
         <Type>Face</Type>
         <Level>Raw</Level>
         <Purpose>Enroll</Purpose>
         <Quality>
            <Algorithm>
               <Organization>HMAC</Organization>
               <Type>SHA-256</Type>
            </Algorithm>
            <Score>100</Score>
         </Quality>
      </BDBInfo>
      <BDB>RklSAD...</BDB>
    </BIR>
    <!-- right iris -->
    <BIR>
      <Version>
         <Major>1</Major>
         <Minor>1</Minor>
      </Version>
      <CBEFFVersion>
         <Major>1</Major>
         <Minor>1</Minor>
      </CBEFFVersion>
      <BIRInfo>
         <Integrity>false</Integrity>
      </BIRInfo>
      <BDBInfo>
         <Format>
            <Organization>Mosip</Organization>
            <Type>257</Type>
         </Format>
         <CreationDate>2019-06-27T13:40:06.211Z</CreationDate>
         <Type>Iris</Type>
         <Subtype>Right</Subtype>
         <Level>Raw</Level>
         <Purpose>Enroll</Purpose>
         <Quality>
            <Algorithm>
               <Organization>HMAC</Organization>
               <Type>SHA-256</Type>
            </Algorithm>
            <Score>100</Score>
         </Quality>
      </BDBInfo>
      <BDB>RklSAD...</BDB>
    </BIR>
    <!-- left iris -->
    <BIR>
      <Version>
         <Major>1</Major>
         <Minor>1</Minor>
      </Version>
      <CBEFFVersion>
         <Major>1</Major>
         <Minor>1</Minor>
      </CBEFFVersion>
      <BIRInfo>
         <Integrity>false</Integrity>
      </BIRInfo>
      <BDBInfo>
         <Format>
            <Organization>Mosip</Organization>
            <Type>257</Type>
         </Format>
         <CreationDate>2019-06-27T13:40:06.211Z</CreationDate>
         <Type>Iris</Type>
         <Subtype>Left</Subtype>
         <Level>Raw</Level>
         <Purpose>Enroll</Purpose>
         <Quality>
            <Algorithm>
               <Organization>HMAC</Organization>
               <Type>SHA-256</Type>
            </Algorithm>
            <Score>100</Score>
         </Quality>
      </BDBInfo>
      <BDB>RklSAD...</BDB>
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
   

