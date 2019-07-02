/**
 * 
 */
package io.mosip.registration.processor.bio.dedupe.api.controller.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import javax.servlet.http.Cookie;

import io.mosip.registration.processor.core.util.DigitalSignatureUtility;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import io.mosip.registration.processor.bio.dedupe.api.config.BioDedupeConfigTest;
import io.mosip.registration.processor.bio.dedupe.api.controller.BioDedupeController;
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
@ImportAutoConfiguration(RefreshAutoConfiguration.class)
public class BioDedupeControllerTest {

	@InjectMocks
	private BioDedupeController bioDedupeController;

	@MockBean
	private BioDedupeService bioDedupeService;

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private TokenValidator tokenValidator;

	@MockBean
	private DigitalSignatureUtility digitalSignatureUtility;

	String regId;

	byte[] file;

	@Before
	public void setUp() {
		ReflectionTestUtils.setField(bioDedupeController, "isEnabled", true);
		regId = "1234";
		file = regId.getBytes();
		Mockito.when(bioDedupeService.getFile(anyString(),any())).thenReturn(file);
		Mockito.doNothing().when(tokenValidator).validate(any(), any());
	}

	@Test
	public void getFileSuccessTest() throws Exception {
		Mockito.when(digitalSignatureUtility.getDigitalSignature(any())).thenReturn("abc");
		this.mockMvc
				.perform(MockMvcRequestBuilders.get("/biometricfile/1234").cookie(new Cookie("Authorization", "token"))
						.param("regId", regId).accept(MediaType.ALL_VALUE).contentType(MediaType.ALL_VALUE))

				.andExpect(MockMvcResultMatchers.status().isOk());

	}
}
