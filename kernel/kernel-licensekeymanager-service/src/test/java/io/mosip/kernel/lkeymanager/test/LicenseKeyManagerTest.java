package io.mosip.kernel.lkeymanager.test;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import io.mosip.kernel.core.licensekeymanager.spi.LicenseKeyManagerService;
import io.mosip.kernel.lkeymanager.LicenseKeyManagerBootApplication;
import io.mosip.kernel.lkeymanager.dto.LicenseKeyGenerationDto;
import io.mosip.kernel.lkeymanager.dto.LicenseKeyMappingDto;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest(classes = LicenseKeyManagerBootApplication.class)
public class LicenseKeyManagerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private LicenseKeyManagerService<String, LicenseKeyGenerationDto, LicenseKeyMappingDto> service;

	@Test
	public void testLicenseKeyGeneration() throws Exception {
		LicenseKeyGenerationDto licenseKeyGenerationDto = new LicenseKeyGenerationDto();
		licenseKeyGenerationDto.setTspId("TESTID");
		given(service.generateLicenseKey(licenseKeyGenerationDto)).willReturn("asdfghkngyrthgfyt");
		String json = "{\"tspId\":\"TESTID\"}";
		mockMvc.perform(post("/v1.0/license/generate").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk()).andExpect(jsonPath("$.licenseKey", is("asdfghkngyrthgfyt")));
	}

	@Test
	public void testLicenseKeyMapping() throws Exception {
		LicenseKeyGenerationDto licenseKeyGenerationDto = new LicenseKeyGenerationDto();
		licenseKeyGenerationDto.setTspId("TESTID");
		given(service.generateLicenseKey(licenseKeyGenerationDto)).willReturn("asdfghkngyrthgfyt");
		String json = "{\"tspId\":\"TESTID\"}";
		mockMvc.perform(post("/v1.0/license/generate").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk()).andExpect(jsonPath("$.licenseKey", is("asdfghkngyrthgfyt")));
	}

	@Test
	public void testLicenseKeyFetch() throws Exception {
		LicenseKeyGenerationDto licenseKeyGenerationDto = new LicenseKeyGenerationDto();
		licenseKeyGenerationDto.setTspId("TESTID");
		given(service.generateLicenseKey(licenseKeyGenerationDto)).willReturn("asdfghkngyrthgfyt");
		String json = "{\"tspId\":\"TESTID\"}";
		mockMvc.perform(post("/v1.0/license/generate").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk()).andExpect(jsonPath("$.licenseKey", is("asdfghkngyrthgfyt")));
	}
}
