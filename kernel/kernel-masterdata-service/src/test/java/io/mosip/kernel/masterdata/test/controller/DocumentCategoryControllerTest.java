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

	private static final String EXPECTED_LIST = "[\r\n" + "  {\r\n" + "    \"code\": \"101\",\r\n"
			+ "    \"name\": \"POI\",\r\n" + "    \"description\": null,\r\n" + "    \"langCode\": \"ENG\",\r\n"
			+ "    \"createdBy\": \"Neha\",\r\n" + "    \"updatedBy\": null,\r\n" + "    \"active\": true,\r\n"
			+ "    \"deleted\": false\r\n" + "  },\r\n" + "  {\r\n" + "    \"code\": \"102\",\r\n"
			+ "    \"name\": \"POR\",\r\n" + "    \"description\": null,\r\n" + "    \"langCode\": \"ENG\",\r\n"
			+ "    \"createdBy\": \"Neha\",\r\n" + "    \"updatedBy\": null,\r\n" + "    \"active\": true,\r\n"
			+ "    \"deleted\": false\r\n" + "  }\r\n" + "]";

	private static final String EXPECTED_OBJECT = "{\r\n" + "  \"code\": \"101\",\r\n" + "  \"name\": \"POI\",\r\n"
			+ "  \"description\": null,\r\n" + "  \"langCode\": \"ENG\",\r\n" + "  \"createdBy\": \"Neha\",\r\n"
			+ "  \"updatedBy\": null,\r\n" + "  \"active\": true,\r\n" + "  \"deleted\": false\r\n" + "}";

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
		documentCategoryDto1.setActive(true);
		documentCategoryDto1.setDeleted(false);
		documentCategoryDto1.setDescription(null);
		documentCategoryDto1.setCreatedBy("Neha");
		documentCategoryDto1.setUpdatedBy(null);

		documentCategoryDto2 = new DocumentCategoryDto();
		documentCategoryDto2.setCode("102");
		documentCategoryDto2.setName("POR");
		documentCategoryDto2.setLangCode("ENG");
		documentCategoryDto2.setActive(true);
		documentCategoryDto2.setDeleted(false);
		documentCategoryDto2.setDescription(null);
		documentCategoryDto2.setCreatedBy("Neha");
		documentCategoryDto2.setUpdatedBy(null);

		documentCategoryDtoList.add(documentCategoryDto1);
		documentCategoryDtoList.add(documentCategoryDto2);
	}

	@Test
	public void fetchAllDocumentCategoryTest() throws Exception {

		Mockito.when(service.getAllDocumentCategory()).thenReturn(documentCategoryDtoList);

		mockMvc.perform(MockMvcRequestBuilders.get("/documentcategories/all"))
				.andExpect(MockMvcResultMatchers.content().json(EXPECTED_LIST))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void fetchAllDocumentCategoryUsingLangCodeTest() throws Exception {

		Mockito.when(service.getAllDocumentCategoryByLaguageCode(Mockito.anyString()))
				.thenReturn(documentCategoryDtoList);

		mockMvc.perform(MockMvcRequestBuilders.get("/documentcategories/all/ENG"))
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
