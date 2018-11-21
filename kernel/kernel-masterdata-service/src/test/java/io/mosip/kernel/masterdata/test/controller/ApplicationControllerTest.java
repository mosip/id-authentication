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

import io.mosip.kernel.masterdata.dto.ApplicationDto;
import io.mosip.kernel.masterdata.service.ApplicationService;

/**
 * 
 * @author Neha
 * @since 1.0.0
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ApplicationControllerTest {

	@Autowired
	MockMvc mockMvc;

	@MockBean
	private ApplicationService applicationService;

	private static final String EXPECTED_LIST = "[\n" + "  {\n" + "    \"code\": \"101\",\n"
			+ "    \"name\": \"pre-registeration\",\n" + "    \"description\": \"Pre-registration Application Form\",\n"
			+ "    \"languageCode\": \"ENG\"\n" + "  }\n" + "]";

	private static final String EXPECTED_OBJECT = "{\n" + "    \"code\": \"101\",\n"
			+ "    \"name\": \"pre-registeration\",\n" + "    \"description\": \"Pre-registration Application Form\",\n"
			+ "    \"languageCode\": \"ENG\"\n" + "  }";

	private ApplicationDto applicationDto = new ApplicationDto();

	private List<ApplicationDto> applicationDtoList = new ArrayList<>();

	@Before
	public void setUp() {
		applicationDto.setCode("101");
		applicationDto.setName("pre-registeration");
		applicationDto.setDescription("Pre-registration Application Form");
		applicationDto.setLanguageCode("ENG");
		

		applicationDtoList.add(applicationDto);
	}

	@Test
	public void fetchAllApplicationTest() throws Exception {

		Mockito.when(applicationService.getAllApplication()).thenReturn(applicationDtoList);

		mockMvc.perform(MockMvcRequestBuilders.get("/applicationtypes"))
				.andExpect(MockMvcResultMatchers.content().json(EXPECTED_LIST))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void fetchAllApplicationUsingLangCodeTest() throws Exception {
		Mockito.when(applicationService.getAllApplicationByLanguageCode(Mockito.anyString()))
				.thenReturn(applicationDtoList);
		mockMvc.perform(MockMvcRequestBuilders.get("/applicationtypes/ENG"))
				.andExpect(MockMvcResultMatchers.content().json(EXPECTED_LIST))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void fetchApplicationUsingCodeAndLangCode() throws Exception {
		Mockito.when(applicationService.getApplicationByCodeAndLanguageCode(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(applicationDto);
		mockMvc.perform(MockMvcRequestBuilders.get("/applicationtypes/101/ENG"))
				.andExpect(MockMvcResultMatchers.content().json(EXPECTED_OBJECT))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}
}
