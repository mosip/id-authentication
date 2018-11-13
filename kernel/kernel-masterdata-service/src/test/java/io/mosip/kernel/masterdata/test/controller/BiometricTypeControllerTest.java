package io.mosip.kernel.masterdata.test.controller;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import io.mosip.kernel.masterdata.dto.BiometricTypeDto;
import io.mosip.kernel.masterdata.service.BiometricTypeService;

/**
 * 
 * @author Neha
 * @since 1.0.0
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class BiometricTypeControllerTest {

	@Autowired
	MockMvc mockMvc;

	@MockBean
	private BiometricTypeService biometricTypeService;

	private static final String EXPECTED_LIST = "[\r\n" + "  {\r\n" + "    \"code\": \"1\",\r\n"
			+ "    \"name\": \"DNA MATCHING\",\r\n" + "    \"description\": null,\r\n"
			+ "    \"langCode\": \"ENG\",\r\n" + "    \"createdBy\": \"Neha\",\r\n" + "    \"updatedBy\": null,\r\n"
			+ "    \"active\": true,\r\n" + "    \"deleted\": false\r\n" + "  },\r\n" + "  {\r\n"
			+ "    \"code\": \"3\",\r\n" + "    \"name\": \"EYE SCAN\",\r\n" + "    \"description\": null,\r\n"
			+ "    \"langCode\": \"ENG\",\r\n" + "    \"createdBy\": \"Neha\",\r\n" + "    \"updatedBy\": null,\r\n"
			+ "    \"active\": true,\r\n" + "    \"deleted\": false\r\n" + "  }\r\n" + "]";

	private static final String EXPECTED_OBJECT = "{\r\n" + "    \"code\": \"1\",\r\n"
			+ "    \"name\": \"DNA MATCHING\",\r\n" + "    \"description\": null,\r\n"
			+ "    \"langCode\": \"ENG\",\r\n" + "    \"createdBy\": \"Neha\",\r\n" + "    \"updatedBy\": null,\r\n"
			+ "    \"active\": true,\r\n" + "    \"deleted\": false\r\n" + "  }";

	private BiometricTypeDto biometricTypeDto1 = new BiometricTypeDto();
	private BiometricTypeDto biometricTypeDto2 = new BiometricTypeDto();

	private List<BiometricTypeDto> biometricTypeDtoList = new ArrayList<>();

	@Before
	public void setUp() {
		biometricTypeDto1.setCode("1");
		biometricTypeDto1.setName("DNA MATCHING");
		biometricTypeDto1.setDescription(null);
		biometricTypeDto1.setLangCode("ENG");
		biometricTypeDto1.setActive(true);
		biometricTypeDto1.setCreatedBy("Neha");
		biometricTypeDto1.setUpdatedBy(null);
		biometricTypeDto1.setDeleted(false);

		biometricTypeDto2.setCode("3");
		biometricTypeDto2.setName("EYE SCAN");
		biometricTypeDto2.setDescription(null);
		biometricTypeDto2.setLangCode("ENG");
		biometricTypeDto2.setActive(true);
		biometricTypeDto2.setCreatedBy("Neha");
		biometricTypeDto2.setUpdatedBy(null);
		biometricTypeDto2.setDeleted(false);

		biometricTypeDtoList.add(biometricTypeDto1);
		biometricTypeDtoList.add(biometricTypeDto2);
	}

	@Test
	public void fetchAllBioMetricTypeTest() throws Exception {

		Mockito.when(biometricTypeService.getAllBiometricTypes()).thenReturn(biometricTypeDtoList);

		mockMvc.perform(MockMvcRequestBuilders.get("/biometrictypes"))
				.andExpect(MockMvcResultMatchers.content().json(EXPECTED_LIST))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void fetchAllBiometricTypeUsingLangCodeTest() throws Exception {
		Mockito.when(biometricTypeService.getAllBiometricTypesByLanguageCode(Mockito.anyString()))
				.thenReturn(biometricTypeDtoList);
		mockMvc.perform(MockMvcRequestBuilders.get("/biometrictypes/ENG"))
				.andExpect(MockMvcResultMatchers.content().json(EXPECTED_LIST))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void fetchBiometricTypeUsingCodeAndLangCode() throws Exception {
		Mockito.when(biometricTypeService.getBiometricTypeByCodeAndLangCode(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(biometricTypeDto1);
		mockMvc.perform(MockMvcRequestBuilders.get("/biometrictypes/1/ENG"))
				.andExpect(MockMvcResultMatchers.content().json(EXPECTED_OBJECT))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}
}
