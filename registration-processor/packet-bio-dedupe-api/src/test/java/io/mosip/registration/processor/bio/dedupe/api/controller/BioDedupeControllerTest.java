/**
 * 
 */
package io.mosip.registration.processor.bio.dedupe.api.controller;

import static org.mockito.ArgumentMatchers.anyString;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import io.mosip.registration.processor.bio.dedupe.api.config.BioDedupeConfig;
import io.mosip.registration.processor.core.config.CoreConfigBean;
import io.mosip.registration.processor.core.kernel.beans.KernelConfig;
import io.mosip.registration.processor.core.spi.biodedupe.BioDedupeService;
import io.mosip.registration.processor.packet.storage.config.PacketStorageBeanConfig;
import io.mosip.registration.processor.rest.client.config.RestConfigBean;
import io.mosip.registration.processor.status.config.RegistrationStatusBeanConfig;

/**
 * @author M1022006
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class BioDedupeControllerTest {

	@InjectMocks
	BioDedupeController bioDedupeController = new BioDedupeController();

	@MockBean
	BioDedupeService bioDedupeService;

	@Autowired
	private MockMvc mockMvc;

	String regId;

	byte[] file;

	@Before
	public void setUp() {
		regId = "1234";
		file = regId.getBytes();
		Mockito.when(bioDedupeService.getFile(anyString())).thenReturn(file);
	}

	@Test
	public void getFileSuccessTest() throws Exception {

		this.mockMvc
				.perform(MockMvcRequestBuilders.get("/v0.1/registration-processor/bio-dedupe/1234")
						.param("regId", regId).accept(MediaType.ALL_VALUE).contentType(MediaType.ALL_VALUE))
				.andExpect(MockMvcResultMatchers.status().isOk());

	}
}
