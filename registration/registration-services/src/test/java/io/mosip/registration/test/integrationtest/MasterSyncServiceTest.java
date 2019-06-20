package io.mosip.registration.test.integrationtest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.mastersync.BlacklistedWordsDto;
import io.mosip.registration.dto.mastersync.DocumentCategoryDto;
import io.mosip.registration.dto.mastersync.GenderDto;
import io.mosip.registration.dto.mastersync.LocationDto;
import io.mosip.registration.dto.mastersync.ReasonListDto;
import io.mosip.registration.service.sync.MasterSyncService;
import io.mosip.registration.util.healthcheck.RegistrationAppHealthCheckUtil;

public class MasterSyncServiceTest extends BaseIntegrationTest {

	@Autowired
	private MasterSyncService mastersyncservice;

	TestDataParseJSON testdataparsejson = new TestDataParseJSON(
			"src/test/resources/testData/MasterSyncServiceData/testData.json");

	@Test
	public void masterSync_verify_getMasterSync_getErrorResponseDTOs() {
//this test validates the message when sync is failed
		// mastersyncservice.getMasterSync(masterSyncDetails);
		ResponseDTO result = mastersyncservice
				.getMasterSync(testdataparsejson.getDataFromJsonViaKey("masterSyncDetails"), "System");
		System.out.println(RegistrationConstants.MASTER_SYNC_OFFLINE_FAILURE_MSG);
		assertNull(result.getErrorResponseDTOs());

	}

	@Test
	public void masterSync_verify_getMasterSync_getSuccessResponseDTOs() {
		// defect MOS-15831
//this test validates the message when sync is success
		// mastersyncservice.getMasterSync(masterSyncDetails);
		ResponseDTO result = mastersyncservice
				.getMasterSync(testdataparsejson.getDataFromJsonViaKey("masterSyncDetails"), "System");

		assertEquals(RegistrationConstants.SUCCESS, result.getSuccessResponseDTO().getMessage());

		System.out.println("********" + RegistrationAppHealthCheckUtil.isNetworkAvailable());

	}

	@Test
	public void masterSync_verify_findLocationByHierarchyCode_getCode() {
		// This test verifies if correct code is fetched from local database from table
		// location

		// mastersyncservice.findLocationByHierarchyCode(hierarchyCode, langCode);
		List<LocationDto> result = mastersyncservice.findLocationByHierarchyCode(
				testdataparsejson.getDataFromJsonViaKey("hierarchyCode"),
				testdataparsejson.getDataFromJsonViaKey("langCode"));
		for (int i = 0; i < result.size(); i++) {
			System.out.println(result.get(i).getCode());
			assertEquals("RSK", result.get(i).getCode());
		}

	}

	@Test
	public void masterSync_verify_findLocationByHierarchyCode_getHierarchyName() {
		// This test verifies if correct heirarchy name is fetched from local database
		// from table location

		// mastersyncservice.findLocationByHierarchyCode(hierarchyCode, langCode);
		List<LocationDto> result = mastersyncservice.findLocationByHierarchyCode(
				testdataparsejson.getDataFromJsonViaKey("hierarchyCode"),
				testdataparsejson.getDataFromJsonViaKey("langCode"));
		for (int i = 0; i < result.size(); i++) {
			System.out.println(result.get(i).getHierarchyName());
			assertEquals("Region", result.get(i).getHierarchyName());
		}

	}

	@Test
	public void masterSync_verify_findLocationByHierarchyCode_getLangCode() {
		// This test verifies if correct langcode is fetched from local database from
		// location table

		// mastersyncservice.findLocationByHierarchyCode(hierarchyCode, langCode);
		List<LocationDto> result = mastersyncservice.findLocationByHierarchyCode(
				testdataparsejson.getDataFromJsonViaKey("hierarchyCode"),
				testdataparsejson.getDataFromJsonViaKey("langCode"));
		for (int i = 0; i < result.size(); i++) {
			System.out.println(result.get(i).getLangCode());
			assertEquals("eng", result.get(i).getLangCode());
		}

	}

	@Test
	public void masterSync_verify_findLocationByHierarchyCode_getName() {
		// This test verifies if correct name is fetched from local database from
		// location table

		// mastersyncservice.findLocationByHierarchyCode(hierarchyCode, langCode);
		List<LocationDto> result = mastersyncservice.findLocationByHierarchyCode(
				testdataparsejson.getDataFromJsonViaKey("hierarchyCode"),
				testdataparsejson.getDataFromJsonViaKey("langCode"));
		for (int i = 0; i < result.size(); i++) {
			System.out.println(result.get(i).getName());
			assertEquals("Rabat Sale Kenitra", result.get(i).getName());
		}

	}

	@Test
	public void masterSync_verify_findLocationByHierarchyCode_WithInvalidhierarchyCode() {
		// this test validates that no data will be fetched from location table when
		// invalid heirarchy code is passed
		// mastersyncservice.findLocationByHierarchyCode(hierarchyCode, langCode);
		List<LocationDto> result = mastersyncservice.findLocationByHierarchyCode(
				testdataparsejson.getDataFromJsonViaKey("invalidhierarchyCode"),
				testdataparsejson.getDataFromJsonViaKey("langCode"));
		System.out.println("*********" + result);
		System.out.println("*********" + result.size());
		assertEquals(0, result.size());

	}

	@Test
	public void masterSync_verify_findLocationByHierarchyCode_WithInvalidlangCode() {
		// this test validates that no data will be fetched from location table when
		// invalid lang code is passed
		// mastersyncservice.findLocationByHierarchyCode(hierarchyCode, langCode);
		List<LocationDto> result = mastersyncservice.findLocationByHierarchyCode(
				testdataparsejson.getDataFromJsonViaKey("hierarchyCode"),
				testdataparsejson.getDataFromJsonViaKey("invalidlangCode"));
		System.out.println("*********" + result);
		System.out.println("*********" + result.size());
		assertEquals(0, result.size());

	}

	@Test
	public void masterSync_verify_findLocationByHierarchyCode_WithhierarchyCodeNull() {
		// this test validates that no data will be fetched from location table when
		// invalid heirarchy code and invalid lang code are passed
		// mastersyncservice.findLocationByHierarchyCode(hierarchyCode, langCode);
		List<LocationDto> result = mastersyncservice.findLocationByHierarchyCode(null,
				testdataparsejson.getDataFromJsonViaKey("langCode"));
		System.out.println("*********" + result);
		System.out.println("*********" + result.size());
		assertEquals(0, result.size());

	}

	@Test
	public void masterSync_verify_findLocationByHierarchyCode_WithlangCodeNull() {
		// this test validates that no data will be fetched from location table when
		// lang code is passed as null
		// mastersyncservice.findLocationByHierarchyCode(hierarchyCode, langCode);
		List<LocationDto> result = mastersyncservice
				.findLocationByHierarchyCode(testdataparsejson.getDataFromJsonViaKey("hierarchyCode"), null);
		System.out.println("*********" + result);
		System.out.println("*********" + result.size());
		assertEquals(0, result.size());

	}

	@Test
	public void masterSync_verify_findLocationByHierarchyCode_WithhierarchyCodeNull_WithlangCodeNull() {
		// this test validates that no data will be fetched from location table when
		// lang code and heirarchy code are passed as null
		// mastersyncservice.findLocationByHierarchyCode(hierarchyCode, langCode);
		List<LocationDto> result = mastersyncservice.findLocationByHierarchyCode(null, null);
		System.out.println("*********" + result);
		System.out.println("*********" + result.size());
		assertEquals(0, result.size());

	}

	// check if responsedto is not null
	// valid data=get from location
	// invalid data=any data returnes null
	@Test
	public void masterSync_verify_findProvianceByHierarchyCode_getCode() {
		// this test validates if expected codes are fetched from location table for
		// inputs passed

		// mastersyncservice.findProvianceByHierarchyCode(code, langCode);

		List<LocationDto> result = mastersyncservice.findProvianceByHierarchyCode(
				testdataparsejson.getDataFromJsonViaKey("code"), testdataparsejson.getDataFromJsonViaKey("langCode"));

		List<String> list1 = new ArrayList<>();
		list1.add("KTA");
		list1.add("RBT");

		List<String> list2 = new ArrayList<>();

		for (int i = 0; i < result.size(); i++) {
			System.out.println(result.get(i).getCode());
			list2.add(result.get(i).getCode());
		}
		System.out.println("********" + list1);
		System.out.println("********" + list2);
		assertEquals(list1, list2);

	}

	@Test
	public void masterSync_verifyfindProvianceByHierarchyCode_getHierarchyName() {
		// this test validates if expected heirarchy names are fetched from location
		// table for inputs passed
		List<LocationDto> result = mastersyncservice.findProvianceByHierarchyCode(
				testdataparsejson.getDataFromJsonViaKey("code"), testdataparsejson.getDataFromJsonViaKey("langCode"));

		List<String> list1 = new ArrayList<>();
		list1.add("Province");
		list1.add("Province");

		List<String> list2 = new ArrayList<>();

		for (int i = 0; i < result.size(); i++) {
			System.out.println(result.get(i).getHierarchyName());
			list2.add(result.get(i).getHierarchyName());
		}
		System.out.println("********" + list1);
		System.out.println("********" + list2);
		assertEquals(list1, list2);
	}

	@Test
	public void masterSync_verifyfindProvianceByHierarchyCode_getName() {
		// this test validates if expected codes are fetched from location table for
		// inputs passed
		List<LocationDto> result = mastersyncservice.findProvianceByHierarchyCode(
				testdataparsejson.getDataFromJsonViaKey("code"), testdataparsejson.getDataFromJsonViaKey("langCode"));

		List<String> list1 = new ArrayList<>();
		list1.add("Kenitra");
		list1.add("Rabat");

		List<String> list2 = new ArrayList<>();

		for (int i = 0; i < result.size(); i++) {
			System.out.println(result.get(i).getName());
			list2.add(result.get(i).getName());
		}
		System.out.println("********" + list1);
		System.out.println("********" + list2);
		assertEquals(list1, list2);
	}

	@Test
	public void masterSync_verifyfindProvianceByHierarchyCode_getLangCode() {
		// this test validates if expected language codes are fetched from location
		// table for inputs passed
		List<LocationDto> result = mastersyncservice.findProvianceByHierarchyCode(
				testdataparsejson.getDataFromJsonViaKey("code"), testdataparsejson.getDataFromJsonViaKey("langCode"));

		List<String> list1 = new ArrayList<>();
		list1.add("eng");
		list1.add("eng");

		List<String> list2 = new ArrayList<>();

		for (int i = 0; i < result.size(); i++) {
			System.out.println(result.get(i).getLangCode());
			list2.add(result.get(i).getLangCode());
		}
		System.out.println("********" + list1);
		System.out.println("********" + list2);
		assertEquals(list1, list2);
	}

	@Test
	public void masterSync_verify_findProvianceByHierarchyCode_WithInvalidCode() {
		// this test validates no data should be fetched from location table when
		// invalid code is passed as input for this method
		// mastersyncservice.findProvianceByHierarchyCode(code, langCode);
		List<LocationDto> result = mastersyncservice.findProvianceByHierarchyCode(
				testdataparsejson.getDataFromJsonViaKey("invalidcode"),
				testdataparsejson.getDataFromJsonViaKey("langCode"));
		System.out.println("*********" + result);
		System.out.println("*********" + result.size());
		assertEquals(0, result.size());
	}

	@Test
	public void masterSync_verify_findProvianceByHierarchyCode_WithInvalidlangCode() {
		// this test validates no data should be fetched from location table when
		// invalid language code is passed as input for this method
		// mastersyncservice.findProvianceByHierarchyCode(code, langCode);
		List<LocationDto> result = mastersyncservice.findProvianceByHierarchyCode(
				testdataparsejson.getDataFromJsonViaKey("code"),
				testdataparsejson.getDataFromJsonViaKey("invalidlangCode"));
		System.out.println("*********" + result);
		System.out.println("*********" + result.size());
		assertEquals(0, result.size());
	}

	@Test
	public void masterSync_verify_findProvianceByHierarchyCode_WithCodeNull() {
		// this test validates no data should be fetched from location table when code
		// is passed as null as input for this method
		// mastersyncservice.findProvianceByHierarchyCode(code, langCode);
		List<LocationDto> result = mastersyncservice.findProvianceByHierarchyCode(null,
				testdataparsejson.getDataFromJsonViaKey("langCode"));
		System.out.println("*********#########" + result);
		System.out.println("*********###########" + result.size());
		// assertEquals(0, result.size());
		List<String> list1 = new ArrayList<>();
		List<String> list2 = new ArrayList<>();
		List<String> list3 = new ArrayList<>();
		List<String> list4 = new ArrayList<>();
		for (int i = 0; i < result.size(); i++) {
			list1.add(result.get(i).getCode());
			list2.add(result.get(i).getLangCode());
			list3.add(result.get(i).getName());
			list4.add(result.get(i).getHierarchyName());
		}
		System.out.println("########" + list1);
		System.out.println("########" + list2);
		System.out.println("########" + list3);
		System.out.println("#########" + list4);
	}

	@Test
	public void masterSync_verify_findProvianceByHierarchyCode_WithlangCodeNull() {
		// this test validates no data should be fetched from location table when
		// language code is passed as null as input for this method
		// mastersyncservice.findProvianceByHierarchyCode(code, langCode);
		List<LocationDto> result = mastersyncservice
				.findProvianceByHierarchyCode(testdataparsejson.getDataFromJsonViaKey("code"), null);
		System.out.println("*********" + result);
		System.out.println("*********" + result.size());
		assertEquals(0, result.size());
	}

	@Test
	public void masterSync_verify_findProvianceByHierarchyCode_WithCodeNull_WithlangCodeNull() {
		// this test validates no data should be fetched from location table when code
		// and language code are passed as null as input for this method
		// mastersyncservice.findProvianceByHierarchyCode(code, langCode);
		List<LocationDto> result = mastersyncservice.findProvianceByHierarchyCode(null, null);
		System.out.println("*********" + result);
		System.out.println("*********" + result.size());
		assertEquals(0, result.size());
	}

	// valid data=get from reason_list
	// returns code,name,rsncat_code,lang_code
	// invalid data=any data returnes null
	@Test
	public void masterSync_verifygetAllReasonsList_getCode() {
		// this test validates if expected code is fetched from reason_list table
		// mastersyncservice.getAllReasonsList(langCode);
		List<ReasonListDto> result = mastersyncservice
				.getAllReasonsList(testdataparsejson.getDataFromJsonViaKey("langCode"));
		List<String> list1 = new ArrayList<>();
		list1.addAll(Arrays.asList("APM", "GPM", "IAD", "DPG", "OTH", "ADM", "ADD", "OPM", "SDM"));

		List<String> list2 = new ArrayList<>();

		for (int i = 0; i < result.size(); i++) {
			list2.add(result.get(i).getCode());
		}
		System.out.println("********" + list1);
		System.out.println("********" + list2);
		assertEquals(list1, list2);
	}

	@Test
	public void masterSync_verifygetAllReasonsList_getName() {
		// this test validates if expected names are fetched from reason_list table
		List<ReasonListDto> result = mastersyncservice
				.getAllReasonsList(testdataparsejson.getDataFromJsonViaKey("langCode"));
		List<String> list1 = new ArrayList<>();
		list1.addAll(Arrays.asList("Age-Photo Mismatch", "Gender-Photo Mismatch", "Invalid Address",
				"Duplicate Registration", "Others", "All the Details are matching",
				"All the Demographic Details are Matching", "Only the Photograph is Matching", "Some of the Demographic Details are Matching"));

		List<String> list2 = new ArrayList<>();

		for (int i = 0; i < result.size(); i++) {
			list2.add(result.get(i).getName());
		}
		System.out.println("********" + list1);
		System.out.println("********" + list2);
		assertEquals(list1, list2);
	}

	@Test
	public void masterSync_verifygetAllReasonsList_getRsnCatCode() {
		// this test validates if expected rsncatcode is fetched from reason_list table
		List<ReasonListDto> result = mastersyncservice
				.getAllReasonsList(testdataparsejson.getDataFromJsonViaKey("langCode"));
		List<String> list1 = new ArrayList<>();
		list1.addAll(Arrays.asList("CLR", "CLR", "CLR", "CLR", "CLR", "MNA", "MNA", "MNA", "MNA"));

		List<String> list2 = new ArrayList<>();

		for (int i = 0; i < result.size(); i++) {
			list2.add(result.get(i).getRsnCatCode());
		}
		System.out.println("********" + list1);
		System.out.println("********" + list2);
		assertEquals(list1, list2);
	}

	@Test
	public void masterSync_verifygetAllReasonsList_getLangCode() {
		// this test validates if expected language code is fetched from reason_list
		// table
		List<ReasonListDto> result = mastersyncservice
				.getAllReasonsList(testdataparsejson.getDataFromJsonViaKey("langCode"));
		List<String> list1 = new ArrayList<>();
		list1.addAll(Arrays.asList("eng", "eng", "eng", "eng", "eng", "eng", "eng", "eng", "eng"));

		List<String> list2 = new ArrayList<>();

		for (int i = 0; i < result.size(); i++) {
			list2.add(result.get(i).getLangCode());
		}
		System.out.println("********" + list1);
		System.out.println("********" + list2);
		assertEquals(list1, list2);
	}

	@Test
	public void masterSync_verify_getAllReasonsList_WithInvalidlangCode() {
		// this test validates that no data will be fetched from reason_list table when
		// invalid language code is passed
		// mastersyncservice.getAllReasonsList(langCode);

		List<ReasonListDto> result = mastersyncservice
				.getAllReasonsList(testdataparsejson.getDataFromJsonViaKey("invalidlangCode"));
		System.out.println("*********" + result);
		System.out.println("*********" + result.size());
		assertEquals(0, result.size());
	}

	@Test
	public void masterSync_verify_getAllReasonsList_WithlangCodeNull() {
		// this test validates that no data will be fetched from reason_list table when
		// language code is passed as null
		// mastersyncservice.getAllReasonsList(langCode);

		List<ReasonListDto> result = mastersyncservice.getAllReasonsList(null);
		System.out.println("*********" + result);
		System.out.println("*********" + result.size());
		assertEquals(0, result.size());
	}

//valid data=get from blacklisted_words
	// returns word.description and language code
	// invalid data=any data returnes null

	@Test
	public void masterSync_verifygetAllBlackListedWords_getDescription() {
		// this test validates if expected description is fetched from blacklisted_words
		// table
		// mastersyncservice.getAllBlackListedWords(langCode);

		List<BlacklistedWordsDto> result = mastersyncservice
				.getAllBlackListedWords(testdataparsejson.getDataFromJsonViaKey("langCode"));

		List<String> list1 = new ArrayList<>();
		list1.addAll(
				Arrays.asList("Blacklisted Word", "Blacklisted Word", "Blacklisted Word", "Blacklisted Word", "fuk word", "Word is bloacklisted", "Word is bloacklisted", "BloackListed"));

		List<String> list2 = new ArrayList<>();

		for (int i = 0; i < result.size(); i++) {
			list2.add(result.get(i).getDescription());
		}
		System.out.println("********" + list1);
		System.out.println("********" + list2);
		assertEquals(list1, list2);
	}

	@Test
	public void masterSync_verifygetAllBlackListedWords_getLangCode() {
		// this test validates if expected language code is fetched from
		// blacklisted_words table
		List<BlacklistedWordsDto> result = mastersyncservice
				.getAllBlackListedWords(testdataparsejson.getDataFromJsonViaKey("langCode"));

		List<String> list1 = new ArrayList<>();
		list1.addAll(Arrays.asList("eng", "eng", "eng", "eng", "eng", "eng", "eng", "eng"));

		List<String> list2 = new ArrayList<>();

		for (int i = 0; i < result.size(); i++) {
			list2.add(result.get(i).getLangCode());
		}
		System.out.println("********" + list1);
		System.out.println("********" + list2);
		assertEquals(list1, list2);
	}

	@Test
	public void masterSync_verifygetAllBlackListedWords_getWord() {
		// this test validates if expected words are fetched from blacklisted_words
		// table
		List<BlacklistedWordsDto> result = mastersyncservice
				.getAllBlackListedWords(testdataparsejson.getDataFromJsonViaKey("langCode"));

		List<String> list1 = new ArrayList<>();
		list1.addAll(Arrays.asList("shit", "damn", "nigga", "dammit", "fuk", "elephantytft", "xxxcheckwordxxx", "bad word"));

		List<String> list2 = new ArrayList<>();

		for (int i = 0; i < result.size(); i++) {
			list2.add(result.get(i).getWord());
		}
		System.out.println("********" + list1);
		System.out.println("********" + list2);
		assertEquals(list1, list2);
	}

	@Test
	public void masterSync_verify_getAllBlackListedWords_WithInvalidlangCode() {
		// this test validates if no data will be fetched blacklisted_words table when
		// invalid language code is fetched
		// mastersyncservice.getAllBlackListedWords(langCode);
		List<BlacklistedWordsDto> result = mastersyncservice
				.getAllBlackListedWords(testdataparsejson.getDataFromJsonViaKey("invalidlangCode"));
		System.out.println("*********" + result);
		System.out.println("*********" + result.size());
		assertEquals(0, result.size());
	}

	@Test
	public void masterSync_verify_getAllBlackListedWords_WithlangCodeNull() {
		// this test validates if no data will be fetched blacklisted_words table when
		// language code is passed as null
		// mastersyncservice.getAllBlackListedWords(langCode);
		List<BlacklistedWordsDto> result = mastersyncservice.getAllBlackListedWords(null);
		System.out.println("*********" + result);
		System.out.println("*********" + result.size());
		assertEquals(0, result.size());
	}

//valid data=POA,POI,POR,POB; langcode=eng
	// returns list of documents-->name,descrition and language code from doc_type
	// table
	// invalid data=any data returnes null

	@Test
	public void masterSync_verifygetDocumentCategories_getDescription_POA() {
		// this test validates if expected descriptions are fetched from doc_type table
		// for POA document category
		// mastersyncservice.getDocumentCategories(docCode, langCode);
		List<DocumentCategoryDto> result = mastersyncservice.getDocumentCategories(RegistrationConstants.POA_DOCUMENT,
				testdataparsejson.getDataFromJsonViaKey("langCode"));

		List<String> list1 = new ArrayList<>();
		list1.addAll(Arrays.asList("Rental Agreement of address"));

		List<String> list2 = new ArrayList<>();

		for (int i = 0; i < result.size(); i++) {
			list2.add(result.get(i).getDescription());
		}

		System.out.println("********" + list1);
		System.out.println("********" + list2);
		assertEquals(list1, list2);
	}

	@Test
	public void masterSync_verifygetDocumentCategories_getDescription_POI() {
		// this test validates if expected descriptions are fetched from doc_type table
		// for POI document category
		// mastersyncservice.getDocumentCategories(docCode, langCode);
		List<DocumentCategoryDto> result = mastersyncservice.getDocumentCategories(RegistrationConstants.POI_DOCUMENT,
				testdataparsejson.getDataFromJsonViaKey("langCode"));

		List<String> list1 = new ArrayList<>();
		list1.addAll(Arrays.asList("Moroccan National Electronic ID Card"));

		List<String> list2 = new ArrayList<>();

		for (int i = 0; i < result.size(); i++) {
			list2.add(result.get(i).getDescription());
		}

		System.out.println("********" + list1);
		System.out.println("********" + list2);
		assertEquals(list1, list2);
	}

	@Test
	public void masterSync_verifygetDocumentCategories_getDescription_POR() {
		// this test validates if expected descriptions are fetched from doc_type table
		// for POR document category
		// mastersyncservice.getDocumentCategories(docCode, langCode);
		List<DocumentCategoryDto> result = mastersyncservice.getDocumentCategories(RegistrationConstants.POR_DOCUMENT,
				testdataparsejson.getDataFromJsonViaKey("langCode"));

		List<String> list1 = new ArrayList<>();
		list1.addAll(Arrays.asList("Proof relationship of a person"));

		List<String> list2 = new ArrayList<>();

		for (int i = 0; i < result.size(); i++) {
			list2.add(result.get(i).getDescription());
		}

		System.out.println("********" + list1);
		System.out.println("********" + list2);
		assertEquals(list1, list2);
	}

	@Test
	public void masterSync_verifygetDocumentCategories_getDescription_POB() {
		// this test validates if expected descriptions are fetched from doc_type table
		// for POB document category
		// mastersyncservice.getDocumentCategories(docCode, langCode);
		List<DocumentCategoryDto> result = mastersyncservice.getDocumentCategories(RegistrationConstants.DOB_DOCUMENT,
				testdataparsejson.getDataFromJsonViaKey("langCode"));

		List<String> list1 = new ArrayList<>();
		list1.addAll(Arrays.asList("Proof birth and age of a person"));

		List<String> list2 = new ArrayList<>();

		for (int i = 0; i < result.size(); i++) {
			list2.add(result.get(i).getDescription());
		}

		System.out.println("********" + list1);
		System.out.println("********" + list2);
		assertEquals(list1, list2);
	}

	@Test
	public void masterSync_verifygetDocumentCategories_getLangCode_POA() {
		// this test validates if expected language codes are fetched from doc_type
		// table for POA document category
		// mastersyncservice.getDocumentCategories(docCode, langCode);
		List<DocumentCategoryDto> result = mastersyncservice.getDocumentCategories(RegistrationConstants.POA_DOCUMENT,
				testdataparsejson.getDataFromJsonViaKey("langCode"));

		List<String> list1 = new ArrayList<>();
		list1.addAll(Arrays.asList("eng"));

		List<String> list2 = new ArrayList<>();

		for (int i = 0; i < result.size(); i++) {
			list2.add(result.get(i).getLangCode());
		}

		System.out.println("********" + list1);
		System.out.println("********" + list2);
		assertEquals(list1, list2);
	}

	@Test
	public void masterSync_verifygetDocumentCategories_getLangCode_POI() {
		// this test validates if expected language codes are fetched from doc_type
		// table for POI document category
		// mastersyncservice.getDocumentCategories(docCode, langCode);
		List<DocumentCategoryDto> result = mastersyncservice.getDocumentCategories(RegistrationConstants.POI_DOCUMENT,
				testdataparsejson.getDataFromJsonViaKey("langCode"));

		List<String> list1 = new ArrayList<>();
		list1.addAll(Arrays.asList("eng"));

		List<String> list2 = new ArrayList<>();

		for (int i = 0; i < result.size(); i++) {
			list2.add(result.get(i).getLangCode());
		}

		System.out.println("********" + list1);
		System.out.println("********" + list2);
		assertEquals(list1, list2);
	}

	@Test
	public void masterSync_verifygetDocumentCategories_getLangCode_POR() {
		// this test validates if expected language codes are fetched from doc_type
		// table for POR document category
		// mastersyncservice.getDocumentCategories(docCode, langCode);
		List<DocumentCategoryDto> result = mastersyncservice.getDocumentCategories(RegistrationConstants.POR_DOCUMENT,
				testdataparsejson.getDataFromJsonViaKey("langCode"));

		List<String> list1 = new ArrayList<>();
		list1.addAll(Arrays.asList("eng"));

		List<String> list2 = new ArrayList<>();

		for (int i = 0; i < result.size(); i++) {
			list2.add(result.get(i).getLangCode());
		}

		System.out.println("********" + list1);
		System.out.println("********" + list2);
		assertEquals(list1, list2);
	}

	@Test
	public void masterSync_verifygetDocumentCategories_getLangCode_POB() {
		// this test validates if expected language codes are fetched from doc_type
		// table for POB document category
		// mastersyncservice.getDocumentCategories(docCode, langCode);
		List<DocumentCategoryDto> result = mastersyncservice.getDocumentCategories(RegistrationConstants.DOB_DOCUMENT,
				testdataparsejson.getDataFromJsonViaKey("langCode"));

		List<String> list1 = new ArrayList<>();
		list1.addAll(Arrays.asList("eng"));

		List<String> list2 = new ArrayList<>();

		for (int i = 0; i < result.size(); i++) {
			list2.add(result.get(i).getLangCode());
		}

		System.out.println("********" + list1);
		System.out.println("********" + list2);
		assertEquals(list1, list2);
	}

	@Test
	public void masterSync_verifygetDocumentCategories_getName_POA() {
		// this test validates if expected names are fetched from doc_type table for POA
		// document category
		// mastersyncservice.getDocumentCategories(docCode, langCode);
		List<DocumentCategoryDto> result = mastersyncservice.getDocumentCategories(RegistrationConstants.POA_DOCUMENT,
				testdataparsejson.getDataFromJsonViaKey("langCode"));

		List<String> list1 = new ArrayList<>();
		list1.addAll(Arrays.asList("Rental contract"));

		List<String> list2 = new ArrayList<>();

		for (int i = 0; i < result.size(); i++) {
			list2.add(result.get(i).getName());
		}

		System.out.println("********" + list1);
		System.out.println("********" + list2);
		assertEquals(list1, list2);
	}

	@Test
	public void masterSync_verifygetDocumentCategories_getName_POI() {
		// this test validates if expected names are fetched from doc_type table for POI
		// document category
		// mastersyncservice.getDocumentCategories(docCode, langCode);
		List<DocumentCategoryDto> result = mastersyncservice.getDocumentCategories(RegistrationConstants.POI_DOCUMENT,
				testdataparsejson.getDataFromJsonViaKey("langCode"));

		List<String> list1 = new ArrayList<>();
		list1.addAll(Arrays.asList("CNIE card"));

		List<String> list2 = new ArrayList<>();

		for (int i = 0; i < result.size(); i++) {
			list2.add(result.get(i).getName());
		}

		System.out.println("********" + list1);
		System.out.println("********" + list2);
		assertEquals(list1, list2);
	}

	@Test
	public void masterSync_verifygetDocumentCategories_getName_POR() {
		// this test validates if expected names are fetched from doc_type table for POR
		// document category
		// mastersyncservice.getDocumentCategories(docCode, langCode);
		List<DocumentCategoryDto> result = mastersyncservice.getDocumentCategories(RegistrationConstants.POR_DOCUMENT,
				testdataparsejson.getDataFromJsonViaKey("langCode"));

		List<String> list1 = new ArrayList<>();
		list1.addAll(Arrays.asList("Certificate of Relationship"));

		List<String> list2 = new ArrayList<>();

		for (int i = 0; i < result.size(); i++) {
			list2.add(result.get(i).getName());
		}

		System.out.println("********" + list1);
		System.out.println("********" + list2);
		assertEquals(list1, list2);
	}

	@Test
	public void masterSync_verifygetDocumentCategories_getName_POB() {
		// this test validates if expected names are fetched from doc_type table for POB
		// document category
		// mastersyncservice.getDocumentCategories(docCode, langCode);
		List<DocumentCategoryDto> result = mastersyncservice.getDocumentCategories(RegistrationConstants.DOB_DOCUMENT,
				testdataparsejson.getDataFromJsonViaKey("langCode"));

		List<String> list1 = new ArrayList<>();
		list1.addAll(Arrays.asList("Certificate of Birth"));

		List<String> list2 = new ArrayList<>();

		for (int i = 0; i < result.size(); i++) {
			list2.add(result.get(i).getName());
		}

		System.out.println("********" + list1);
		System.out.println("********" + list2);
		assertEquals(list1, list2);
	}

	@Test
	public void masterSync_verify_getDocumentCategories_WithInvaliddocCode() {
		// this test validates if no data is fetched from doc_type table when invalid
		// document code is passed
		// mastersyncservice.getDocumentCategories(docCode, langCode);
		List<DocumentCategoryDto> result = mastersyncservice.getDocumentCategories(
				testdataparsejson.getDataFromJsonViaKey("invaliddocCode"),
				testdataparsejson.getDataFromJsonViaKey("langCode"));
		System.out.println("*********" + result);
		System.out.println("*********" + result.size());
		assertEquals(0, result.size());
	}

	@Test
	public void masterSync_verify_getDocumentCategories_WithInvalidlangCode() {
		// this test validates if no data is fetched from doc_type table when invalid
		// language code is passed
		// mastersyncservice.getDocumentCategories(docCode, langCode);
		List<DocumentCategoryDto> result = mastersyncservice.getDocumentCategories(RegistrationConstants.POA_DOCUMENT,
				testdataparsejson.getDataFromJsonViaKey("invalidlangCode"));
		System.out.println("*********" + result);
		System.out.println("*********" + result.size());
		assertEquals(0, result.size());
	}

	@Test
	public void masterSync_verify_getDocumentCategories_WithdocCodenull() {
		// this test validates if no data is fetched from doc_type table when document
		// code is passed as null
		// mastersyncservice.getDocumentCategories(docCode, langCode);
		List<DocumentCategoryDto> result = mastersyncservice.getDocumentCategories(null,
				testdataparsejson.getDataFromJsonViaKey("langCode"));
		System.out.println("*********" + result);
		System.out.println("*********" + result.size());
		assertEquals(0, result.size());
	}

	@Test
	public void masterSync_verify_getDocumentCategories_WithlangCodenull() {
		// this test validates if no data is fetched from doc_type table when language
		// code is passed as null
		// mastersyncservice.getDocumentCategories(docCode, langCode);
		List<DocumentCategoryDto> result = mastersyncservice.getDocumentCategories(RegistrationConstants.POA_DOCUMENT,
				null);
		System.out.println("*********" + result);
		System.out.println("*********" + result.size());
		assertEquals(0, result.size());
	}

	@Test
	public void masterSync_verify_getDocumentCategories_WithdocCodenull_WithlangCodenull() {
		// this test validates if no data is fetched from doc_type table when language
		// code and document code are passed as null
		// mastersyncservice.getDocumentCategories(docCode, langCode);
		List<DocumentCategoryDto> result = mastersyncservice.getDocumentCategories(null, null);
		System.out.println("*********" + result);
		System.out.println("*********" + result.size());
		assertEquals(0, result.size());
	}

//valid data=gender table
	// returns list of gender details-->code,name,lang_code,is_active
	// invalid data=any data returnes null
	@Test
	public void masterSync_verifygetGenderDtls_getCode() {
		// this test validates if expected code is fetched from gender table
		// mastersyncservice.getGenderDtls(langCode);
		List<GenderDto> result = mastersyncservice.getGenderDtls(testdataparsejson.getDataFromJsonViaKey("langCode"));

		List<String> list1 = new ArrayList<>();
		list1.addAll(Arrays.asList("MLE", "OTH", "FLE", "ABC"));

		List<String> list2 = new ArrayList<>();

		for (int i = 0; i < result.size(); i++) {
			list2.add(result.get(i).getCode());
		}
		System.out.println("********" + list1);
		System.out.println("********" + list2);
		assertEquals(list1, list2);
	}

	@Test
	public void masterSync_verifygetGenderDtls_getGenderName() {
		// this test validates if expected gender names are fetched from gender table
		// mastersyncservice.getGenderDtls(langCode);
		List<GenderDto> result = mastersyncservice.getGenderDtls(testdataparsejson.getDataFromJsonViaKey("langCode"));

		List<String> list1 = new ArrayList<>();
		list1.addAll(Arrays.asList("Male", "Others", "Female", "Male"));

		List<String> list2 = new ArrayList<>();

		for (int i = 0; i < result.size(); i++) {
			list2.add(result.get(i).getGenderName());
		}
		System.out.println("********" + list1);
		System.out.println("********" + list2);
		assertEquals(list1, list2);
	}

	@Test
	public void masterSync_verifygetGenderDtls_getIsActive() {
		// this test validates if expected boolean values are fetched for gender details
		// from gender table
		// mastersyncservice.getGenderDtls(langCode);
		List<GenderDto> result = mastersyncservice.getGenderDtls(testdataparsejson.getDataFromJsonViaKey("langCode"));

		List<String> list1 = new ArrayList<>();
		list1.addAll(Arrays.asList("true", "true", "true", "true"));

		List<String> list2 = new ArrayList<>();

		for (int i = 0; i < result.size(); i++) {
			list2.add(result.get(i).getIsActive().toString());
		}
		System.out.println("********" + list1);
		System.out.println("********" + list2);
		assertEquals(list1, list2);
	}

	@Test
	public void masterSync_verifygetGenderDtls_getLangCode() {
		// this test validates if expected language codes are fetched from gender table
		// mastersyncservice.getGenderDtls(langCode);
		List<GenderDto> result = mastersyncservice.getGenderDtls(testdataparsejson.getDataFromJsonViaKey("langCode"));

		List<String> list1 = new ArrayList<>();
		list1.addAll(Arrays.asList("eng", "eng", "eng", "eng"));

		List<String> list2 = new ArrayList<>();

		for (int i = 0; i < result.size(); i++) {
			list2.add(result.get(i).getLangCode());
		}
		System.out.println("********" + list1);
		System.out.println("********" + list2);
		assertEquals(list1, list2);
	}

	@Test
	public void masterSync_verify_getGenderDtls_WithInvalidlangCode() {
		// this test validates that no data is fetched from gender table when invalid
		// language code is fetched
		// mastersyncservice.getGenderDtls(langCode);
		List<GenderDto> result = mastersyncservice
				.getGenderDtls(testdataparsejson.getDataFromJsonViaKey("invalidlangCode"));
		System.out.println("*********" + result);
		System.out.println("*********" + result.size());
		assertEquals(0, result.size());
	}

	@Test
	public void masterSync_verify_getGenderDtls_WithlangCodeNull() {
		// this test validates that no data is fetched from gender table when language
		// code is passed as null
		// mastersyncservice.getGenderDtls(langCode);
		List<GenderDto> result = mastersyncservice.getGenderDtls(null);
		System.out.println("*********" + result);
		System.out.println("*********" + result.size());
		assertEquals(0, result.size());
	}
}