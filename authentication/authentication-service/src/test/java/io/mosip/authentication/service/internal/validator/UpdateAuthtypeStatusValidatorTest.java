package io.mosip.authentication.service.internal.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import io.mosip.authentication.core.spi.authtype.status.service.AuthTypeStatusDto;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UpdateAuthtypeStatusValidatorTest {

	@InjectMocks
	private UpdateAuthtypeStatusValidator updateAuthtypeStatusValidator;
	
	@BeforeEach
	public void before() {
		// Setup if needed
	}
	
	@Test
	public void testSupports() {
		assertTrue( 
			updateAuthtypeStatusValidator.supports(AuthTypeStatusDto.class), "Should support AuthTypeStatusDto");
		assertFalse( 
			updateAuthtypeStatusValidator.supports(String.class), "Should not support other classes");
	}
	
	@Test
	public void testValidateWithNullTarget() {
		Errors errors = new BeanPropertyBindingResult(null, "authTypeStatusDto");
		
		updateAuthtypeStatusValidator.validate(null, errors);
		
		// Should handle null gracefully
		assertFalse(errors.hasErrors(), "Should not have errors for null target");
	}

}
