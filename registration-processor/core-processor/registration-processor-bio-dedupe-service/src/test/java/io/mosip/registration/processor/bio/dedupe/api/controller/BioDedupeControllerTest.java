/**
 * 
 */
package io.mosip.registration.processor.bio.dedupe.api.controller;

import static org.mockito.ArgumentMatchers.anyString;

import javax.servlet.http.Cookie;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import io.mosip.registration.processor.bio.dedupe.api.config.BioDedupeConfigTest;
import io.mosip.registration.processor.core.spi.biodedupe.BioDedupeService;
import io.mosip.registration.processor.core.token.validation.TokenValidator;

/**
 * @author M1022006
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = BioDedupeConfigTest.class)
@TestPropertySource(locations = "classpath:application.properties")
public class BioDedupeControllerTest {

	@InjectMocks
	BioDedupeController bioDedupeController = new BioDedupeController();

	@MockBean
	BioDedupeService bioDedupeService;

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private TokenValidator tokenValidator;

	@Mock
	Environment env;

	String regId;

	byte[] file;

	@Before
	public void setUp() {
		regId = "1234";
		file = regId.getBytes();
		Mockito.when(bioDedupeService.getFile(anyString())).thenReturn(file);
		Mockito.when(env.getProperty("TOKENVALIDATE")).thenReturn(null);
		Mockito.doNothing().when(tokenValidator).validate(ArgumentMatchers.any(), ArgumentMatchers.any());
	}

	@Test
	public void getFileSuccessTest() throws Exception {

		this.mockMvc.perform(MockMvcRequestBuilders.get("/v0.1/registration-processor/bio-dedupe/1234")
				.cookie(new Cookie("Authorization", "token")).param("regId", regId).accept(MediaType.ALL_VALUE)
				.contentType(MediaType.ALL_VALUE)).andExpect(MockMvcResultMatchers.status().isOk());

	}
}
