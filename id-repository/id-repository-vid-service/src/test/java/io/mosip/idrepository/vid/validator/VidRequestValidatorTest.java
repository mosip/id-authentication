package io.mosip.idrepository.vid.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.idrepository.core.dto.IdRequestDTO;
import io.mosip.idrepository.vid.dto.VidRequestDTO;

/**
 * 
 * @author Prem Kumar
 *
 */
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@RunWith(SpringRunner.class)
@WebMvcTest
public class VidRequestValidatorTest {

	@InjectMocks
	private VidRequestValidator requestValidator;

	@Test
	public void testSupport() {
		assertTrue(requestValidator.supports(VidRequestDTO.class));
	}
	
	@Test
	public void testSupport_Invalid() {
		assertFalse(requestValidator.supports(IdRequestDTO.class));
	}
}
