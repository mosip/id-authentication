package io.mosip.registration.test.validator;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.mosip.kernel.core.exception.BaseCheckedException;
import io.mosip.kernel.core.idobjectvalidator.exception.IdObjectIOException;
import io.mosip.kernel.idobjectvalidator.impl.IdObjectPatternValidator;
import io.mosip.kernel.idobjectvalidator.impl.IdObjectSchemaValidator;
import io.mosip.registration.constants.RegistrationConstants;
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
	

	@InjectMocks
	private RegIdObjectValidator regIdObjectValidator;
	
	@Test
	public void validateIdObjectTest() throws BaseCheckedException {
		
		
		Mockito.when(idObjectValidator.validateIdObject(Mockito.anyObject(), Mockito.any())).thenReturn(true);
		Mockito.when(idOjectPatternvalidator.validateIdObject(Mockito.anyObject(), Mockito.any())).thenReturn(true);
		Mockito.when(regIdObjectMasterDataValidator.validateIdObject(Mockito.anyObject(), Mockito.any())).thenReturn(true);
		regIdObjectValidator.validateIdObject(new Object(), RegistrationConstants.PACKET_TYPE_NEW);
	}
	
	@Test(expected = RegBaseCheckedException.class)
	public void idObjectValidatorExceptionTest() throws BaseCheckedException {
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
	public void idObjectValidatorNegativeTest() throws BaseCheckedException {
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
	

}
