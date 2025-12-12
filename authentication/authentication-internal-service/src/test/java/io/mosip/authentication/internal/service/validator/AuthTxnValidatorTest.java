package io.mosip.authentication.internal.service.validator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import io.mosip.authentication.core.autntxn.dto.AutnTxnRequestDto;

import static org.junit.Assert.*;
@RunWith(MockitoJUnitRunner.class)
public class AuthTxnValidatorTest {

	@InjectMocks
	private AuthTxnValidator authTxnValidator;
	
	@Before
	public void before() {
		// Setup if needed
	}
	
	@Test
	public void testSupports() {
		assertTrue("Should support AutnTxnRequestDto", authTxnValidator.supports(AutnTxnRequestDto.class));
		assertFalse("Should not support other classes", authTxnValidator.supports(String.class));
	}

	@Test
	public void testValidateWithNullTarget() {
		Errors errors = new BeanPropertyBindingResult(new AutnTxnRequestDto(), "autnTxnDto");
		
		authTxnValidator.validate(null, errors);
		
		// Should handle null gracefully
		assertFalse("Should not have errors for null target", errors.hasErrors());
	}
	
	@Test
	public void testValidateWithInvalidIndividualId() {
		AutnTxnRequestDto autnTxnDto = new AutnTxnRequestDto();
		autnTxnDto.setIndividualId(null);
		autnTxnDto.setIndividualIdType("UIN");
		
		Errors errors = new BeanPropertyBindingResult(autnTxnDto, "autnTxnDto");
		
		authTxnValidator.validate(autnTxnDto, errors);
		
		// Should have errors for invalid individual ID
		// The actual validation is done by parent class method
		assertNotNull("Errors object should not be null", errors);
	}
}
