package io.mosip.registration.test.validator;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.exception.BaseCheckedException;
import io.mosip.kernel.core.idobjectvalidator.exception.IdObjectIOException;
import io.mosip.kernel.idobjectvalidator.impl.IdObjectPatternValidator;
import io.mosip.kernel.idobjectvalidator.impl.IdObjectSchemaValidator;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.validator.RegIdObjectMasterDataValidator;
import io.mosip.registration.validator.RegIdObjectValidator;

public class RegIdObjectValidatorTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@Mock
	private IdObjectSchemaValidator idObjectValidator;
	
	@Mock
	private IdObjectPatternValidator idOjectPatternvalidator;
	
	/*@Mock
	private IdObjectValidator idObjectValidator;*/
	
	@Mock
	private RegIdObjectMasterDataValidator regIdObjectMasterDataValidator;
	
	@Mock
	private ObjectMapper mapper;
	

	@InjectMocks
	private RegIdObjectValidator regIdObjectValidator;
	
	@Before
	public void BeforeClass() {
		 Map<String, Object> appMap = new HashMap<>();
         appMap.put(RegistrationConstants.MAX_AGE, 150);
       ApplicationContext.getInstance().setApplicationMap(appMap);
	}
	
	@Test
	public void validateIdObjectTest() throws BaseCheckedException, JsonProcessingException {
		String identityString = "{\"identity\":{\"IDSchemaVersion\":1.0,\"UIN\":4920546943,\"fullName\":[{\"language\":\"eng\",\"value\":\"Ibrahim Ibn Ali\"}],\"dateOfBirth\":\"1955/04/15\",\"age\":45,\"gender\":[{\"language\":\"eng\",\"value\":\"MLE\"}],\"addressLine1\":[{\"language\":\"eng\",\"value\":\"exemple d'adresse ligne 1\"}],\"addressLine2\":[{\"language\":\"eng\",\"value\":\"exemple d'adresse ligne 2\"}],\"addressLine3\":[{\"language\":\"eng\",\"value\":\"exemple d'adresse ligne 2\"}],\"region\":[{\"language\":\"eng\",\"value\":\"Rabat Sale Kenitra\"}],\"province\":[{\"language\":\"eng\",\"value\":\"Kenitra\"}],\"city\":[{\"language\":\"eng\",\"value\":\"Kenitra\"}],\"postalCode\":\"10112\",\"phone\":\"9876543210\",\"email\":\"abc@xyz.com\",\"CNIENumber\":\"6789545678909\",\"localAdministrativeAuthority\":[{\"language\":\"eng\",\"value\":\"Mograne\"}],\"parentOrGuardianRID\":212124324784912,\"parentOrGuardianUIN\":212124324784912,\"parentOrGuardianName\":[{\"language\":\"eng\",\"value\":\"salma\"}],\"proofOfAddress\":{\"format\":\"pdf\",\"type\":\"Ration Card\",\"value\":\"fileReferenceID\"},\"proofOfIdentity\":{\"format\":\"txt\",\"type\":\"Passport\",\"value\":\"fileReferenceID\"},\"proofOfRelationship\":{\"format\":\"pdf\",\"type\":\"Birth Certificate\",\"value\":\"fileReferenceID\"},\"proofOfDateOfBirth\":{\"format\":\"pdf\",\"type\":\"passport\",\"value\":\"fileReferenceID\"},\"individualBiometrics\":{\"format\":\"cbeff\",\"version\":1.0,\"value\":\"fileReferenceID\"},\"parentOrGuardianBiometrics\":{\"format\":\"cbeff\",\"version\":1.1,\"value\":\"fileReferenceID\"}}}";
		Mockito.when(mapper.writeValueAsString(Mockito.any())).thenReturn(identityString);
		Mockito.when(idObjectValidator.validateIdObject(Mockito.anyObject(), Mockito.any())).thenReturn(true);
		Mockito.when(idOjectPatternvalidator.validateIdObject(Mockito.anyObject(), Mockito.any())).thenReturn(true);
		Mockito.when(regIdObjectMasterDataValidator.validateIdObject(Mockito.anyObject(), Mockito.any())).thenReturn(true);
		regIdObjectValidator.validateIdObject(new Object(), RegistrationConstants.PACKET_TYPE_NEW);
	}
	
	@Test(expected = RegBaseCheckedException.class)
	public void idObjectValidatorExceptionTest() throws BaseCheckedException, JsonProcessingException {
		String identityString = "{\"identity\":{\"IDSchemaVersion\":1.0,\"UIN\":4920546943,\"fullName\":[{\"language\":\"eng\",\"value\":\"Ibrahim Ibn Ali\"}],\"dateOfBirth\":\"1955/04/15\",\"age\":45,\"gender\":[{\"language\":\"eng\",\"value\":\"MLE\"}],\"addressLine1\":[{\"language\":\"eng\",\"value\":\"exemple d'adresse ligne 1\"}],\"addressLine2\":[{\"language\":\"eng\",\"value\":\"exemple d'adresse ligne 2\"}],\"addressLine3\":[{\"language\":\"eng\",\"value\":\"exemple d'adresse ligne 2\"}],\"region\":[{\"language\":\"eng\",\"value\":\"Rabat Sale Kenitra\"}],\"province\":[{\"language\":\"eng\",\"value\":\"Kenitra\"}],\"city\":[{\"language\":\"eng\",\"value\":\"Kenitra\"}],\"postalCode\":\"10112\",\"phone\":\"9876543210\",\"email\":\"abc@xyz.com\",\"CNIENumber\":\"6789545678909\",\"localAdministrativeAuthority\":[{\"language\":\"eng\",\"value\":\"Mograne\"}],\"parentOrGuardianRID\":212124324784912,\"parentOrGuardianUIN\":212124324784912,\"parentOrGuardianName\":[{\"language\":\"eng\",\"value\":\"salma\"}],\"proofOfAddress\":{\"format\":\"pdf\",\"type\":\"Ration Card\",\"value\":\"fileReferenceID\"},\"proofOfIdentity\":{\"format\":\"txt\",\"type\":\"Passport\",\"value\":\"fileReferenceID\"},\"proofOfRelationship\":{\"format\":\"pdf\",\"type\":\"Birth Certificate\",\"value\":\"fileReferenceID\"},\"proofOfDateOfBirth\":{\"format\":\"pdf\",\"type\":\"passport\",\"value\":\"fileReferenceID\"},\"individualBiometrics\":{\"format\":\"cbeff\",\"version\":1.0,\"value\":\"fileReferenceID\"},\"parentOrGuardianBiometrics\":{\"format\":\"cbeff\",\"version\":1.1,\"value\":\"fileReferenceID\"}}}";
		Mockito.when(mapper.writeValueAsString(Mockito.any())).thenReturn(identityString);
		Mockito.when(idObjectValidator.validateIdObject(Mockito.anyObject(), Mockito.any())).thenReturn(true);
		Mockito.when(idOjectPatternvalidator.validateIdObject(Mockito.anyObject(), Mockito.any())).thenReturn(true);
		Mockito.when(regIdObjectMasterDataValidator.validateIdObject(Mockito.anyObject(), Mockito.any())).thenReturn(false);
		regIdObjectValidator.validateIdObject(new Object(), RegistrationConstants.PACKET_TYPE_UPDATE);
	}
	
	@Test(expected = RegBaseCheckedException.class)
	public void idObjectValidatorExceptionTest_1() throws BaseCheckedException {
		Mockito.when(idObjectValidator.validateIdObject(Mockito.anyObject(), Mockito.any())).thenReturn(true);
		Mockito.when(idOjectPatternvalidator.validateIdObject(Mockito.anyObject(), Mockito.any())).thenReturn(false);
		regIdObjectValidator.validateIdObject(new Object(), RegistrationConstants.PACKET_TYPE_UPDATE);
	}
	
	@Test(expected = RegBaseCheckedException.class)
	public void idObjectValidatorExceptionTest_2() throws BaseCheckedException {
		Mockito.when(idObjectValidator.validateIdObject(Mockito.anyObject(), Mockito.any())).thenReturn(false);
		regIdObjectValidator.validateIdObject(new Object(), RegistrationConstants.PACKET_TYPE_UPDATE);
	}
	
	@SuppressWarnings("unchecked")
	@Test(expected = IdObjectIOException.class)
	public void idObjectValidatorNegativeTest() throws BaseCheckedException, JsonProcessingException {
		String identityString = "{\"identity\":{\"IDSchemaVersion\":1.0,\"UIN\":4920546943,\"fullName\":[{\"language\":\"eng\",\"value\":\"Ibrahim Ibn Ali\"}],\"dateOfBirth\":\"1955/04/15\",\"age\":45,\"gender\":[{\"language\":\"eng\",\"value\":\"MLE\"}],\"addressLine1\":[{\"language\":\"eng\",\"value\":\"exemple d'adresse ligne 1\"}],\"addressLine2\":[{\"language\":\"eng\",\"value\":\"exemple d'adresse ligne 2\"}],\"addressLine3\":[{\"language\":\"eng\",\"value\":\"exemple d'adresse ligne 2\"}],\"region\":[{\"language\":\"eng\",\"value\":\"Rabat Sale Kenitra\"}],\"province\":[{\"language\":\"eng\",\"value\":\"Kenitra\"}],\"city\":[{\"language\":\"eng\",\"value\":\"Kenitra\"}],\"postalCode\":\"10112\",\"phone\":\"9876543210\",\"email\":\"abc@xyz.com\",\"CNIENumber\":\"6789545678909\",\"localAdministrativeAuthority\":[{\"language\":\"eng\",\"value\":\"Mograne\"}],\"parentOrGuardianRID\":212124324784912,\"parentOrGuardianUIN\":212124324784912,\"parentOrGuardianName\":[{\"language\":\"eng\",\"value\":\"salma\"}],\"proofOfAddress\":{\"format\":\"pdf\",\"type\":\"Ration Card\",\"value\":\"fileReferenceID\"},\"proofOfIdentity\":{\"format\":\"txt\",\"type\":\"Passport\",\"value\":\"fileReferenceID\"},\"proofOfRelationship\":{\"format\":\"pdf\",\"type\":\"Birth Certificate\",\"value\":\"fileReferenceID\"},\"proofOfDateOfBirth\":{\"format\":\"pdf\",\"type\":\"passport\",\"value\":\"fileReferenceID\"},\"individualBiometrics\":{\"format\":\"cbeff\",\"version\":1.0,\"value\":\"fileReferenceID\"},\"parentOrGuardianBiometrics\":{\"format\":\"cbeff\",\"version\":1.1,\"value\":\"fileReferenceID\"}}}";
		Mockito.when(mapper.writeValueAsString(Mockito.any())).thenReturn(identityString);
		Mockito.when(idObjectValidator.validateIdObject(Mockito.anyObject(), Mockito.any())).thenReturn(true);
		Mockito.when(idOjectPatternvalidator.validateIdObject(Mockito.anyObject(), Mockito.any())).thenReturn(true);
		Mockito.when(regIdObjectMasterDataValidator.validateIdObject(Mockito.anyObject(), Mockito.any())).thenThrow(IdObjectIOException.class);
		regIdObjectValidator.validateIdObject(new Object(), RegistrationConstants.PACKET_TYPE_LOST);
	}
	
	@SuppressWarnings("unchecked")
	@Test(expected = RuntimeException.class)
	public void idObjectValidatorRuntimeException() throws BaseCheckedException {
		Mockito.when(idObjectValidator.validateIdObject(Mockito.anyObject(), Mockito.any())).thenReturn(true);
		Mockito.when(idOjectPatternvalidator.validateIdObject(Mockito.anyObject(), Mockito.any())).thenReturn(true);
		Mockito.when(regIdObjectMasterDataValidator.validateIdObject(Mockito.anyObject(), Mockito.any())).thenThrow(RuntimeException.class);
		regIdObjectValidator.validateIdObject(new Object(), RegistrationConstants.PACKET_TYPE_LOST);
	}
	
	@Test
	public void idObjectValidatorAgeTest() throws BaseCheckedException, JsonProcessingException {
		String identityString = "{\"identity\":{\"IDSchemaVersion\":1.0,\"UIN\":4920546943,\"fullName\":[{\"language\":\"eng\",\"value\":\"Ibrahim Ibn Ali\"}],\"dateOfBirth\":\"1955/04/15\",\"gender\":[{\"language\":\"eng\",\"value\":\"MLE\"}],\"addressLine1\":[{\"language\":\"eng\",\"value\":\"exemple d'adresse ligne 1\"}],\"addressLine2\":[{\"language\":\"eng\",\"value\":\"exemple d'adresse ligne 2\"}],\"addressLine3\":[{\"language\":\"eng\",\"value\":\"exemple d'adresse ligne 2\"}],\"region\":[{\"language\":\"eng\",\"value\":\"Rabat Sale Kenitra\"}],\"province\":[{\"language\":\"eng\",\"value\":\"Kenitra\"}],\"city\":[{\"language\":\"eng\",\"value\":\"Kenitra\"}],\"postalCode\":\"10112\",\"phone\":\"9876543210\",\"email\":\"abc@xyz.com\",\"CNIENumber\":\"6789545678909\",\"localAdministrativeAuthority\":[{\"language\":\"eng\",\"value\":\"Mograne\"}],\"parentOrGuardianRID\":212124324784912,\"parentOrGuardianUIN\":212124324784912,\"parentOrGuardianName\":[{\"language\":\"eng\",\"value\":\"salma\"}],\"proofOfAddress\":{\"format\":\"pdf\",\"type\":\"Ration Card\",\"value\":\"fileReferenceID\"},\"proofOfIdentity\":{\"format\":\"txt\",\"type\":\"Passport\",\"value\":\"fileReferenceID\"},\"proofOfRelationship\":{\"format\":\"pdf\",\"type\":\"Birth Certificate\",\"value\":\"fileReferenceID\"},\"proofOfDateOfBirth\":{\"format\":\"pdf\",\"type\":\"passport\",\"value\":\"fileReferenceID\"},\"individualBiometrics\":{\"format\":\"cbeff\",\"version\":1.0,\"value\":\"fileReferenceID\"},\"parentOrGuardianBiometrics\":{\"format\":\"cbeff\",\"version\":1.1,\"value\":\"fileReferenceID\"}}}";
		Mockito.when(mapper.writeValueAsString(Mockito.any())).thenReturn(identityString);
		Mockito.when(idObjectValidator.validateIdObject(Mockito.anyObject(), Mockito.any())).thenReturn(true);
		Mockito.when(idOjectPatternvalidator.validateIdObject(Mockito.anyObject(), Mockito.any())).thenReturn(true);
		Mockito.when(regIdObjectMasterDataValidator.validateIdObject(Mockito.anyObject(), Mockito.any())).thenReturn(true);
		regIdObjectValidator.validateIdObject(new Object(), RegistrationConstants.PACKET_TYPE_LOST);
	}
	
	@SuppressWarnings("unchecked")
	@Test(expected = RegBaseCheckedException.class)
	public void idObjectValidatorJsonProcessingException() throws BaseCheckedException {
		Mockito.when(idObjectValidator.validateIdObject(Mockito.anyObject(), Mockito.any())).thenReturn(true);
		Mockito.when(idOjectPatternvalidator.validateIdObject(Mockito.anyObject(), Mockito.any())).thenThrow(JsonProcessingException.class);
		Mockito.when(regIdObjectMasterDataValidator.validateIdObject(Mockito.anyObject(), Mockito.any())).thenReturn(true);
		regIdObjectValidator.validateIdObject(new Object(), RegistrationConstants.PACKET_TYPE_LOST);
	}

}
