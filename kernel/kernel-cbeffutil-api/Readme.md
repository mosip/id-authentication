## kernel-cbeffutil-api
This api can be used to create/update cbeff xml.This api can be used to validate Cbeff and Search Cbeff based on Type and Subtype data.

Api Documentation

1) Create Cbeff : 

   Creating an CBEFF XML with list of BIR’s which can be dynamically built based on the BIR data.
   
   Class  : CbeffI
   
   Method : byte[] createXML(List<BIR> birList)
   
   Params : List<BIR> birList – List of Bio Metric Block can be added dynamically using BIR Builder.
   
   Return Type : XML file as Byte Array
   
   BIR - Biometric Information Records (BIRs)
   
   Please refer the following link for Sample Cbeff XML data : 
   
   https://github.com/mosip/mosip/wiki/MOSIP-Biometric-Data-Specifications
   
   BIR Creation Sample : 
   
   Each BIR can be created using builder with the types and subtypes
   
   BIR rFinger = new BIR.BIRBuilder().withBdb(fingerImg)
				.withBirInfo(new BIRInfo.BIRInfoBuilder().withIntegrity(false).build())
				.withBdbInfo(new BDBInfo.BDBInfoBuilder().withFormatOwner(new Long(257)).withFormatType(new Long(7))
						.withQuality(95).withType(Arrays.asList(SingleType.FINGER))
						.withSubtype(Arrays.asList("Right IndexFinger MiddleFinger RingFinger LittleFinger"))
						.withPurpose(PurposeType.ENROLL).withLevel(ProcessedLevelType.RAW).withCreationDate(new Date())
						.build())
				.build();
				
	
2) Update Cbeff : 

   Updating the existing CBEFF XML with BIR blocks by passing XML as bytes and List of BIR blocks.
   
   Class       : CbeffI
   
   Method      : byte[] updateXML(List<BIR> birList, byte[] fileBytes)
   
   Params      : *) List<BIR> birList – List of Bio Metric Block can be added dynamically using BIR Builder.
                 *) Existing XML File to be updated as Byte Array.
		 
   Return Type : XML file as Byte Array
   
3) Validate XML with XSD : 
	
   Validating the existing XML data with Cbeff XSD.
	
	Class       : CbeffI
	
	Method      : validateXML(byte[] xmlBytes, byte[] xsdBytes)
	
	Params      : Byte Array of XSD and XML to be validated.
	
	Return Type : Boolean
	
4) Search Cbeff based on Type and Subtype :

   Searching an existing Cbeff data based on type and Subtype.
   
   Class       : CbeffI
   
   Method      : Map<String,String> getBDBBasedOnType(byte[] fileBytes,String type,String subType)
   
   Params      : *) Existing XML File to be updated as Byte Array.
                 *) Type as String Example : Finger for Finger based data, FMR for Finger minutiae , Iris etc.
	         *) Sub Type as String Example : Left , Right etc.
   Return Type : Map of Type and Subtype with data as String
   
   Incase of only Searching based on Type only , leave the subtype empty or null and vice versa.
   

