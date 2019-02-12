package io.mosip.registration.test.integrationtest;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dto.mastersync.BlacklistedWordsDto;
import io.mosip.registration.dto.mastersync.DocumentCategoryDto;
import io.mosip.registration.dto.mastersync.GenderDto;
import io.mosip.registration.dto.mastersync.LocationDto;
import io.mosip.registration.dto.mastersync.MasterReasonListDto;
import io.mosip.registration.service.MasterSyncService;

public class MasterSyncServiceTest extends BaseIntegrationTest{
	

	@Autowired
	private MasterSyncService mastersyncservice;
	
	TestDataParseJSON testdataparsejson=new TestDataParseJSON("src/test/resources/testData/MasterSyncServiceData/testData.json");
	
	
	//check if responsedto is not null
	//valid data=get from synch_control-->"MDS_J00001" should return success or failure
	//invalid data=any data returnes null
	/*@Test
	public void masterSync1()
	{
		
		ResponseDTO result = mastersyncservice.getMasterSync("MDS_J00001");
		System.out.println("**************"+result.getErrorResponseDTOs());
}*/
	
	//check if responsedto is not null
		//valid data=get from location table
		//invalid data=any data returnes null
	@Test
	public void masterSync_verify_findLocationByHierarchyCode_getCode()
	{
		//This test verifies if correct code is fetched from local database for given inputs
		//inputs passed are hierarchyCode and langCode from table called location
		//Expected output is RSK
		//mastersyncservice.findLocationByHierarchyCode(hierarchyCode, langCode);
		List<LocationDto> result = mastersyncservice.findLocationByHierarchyCode(testdataparsejson.getDataFromJsonViaKey("hierarchyCode"),testdataparsejson.getDataFromJsonViaKey("langCode"));
		for(int i=0;i<result.size();i++) 
		{
			System.out.println(result.get(i).getCode());
			assertEquals("RSK", result.get(i).getCode());
		}

	}
	
	@Test
	public void masterSync_verify_findLocationByHierarchyCode_getHierarchyName()
	{
		//This test verifies if correct heirarchy name is fetched from local database for given inputs
				//inputs passed are hierarchyCode and langCode from table called location
				//Expected output is Region
		//mastersyncservice.findLocationByHierarchyCode(hierarchyCode, langCode);
		List<LocationDto> result = mastersyncservice.findLocationByHierarchyCode(testdataparsejson.getDataFromJsonViaKey("hierarchyCode"),testdataparsejson.getDataFromJsonViaKey("langCode"));
		for(int i=0;i<result.size();i++) 
		{
			System.out.println(result.get(i).getHierarchyName());
			assertEquals("Region", result.get(i).getHierarchyName());
		}

	}
	
	@Test
	public void masterSync_verify_findLocationByHierarchyCode_getLangCode()
	{
		//This test verifies if correct langcode is fetched from local database for given inputs
				//inputs passed are hierarchyCode and langCode from table called location
				//Expected output is eng
		//mastersyncservice.findLocationByHierarchyCode(hierarchyCode, langCode);
		List<LocationDto> result = mastersyncservice.findLocationByHierarchyCode(testdataparsejson.getDataFromJsonViaKey("hierarchyCode"),testdataparsejson.getDataFromJsonViaKey("langCode"));
		for(int i=0;i<result.size();i++) 
		{
			System.out.println(result.get(i).getLangCode());
			assertEquals("eng", result.get(i).getLangCode());
		}

	}
	
	@Test
	public void masterSync_verify_findLocationByHierarchyCode_getName()
	{
		//This test verifies if correct name is fetched from local database for given inputs
		//inputs passed are hierarchyCode and langCode from table called location
		//Expected output is eng
		//mastersyncservice.findLocationByHierarchyCode(hierarchyCode, langCode);
		List<LocationDto> result = mastersyncservice.findLocationByHierarchyCode(testdataparsejson.getDataFromJsonViaKey("hierarchyCode"),testdataparsejson.getDataFromJsonViaKey("langCode"));
		for(int i=0;i<result.size();i++) 
		{
			System.out.println(result.get(i).getName());
			assertEquals("Rabat Sale Kenitra", result.get(i).getName());
		}

	}
	
	@Test
	public void masterSync_verify_findLocationByHierarchyCode_WithInvalidhierarchyCode()
	{
		//mastersyncservice.findLocationByHierarchyCode(hierarchyCode, langCode);
		List<LocationDto> result = mastersyncservice.findLocationByHierarchyCode(testdataparsejson.getDataFromJsonViaKey("invalidhierarchyCode"),testdataparsejson.getDataFromJsonViaKey("langCode"));
		System.out.println("*********"+result);
		System.out.println("*********"+result.size());
		assertEquals(0, result.size());

	}
	
	@Test
	public void masterSync_verify_findLocationByHierarchyCode_WithInvalidlangCode()
	{
		//mastersyncservice.findLocationByHierarchyCode(hierarchyCode, langCode);
		List<LocationDto> result = mastersyncservice.findLocationByHierarchyCode(testdataparsejson.getDataFromJsonViaKey("hierarchyCode"),testdataparsejson.getDataFromJsonViaKey("invalidlangCode"));
		System.out.println("*********"+result);
		System.out.println("*********"+result.size());
		assertEquals(0, result.size());

	}
	
	
	@Test
	public void masterSync_verify_findLocationByHierarchyCode_WithhierarchyCodeNull()
	{
		//mastersyncservice.findLocationByHierarchyCode(hierarchyCode, langCode);
		List<LocationDto> result = mastersyncservice.findLocationByHierarchyCode(null, testdataparsejson.getDataFromJsonViaKey("langCode"));
		System.out.println("*********"+result);
		System.out.println("*********"+result.size());
		assertEquals(0, result.size());

	}
	
	
	@Test
	public void masterSync_verify_findLocationByHierarchyCode_WithlangCodeNull()
	{
		//mastersyncservice.findLocationByHierarchyCode(hierarchyCode, langCode);
		List<LocationDto> result = mastersyncservice.findLocationByHierarchyCode(testdataparsejson.getDataFromJsonViaKey("hierarchyCode"), null);
		System.out.println("*********"+result);
		System.out.println("*********"+result.size());
		assertEquals(0, result.size());

	}
	
	
	@Test
	public void masterSync_verify_findLocationByHierarchyCode_WithhierarchyCodeNull_WithlangCodeNull()
	{
		//mastersyncservice.findLocationByHierarchyCode(hierarchyCode, langCode);
		List<LocationDto> result = mastersyncservice.findLocationByHierarchyCode(null, null);
		System.out.println("*********"+result);
		System.out.println("*********"+result.size());
		assertEquals(0, result.size());

	}
	
	
	//check if responsedto is not null
			//valid data=get from location
			//invalid data=any data returnes null
	@Test
	public void masterSync_verify_findProvianceByHierarchyCode_getCode()
	{
		
		//mastersyncservice.findProvianceByHierarchyCode(code, langCode);
		
		List<LocationDto> result = mastersyncservice.findProvianceByHierarchyCode(testdataparsejson.getDataFromJsonViaKey("code"),testdataparsejson.getDataFromJsonViaKey("langCode"));
		
		
		List<String> list1=new ArrayList<>();
		list1.add("KTA");
		list1.add("RBT");
		
		List<String> list2=new ArrayList<>();
		
		for(int i=0;i<result.size();i++)
		{
			System.out.println(result.get(i).getCode());
			list2.add(result.get(i).getCode());
		}
		System.out.println("********"+list1);
		System.out.println("********"+list2);
		assertEquals(list1, list2);

}
	
	@Test
	public void masterSync_verifyfindProvianceByHierarchyCode_getHierarchyName()
	{
		List<LocationDto> result = mastersyncservice.findProvianceByHierarchyCode(testdataparsejson.getDataFromJsonViaKey("code"),testdataparsejson.getDataFromJsonViaKey("langCode"));
		
		
		List<String> list1=new ArrayList<>();
		list1.add("Province");
		list1.add("Province");
		
		List<String> list2=new ArrayList<>();
		
		for(int i=0;i<result.size();i++)
		{
			System.out.println(result.get(i).getHierarchyName());
			list2.add(result.get(i).getHierarchyName());
		}
		System.out.println("********"+list1);
		System.out.println("********"+list2);
		assertEquals(list1, list2);
	}
	
	
	@Test
	public void masterSync_verifyfindProvianceByHierarchyCode_getName()
	{
		List<LocationDto> result = mastersyncservice.findProvianceByHierarchyCode(testdataparsejson.getDataFromJsonViaKey("code"),testdataparsejson.getDataFromJsonViaKey("langCode"));
		
		
		List<String> list1=new ArrayList<>();
		list1.add("Kenitra");
		list1.add("Rabat");
		
		List<String> list2=new ArrayList<>();
		
		for(int i=0;i<result.size();i++)
		{
			System.out.println(result.get(i).getName());
			list2.add(result.get(i).getName());
		}
		System.out.println("********"+list1);
		System.out.println("********"+list2);
		assertEquals(list1, list2);
	}
	
	
	@Test
	public void masterSync_verifyfindProvianceByHierarchyCode_getLangCode()
	{
		List<LocationDto> result = mastersyncservice.findProvianceByHierarchyCode(testdataparsejson.getDataFromJsonViaKey("code"),testdataparsejson.getDataFromJsonViaKey("langCode"));
		
		
		List<String> list1=new ArrayList<>();
		list1.add("eng");
		list1.add("eng");
		
		List<String> list2=new ArrayList<>();
		
		for(int i=0;i<result.size();i++)
		{
			System.out.println(result.get(i).getLangCode());
			list2.add(result.get(i).getLangCode());
		}
		System.out.println("********"+list1);
		System.out.println("********"+list2);
		assertEquals(list1, list2);
	}
	
	
	@Test
	public void masterSync_verify_findProvianceByHierarchyCode_WithInvalidCode()
	{
		//mastersyncservice.findProvianceByHierarchyCode(code, langCode);
		List<LocationDto> result = mastersyncservice.findProvianceByHierarchyCode(testdataparsejson.getDataFromJsonViaKey("invalidcode"),testdataparsejson.getDataFromJsonViaKey("langCode"));
		System.out.println("*********"+result);
		System.out.println("*********"+result.size());
		assertEquals(0, result.size());
	}
	
	
	@Test
	public void masterSync_verify_findProvianceByHierarchyCode_WithInvalidlangCode()
	{
		//mastersyncservice.findProvianceByHierarchyCode(code, langCode);
		List<LocationDto> result = mastersyncservice.findProvianceByHierarchyCode(testdataparsejson.getDataFromJsonViaKey("code"),testdataparsejson.getDataFromJsonViaKey("invalidlangCode"));
		System.out.println("*********"+result);
		System.out.println("*********"+result.size());
		assertEquals(0, result.size());
	}
	
	
	@Test
	public void masterSync_verify_findProvianceByHierarchyCode_WithCodeNull()
	{
		//mastersyncservice.findProvianceByHierarchyCode(code, langCode);
		List<LocationDto> result = mastersyncservice.findProvianceByHierarchyCode(null, testdataparsejson.getDataFromJsonViaKey("langCode"));
		System.out.println("*********"+result);
		System.out.println("*********"+result.size());
		assertEquals(0, result.size());
	}
	
	
	@Test
	public void masterSync_verify_findProvianceByHierarchyCode_WithlangCodeNull()
	{
		//mastersyncservice.findProvianceByHierarchyCode(code, langCode);
		List<LocationDto> result = mastersyncservice.findProvianceByHierarchyCode(testdataparsejson.getDataFromJsonViaKey("code"), null);
		System.out.println("*********"+result);
		System.out.println("*********"+result.size());
		assertEquals(0, result.size());
	}
	
	
	@Test
	public void masterSync_verify_findProvianceByHierarchyCode_WithCodeNull_WithlangCodeNull()
	{
		//mastersyncservice.findProvianceByHierarchyCode(code, langCode);
		List<LocationDto> result = mastersyncservice.findProvianceByHierarchyCode(null, null);
		System.out.println("*********"+result);
		System.out.println("*********"+result.size());
		assertEquals(0, result.size());
	}
	
	
			//valid data=get from reason_list
			 //returns code,name,rsncat_code,lang_code
			//invalid data=any data returnes null
	@Test
	public void masterSync_verifygetAllReasonsList_getCode()
	{
		//mastersyncservice.getAllReasonsList(langCode);
		List<MasterReasonListDto> result = mastersyncservice.getAllReasonsList(testdataparsejson.getDataFromJsonViaKey("langCode"));
		List<String> list1=new ArrayList<>();
		list1.addAll(Arrays.asList("APM", "GPM", "IAD", "DPG", "OTH", "OPM", "SDM"));
		
		List<String> list2=new ArrayList<>();
		
		for (int i=0;i<result.size();i++)
		{
			list2.add(result.get(i).getCode());
		}
		System.out.println("********"+list1);
		System.out.println("********"+list2);
		assertEquals(list1, list2);
}
	
	
	@Test
	public void masterSync_verifygetAllReasonsList_getName()
	{
		List<MasterReasonListDto> result = mastersyncservice.getAllReasonsList(testdataparsejson.getDataFromJsonViaKey("langCode"));
		List<String> list1=new ArrayList<>();
		list1.addAll(Arrays.asList("Age-Photo Mismatch", "Gender-Photo Mismatch", "Invalid Address", "Duplicate Registration", "Others", "Only the Photograph is Matching", "Some of the Demographic Details are Matching"));
		
		List<String> list2=new ArrayList<>();
		
		for (int i=0;i<result.size();i++)
		{
			list2.add(result.get(i).getName());
		}
		System.out.println("********"+list1);
		System.out.println("********"+list2);
		assertEquals(list1, list2);
}
	
	
	@Test
	public void masterSync_verifygetAllReasonsList_getRsnCatCode()
	{
		List<MasterReasonListDto> result = mastersyncservice.getAllReasonsList(testdataparsejson.getDataFromJsonViaKey("langCode"));
		List<String> list1=new ArrayList<>();
		list1.addAll(Arrays.asList("CLR", "CLR", "CLR", "CLR", "CLR", "MNA", "MNA"));
		
		List<String> list2=new ArrayList<>();
		
		for (int i=0;i<result.size();i++)
		{
			list2.add(result.get(i).getRsnCatCode());
		}
		System.out.println("********"+list1);
		System.out.println("********"+list2);
		assertEquals(list1, list2);
}
	
	
	@Test
	public void masterSync_verifygetAllReasonsList_getLangCode()
	{
		List<MasterReasonListDto> result = mastersyncservice.getAllReasonsList(testdataparsejson.getDataFromJsonViaKey("langCode"));
		List<String> list1=new ArrayList<>();
		list1.addAll(Arrays.asList("eng", "eng", "eng", "eng", "eng", "eng", "eng"));
		
		List<String> list2=new ArrayList<>();
		
		for (int i=0;i<result.size();i++)
		{
			list2.add(result.get(i).getLangCode());
		}
		System.out.println("********"+list1);
		System.out.println("********"+list2);
		assertEquals(list1, list2);
}
	
	
	@Test
	public void masterSync_verify_getAllReasonsList_WithInvalidlangCode()
	{
		//mastersyncservice.getAllReasonsList(langCode);
	
		List<MasterReasonListDto> result = mastersyncservice.getAllReasonsList(testdataparsejson.getDataFromJsonViaKey("invalidlangCode"));
		System.out.println("*********"+result);
		System.out.println("*********"+result.size());
		assertEquals(0, result.size());
	}
	
	
	@Test
	public void masterSync_verify_getAllReasonsList_WithlangCodeNull()
	{
		//mastersyncservice.getAllReasonsList(langCode);
	
		List<MasterReasonListDto> result = mastersyncservice.getAllReasonsList(null);
		System.out.println("*********"+result);
		System.out.println("*********"+result.size());
		assertEquals(0, result.size());
	}

//valid data=get from blacklisted_words
			 //returns word.description and language code
			//invalid data=any data returnes null
	
	@Test
	public void masterSync_verifygetAllBlackListedWords_getDescription()
	{
		//mastersyncservice.getAllBlackListedWords(langCode);
		
		List<BlacklistedWordsDto> result = mastersyncservice.getAllBlackListedWords(testdataparsejson.getDataFromJsonViaKey("langCode"));

		List<String> list1=new ArrayList<>();
		list1.addAll(Arrays.asList("asdf1", "asdf2", "asdf3", "Blacklisted Word", "Blacklisted Word", "Blacklisted Word", "Blacklisted Word","*%^%^^%&*^^%&^%&*!&%*%&*&&^&&^^","stri","string","string","string"));
		
		List<String> list2=new ArrayList<>();
		
		for (int i=0;i<result.size();i++)
		{
			list2.add(result.get(i).getDescription());
		}
		System.out.println("********"+list1);
		System.out.println("********"+list2);
		assertEquals(list1, list2);
}
	
	
	@Test
	public void masterSync_verifygetAllBlackListedWords_getLangCode()
	{
		List<BlacklistedWordsDto> result = mastersyncservice.getAllBlackListedWords(testdataparsejson.getDataFromJsonViaKey("langCode"));

		List<String> list1=new ArrayList<>();
		list1.addAll(Arrays.asList("eng", "eng", "eng", "eng", "eng", "eng", "eng","eng","eng","eng","eng","eng"));
		
		List<String> list2=new ArrayList<>();
		
		for (int i=0;i<result.size();i++)
		{
			list2.add(result.get(i).getLangCode());
		}
		System.out.println("********"+list1);
		System.out.println("********"+list2);
		assertEquals(list1, list2);
}
	
	
	@Test
	public void masterSync_verifygetAllBlackListedWords_getWord()
	{
		List<BlacklistedWordsDto> result = mastersyncservice.getAllBlackListedWords(testdataparsejson.getDataFromJsonViaKey("langCode"));

		List<String> list1=new ArrayList<>();
		list1.addAll(Arrays.asList("1", "2", "3", "shit", "damn", "nigga", "dammit","elephant","dra","mou","cat","*shjs"));
		
		List<String> list2=new ArrayList<>();
		
		for (int i=0;i<result.size();i++)
		{
			list2.add(result.get(i).getWord());
		}
		System.out.println("********"+list1);
		System.out.println("********"+list2);
		assertEquals(list1, list2);
}
	
	
	@Test
	public void masterSync_verify_getAllBlackListedWords_WithInvalidlangCode()
	{
		//mastersyncservice.getAllBlackListedWords(langCode);
		List<BlacklistedWordsDto> result = mastersyncservice.getAllBlackListedWords(testdataparsejson.getDataFromJsonViaKey("invalidlangCode"));
		System.out.println("*********"+result);
		System.out.println("*********"+result.size());
		assertEquals(0, result.size());
	}
	
	
	@Test
	public void masterSync_verify_getAllBlackListedWords_WithlangCodeNull()
	{
		//mastersyncservice.getAllBlackListedWords(langCode);
		List<BlacklistedWordsDto> result = mastersyncservice.getAllBlackListedWords(null);
		System.out.println("*********"+result);
		System.out.println("*********"+result.size());
		assertEquals(0, result.size());
	}

//valid data=POA,POI,POR,POB; langcode=eng
			 //returns list of documents-->name,descrition and language code from doc_type table
			//invalid data=any data returnes null
	
	@Test
	public void masterSync_verifygetDocumentCategories_getDescription_POA()
	{
		//mastersyncservice.getDocumentCategories(docCode, langCode);
		List<DocumentCategoryDto> result = mastersyncservice.getDocumentCategories(RegistrationConstants.POA_DOCUMENT, testdataparsejson.getDataFromJsonViaKey("langCode"));
		
		List<String> list1=new ArrayList<>();
		list1.addAll(Arrays.asList("Rental Agreement of address", "Proof of Resident"));
		
		List<String> list2=new ArrayList<>();
		
		for(int i=0; i<result.size(); i++)
		{
			list2.add(result.get(i).getDescription());
		}
		
		System.out.println("********"+list1);
		System.out.println("********"+list2);
		assertEquals(list1, list2);
}
	
	
	@Test
	public void masterSync_verifygetDocumentCategories_getDescription_POI()
	{
		//mastersyncservice.getDocumentCategories(docCode, langCode);
		List<DocumentCategoryDto> result = mastersyncservice.getDocumentCategories(RegistrationConstants.POI_DOCUMENT, testdataparsejson.getDataFromJsonViaKey("langCode"));
		
		List<String> list1=new ArrayList<>();
		list1.addAll(Arrays.asList("Moroccan National Electronic ID Card", "Proof of Idendity"));
		
		List<String> list2=new ArrayList<>();
		
		for(int i=0; i<result.size(); i++)
		{
			list2.add(result.get(i).getDescription());
		}
		
		System.out.println("********"+list1);
		System.out.println("********"+list2);
		assertEquals(list1, list2);
}
	
	
	@Test
	public void masterSync_verifygetDocumentCategories_getDescription_POR()
	{
		//mastersyncservice.getDocumentCategories(docCode, langCode);
		List<DocumentCategoryDto> result = mastersyncservice.getDocumentCategories(RegistrationConstants.POR_DOCUMENT, testdataparsejson.getDataFromJsonViaKey("langCode"));
		
		List<String> list1=new ArrayList<>();
		list1.addAll(Arrays.asList("Proof relationship of a person"));
		
		List<String> list2=new ArrayList<>();
		
		for(int i=0; i<result.size(); i++)
		{
			list2.add(result.get(i).getDescription());
		}
		
		System.out.println("********"+list1);
		System.out.println("********"+list2);
		assertEquals(list1, list2);
}
	
	
	@Test
	public void masterSync_verifygetDocumentCategories_getDescription_POB()
	{
		//mastersyncservice.getDocumentCategories(docCode, langCode);
		List<DocumentCategoryDto> result = mastersyncservice.getDocumentCategories(RegistrationConstants.DOB_DOCUMENT, testdataparsejson.getDataFromJsonViaKey("langCode"));
		
		List<String> list1=new ArrayList<>();
		list1.addAll(Arrays.asList("Proof birth and age of a person"));
		
		List<String> list2=new ArrayList<>();
		
		for(int i=0; i<result.size(); i++)
		{
			list2.add(result.get(i).getDescription());
		}
		
		System.out.println("********"+list1);
		System.out.println("********"+list2);
		assertEquals(list1, list2);
}
	
	
	@Test
	public void masterSync_verifygetDocumentCategories_getLangCode_POA()
	{
		//mastersyncservice.getDocumentCategories(docCode, langCode);
		List<DocumentCategoryDto> result = mastersyncservice.getDocumentCategories(RegistrationConstants.POA_DOCUMENT, testdataparsejson.getDataFromJsonViaKey("langCode"));
		
		List<String> list1=new ArrayList<>();
		list1.addAll(Arrays.asList("eng","eng"));
		
		List<String> list2=new ArrayList<>();
		
		for(int i=0; i<result.size(); i++)
		{
			list2.add(result.get(i).getLangCode());
		}
		
		System.out.println("********"+list1);
		System.out.println("********"+list2);
		assertEquals(list1, list2);
}
	
	
	
	@Test
	public void masterSync_verifygetDocumentCategories_getLangCode_POI()
	{
		//mastersyncservice.getDocumentCategories(docCode, langCode);
		List<DocumentCategoryDto> result = mastersyncservice.getDocumentCategories(RegistrationConstants.POI_DOCUMENT, testdataparsejson.getDataFromJsonViaKey("langCode"));
		
		List<String> list1=new ArrayList<>();
		list1.addAll(Arrays.asList("eng","eng"));
		
		List<String> list2=new ArrayList<>();
		
		for(int i=0; i<result.size(); i++)
		{
			list2.add(result.get(i).getLangCode());
		}
		
		System.out.println("********"+list1);
		System.out.println("********"+list2);
		assertEquals(list1, list2);
}
	
	
	@Test
	public void masterSync_verifygetDocumentCategories_getLangCode_POR()
	{
		//mastersyncservice.getDocumentCategories(docCode, langCode);
		List<DocumentCategoryDto> result = mastersyncservice.getDocumentCategories(RegistrationConstants.POR_DOCUMENT, testdataparsejson.getDataFromJsonViaKey("langCode"));
		
		List<String> list1=new ArrayList<>();
		list1.addAll(Arrays.asList("eng"));
		
		List<String> list2=new ArrayList<>();
		
		for(int i=0; i<result.size(); i++)
		{
			list2.add(result.get(i).getLangCode());
		}
		
		System.out.println("********"+list1);
		System.out.println("********"+list2);
		assertEquals(list1, list2);
}
	
	
	@Test
	public void masterSync_verifygetDocumentCategories_getLangCode_POB()
	{
		//mastersyncservice.getDocumentCategories(docCode, langCode);
		List<DocumentCategoryDto> result = mastersyncservice.getDocumentCategories(RegistrationConstants.DOB_DOCUMENT, testdataparsejson.getDataFromJsonViaKey("langCode"));
		
		List<String> list1=new ArrayList<>();
		list1.addAll(Arrays.asList("eng"));
		
		List<String> list2=new ArrayList<>();
		
		for(int i=0; i<result.size(); i++)
		{
			list2.add(result.get(i).getLangCode());
		}
		
		System.out.println("********"+list1);
		System.out.println("********"+list2);
		assertEquals(list1, list2);
}
	
	
	@Test
	public void masterSync_verifygetDocumentCategories_getName_POA()
	{
		//mastersyncservice.getDocumentCategories(docCode, langCode);
		List<DocumentCategoryDto> result = mastersyncservice.getDocumentCategories(RegistrationConstants.POA_DOCUMENT, testdataparsejson.getDataFromJsonViaKey("langCode"));
		
		List<String> list1=new ArrayList<>();
		list1.addAll(Arrays.asList("Rental contract","Certificate of residence"));
		
		List<String> list2=new ArrayList<>();
		
		for(int i=0; i<result.size(); i++)
		{
			list2.add(result.get(i).getName());
		}
		
		System.out.println("********"+list1);
		System.out.println("********"+list2);
		assertEquals(list1, list2);
}
	
	
	@Test
	public void masterSync_verifygetDocumentCategories_getName_POI()
	{
		//mastersyncservice.getDocumentCategories(docCode, langCode);
		List<DocumentCategoryDto> result = mastersyncservice.getDocumentCategories(RegistrationConstants.POI_DOCUMENT, testdataparsejson.getDataFromJsonViaKey("langCode"));
		
		List<String> list1=new ArrayList<>();
		list1.addAll(Arrays.asList("CNIE card","Passport"));
		
		List<String> list2=new ArrayList<>();
		
		for(int i=0; i<result.size(); i++)
		{
			list2.add(result.get(i).getName());
		}
		
		System.out.println("********"+list1);
		System.out.println("********"+list2);
		assertEquals(list1, list2);
}
	
	
	@Test
	public void masterSync_verifygetDocumentCategories_getName_POR()
	{
		//mastersyncservice.getDocumentCategories(docCode, langCode);
		List<DocumentCategoryDto> result = mastersyncservice.getDocumentCategories(RegistrationConstants.POR_DOCUMENT, testdataparsejson.getDataFromJsonViaKey("langCode"));
		
		List<String> list1=new ArrayList<>();
		list1.addAll(Arrays.asList("Certificate of Relationship"));
		
		List<String> list2=new ArrayList<>();
		
		for(int i=0; i<result.size(); i++)
		{
			list2.add(result.get(i).getName());
		}
		
		System.out.println("********"+list1);
		System.out.println("********"+list2);
		assertEquals(list1, list2);
}
	
	
	@Test
	public void masterSync_verifygetDocumentCategories_getName_POB()
	{
		//mastersyncservice.getDocumentCategories(docCode, langCode);
		List<DocumentCategoryDto> result = mastersyncservice.getDocumentCategories(RegistrationConstants.DOB_DOCUMENT, testdataparsejson.getDataFromJsonViaKey("langCode"));
		
		List<String> list1=new ArrayList<>();
		list1.addAll(Arrays.asList("Certificate of Birth"));
		
		List<String> list2=new ArrayList<>();
		
		for(int i=0; i<result.size(); i++)
		{
			list2.add(result.get(i).getName());
		}
		
		System.out.println("********"+list1);
		System.out.println("********"+list2);
		assertEquals(list1, list2);
}
	
	
	@Test
	public void masterSync_verify_getDocumentCategories_WithInvaliddocCode()
	{
		//mastersyncservice.getDocumentCategories(docCode, langCode);
		List<DocumentCategoryDto> result = mastersyncservice.getDocumentCategories(testdataparsejson.getDataFromJsonViaKey("invaliddocCode"),testdataparsejson.getDataFromJsonViaKey("langCode"));
		System.out.println("*********"+result);
		System.out.println("*********"+result.size());
		assertEquals(0, result.size());
	}
	
	
	@Test
	public void masterSync_verify_getDocumentCategories_WithInvalidlangCode()
	{
		//mastersyncservice.getDocumentCategories(docCode, langCode);
		List<DocumentCategoryDto> result = mastersyncservice.getDocumentCategories(RegistrationConstants.POA_DOCUMENT,testdataparsejson.getDataFromJsonViaKey("invalidlangCode"));
		System.out.println("*********"+result);
		System.out.println("*********"+result.size());
		assertEquals(0, result.size());
	}
	
	
	@Test
	public void masterSync_verify_getDocumentCategories_WithdocCodenull()
	{
		//mastersyncservice.getDocumentCategories(docCode, langCode);
		List<DocumentCategoryDto> result = mastersyncservice.getDocumentCategories(null,testdataparsejson.getDataFromJsonViaKey("langCode"));
		System.out.println("*********"+result);
		System.out.println("*********"+result.size());
		assertEquals(0, result.size());
	}
	
	
	@Test
	public void masterSync_verify_getDocumentCategories_WithlangCodenull()
	{
		//mastersyncservice.getDocumentCategories(docCode, langCode);
		List<DocumentCategoryDto> result = mastersyncservice.getDocumentCategories(RegistrationConstants.POA_DOCUMENT,null);
		System.out.println("*********"+result);
		System.out.println("*********"+result.size());
		assertEquals(0, result.size());
	}
	
	
	@Test
	public void masterSync_verify_getDocumentCategories_WithdocCodenull_WithlangCodenull()
	{
		//mastersyncservice.getDocumentCategories(docCode, langCode);
		List<DocumentCategoryDto> result = mastersyncservice.getDocumentCategories(null,null);
		System.out.println("*********"+result);
		System.out.println("*********"+result.size());
		assertEquals(0, result.size());
	}
	

	
	
//valid data=gender table
			 //returns list of gender details-->code,name,lang_code,is_active
			//invalid data=any data returnes null
	@Test
	public void masterSync_verifygetGenderDtls_getCode()
	{
		//mastersyncservice.getGenderDtls(langCode);
		List<GenderDto> result = mastersyncservice.getGenderDtls(testdataparsejson.getDataFromJsonViaKey("langCode"));
		
		List<String> list1=new ArrayList<>();
		list1.addAll(Arrays.asList("1","2","MLE","FLE"));
		
		List<String> list2=new ArrayList<>();
		
		for(int i=0;i<result.size();i++)
		{
			list2.add(result.get(i).getCode());
		}
		System.out.println("********"+list1);
		System.out.println("********"+list2);
		assertEquals(list1, list2);
}
	
	
	@Test
	public void masterSync_verifygetGenderDtls_getGenderName()
	{
		//mastersyncservice.getGenderDtls(langCode);
		List<GenderDto> result = mastersyncservice.getGenderDtls(testdataparsejson.getDataFromJsonViaKey("langCode"));
		
		List<String> list1=new ArrayList<>();
		list1.addAll(Arrays.asList("female","female","Male","Female"));
		
		List<String> list2=new ArrayList<>();
		
		for(int i=0;i<result.size();i++)
		{
			list2.add(result.get(i).getGenderName());
		}
		System.out.println("********"+list1);
		System.out.println("********"+list2);
		assertEquals(list1, list2);
}
	
	
	@Test
	public void masterSync_verifygetGenderDtls_getIsActive()
	{
		//mastersyncservice.getGenderDtls(langCode);
		List<GenderDto> result = mastersyncservice.getGenderDtls(testdataparsejson.getDataFromJsonViaKey("langCode"));
		
		List<String> list1=new ArrayList<>();
		list1.addAll(Arrays.asList("true","true","true","true"));
		
		List<String> list2=new ArrayList<>();
		
		for(int i=0;i<result.size();i++)
		{
		list2.add(result.get(i).getIsActive().toString());
		}
		System.out.println("********"+list1);
		System.out.println("********"+list2);
		assertEquals(list1, list2);
}
	
	
	@Test
	public void masterSync_verifygetGenderDtls_getLangCode()
	{
		//mastersyncservice.getGenderDtls(langCode);
		List<GenderDto> result = mastersyncservice.getGenderDtls(testdataparsejson.getDataFromJsonViaKey("langCode"));
		
		List<String> list1=new ArrayList<>();
		list1.addAll(Arrays.asList("eng","eng","eng","eng"));
		
		List<String> list2=new ArrayList<>();
		
		for(int i=0;i<result.size();i++)
		{
		list2.add(result.get(i).getLangCode());
		}
		System.out.println("********"+list1);
		System.out.println("********"+list2);
		assertEquals(list1, list2);
}
	
	
	@Test
	public void masterSync_verify_getGenderDtls_WithInvalidlangCode()
	{
		//mastersyncservice.getGenderDtls(langCode);
		List<GenderDto> result = mastersyncservice.getGenderDtls(testdataparsejson.getDataFromJsonViaKey("invalidlangCode"));
		System.out.println("*********"+result);
		System.out.println("*********"+result.size());
		assertEquals(0, result.size());
	}
	
	
	@Test
	public void masterSync_verify_getGenderDtls_WithlangCodeNull()
	{
		//mastersyncservice.getGenderDtls(langCode);
		List<GenderDto> result = mastersyncservice.getGenderDtls(null);
		System.out.println("*********"+result);
		System.out.println("*********"+result.size());
		assertEquals(0, result.size());
	}
}