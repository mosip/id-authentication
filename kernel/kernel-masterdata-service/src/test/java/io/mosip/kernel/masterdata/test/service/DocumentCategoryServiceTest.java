package io.mosip.kernel.masterdata.test.service;

import static org.junit.Assert.assertEquals;

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

import io.mosip.kernel.masterdata.dto.DocumentCategoryDto;
import io.mosip.kernel.masterdata.entity.DocumentCategory;
import io.mosip.kernel.masterdata.repository.DocumentCategoryRepository;
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
public class DocumentCategoryServiceTest {

	@MockBean
	DocumentCategoryRepository repository;

	@Autowired
	DocumentCategoryService service;

	private DocumentCategory documentCategory1;
	private DocumentCategory documentCategory2;

	private List<DocumentCategory> documentCategoryList = new ArrayList<>();

	@Before
	public void setUp() {
		documentCategory1 = new DocumentCategory();
		documentCategory1.setCode("101");
		documentCategory1.setName("POI");
		documentCategory1.setLangCode("ENG");
		documentCategory1.setIsActive(true);
		documentCategory1.setIsDeleted(false);
		documentCategory1.setDescription(null);
		documentCategory1.setCreatedBy("Neha");
		documentCategory1.setUpdatedBy(null);

		documentCategory2 = new DocumentCategory();
		documentCategory2.setCode("102");
		documentCategory2.setName("POR");
		documentCategory2.setLangCode("ENG");
		documentCategory2.setIsActive(true);
		documentCategory2.setIsDeleted(false);
		documentCategory2.setDescription(null);
		documentCategory2.setCreatedBy("Neha");
		documentCategory2.setUpdatedBy(null);

		documentCategoryList.add(documentCategory1);
		documentCategoryList.add(documentCategory2);
	}

	@Test
	public void getAllDocumentCategorySuccess() {

		Mockito.when(repository.findAllByIsActiveTrueAndIsDeletedFalse(DocumentCategory.class)).thenReturn(documentCategoryList);
		List<DocumentCategoryDto> DocumentCategoryDtoList = service.getAllDocumentCategory();
		assertEquals(documentCategoryList.get(0).getCode(), DocumentCategoryDtoList.get(0).getCode());
		assertEquals(documentCategoryList.get(0).getName(), DocumentCategoryDtoList.get(0).getName());
	}

	@Test
	public void getAllDocumentCategoryByLaguageCodeSuccess() {
		Mockito.when(repository.findAllByLangCodeAndIsActiveTrueAndIsDeletedFalse(Mockito.anyString())).thenReturn(documentCategoryList);
		List<DocumentCategoryDto> DocumentCategoryDtoList = service.getAllDocumentCategoryByLaguageCode("ENG");
		assertEquals(documentCategoryList.get(0).getCode(), DocumentCategoryDtoList.get(0).getCode());
		assertEquals(documentCategoryList.get(0).getName(), DocumentCategoryDtoList.get(0).getName());
	}

	@Test
	public void getDocumentCategoryByCodeAndLangCodeSuccess() {
		Mockito.when(repository.findByCodeAndLangCodeAndIsActiveTrueAndIsDeletedFalse(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(documentCategory1);
		DocumentCategoryDto actual = service.getDocumentCategoryByCodeAndLangCode("101", "ENG");
		assertEquals(documentCategory1.getCode(), actual.getCode());
		assertEquals(documentCategory1.getName(), actual.getName());
	}

}
