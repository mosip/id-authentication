package io.mosip.registration.processor.stages.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.PacketDecryptionFailureException;
import io.mosip.registration.processor.core.spi.filesystem.manager.PacketManager;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.packet.storage.utils.Utilities;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Utilities.class, JsonUtil.class, IOUtils.class })
public class MandatoryValidationTest {

	/** The adapter. */
	@Mock
	private PacketManager adapter;

	@Mock
	private Utilities utility;

	@Mock
	private InputStream inputStream;

	/** The registration status dto. */
	private InternalRegistrationStatusDto registrationStatusDto;

	@Mock
	ObjectMapper mapIdentityJsonStringToObject;

	private MandatoryValidation mandatoryValidation;
	private String idJsonString;

	private String mappingJsonString;

	@Before
	public void setUp() throws Exception {
		mappingJsonString = "{\"identity\":{\"name\":{\"value\":\"fullName\",\"isMandatory\":true},\"gender\":{\"value\":\"gender\",\"isMandatory\":true},\"dob\":{\"value\":\"dateOfBirth\",\"isMandatory\":true},\"parentOrGuardianRID\":{\"value\":\"parentOrGuardianRID\"},\"parentOrGuardianUIN\":{\"value\":\"parentOrGuardianUIN\"},\"parentOrGuardianName\":{\"value\":\"parentOrGuardianName\"},\"poa\":{\"value\":\"proofOfAddress\"},\"poi\":{\"value\":\"proofOfIdentity\"},\"por\":{\"value\":\"proofOfRelationship\"},\"pob\":{\"value\":\"proofOfDateOfBirth\"},\"individualBiometrics\":{\"value\":\"individualBiometrics\"},\"age\":{\"value\":\"age\"},\"address\":{\"value\":\"addressLine1\"},\"region\":{\"value\":\"region\"},\"province\":{\"value\":\"province\"},\"postalCode\":{\"value\":\"postalCode\"},\"phone\":{\"value\":\"phone\"},\"email\":{\"value\":\"email\"},\"localAdministrativeAuthority\":{\"value\":\"localAdministrativeAuthority\"},\"idschemaversion\":{\"value\":\"IDSchemaVersion\"},\"cnienumber\":{\"value\":\"CNIENumber\"},\"city\":{\"value\":\"city\",\"isMandatory\":true}}}";
		idJsonString = "{\"identity\":{\"fullName\":[{\"language\":\"eng\",\"value\":\"Ragavendran V\"},{\"language\":\"ara\",\"value\":\"قشلشرثىيقشى ر\"}],\"dateOfBirth\":\"1999/01/01\",\"age\":20,\"gender\":[{\"language\":\"eng\",\"value\":\"Male\"},{\"language\":\"ara\",\"value\":\"الذكر\"}],\"residenceStatus\":[{\"language\":\"eng\",\"value\":\"Non-Foreigner\"},{\"language\":\"ara\",\"value\":\"غير أجنبي\"}],\"addressLine1\":[{\"language\":\"eng\",\"value\":\"Kumar street\"},{\"language\":\"ara\",\"value\":\"نعةشق سفقثثف\"}],\"addressLine3\":[{\"language\":\"eng\",\"value\":\"Line 3\"},{\"language\":\"ara\",\"value\":\"مىث ٣\"}],\"region\":[{\"language\":\"eng\",\"value\":\"Rabat Sale Kenitra\"},{\"language\":\"ara\",\"value\":\"جهة الرباط سلا القنيطرة\"}],\"province\":[{\"language\":\"eng\",\"value\":\"Kenitra\"},{\"language\":\"ara\",\"value\":\"القنيطرة\"}],\"city\":[{\"language\":\"eng\",\"value\":\"Mograne\"},{\"language\":\"ara\",\"value\":\"مڭرن\"}],\"postalCode\":\"123456\",\"phone\":\"9962385854\",\"email\":\"raghavdce@gmail.com\",\"localAdministrativeAuthority\":[{\"language\":\"eng\",\"value\":\"14023\"},{\"language\":\"ara\",\"value\":\"14023\"}],\"proofOfAddress\":{\"value\":\"POA_Rental contract\",\"type\":\"Rental contract\",\"format\":\"jpg\"},\"proofOfIdentity\":{\"value\":\"POI_CNIE card\",\"type\":\"CNIE card\",\"format\":\"jpg\"},\"proofOfRelationship\":{\"value\":\"POR_Certificate of Relationship\",\"type\":\"Certificate of Relationship\",\"format\":\"jpg\"},\"individualBiometrics\":{\"format\":\"cbeff\",\"version\":1,\"value\":\"applicant_bio_CBEFF\"},\"IDSchemaVersion\":1,\"CNIENumber\":\"12345678809\"}}";

		registrationStatusDto = new InternalRegistrationStatusDto();
		registrationStatusDto.setRegistrationId("10003100030001120190410111048");

		mandatoryValidation = new MandatoryValidation(adapter, registrationStatusDto, utility);

		Mockito.when(adapter.getFile(any(), any())).thenReturn(inputStream);

		PowerMockito.mockStatic(IOUtils.class);
		PowerMockito.when(IOUtils.class, "toByteArray", inputStream).thenReturn(idJsonString.getBytes());

		PowerMockito.mockStatic(Utilities.class);
		PowerMockito.when(Utilities.class, "getJson", any(), any()).thenReturn(mappingJsonString);
	}

	@Test
	public void mandatoryValidationSuccessTest() throws IOException, JSONException, PacketDecryptionFailureException, ApisResourceAccessException, io.mosip.kernel.core.exception.IOException {

		boolean result = mandatoryValidation.mandatoryFieldValidation(registrationStatusDto.getRegistrationId());
		assertTrue("Test for mandate fields", result);
	}

	@Test
	public void mandatoryValidationMissingFieldFailureTest() throws Exception {
		// Removing fullname field from ID json
		idJsonString = "{\"identity\":{\"dateOfBirth\":\"1999/01/01\",\"age\":20,\"gender\":[{\"language\":\"eng\",\"value\":\"Male\"},{\"language\":\"ara\",\"value\":\"الذكر\"}],\"residenceStatus\":[{\"language\":\"eng\",\"value\":\"Non-Foreigner\"},{\"language\":\"ara\",\"value\":\"غير أجنبي\"}],\"addressLine1\":[{\"language\":\"eng\",\"value\":\"Kumar street\"},{\"language\":\"ara\",\"value\":\"نعةشق سفقثثف\"}],\"addressLine3\":[{\"language\":\"eng\",\"value\":\"Line 3\"},{\"language\":\"ara\",\"value\":\"مىث ٣\"}],\"region\":[{\"language\":\"eng\",\"value\":\"Rabat Sale Kenitra\"},{\"language\":\"ara\",\"value\":\"جهة الرباط سلا القنيطرة\"}],\"province\":[{\"language\":\"eng\",\"value\":\"Kenitra\"},{\"language\":\"ara\",\"value\":\"القنيطرة\"}],\"city\":[{\"language\":\"eng\",\"value\":\"Mograne\"},{\"language\":\"ara\",\"value\":\"مڭرن\"}],\"postalCode\":\"123456\",\"phone\":\"9962385854\",\"email\":\"raghavdce@gmail.com\",\"localAdministrativeAuthority\":[{\"language\":\"eng\",\"value\":\"14023\"},{\"language\":\"ara\",\"value\":\"14023\"}],\"proofOfAddress\":{\"value\":\"POA_Rental contract\",\"type\":\"Rental contract\",\"format\":\"jpg\"},\"proofOfIdentity\":{\"value\":\"POI_CNIE card\",\"type\":\"CNIE card\",\"format\":\"jpg\"},\"proofOfRelationship\":{\"value\":\"POR_Certificate of Relationship\",\"type\":\"Certificate of Relationship\",\"format\":\"jpg\"},\"individualBiometrics\":{\"format\":\"cbeff\",\"version\":1,\"value\":\"applicant_bio_CBEFF\"},\"IDSchemaVersion\":1,\"CNIENumber\":\"12345678809\"}}";
		PowerMockito.mockStatic(IOUtils.class);
		PowerMockito.when(IOUtils.class, "toByteArray", inputStream).thenReturn(idJsonString.getBytes());
		boolean result = mandatoryValidation.mandatoryFieldValidation(registrationStatusDto.getRegistrationId());
		assertFalse("Test for mandatory missing fields", result);
	}

	@Test
	public void mandatoryValidationMarkMandatoryFalseTest() throws Exception {
		// Mark mandatory field false for fullName from IdentityMapping json
		mappingJsonString = "{\"identity\":{\"name\":{\"value\":\"fullName\",\"isMandatory\":false},\"gender\":{\"value\":\"gender\",\"isMandatory\":true},\"dob\":{\"value\":\"dateOfBirth\",\"isMandatory\":true},\"parentOrGuardianRID\":{\"value\":\"parentOrGuardianRID\"},\"parentOrGuardianUIN\":{\"value\":\"parentOrGuardianUIN\"},\"parentOrGuardianName\":{\"value\":\"parentOrGuardianName\"},\"poa\":{\"value\":\"proofOfAddress\"},\"poi\":{\"value\":\"proofOfIdentity\"},\"por\":{\"value\":\"proofOfRelationship\"},\"pob\":{\"value\":\"proofOfDateOfBirth\"},\"individualBiometrics\":{\"value\":\"individualBiometrics\"},\"age\":{\"value\":\"age\"},\"addressLine1\":{\"value\":\"addressLine1\"},\"addressLine2\":{\"value\":\"addressLine2\"},\"addressLine3\":{\"value\":\"addressLine3\"},\"region\":{\"value\":\"region\"},\"province\":{\"value\":\"province\"},\"postalCode\":{\"value\":\"postalCode\"},\"phone\":{\"value\":\"phone\"},\"email\":{\"value\":\"email\"},\"localAdministrativeAuthority\":{\"value\":\"localAdministrativeAuthority\"},\"idschemaversion\":{\"value\":\"IDSchemaVersion\"},\"cnienumber\":{\"value\":\"CNIENumber\"},\"city\":{\"value\":\"city\",\"isMandatory\":true}}}";
		PowerMockito.mockStatic(Utilities.class);
		PowerMockito.when(Utilities.class, "getJson", any(), any()).thenReturn(mappingJsonString);
		boolean result = mandatoryValidation.mandatoryFieldValidation(registrationStatusDto.getRegistrationId());
		assertTrue("Test for mandate field marked false", result);
	}

	@Test
	public void mandatoryValidationNullOrEmptyValueTest() throws Exception {
		// null or empty value for mandate field
		idJsonString = "{\"identity\":{\"fullName\":\"\",\"dateOfBirth\":null,\"age\":20,\"gender\":[{\"language\":\"eng\",\"value\":\"Male\"},{\"language\":\"ara\",\"value\":\"الذكر\"}],\"residenceStatus\":[{\"language\":\"eng\",\"value\":\"Non-Foreigner\"},{\"language\":\"ara\",\"value\":\"غير أجنبي\"}],\"addressLine1\":[{\"language\":\"eng\",\"value\":\"Kumar street\"},{\"language\":\"ara\",\"value\":\"نعةشق سفقثثف\"}],\"addressLine3\":[{\"language\":\"eng\",\"value\":\"Line 3\"},{\"language\":\"ara\",\"value\":\"مىث ٣\"}],\"region\":[{\"language\":\"eng\",\"value\":\"Rabat Sale Kenitra\"},{\"language\":\"ara\",\"value\":\"جهة الرباط سلا القنيطرة\"}],\"province\":[{\"language\":\"eng\",\"value\":\"Kenitra\"},{\"language\":\"ara\",\"value\":\"القنيطرة\"}],\"city\":[{\"language\":\"eng\",\"value\":\"Mograne\"},{\"language\":\"ara\",\"value\":\"مڭرن\"}],\"postalCode\":\"123456\",\"phone\":\"9962385854\",\"email\":\"raghavdce@gmail.com\",\"localAdministrativeAuthority\":[{\"language\":\"eng\",\"value\":\"14023\"},{\"language\":\"ara\",\"value\":\"14023\"}],\"proofOfAddress\":{\"value\":\"POA_Rental contract\",\"type\":\"Rental contract\",\"format\":\"jpg\"},\"proofOfIdentity\":{\"value\":\"POI_CNIE card\",\"type\":\"CNIE card\",\"format\":\"jpg\"},\"proofOfRelationship\":{\"value\":\"POR_Certificate of Relationship\",\"type\":\"Certificate of Relationship\",\"format\":\"jpg\"},\"individualBiometrics\":{\"format\":\"cbeff\",\"version\":1,\"value\":\"applicant_bio_CBEFF\"},\"IDSchemaVersion\":1,\"CNIENumber\":\"12345678809\"}}";
		PowerMockito.mockStatic(IOUtils.class);
		PowerMockito.when(IOUtils.class, "toByteArray", inputStream).thenReturn(idJsonString.getBytes());
		boolean result = mandatoryValidation.mandatoryFieldValidation(registrationStatusDto.getRegistrationId());
		assertFalse("Test for mandatory missing fields", result);
	}

	@Test
	public void mandatoryValidationNullOrEmptyValueInIDJsonTest() throws Exception {
		// null or empty value for mandate field from ID JSON value object
		idJsonString = "{\"identity\":{\"fullName\":[{\"language\":\"eng\",\"value\":null},{\"language\":\"ara\",\"value\":\"قشلشرثىيقشى ر\"}],\"dateOfBirth\":\"1999/01/01\",\"age\":20,\"gender\":[{\"language\":\"eng\",\"value\":\"Male\"},{\"language\":\"ara\",\"value\":\"الذكر\"}],\"residenceStatus\":[{\"language\":\"eng\",\"value\":\"Non-Foreigner\"},{\"language\":\"ara\",\"value\":\"غير أجنبي\"}],\"addressLine1\":[{\"language\":\"eng\",\"value\":\"Kumar street\"},{\"language\":\"ara\",\"value\":\"نعةشق سفقثثف\"}],\"addressLine3\":[{\"language\":\"eng\",\"value\":\"Line 3\"},{\"language\":\"ara\",\"value\":\"مىث ٣\"}],\"region\":[{\"language\":\"eng\",\"value\":\"Rabat Sale Kenitra\"},{\"language\":\"ara\",\"value\":\"جهة الرباط سلا القنيطرة\"}],\"province\":[{\"language\":\"eng\",\"value\":\"Kenitra\"},{\"language\":\"ara\",\"value\":\"القنيطرة\"}],\"city\":[{\"language\":\"eng\",\"value\":\"Mograne\"},{\"language\":\"ara\",\"value\":\"مڭرن\"}],\"postalCode\":\"123456\",\"phone\":\"9962385854\",\"email\":\"raghavdce@gmail.com\",\"localAdministrativeAuthority\":[{\"language\":\"eng\",\"value\":\"14023\"},{\"language\":\"ara\",\"value\":\"14023\"}],\"proofOfAddress\":{\"value\":\"POA_Rental contract\",\"type\":\"Rental contract\",\"format\":\"jpg\"},\"proofOfIdentity\":{\"value\":\"POI_CNIE card\",\"type\":\"CNIE card\",\"format\":\"jpg\"},\"proofOfRelationship\":{\"value\":\"POR_Certificate of Relationship\",\"type\":\"Certificate of Relationship\",\"format\":\"jpg\"},\"individualBiometrics\":{\"format\":\"cbeff\",\"version\":1,\"value\":\"applicant_bio_CBEFF\"},\"IDSchemaVersion\":1,\"CNIENumber\":\"12345678809\"}}";
		PowerMockito.mockStatic(IOUtils.class);
		PowerMockito.when(IOUtils.class, "toByteArray", inputStream).thenReturn(idJsonString.getBytes());
		boolean result = mandatoryValidation.mandatoryFieldValidation(registrationStatusDto.getRegistrationId());
		assertFalse("Test for mandatory missing fields", result);
	}
}
