package io.mosip.registration.test.validator;

import static org.junit.Assert.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import io.mosip.kernel.core.idobjectvalidator.constant.IdObjectValidatorSupportedOperations;
import io.mosip.kernel.core.idobjectvalidator.exception.IdObjectValidationFailedException;
import io.mosip.registration.dao.MasterSyncDao;
import io.mosip.registration.entity.DocumentCategory;
import io.mosip.registration.entity.DocumentType;
import io.mosip.registration.entity.Gender;
import io.mosip.registration.entity.Language;
import io.mosip.registration.entity.Location;
import io.mosip.registration.entity.ValidDocument;
import io.mosip.registration.validator.RegIdObjectMasterDataValidator;

public class RegIdObjectMasterDataValidatorTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@Mock
	private MasterSyncDao masterSyncDao;
	
	@Mock
	private ObjectMapper mapper;

	@InjectMocks
	private RegIdObjectMasterDataValidator regIdObjectMasterDataValidator;

	@Test
	public void validateIdObjectTest() throws Exception {
		List<Language> langList = new ArrayList<>();
		Language lang = new Language();
		lang.setCode("eng");
		langList.add(lang);
		List<Gender> genderList = new ArrayList<>();
		Gender gender = new Gender();
		gender.setGenderName("Male");
		gender.setCode("MLE");
		genderList.add(gender);
		List<DocumentCategory> docList = new ArrayList<>();
		DocumentCategory documentCategory = new DocumentCategory();
		documentCategory.setLangCode("eng");
		documentCategory.setCode("POI");
		docList.add(documentCategory);
		List<ValidDocument> masterValidDocuments = new ArrayList<>();
		ValidDocument validDocument = new ValidDocument();
		validDocument.setDocTypeCode("POI");
		masterValidDocuments.add(validDocument);
		List<DocumentType> masterDocuments = new ArrayList<>();
		DocumentType documentType = new DocumentType();
		documentType.setName("Passport");
		documentType.setCode("DOC001");
		masterDocuments.add(documentType);
		List<Location> locationList = new ArrayList<>();
		Location location = new Location();
		location.setHierarchyLevel(0);
		location.setHierarchyName("Country");
		location.setLangCode("eng");
		location.setName("Morocco");
		location.setCode("MOR");
		locationList.add(location);
		Mockito.when(masterSyncDao.getActiveLanguages()).thenReturn(langList);
		Mockito.when(masterSyncDao.getGenders()).thenReturn(genderList);
		Mockito.when(masterSyncDao.getDocumentCategory()).thenReturn(docList);
		Mockito.when(masterSyncDao.getValidDocumets(Mockito.any())).thenReturn(masterValidDocuments);
		Mockito.when(masterSyncDao.getDocumentTypes(Mockito.any(), Mockito.any())).thenReturn(masterDocuments);
		Mockito.when(masterSyncDao.getLocationDetails()).thenReturn(locationList);
		regIdObjectMasterDataValidator.loadData();
		String identityString = "{\"identity\":{\"IDSchemaVersion\":1.0,\"UIN\":4920546943,\"fullName\":[{\"language\":\"eng\",\"value\":\"Ibrahim Ibn Ali\"}],\"dateOfBirth\":\"1955/04/15\",\"age\":45,\"gender\":[{\"language\":\"eng\",\"value\":\"MLE\"}],\"addressLine1\":[{\"language\":\"eng\",\"value\":\"exemple d'adresse ligne 1\"}],\"addressLine2\":[{\"language\":\"eng\",\"value\":\"exemple d'adresse ligne 2\"}],\"addressLine3\":[{\"language\":\"eng\",\"value\":\"exemple d'adresse ligne 2\"}],\"region\":[{\"language\":\"eng\",\"value\":\"Rabat Sale Kenitra\"}],\"province\":[{\"language\":\"eng\",\"value\":\"Kenitra\"}],\"city\":[{\"language\":\"eng\",\"value\":\"Kenitra\"}],\"postalCode\":\"10112\",\"phone\":\"9876543210\",\"email\":\"abc@xyz.com\",\"CNIENumber\":\"6789545678909\",\"localAdministrativeAuthority\":[{\"language\":\"eng\",\"value\":\"Mograne\"}],\"parentOrGuardianRID\":212124324784912,\"parentOrGuardianUIN\":212124324784912,\"parentOrGuardianName\":[{\"language\":\"eng\",\"value\":\"salma\"}],\"proofOfAddress\":{\"format\":\"pdf\",\"type\":\"Ration Card\",\"value\":\"fileReferenceID\"},\"proofOfIdentity\":{\"format\":\"txt\",\"type\":\"Passport\",\"value\":\"fileReferenceID\"},\"proofOfRelationship\":{\"format\":\"pdf\",\"type\":\"Birth Certificate\",\"value\":\"fileReferenceID\"},\"proofOfDateOfBirth\":{\"format\":\"pdf\",\"type\":\"passport\",\"value\":\"fileReferenceID\"},\"individualBiometrics\":{\"format\":\"cbeff\",\"version\":1.0,\"value\":\"fileReferenceID\"},\"parentOrGuardianBiometrics\":{\"format\":\"cbeff\",\"version\":1.1,\"value\":\"fileReferenceID\"}}}";
		Mockito.when(mapper.writeValueAsString(Mockito.any())).thenReturn(identityString);
		assertTrue(regIdObjectMasterDataValidator.validateIdObject(
				new ObjectMapper().readValue(identityString.getBytes(StandardCharsets.UTF_8), Object.class),
				IdObjectValidatorSupportedOperations.NEW_REGISTRATION));
	}
	
	@Test(expected = IdObjectValidationFailedException.class)
	public void validateIdObjectTestException() throws Exception {
		
		List<Language> langList = new ArrayList<>();
		Language lang = new Language();
		lang.setCode("eng");
		langList.add(lang);
		List<Gender> genderList = new ArrayList<>();
		Gender gender = new Gender();
		gender.setGenderName("test");
		gender.setCode("Test");
		gender.setLangCode("eng");
		genderList.add(gender);
		List<DocumentCategory> docList = new ArrayList<>();
		DocumentCategory documentCategory = new DocumentCategory();
		documentCategory.setLangCode("eng");
		documentCategory.setCode("POI");
		docList.add(documentCategory);
		List<ValidDocument> masterValidDocuments = new ArrayList<>();
		ValidDocument validDocument = new ValidDocument();
		validDocument.setDocTypeCode("POI");
		validDocument.setLangCode("eng");
		masterValidDocuments.add(validDocument);
		List<DocumentType> masterDocuments = new ArrayList<>();
		DocumentType documentType = new DocumentType();
		documentType.setName("Passport");
		documentType.setCode("DOC001");
		documentType.setLangCode("eng");
		masterDocuments.add(documentType);
		List<Location> locationList = new ArrayList<>();
		Location location = new Location();
		location.setHierarchyLevel(1);
		location.setHierarchyName("region");
		location.setLangCode("eng");
		location.setName("Rabat Sale");
		location.setCode("RSK");
		locationList.add(location);
		Location location_province = new Location();
		location_province.setHierarchyLevel(2);
		location_province.setHierarchyName("Province");
		location_province.setLangCode("eng");
		location_province.setName("Rabat Sale");
		location_province.setCode("RSK");
		locationList.add(location_province);
		Location location_city = new Location();
		location_city.setHierarchyLevel(3);
		location_city.setHierarchyName("City");
		location_city.setLangCode("eng");
		location_city.setName("Rabat Sale");
		location_city.setCode("RSK");
		locationList.add(location_city);
		Mockito.when(masterSyncDao.getActiveLanguages()).thenReturn(langList);
		Mockito.when(masterSyncDao.getGenders()).thenReturn(genderList);
		Mockito.when(masterSyncDao.getDocumentCategory()).thenReturn(docList);
		Mockito.when(masterSyncDao.getValidDocumets(Mockito.any())).thenReturn(masterValidDocuments);
		Mockito.when(masterSyncDao.getDocumentTypes(Mockito.any(), Mockito.any())).thenReturn(masterDocuments);
		Mockito.when(masterSyncDao.getLocationDetails()).thenReturn(locationList);
		regIdObjectMasterDataValidator.loadData();
		String identityString = "{\"identity\":{\"IDSchemaVersion\":1.0,\"UIN\":4920546943,\"fullName\":[{\"language\":\"eng\",\"value\":\"Ibrahim Ibn Ali\"}],\"dateOfBirth\":\"1955/04/15\",\"age\":45,\"gender\":[{\"language\":\"eng\",\"value\":\"MLE\"}],\"addressLine1\":[{\"language\":\"eng\",\"value\":\"exemple d'adresse ligne 1\"}],\"addressLine2\":[{\"language\":\"eng\",\"value\":\"exemple d'adresse ligne 2\"}],\"addressLine3\":[{\"language\":\"eng\",\"value\":\"exemple d'adresse ligne 2\"}],\"region\":[{\"language\":\"eng\",\"value\":\"Rabat Sale Kenitra\"}],\"province\":[{\"language\":\"eng\",\"value\":\"Kenitra\"}],\"city\":[{\"language\":\"eng\",\"value\":\"Kenitra\"}],\"postalCode\":\"10112\",\"phone\":\"9876543210\",\"email\":\"abc@xyz.com\",\"CNIENumber\":\"6789545678909\",\"localAdministrativeAuthority\":[{\"language\":\"eng\",\"value\":\"Mograne\"}],\"parentOrGuardianRID\":212124324784912,\"parentOrGuardianUIN\":212124324784912,\"parentOrGuardianName\":[{\"language\":\"eng\",\"value\":\"salma\"}],\"proofOfAddress\":{\"format\":\"pdf\",\"type\":\"Ration Card\",\"value\":\"fileReferenceID\"},\"proofOfIdentity\":{\"format\":\"txt\",\"type\":\"Passport\",\"value\":\"fileReferenceID\"},\"proofOfRelationship\":{\"format\":\"pdf\",\"type\":\"Birth Certificate\",\"value\":\"fileReferenceID\"},\"proofOfDateOfBirth\":{\"format\":\"pdf\",\"type\":\"passport\",\"value\":\"fileReferenceID\"},\"individualBiometrics\":{\"format\":\"cbeff\",\"version\":1.0,\"value\":\"fileReferenceID\"},\"parentOrGuardianBiometrics\":{\"format\":\"cbeff\",\"version\":1.1,\"value\":\"fileReferenceID\"}}}";
		Mockito.when(mapper.writeValueAsString(Mockito.any())).thenReturn(identityString);
		regIdObjectMasterDataValidator.validateIdObject(
				new ObjectMapper().readValue(identityString.getBytes(StandardCharsets.UTF_8), Object.class),
				IdObjectValidatorSupportedOperations.NEW_REGISTRATION);
	}
	
}
