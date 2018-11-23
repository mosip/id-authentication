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

import io.mosip.kernel.masterdata.dto.DocumentTypeDto;
import io.mosip.kernel.masterdata.dto.ValidDocumentTypeResponseDto;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.service.DocumentTypeService;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class DocumentTypeControllerTest {
	@Autowired
	MockMvc mockMvc;

	@MockBean
	private DocumentTypeService documentTypeService;

	private final String expected = "{ \"documents\": [ { \"code\": \"addhar\", \"name\": \"adhar card\", \"description\": \"Uid\", \"langCode\": \"eng\", \"isActive\": true }, { \"code\": \"residensial\", \"name\": \"residensial_prof\", \"description\": \"document for residential prof\", \"langCode\": \"eng\", \"isActive\": true } ] }";

	ValidDocumentTypeResponseDto validDocumentTypeResponseDto = null;

	List<DocumentTypeDto> documents = null;

	@Before
	public void Setup() {

		documents = new ArrayList<DocumentTypeDto>();
		DocumentTypeDto documentType = new DocumentTypeDto();
		documentType.setCode("addhar");
		documentType.setName("adhar card");
		documentType.setDescription("Uid");
		documentType.setLangCode("eng");
		documentType.setIsActive(true);
		documents.add(documentType);
		DocumentTypeDto documentType1 = new DocumentTypeDto();
		documentType1.setCode("residensial");
		documentType1.setName("residensial_prof");
		documentType1.setDescription("document for residential prof");
		documentType1.setLangCode("eng");
		documentType1.setIsActive(true);
		documents.add(documentType1);
		validDocumentTypeResponseDto = new ValidDocumentTypeResponseDto(documents);

	}

	@Test
	public void testGetDoucmentTypesForDocumentCategoryAndLangCode() throws Exception {

		Mockito.when(documentTypeService.getAllValidDocumentType(Mockito.anyString(), Mockito.anyString()))
				.thenReturn((documents));
		mockMvc.perform(MockMvcRequestBuilders.get("/documenttypes/poa/eng"))
				.andExpect(MockMvcResultMatchers.content().json(expected))
				.andExpect(MockMvcResultMatchers.status().isOk());

	}

	@Test
	public void testDocumentCategoryNotFoundException() throws Exception {
		Mockito.when(documentTypeService.getAllValidDocumentType(Mockito.anyString(), Mockito.anyString()))
				.thenThrow(new DataNotFoundException("KER-DOC-10001",
						"No documents found for specified document category code and language code"));
		mockMvc.perform(MockMvcRequestBuilders.get("/documenttypes/poc/eng"))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	public void testDocumentCategoryFetchException() throws Exception {
		Mockito.when(documentTypeService.getAllValidDocumentType(Mockito.anyString(), Mockito.anyString()))
				.thenThrow(new MasterDataServiceException("KER-DOC-10000", "exception during fatching data from db"));
		mockMvc.perform(MockMvcRequestBuilders.get("/documenttypes/poc/eng"))
				.andExpect(MockMvcResultMatchers.status().isInternalServerError());
	}
}
