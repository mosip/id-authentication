package io.mosip.kernel.masterdata.test.controller;

/**
 * @author Neha
 * @since 1.0.0
 */
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import io.mosip.kernel.masterdata.dto.TemplateDto;
import io.mosip.kernel.masterdata.service.TemplateService;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TemplateControllerTest {

	@Autowired
	MockMvc mockMvc;

	@MockBean
	private TemplateService templateService;

	private static final String EXPECTED_LIST = "[\r\n" + 
			"  {\r\n" + 
			"	\"id\": \"3\",\r\n" + 
			"    \"name\": \"Email template\",\r\n" + 
			"    \"description\": null,\r\n" + 
			"	\"fileFormatCode\": \"xml\",\r\n" + 
			"	\"model\": null,\r\n" + 
			"	\"fileText\": null,\r\n" + 
			"	\"moduleId\": null,\r\n" + 
			"	\"moduleName\": null,\r\n" + 
			"	\"templateTypeCode\": \"EMAIL\",\r\n" + 
			"    \"languageCode\": \"HIN\"\r\n" + 
			"  }\r\n" + 
			"]";

	private List<TemplateDto> templateDtoList = new ArrayList<>();

	@Before
	public void setUp() {

		TemplateDto templateDto = new TemplateDto();

		templateDto.setId("3");
		templateDto.setName("Email template");
		templateDto.setFileFormatCode("xml");
		templateDto.setTemplateTypeCode("EMAIL");
		templateDto.setLanguageCode("HIN");

		templateDtoList.add(templateDto);
	}

	@Test
	public void getAllTemplateByTest() throws Exception {

		Mockito.when(templateService.getAllTemplate()).thenReturn(templateDtoList);

		mockMvc.perform(MockMvcRequestBuilders.get("/templates"))
				.andExpect(MockMvcResultMatchers.content().json(EXPECTED_LIST)).andExpect(status().isOk());

	}

	@Test
	public void getAllTemplateByLanguageCodeTest() throws Exception {

		Mockito.when(templateService.getAllTemplateByLanguageCode(Mockito.anyString())).thenReturn(templateDtoList);

		mockMvc.perform(MockMvcRequestBuilders.get("/templates/HIN"))
				.andExpect(MockMvcResultMatchers.content().json(EXPECTED_LIST)).andExpect(status().isOk());
	}

	@Test
	public void getAllTemplateByLanguageCodeAndTemplateTypeCodeTest() throws Exception {

		Mockito.when(templateService.getAllTemplateByLanguageCodeAndTemplateTypeCode(Mockito.anyString(),
				Mockito.anyString())).thenReturn(templateDtoList);

		mockMvc.perform(MockMvcRequestBuilders.get("/templates/HIN/EMAIL"))
				.andExpect(MockMvcResultMatchers.content().json(EXPECTED_LIST)).andExpect(status().isOk());
	}

}
