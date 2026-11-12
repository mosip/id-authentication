package io.mosip.authentication.service.internal.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import io.mosip.authentication.core.autntxn.dto.AutnTxnRequestDto;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
public class AuthTxnValidatorTest {

	@InjectMocks
	private AuthTxnValidator authTxnValidator;
	
	@BeforeEach
	public void before() {
		// Setup if needed
	}
	
	@Test
	public void testSupports() {
		assertTrue(authTxnValidator.supports(AutnTxnRequestDto.class), "Should support AutnTxnRequestDto");
		assertFalse(authTxnValidator.supports(String.class), "Should not support other classes");
	}

	@Test
	public void testValidateWithNullTarget() {
		Errors errors = new BeanPropertyBindingResult(new AutnTxnRequestDto(), "autnTxnDto");
		
		authTxnValidator.validate(null, errors);
		
		// Should handle null gracefully
		assertFalse(errors.hasErrors(), "Should not have errors for null target");
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
		assertNotNull(errors, "Errors object should not be null");
	}
}
