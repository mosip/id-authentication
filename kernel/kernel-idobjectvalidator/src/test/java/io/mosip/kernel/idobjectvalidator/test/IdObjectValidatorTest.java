package io.mosip.kernel.idobjectvalidator.test;

import static io.mosip.kernel.core.idobjectvalidator.constant.IdObjectValidatorErrorConstant.ID_OBJECT_PARSING_FAILED;
import static io.mosip.kernel.core.idobjectvalidator.constant.IdObjectValidatorErrorConstant.INVALID_INPUT_PARAMETER;
import static io.mosip.kernel.core.idobjectvalidator.constant.IdObjectValidatorErrorConstant.MANDATORY_FIELDS_NOT_FOUND;
import static io.mosip.kernel.core.idobjectvalidator.constant.IdObjectValidatorErrorConstant.MISSING_INPUT_PARAMETER;
import static io.mosip.kernel.idobjectvalidator.constant.IdObjectValidatorConstant.APPLICATION_ID;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import io.mosip.kernel.core.idobjectvalidator.constant.IdObjectValidatorSupportedOperations;
import io.mosip.kernel.core.idobjectvalidator.exception.IdObjectIOException;
import io.mosip.kernel.core.idobjectvalidator.exception.IdObjectValidationFailedException;
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
@SpringBootTest
@Import({ IdObjectMasterDataValidator.class, IdObjectPatternValidator.class, IdObjectSchemaValidator.class,
		IdObjectCompositeValidator.class, TestConfig.class })
@EnableConfigurationProperties
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ActiveProfiles("test")
public class IdObjectValidatorTest {

	@Autowired
	Environment env;

	@Autowired
	IdObjectSchemaValidator schemaValidator;

	@Autowired
	IdObjectPatternValidator patternValidator;

	@Autowired
	IdObjectMasterDataValidator masterDataValidator;

	@Autowired
	private IdObjectValidator validator;

	@Test
	public void testValidData() throws JsonParseException, JsonMappingException, IdObjectValidationFailedException,
			IdObjectIOException, IOException {
		String identityString = "{\"identity\":{\"IDSchemaVersion\":1.0,\"UIN\":4920546943,\"fullName\":[{\"language\":\"eng\",\"value\":\"Ibrahim Ibn Ali\"}],\"dateOfBirth\":\"1955/04/15\",\"age\":45,\"gender\":[{\"language\":\"eng\",\"value\":\"MLE\"}],\"addressLine1\":[{\"language\":\"eng\",\"value\":\"exemple d'adresse ligne 1\"}],\"addressLine2\":[{\"language\":\"eng\",\"value\":\"exemple d'adresse ligne 2\"}],\"addressLine3\":[{\"language\":\"eng\",\"value\":\"exemple d'adresse ligne 2\"}],\"region\":[{\"language\":\"eng\",\"value\":\"Rabat Sale Kenitra\"}],\"province\":[{\"language\":\"eng\",\"value\":\"Kenitra\"}],\"city\":[{\"language\":\"eng\",\"value\":\"Kenitra\"}],\"postalCode\":\"10112\",\"phone\":\"9876543210\",\"email\":\"abc@xyz.com\",\"CNIENumber\":\"6789545678909\",\"localAdministrativeAuthority\":[{\"language\":\"eng\",\"value\":\"Mograne\"}],\"parentOrGuardianRID\":212124324784912,\"parentOrGuardianUIN\":212124324784912,\"parentOrGuardianName\":[{\"language\":\"eng\",\"value\":\"salma\"}],\"proofOfAddress\":{\"format\":\"pdf\",\"type\":\"Ration Card\",\"value\":\"fileReferenceID\"},\"proofOfIdentity\":{\"format\":\"txt\",\"type\":\"Passport\",\"value\":\"fileReferenceID\"},\"proofOfRelationship\":{\"format\":\"pdf\",\"type\":\"Birth Certificate\",\"value\":\"fileReferenceID\"},\"proofOfDateOfBirth\":{\"format\":\"pdf\",\"type\":\"passport\",\"value\":\"fileReferenceID\"},\"individualBiometrics\":{\"format\":\"cbeff\",\"version\":1.0,\"value\":\"fileReferenceID\"},\"parentOrGuardianBiometrics\":{\"format\":\"cbeff\",\"version\":1.1,\"value\":\"fileReferenceID\"}}}";
		validator.validateIdObject(
				new ObjectMapper().readValue(identityString.getBytes(StandardCharsets.UTF_8), Object.class),
				IdObjectValidatorSupportedOperations.NEW_REGISTRATION);
	}

	@Test
	public void testSchemaValidationFailedError() throws IdObjectValidationFailedException, IdObjectIOException,
			JsonParseException, JsonMappingException, IOException {
		try {
			String identityString = "{\"identity\":{\"IDSchemaVersion\":1.0,\"UIN1\":4920546943,\"fullName\":[{\"language\":\"ara\",\"value\":\"ابراهيم بن علي\"},{\"language\":\"eng\",\"value\":\"Ibrahim Ibn Ali\"}],\"dateOfBirth\":\"2019/02/31\",\"age\":45,\"gender\":[{\"language\":\"ara\",\"value\":\"الذكر\"},{\"language\":\"eng\",\"value\":\"male\"}],\"addressLine1\":[{\"language\":\"ara\",\"value\":\"عنوان العينة سطر 1\"},{\"language\":\"eng\",\"value\":\"exemple d'adresse ligne 1\"}],\"addressLine2\":[{\"language\":\"ara\",\"value\":\"عنوان العينة سطر 2\"},{\"language\":\"eng\",\"value\":\"exemple d'adresse ligne 2\"}],\"addressLine3\":[{\"language\":\"ara\",\"value\":\"عنوان العينة سطر 2\"},{\"language\":\"eng\",\"value\":\"exemple d'adresse ligne 2\"}],\"region\":[{\"language\":\"ara\",\"value\":\"طنجة - تطوان - الحسيمة\"},{\"language\":\"eng\",\"value\":\"T\"}],\"province\":[{\"language\":\"ara\",\"value\":\"فاس-مكناس\"},{\"language\":\"eng\",\"value\":\"Fès-Meknès\"}],\"city\":[{\"language\":\"ara\",\"value\":\"الدار البيضاء\"},{\"language\":\"eng\",\"value\":\"Casablanca\"}],\"postalCode\":\"570004\",\"phone\":\"9876543210\",\"email\":\"abc@xyz.com\",\"CNIENumber\":\"6789545678909\",\"localAdministrativeAuthority\":[{\"language\":\"ara\",\"value\":\"سلمى\"},{\"language\":\"eng\",\"value\":\"salma\"}],\"parentOrGuardianRID\":212124324784912,\"parentOrGuardianUIN\":212124324784912,\"parentOrGuardianName\":[{\"language\":\"ara\",\"value\":\"سلمى\"},{\"language\":\"eng\",\"value\":\"salma\"}],\"proofOfAddress\":{\"format\":\"pdf\",\"type\":\"drivingLicense\",\"value\":\"fileReferenceID\"},\"proofOfIdentity\":{\"format\":\"txt\",\"type\":\"passport\",\"value\":\"fileReferenceID\"},\"proofOfRelationship\":{\"format\":\"pdf\",\"type\":\"passport\",\"value\":\"fileReferenceID\"},\"proofOfDateOfBirth\":{\"format\":\"pdf\",\"type\":\"passport\",\"value\":\"fileReferenceID\"},\"individualBiometrics\":{\"format\":\"cbeff\",\"version\":1.0,\"value\":\"fileReferenceID\"},\"parentOrGuardianBiometrics\":{\"format\":\"cbeff\",\"version\":1.1,\"value\":\"fileReferenceID\"}}}";
			schemaValidator.validateIdObject(
					new ObjectMapper().readValue(identityString.getBytes(StandardCharsets.UTF_8), Object.class),
					IdObjectValidatorSupportedOperations.NEW_REGISTRATION);
		} catch (IdObjectValidationFailedException e) {
			assertTrue(e.getCodes().contains(MISSING_INPUT_PARAMETER.getErrorCode()));
			assertTrue(e.getErrorTexts().contains(String.format(MISSING_INPUT_PARAMETER.getMessage(), "identity/UIN")));
			assertTrue(
					e.getErrorTexts().contains(String.format(INVALID_INPUT_PARAMETER.getMessage(), "identity/UIN1")));
		}
	}

	@Test
	public void testSchemaNullOperationError() throws IdObjectValidationFailedException, IdObjectIOException,
			JsonParseException, JsonMappingException, IOException {
		try {
			String identityString = "{\"identity\":{\"IDSchemaVersion\":1.0,\"UIN1\":4920546943,\"fullName\":[{\"language\":\"ara\",\"value\":\"ابراهيم بن علي\"},{\"language\":\"eng\",\"value\":\"Ibrahim Ibn Ali\"}],\"dateOfBirth\":\"1955/04/15\",\"age\":45,\"gender\":[{\"language\":\"ara\",\"value\":\"الذكر\"},{\"language\":\"eng\",\"value\":\"male\"}],\"addressLine1\":[{\"language\":\"ara\",\"value\":\"عنوان العينة سطر 1\"},{\"language\":\"eng\",\"value\":\"exemple d'adresse ligne 1\"}],\"addressLine2\":[{\"language\":\"ara\",\"value\":\"عنوان العينة سطر 2\"},{\"language\":\"eng\",\"value\":\"exemple d'adresse ligne 2\"}],\"addressLine3\":[{\"language\":\"ara\",\"value\":\"عنوان العينة سطر 2\"},{\"language\":\"eng\",\"value\":\"exemple d'adresse ligne 2\"}],\"region\":[{\"language\":\"ara\",\"value\":\"طنجة - تطوان - الحسيمة\"},{\"language\":\"eng\",\"value\":\"T\"}],\"province\":[{\"language\":\"ara\",\"value\":\"فاس-مكناس\"},{\"language\":\"eng\",\"value\":\"Fès-Meknès\"}],\"city\":[{\"language\":\"ara\",\"value\":\"الدار البيضاء\"},{\"language\":\"eng\",\"value\":\"Casablanca\"}],\"postalCode\":\"570004\",\"phone\":\"9876543210\",\"email\":\"abc@xyz.com\",\"CNIENumber\":\"6789545678909\",\"localAdministrativeAuthority\":[{\"language\":\"ara\",\"value\":\"سلمى\"},{\"language\":\"eng\",\"value\":\"salma\"}],\"parentOrGuardianRID\":212124324784912,\"parentOrGuardianUIN\":212124324784912,\"parentOrGuardianName\":[{\"language\":\"ara\",\"value\":\"سلمى\"},{\"language\":\"eng\",\"value\":\"salma\"}],\"proofOfAddress\":{\"format\":\"pdf\",\"type\":\"drivingLicense\",\"value\":\"fileReferenceID\"},\"proofOfIdentity\":{\"format\":\"txt\",\"type\":\"passport\",\"value\":\"fileReferenceID\"},\"proofOfRelationship\":{\"format\":\"pdf\",\"type\":\"passport\",\"value\":\"fileReferenceID\"},\"proofOfDateOfBirth\":{\"format\":\"pdf\",\"type\":\"passport\",\"value\":\"fileReferenceID\"},\"individualBiometrics\":{\"format\":\"cbeff\",\"version\":1.0,\"value\":\"fileReferenceID\"},\"parentOrGuardianBiometrics\":{\"format\":\"cbeff\",\"version\":1.1,\"value\":\"fileReferenceID\"}}}";
			schemaValidator.validateIdObject(
					new ObjectMapper().readValue(identityString.getBytes(StandardCharsets.UTF_8), Object.class), null);
		} catch (IdObjectIOException e) {
			assertTrue(e.getCodes().contains(MISSING_INPUT_PARAMETER.getErrorCode()));
			assertTrue(e.getErrorTexts().contains(String.format(MISSING_INPUT_PARAMETER.getMessage(), "operation")));
		}
	}

	@Test
	public void vTestSchemaNullAppIdError() throws IdObjectValidationFailedException, IdObjectIOException,
			JsonParseException, JsonMappingException, IOException {
		try {
			ReflectionTestUtils.setField(schemaValidator, "env", new MockEnvironment());
			String identityString = "{\"identity\":{\"IDSchemaVersion\":1.0,\"UIN1\":4920546943,\"fullName\":[{\"language\":\"ara\",\"value\":\"ابراهيم بن علي\"},{\"language\":\"eng\",\"value\":\"Ibrahim Ibn Ali\"}],\"dateOfBirth\":\"1955/04/15\",\"age\":45,\"gender\":[{\"language\":\"ara\",\"value\":\"الذكر\"},{\"language\":\"eng\",\"value\":\"male\"}],\"addressLine1\":[{\"language\":\"ara\",\"value\":\"عنوان العينة سطر 1\"},{\"language\":\"eng\",\"value\":\"exemple d'adresse ligne 1\"}],\"addressLine2\":[{\"language\":\"ara\",\"value\":\"عنوان العينة سطر 2\"},{\"language\":\"eng\",\"value\":\"exemple d'adresse ligne 2\"}],\"addressLine3\":[{\"language\":\"ara\",\"value\":\"عنوان العينة سطر 2\"},{\"language\":\"eng\",\"value\":\"exemple d'adresse ligne 2\"}],\"region\":[{\"language\":\"ara\",\"value\":\"طنجة - تطوان - الحسيمة\"},{\"language\":\"eng\",\"value\":\"T\"}],\"province\":[{\"language\":\"ara\",\"value\":\"فاس-مكناس\"},{\"language\":\"eng\",\"value\":\"Fès-Meknès\"}],\"city\":[{\"language\":\"ara\",\"value\":\"الدار البيضاء\"},{\"language\":\"eng\",\"value\":\"Casablanca\"}],\"postalCode\":\"570004\",\"phone\":\"9876543210\",\"email\":\"abc@xyz.com\",\"CNIENumber\":\"6789545678909\",\"localAdministrativeAuthority\":[{\"language\":\"ara\",\"value\":\"سلمى\"},{\"language\":\"eng\",\"value\":\"salma\"}],\"parentOrGuardianRID\":212124324784912,\"parentOrGuardianUIN\":212124324784912,\"parentOrGuardianName\":[{\"language\":\"ara\",\"value\":\"سلمى\"},{\"language\":\"eng\",\"value\":\"salma\"}],\"proofOfAddress\":{\"format\":\"pdf\",\"type\":\"drivingLicense\",\"value\":\"fileReferenceID\"},\"proofOfIdentity\":{\"format\":\"txt\",\"type\":\"passport\",\"value\":\"fileReferenceID\"},\"proofOfRelationship\":{\"format\":\"pdf\",\"type\":\"passport\",\"value\":\"fileReferenceID\"},\"proofOfDateOfBirth\":{\"format\":\"pdf\",\"type\":\"passport\",\"value\":\"fileReferenceID\"},\"individualBiometrics\":{\"format\":\"cbeff\",\"version\":1.0,\"value\":\"fileReferenceID\"},\"parentOrGuardianBiometrics\":{\"format\":\"cbeff\",\"version\":1.1,\"value\":\"fileReferenceID\"}}}";
			schemaValidator.validateIdObject(
					new ObjectMapper().readValue(identityString.getBytes(StandardCharsets.UTF_8), Object.class),
					IdObjectValidatorSupportedOperations.NEW_REGISTRATION);
		} catch (IdObjectIOException e) {
			assertTrue(e.getCodes().contains(MISSING_INPUT_PARAMETER.getErrorCode()));
			assertTrue(e.getErrorTexts()
					.contains(String.format(MISSING_INPUT_PARAMETER.getMessage(), APPLICATION_ID.getValue())));
		}
	}

	@Test
	@Ignore
	public void wTestSchemaNullFieldsError() throws IdObjectValidationFailedException, IdObjectIOException,
			JsonParseException, JsonMappingException, IOException {
		try {
			MockEnvironment mockEnv = (MockEnvironment) ReflectionTestUtils.getField(schemaValidator, "env");
			mockEnv.setProperty(APPLICATION_ID.getValue(), "");
			ReflectionTestUtils.setField(schemaValidator, "env", mockEnv);
			String identityString = "{\"identity\":{\"IDSchemaVersion\":1.0,\"UIN1\":4920546943,\"fullName\":[{\"language\":\"ara\",\"value\":\"ابراهيم بن علي\"},{\"language\":\"eng\",\"value\":\"Ibrahim Ibn Ali\"}],\"dateOfBirth\":\"1955/04/15\",\"age\":45,\"gender\":[{\"language\":\"ara\",\"value\":\"الذكر\"},{\"language\":\"eng\",\"value\":\"male\"}],\"addressLine1\":[{\"language\":\"ara\",\"value\":\"عنوان العينة سطر 1\"},{\"language\":\"eng\",\"value\":\"exemple d'adresse ligne 1\"}],\"addressLine2\":[{\"language\":\"ara\",\"value\":\"عنوان العينة سطر 2\"},{\"language\":\"eng\",\"value\":\"exemple d'adresse ligne 2\"}],\"addressLine3\":[{\"language\":\"ara\",\"value\":\"عنوان العينة سطر 2\"},{\"language\":\"eng\",\"value\":\"exemple d'adresse ligne 2\"}],\"region\":[{\"language\":\"ara\",\"value\":\"طنجة - تطوان - الحسيمة\"},{\"language\":\"eng\",\"value\":\"T\"}],\"province\":[{\"language\":\"ara\",\"value\":\"فاس-مكناس\"},{\"language\":\"eng\",\"value\":\"Fès-Meknès\"}],\"city\":[{\"language\":\"ara\",\"value\":\"الدار البيضاء\"},{\"language\":\"eng\",\"value\":\"Casablanca\"}],\"postalCode\":\"570004\",\"phone\":\"9876543210\",\"email\":\"abc@xyz.com\",\"CNIENumber\":\"6789545678909\",\"localAdministrativeAuthority\":[{\"language\":\"ara\",\"value\":\"سلمى\"},{\"language\":\"eng\",\"value\":\"salma\"}],\"parentOrGuardianRID\":212124324784912,\"parentOrGuardianUIN\":212124324784912,\"parentOrGuardianName\":[{\"language\":\"ara\",\"value\":\"سلمى\"},{\"language\":\"eng\",\"value\":\"salma\"}],\"proofOfAddress\":{\"format\":\"pdf\",\"type\":\"drivingLicense\",\"value\":\"fileReferenceID\"},\"proofOfIdentity\":{\"format\":\"txt\",\"type\":\"passport\",\"value\":\"fileReferenceID\"},\"proofOfRelationship\":{\"format\":\"pdf\",\"type\":\"passport\",\"value\":\"fileReferenceID\"},\"proofOfDateOfBirth\":{\"format\":\"pdf\",\"type\":\"passport\",\"value\":\"fileReferenceID\"},\"individualBiometrics\":{\"format\":\"cbeff\",\"version\":1.0,\"value\":\"fileReferenceID\"},\"parentOrGuardianBiometrics\":{\"format\":\"cbeff\",\"version\":1.1,\"value\":\"fileReferenceID\"}}}";
			schemaValidator.validateIdObject(
					new ObjectMapper().readValue(identityString.getBytes(StandardCharsets.UTF_8), Object.class),
					IdObjectValidatorSupportedOperations.NEW_REGISTRATION);
		} catch (IdObjectIOException e) {
			assertTrue(e.getCodes().contains(MISSING_INPUT_PARAMETER.getErrorCode()));
			assertTrue(e.getErrorText().contains(String.format(MANDATORY_FIELDS_NOT_FOUND.getMessage(),
					IdObjectValidatorSupportedOperations.NEW_REGISTRATION.getOperation())));
		}
	}

	@Test
	@Ignore
	public void testMasterDataError() throws IdObjectValidationFailedException, IdObjectIOException, JsonParseException,
			JsonMappingException, IOException {
		try {
			String identityString = "{\"identity\":{\"IDSchemaVersion\":1.0,\"UIN\":4920546943,\"fullName\":[{\"language\":\"ara\",\"value\":\"ابراهيم بن علي\"},{\"language\":\"eng\",\"value\":\"Ibrahim Ibn Ali\"}],\"dateOfBirth\":\"1955/04/15\",\"age\":45,\"gender\":[{\"language\":\"ara\",\"value\":\"الذكر\"},{\"language\":\"eng\",\"value\":\"male\"}],\"addressLine1\":[{\"language\":\"ara\",\"value\":\"عنوان العينة سطر 1\"},{\"language\":\"eng\",\"value\":\"exemple d'adresse ligne 1\"}],\"addressLine2\":[{\"language\":\"ara\",\"value\":\"عنوان العينة سطر 2\"},{\"language\":\"eng\",\"value\":\"exemple d'adresse ligne 2\"}],\"addressLine3\":[{\"language\":\"ara\",\"value\":\"عنوان العينة سطر 2\"},{\"language\":\"eng\",\"value\":\"exemple d'adresse ligne 2\"}],\"region\":[{\"language\":\"ara\",\"value\":\"طنجة - تطوان - الحسيمة\"},{\"language\":\"eng\",\"value\":\"T\"}],\"province\":[{\"language\":\"ara\",\"value\":\"فاس-مكناس\"},{\"language\":\"eng\",\"value\":\"Fès-Meknès\"}],\"city\":[{\"language\":\"ara\",\"value\":\"الدار البيضاء\"},{\"language\":\"eng\",\"value\":\"Casablanca\"}],\"postalCode\":\"570004\",\"phone\":\"9876543210\",\"email\":\"abc@xyz.com\",\"CNIENumber\":\"6789545678909\",\"localAdministrativeAuthority\":[{\"language\":\"ara\",\"value\":\"سلمى\"},{\"language\":\"eng\",\"value\":\"salma\"}],\"parentOrGuardianRID\":212124324784912,\"parentOrGuardianUIN\":212124324784912,\"parentOrGuardianName\":[{\"language\":\"ara\",\"value\":\"سلمى\"},{\"language\":\"eng\",\"value\":\"salma\"}],\"proofOfAddress\":{\"format\":\"pdf\",\"type\":\"drivingLicense\",\"value\":\"fileReferenceID\"},\"proofOfIdentity\":{\"format\":\"txt\",\"type\":\"passport\",\"value\":\"fileReferenceID\"},\"proofOfRelationship\":{\"format\":\"pdf\",\"type\":\"passport\",\"value\":\"fileReferenceID\"},\"proofOfDateOfBirth\":{\"format\":\"pdf\",\"type\":\"passport\",\"value\":\"fileReferenceID\"},\"individualBiometrics\":{\"format\":\"cbeff\",\"version\":1.0,\"value\":\"fileReferenceID\"},\"parentOrGuardianBiometrics\":{\"format\":\"cbeff\",\"version\":1.1,\"value\":\"fileReferenceID\"}}}";
			validator.validateIdObject(
					new ObjectMapper().readValue(identityString.getBytes(StandardCharsets.UTF_8), Object.class),
					IdObjectValidatorSupportedOperations.NEW_REGISTRATION);
		} catch (IdObjectValidationFailedException e) {
			assertTrue(Optional.ofNullable(e.getCodes())
					.map(codes -> codes.contains(INVALID_INPUT_PARAMETER.getErrorCode())).get());
			assertTrue(e.getErrorTexts()
					.contains(String.format(INVALID_INPUT_PARAMETER.getMessage(), "identity/gender/0/language")));
			assertTrue(e.getErrorTexts()
					.contains(String.format(INVALID_INPUT_PARAMETER.getMessage(), "identity/addressLine3/0/language")));
		}
	}

	@Test
	public void testPatternFailure() throws IdObjectValidationFailedException, IdObjectIOException, JsonParseException,
			JsonMappingException, IOException {
		try {
			String identityString = "{\"identity\":{\"IDSchemaVersion\":1.0,\"UIN\":4920546943,\"fullName\":[{\"language\":\"ara\",\"value\":\"ابراهيم بن علي\"},{\"language\":\"eng\",\"value\":\"Ibrahim Ibn Ali\"}],\"dateOfBirth\":\"1955/04\",\"age\":45,\"gender\":[{\"language\":\"ara\",\"value\":\"الذكر\"},{\"language\":\"eng\",\"value\":\"male\"}],\"addressLine1\":[{\"language\":\"ara\",\"value\":\"عنوان العينة سطر 1\"},{\"language\":\"eng\",\"value\":\"exemple d'adresse ligne 1\"}],\"addressLine2\":[{\"language\":\"ara\",\"value\":\"عنوان العينة سطر 2\"},{\"language\":\"eng\",\"value\":\"exemple d'adresse ligne 2\"}],\"addressLine3\":[{\"language\":\"ara\",\"value\":\"عنوان العينة سطر 2\"},{\"language\":\"eng\",\"value\":\"exemple d'adresse ligne 2\"}],\"region\":[{\"language\":\"ara\",\"value\":\"طنجة - تطوان - الحسيمة\"},{\"language\":\"eng\",\"value\":\"Tanger-Tétouan-Al Hoceima\"}],\"province\":[{\"language\":\"ara\",\"value\":\"فاس-مكناس\"},{\"language\":\"eng\",\"value\":\"Fès-Meknès\"}],\"city\":[{\"language\":\"ara\",\"value\":\"الدار البيضاء\"},{\"language\":\"eng\",\"value\":\"Casablanca\"}],\"postalCode\":\"570004\",\"phone\":\"9876543210\",\"email\":\"abc@xyz.com\",\"CNIENumber\":\"abcd\",\"localAdministrativeAuthority\":[{\"language\":\"ara\",\"value\":\"سلمى\"},{\"language\":\"eng\",\"value\":\"salma\"}],\"parentOrGuardianRID\":212124324784912,\"parentOrGuardianUIN\":212124324784912,\"parentOrGuardianName\":[{\"language\":\"ara\",\"value\":\"سلمى\"},{\"language\":\"eng\",\"value\":\"salma\"}],\"proofOfAddress\":{\"format\":\"pdf\",\"type\":\"drivingLicense\",\"value\":\"fileReferenceID\"},\"proofOfIdentity\":{\"format\":\"txt\",\"type\":\"passport\",\"value\":\"fileReferenceID\"},\"proofOfRelationship\":{\"format\":\"pdf\",\"type\":\"passport\",\"value\":\"fileReferenceID\"},\"proofOfDateOfBirth\":{\"format\":\"pdf\",\"type\":\"passport\",\"value\":\"fileReferenceID\"},\"individualBiometrics\":{\"format\":\"cbeff\",\"version\":1.0,\"value\":\"fileReferenceID\"},\"parentOrGuardianBiometrics\":{\"format\":\"cbeff\",\"version\":1.1,\"value\":\"fileReferenceID\"}}}";
			validator.validateIdObject(
					new ObjectMapper().readValue(identityString.getBytes(StandardCharsets.UTF_8), Object.class),
					IdObjectValidatorSupportedOperations.NEW_REGISTRATION);
		} catch (IdObjectValidationFailedException e) {
			assertTrue(e.getCodes().contains(INVALID_INPUT_PARAMETER.getErrorCode()));
			assertTrue(e.getErrorTexts()
					.contains(String.format(INVALID_INPUT_PARAMETER.getMessage(), "identity/dateOfBirth")));
		}
	}

	@Test
	public void xtestSchemaValidatorParsingFailure() throws IdObjectValidationFailedException, IdObjectIOException,
			JsonParseException, JsonMappingException, IOException {
		try {
			ObjectMapper mockMapper = mock(ObjectMapper.class);
			when(mockMapper.writeValueAsString(Mockito.any()))
					.thenThrow(new InvalidFormatException(null, "", null, null));
			ReflectionTestUtils.setField(schemaValidator, "mapper", mockMapper);
			schemaValidator.validateIdObject(null, IdObjectValidatorSupportedOperations.NEW_REGISTRATION);
		} catch (IdObjectIOException e) {
			assertTrue(e.getErrorCode().equals(ID_OBJECT_PARSING_FAILED.getErrorCode()));
			assertTrue(e.getErrorText().equals(ID_OBJECT_PARSING_FAILED.getMessage()));
		}
	}

	@Test
	public void ytestPatternValidatorParsingFailure() throws IdObjectValidationFailedException, IdObjectIOException,
			JsonParseException, JsonMappingException, IOException {
		try {
			ObjectMapper mockMapper = mock(ObjectMapper.class);
			when(mockMapper.writeValueAsString(Mockito.any()))
					.thenThrow(new InvalidFormatException(null, "", null, null));
			ReflectionTestUtils.setField(patternValidator, "mapper", mockMapper);
			patternValidator.validateIdObject(null, IdObjectValidatorSupportedOperations.NEW_REGISTRATION);
		} catch (IdObjectIOException e) {
			assertTrue(e.getErrorCode().equals(ID_OBJECT_PARSING_FAILED.getErrorCode()));
			assertTrue(e.getErrorText().equals(ID_OBJECT_PARSING_FAILED.getMessage()));
		}
	}

	@Test
	public void ztestMasterdataValidaotParsingFailure() throws IdObjectValidationFailedException, IdObjectIOException,
			JsonParseException, JsonMappingException, IOException {
		try {
			ObjectMapper mockMapper = mock(ObjectMapper.class);
			when(mockMapper.writeValueAsString(Mockito.any()))
					.thenThrow(new InvalidFormatException(null, "", null, null));
			ReflectionTestUtils.setField(masterDataValidator, "mapper", mockMapper);
			masterDataValidator.validateIdObject(null, IdObjectValidatorSupportedOperations.NEW_REGISTRATION);
		} catch (IdObjectIOException e) {
			assertTrue(e.getErrorCode().equals(ID_OBJECT_PARSING_FAILED.getErrorCode()));
			assertTrue(e.getErrorText().equals(ID_OBJECT_PARSING_FAILED.getMessage()));
		}
	}

}
