package io.mosip.kernel.jsonvalidator.test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

import io.mosip.kernel.core.idobjectvalidator.constant.IdObjectValidatorLocationMapping;
import io.mosip.kernel.core.idobjectvalidator.exception.FileIOException;
import io.mosip.kernel.core.idobjectvalidator.exception.IdObjectIOException;
import io.mosip.kernel.core.idobjectvalidator.exception.IdObjectSchemaIOException;
import io.mosip.kernel.core.idobjectvalidator.exception.IdObjectValidationProcessingException;
import io.mosip.kernel.core.idobjectvalidator.spi.IdObjectValidator;
import io.mosip.kernel.idobjectvalidator.impl.IdObjectCompositeValidator;
import io.mosip.kernel.idobjectvalidator.impl.IdObjectMasterDataValidator;
import io.mosip.kernel.idobjectvalidator.impl.IdObjectPatternValidator;
import io.mosip.kernel.idobjectvalidator.impl.IdObjectSchemaValidator;

/**
 * @author Manoj SP
 *
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@ImportAutoConfiguration(RefreshAutoConfiguration.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
@Import({ IdObjectMasterDataValidator.class, IdObjectPatternValidator.class, IdObjectSchemaValidator.class,
		IdObjectCompositeValidator.class, TestConfig.class })
@EnableConfigurationProperties
@ActiveProfiles("test")
public class IdObjectValidatorTest {
	
	@InjectMocks
	IdObjectMasterDataValidator masterDataValidator;
	
	@Autowired
	@Qualifier("composite")
	private IdObjectValidator validator;
	
	@Before
	public void setup() {
		ReflectionTestUtils.setField(masterDataValidator, "languageList", Lists.newArrayList("ara", "eng"));
		ReflectionTestUtils.setField(masterDataValidator, "genderMap", new LinkedMultiValueMap<>(Collections.singletonMap("eng", Collections.singletonList("MLE"))));
		ReflectionTestUtils.setField(masterDataValidator, "docCatMap", new LinkedMultiValueMap<>(Collections.singletonMap("eng", Collections.singletonList("POI"))));
		ReflectionTestUtils.setField(masterDataValidator, "docTypeMap", new LinkedMultiValueMap<>(Collections.singletonMap("POI", Collections.singletonList("Passport"))));
		LinkedMultiValueMap<String, String> hierarchyMapping = new LinkedMultiValueMap<String, String>();
		IdObjectValidatorLocationMapping.getAllMapping().entrySet().parallelStream().forEach(entry -> hierarchyMapping.add(entry.getKey(), entry.getValue()));
		ReflectionTestUtils.setField(masterDataValidator, "locationHierarchyDetails", hierarchyMapping);
		Map<String, LinkedMultiValueMap<String, String>> locationDetailMap = new HashMap<>();
		LinkedMultiValueMap<String, String> regionMapping = new LinkedMultiValueMap<String, String>();
		regionMapping.add("eng", "Rabat Sale Kenitra");
		locationDetailMap.put("Region", regionMapping);
		LinkedMultiValueMap<String, String> provinceMapping = new LinkedMultiValueMap<String, String>();
		provinceMapping.add("eng", "Kenitra");
		locationDetailMap.put("Province", provinceMapping);
		LinkedMultiValueMap<String, String> cityMapping = new LinkedMultiValueMap<String, String>();
		cityMapping.add("eng", "Kenitra");
		locationDetailMap.put("City", cityMapping);
		LinkedMultiValueMap<String, String> laaMapping = new LinkedMultiValueMap<String, String>();
		laaMapping.add("eng", "Mograne");
		locationDetailMap.put("Local Administrative Authority", laaMapping);
		LinkedMultiValueMap<String, String> pcMapping = new LinkedMultiValueMap<String, String>();
		pcMapping.add("eng", "10112");
		locationDetailMap.put("Postal Code", pcMapping);
		ReflectionTestUtils.setField(masterDataValidator, "locationDetails", locationDetailMap);
		ReflectionTestUtils.setField(masterDataValidator, "mapper", new ObjectMapper());
		ReflectionTestUtils.setField(validator, "masterDataValidator", masterDataValidator);
	}
	
	//@Test
	public void testValidData() throws JsonParseException, JsonMappingException, IdObjectValidationProcessingException, IdObjectIOException, IdObjectSchemaIOException, FileIOException, IOException {
		String identityString = "{\"identity\":{\"IDSchemaVersion\":1.0,\"UIN\":4920546943,\"fullName\":[{\"language\":\"ara\",\"value\":\"ابراهيم بن علي\"},{\"language\":\"eng\",\"value\":\"Ibrahim Ibn Ali\"}],\"dateOfBirth\":\"1955/04/15\",\"age\":45,\"gender\":[{\"language\":\"ara\",\"value\":\"MLE\"},{\"language\":\"eng\",\"value\":\"MLE\"}],\"addressLine1\":[{\"language\":\"ara\",\"value\":\"عنوان العينة سطر 1\"},{\"language\":\"eng\",\"value\":\"exemple d'adresse ligne 1\"}],\"addressLine2\":[{\"language\":\"ara\",\"value\":\"عنوان العينة سطر 2\"},{\"language\":\"eng\",\"value\":\"exemple d'adresse ligne 2\"}],\"addressLine3\":[{\"language\":\"ara\",\"value\":\"عنوان العينة سطر 2\"},{\"language\":\"eng\",\"value\":\"exemple d'adresse ligne 2\"}],\"region\":[{\"language\":\"ara\",\"value\":\"جهة الرباط سلا القنيطرة\"},{\"language\":\"eng\",\"value\":\"Rabat Sale Kenitra\"}],\"province\":[{\"language\":\"ara\",\"value\":\"القنيطرة\"},{\"language\":\"eng\",\"value\":\"Kenitra\"}],\"city\":[{\"language\":\"ara\",\"value\":\"القنيطرة\"},{\"language\":\"eng\",\"value\":\"Kenitra\"}],\"postalCode\":\"10112\",\"phone\":\"9876543210\",\"email\":\"abc@xyz.com\",\"CNIENumber\":\"6789545678909\",\"localAdministrativeAuthority\":[{\"language\":\"ara\",\"value\":\"بن منصور\"},{\"language\":\"eng\",\"value\":\"Mograne\"}],\"parentOrGuardianRID\":212124324784912,\"parentOrGuardianUIN\":212124324784912,\"parentOrGuardianName\":[{\"language\":\"ara\",\"value\":\"سلمى\"},{\"language\":\"eng\",\"value\":\"salma\"}],\"proofOfAddress\":{\"format\":\"pdf\",\"type\":\"Ration Card\",\"value\":\"fileReferenceID\"},\"proofOfIdentity\":{\"format\":\"txt\",\"type\":\"Passport\",\"value\":\"fileReferenceID\"},\"proofOfRelationship\":{\"format\":\"pdf\",\"type\":\"Birth Certificate\",\"value\":\"fileReferenceID\"},\"proofOfDateOfBirth\":{\"format\":\"pdf\",\"type\":\"passport\",\"value\":\"fileReferenceID\"},\"individualBiometrics\":{\"format\":\"cbeff\",\"version\":1.0,\"value\":\"fileReferenceID\"},\"parentOrGuardianBiometrics\":{\"format\":\"cbeff\",\"version\":1.1,\"value\":\"fileReferenceID\"}}}";
		validator.validateIdObject(new ObjectMapper().readValue(identityString.getBytes(StandardCharsets.UTF_8), Object.class));
	}
	
	@Test(expected = IdObjectValidationProcessingException.class)
	public void testMasterDataError() throws IdObjectValidationProcessingException, IdObjectIOException, IdObjectSchemaIOException, FileIOException, JsonParseException, JsonMappingException, IOException {
		String identityString = "{\"identity\":{\"IDSchemaVersion\":1.0,\"UIN\":4920546943,\"fullName\":[{\"language\":\"ara\",\"value\":\"ابراهيم بن علي\"},{\"language\":\"eng\",\"value\":\"Ibrahim Ibn Ali\"}],\"dateOfBirth\":\"1955/04/15\",\"age\":45,\"gender\":[{\"language\":\"ara\",\"value\":\"الذكر\"},{\"language\":\"eng\",\"value\":\"male\"}],\"addressLine1\":[{\"language\":\"ara\",\"value\":\"عنوان العينة سطر 1\"},{\"language\":\"eng\",\"value\":\"exemple d'adresse ligne 1\"}],\"addressLine2\":[{\"language\":\"ara\",\"value\":\"عنوان العينة سطر 2\"},{\"language\":\"eng\",\"value\":\"exemple d'adresse ligne 2\"}],\"addressLine3\":[{\"language\":\"ara\",\"value\":\"عنوان العينة سطر 2\"},{\"language\":\"eng\",\"value\":\"exemple d'adresse ligne 2\"}],\"region\":[{\"language\":\"ara\",\"value\":\"طنجة - تطوان - الحسيمة\"},{\"language\":\"eng\",\"value\":\"Tanger-Tétouan-Al Hoceima\"}],\"province\":[{\"language\":\"ara\",\"value\":\"فاس-مكناس\"},{\"language\":\"eng\",\"value\":\"Fès-Meknès\"}],\"city\":[{\"language\":\"ara\",\"value\":\"الدار البيضاء\"},{\"language\":\"eng\",\"value\":\"Casablanca\"}],\"postalCode\":\"570004\",\"phone\":\"9876543210\",\"email\":\"abc@xyz.com\",\"CNIENumber\":\"6789545678909\",\"localAdministrativeAuthority\":[{\"language\":\"ara\",\"value\":\"سلمى\"},{\"language\":\"eng\",\"value\":\"salma\"}],\"parentOrGuardianRID\":212124324784912,\"parentOrGuardianUIN\":212124324784912,\"parentOrGuardianName\":[{\"language\":\"ara\",\"value\":\"سلمى\"},{\"language\":\"eng\",\"value\":\"salma\"}],\"proofOfAddress\":{\"format\":\"pdf\",\"type\":\"drivingLicense\",\"value\":\"fileReferenceID\"},\"proofOfIdentity\":{\"format\":\"txt\",\"type\":\"passport\",\"value\":\"fileReferenceID\"},\"proofOfRelationship\":{\"format\":\"pdf\",\"type\":\"passport\",\"value\":\"fileReferenceID\"},\"proofOfDateOfBirth\":{\"format\":\"pdf\",\"type\":\"passport\",\"value\":\"fileReferenceID\"},\"individualBiometrics\":{\"format\":\"cbeff\",\"version\":1.0,\"value\":\"fileReferenceID\"},\"parentOrGuardianBiometrics\":{\"format\":\"cbeff\",\"version\":1.1,\"value\":\"fileReferenceID\"}}}";
		validator.validateIdObject(new ObjectMapper().readValue(identityString.getBytes(StandardCharsets.UTF_8), Object.class));
	}
	
	@Test(expected = IdObjectValidationProcessingException.class)
	public void testPatternFailure() throws IdObjectValidationProcessingException, IdObjectIOException, IdObjectSchemaIOException, FileIOException, JsonParseException, JsonMappingException, IOException {
		String identityString = "{\"identity\":{\"IDSchemaVersion\":1.0,\"UIN\":4920546943,\"fullName\":[{\"language\":\"ara\",\"value\":\"ابراهيم بن علي\"},{\"language\":\"eng\",\"value\":\"Ibrahim Ibn Ali\"}],\"dateOfBirth\":\"1955/04\",\"age\":45,\"gender\":[{\"language\":\"ara\",\"value\":\"الذكر\"},{\"language\":\"eng\",\"value\":\"male\"}],\"addressLine1\":[{\"language\":\"ara\",\"value\":\"عنوان العينة سطر 1\"},{\"language\":\"eng\",\"value\":\"exemple d'adresse ligne 1\"}],\"addressLine2\":[{\"language\":\"ara\",\"value\":\"عنوان العينة سطر 2\"},{\"language\":\"eng\",\"value\":\"exemple d'adresse ligne 2\"}],\"addressLine3\":[{\"language\":\"ara\",\"value\":\"عنوان العينة سطر 2\"},{\"language\":\"eng\",\"value\":\"exemple d'adresse ligne 2\"}],\"region\":[{\"language\":\"ara\",\"value\":\"طنجة - تطوان - الحسيمة\"},{\"language\":\"eng\",\"value\":\"Tanger-Tétouan-Al Hoceima\"}],\"province\":[{\"language\":\"ara\",\"value\":\"فاس-مكناس\"},{\"language\":\"eng\",\"value\":\"Fès-Meknès\"}],\"city\":[{\"language\":\"ara\",\"value\":\"الدار البيضاء\"},{\"language\":\"eng\",\"value\":\"Casablanca\"}],\"postalCode\":\"570004\",\"phone\":\"9876543210\",\"email\":\"abc@xyz.com\",\"CNIENumber\":\"abcd\",\"localAdministrativeAuthority\":[{\"language\":\"ara\",\"value\":\"سلمى\"},{\"language\":\"eng\",\"value\":\"salma\"}],\"parentOrGuardianRID\":212124324784912,\"parentOrGuardianUIN\":212124324784912,\"parentOrGuardianName\":[{\"language\":\"ara\",\"value\":\"سلمى\"},{\"language\":\"eng\",\"value\":\"salma\"}],\"proofOfAddress\":{\"format\":\"pdf\",\"type\":\"drivingLicense\",\"value\":\"fileReferenceID\"},\"proofOfIdentity\":{\"format\":\"txt\",\"type\":\"passport\",\"value\":\"fileReferenceID\"},\"proofOfRelationship\":{\"format\":\"pdf\",\"type\":\"passport\",\"value\":\"fileReferenceID\"},\"proofOfDateOfBirth\":{\"format\":\"pdf\",\"type\":\"passport\",\"value\":\"fileReferenceID\"},\"individualBiometrics\":{\"format\":\"cbeff\",\"version\":1.0,\"value\":\"fileReferenceID\"},\"parentOrGuardianBiometrics\":{\"format\":\"cbeff\",\"version\":1.1,\"value\":\"fileReferenceID\"}}}";
		validator.validateIdObject(new ObjectMapper().readValue(identityString.getBytes(StandardCharsets.UTF_8), Object.class));
	}
	
}
