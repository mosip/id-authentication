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

import io.mosip.kernel.masterdata.dto.DocumentCategoryDto;
import io.mosip.kernel.masterdata.service.DocumentCategoryService;

/**
 * 
 * @author Neha
 * @since 1.0.0
 *
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class DocumentCategoryControllerTest {

	private static final String EXPECTED_LIST = "[\n" + 
			"  {\n" + 
			"    \"code\": \"101\",\n" + 
			"    \"name\": \"POI\",\n" + 
			"    \"description\": null,\n" + 
			"    \"langCode\": \"ENG\"\n" + 
			"  },\n" + 
			"  {\n" + 
			"    \"code\": \"102\",\n" + 
			"    \"name\": \"POR\",\n" + 
			"    \"description\": null,\n" + 
			"    \"langCode\": \"ENG\"\n" + 
			"  }\n" + 
			"]";

	private static final String EXPECTED_OBJECT = "{\n" + 
			"    \"code\": \"101\",\n" + 
			"    \"name\": \"POI\",\n" + 
			"    \"description\": null,\n" + 
			"    \"langCode\": \"ENG\"\n" + 
			"  }";

	private DocumentCategoryDto documentCategoryDto1;
	private DocumentCategoryDto documentCategoryDto2;

	private List<DocumentCategoryDto> documentCategoryDtoList = new ArrayList<>();

	@Autowired
	MockMvc mockMvc;

	@MockBean
	private DocumentCategoryService service;

	@Before
	public void setUp() {

		documentCategoryDto1 = new DocumentCategoryDto();
		documentCategoryDto1.setCode("101");
		documentCategoryDto1.setName("POI");
		documentCategoryDto1.setLangCode("ENG");
	
		documentCategoryDto2 = new DocumentCategoryDto();
		documentCategoryDto2.setCode("102");
		documentCategoryDto2.setName("POR");
		documentCategoryDto2.setLangCode("ENG");

		documentCategoryDtoList.add(documentCategoryDto1);
		documentCategoryDtoList.add(documentCategoryDto2);
	}

	@Test
	public void fetchAllDocumentCategoryTest() throws Exception {

		Mockito.when(service.getAllDocumentCategory()).thenReturn(documentCategoryDtoList);

		mockMvc.perform(MockMvcRequestBuilders.get("/documentcategories"))
				.andExpect(MockMvcResultMatchers.content().json(EXPECTED_LIST))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void fetchAllDocumentCategoryUsingLangCodeTest() throws Exception {

		Mockito.when(service.getAllDocumentCategoryByLaguageCode(Mockito.anyString()))
				.thenReturn(documentCategoryDtoList);

		mockMvc.perform(MockMvcRequestBuilders.get("/documentcategories/ENG"))
				.andExpect(MockMvcResultMatchers.content().json(EXPECTED_LIST))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void fetchDocumentCategoryUsingCodeAndLangCodeTest() throws Exception {

		Mockito.when(service.getDocumentCategoryByCodeAndLangCode(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(documentCategoryDto1);

		mockMvc.perform(MockMvcRequestBuilders.get("/documentcategories/101/ENG"))
				.andExpect(MockMvcResultMatchers.content().json(EXPECTED_OBJECT))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}
}
