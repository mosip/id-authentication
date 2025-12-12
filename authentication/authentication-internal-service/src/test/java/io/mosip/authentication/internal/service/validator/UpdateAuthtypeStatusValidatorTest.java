package io.mosip.authentication.internal.service.validator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import io.mosip.authentication.core.spi.authtype.status.service.AuthTypeStatusDto;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class UpdateAuthtypeStatusValidatorTest {

	@InjectMocks
	private UpdateAuthtypeStatusValidator updateAuthtypeStatusValidator;
	
	@Before
	public void before() {
		// Setup if needed
	}
	
	@Test
	public void testSupports() {
		assertTrue("Should support AuthTypeStatusDto", 
			updateAuthtypeStatusValidator.supports(AuthTypeStatusDto.class));
		assertFalse("Should not support other classes", 
			updateAuthtypeStatusValidator.supports(String.class));
	}
	
	@Test
	public void testValidateWithNullTarget() {
		Errors errors = new BeanPropertyBindingResult(null, "authTypeStatusDto");
		
		updateAuthtypeStatusValidator.validate(null, errors);
		
		// Should handle null gracefully
		assertFalse("Should not have errors for null target", errors.hasErrors());
	}

}
