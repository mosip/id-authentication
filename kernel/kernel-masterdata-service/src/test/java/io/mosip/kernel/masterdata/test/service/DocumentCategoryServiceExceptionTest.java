package io.mosip.kernel.masterdata.test.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modelmapper.ConfigurationException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.test.context.junit4.SpringRunner;

import com.jayway.jsonpath.spi.mapper.MappingException;

import io.mosip.kernel.masterdata.dto.DocumentCategoryDto;
import io.mosip.kernel.masterdata.entity.DocumentCategory;
import io.mosip.kernel.masterdata.exception.DocumentCategoryFetchException;
import io.mosip.kernel.masterdata.exception.DocumentCategoryMappingException;
import io.mosip.kernel.masterdata.exception.DocumentCategoryNotFoundException;
import io.mosip.kernel.masterdata.repository.DocumentCategoryRepository;
import io.mosip.kernel.masterdata.service.DocumentCategoryService;
import io.mosip.kernel.masterdata.utils.ObjectMapperUtil;

/**
 * 
 * @author  Neha
 * @since 1.0.0
 *
 */
@SuppressWarnings("unchecked")
@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class DocumentCategoryServiceExceptionTest {

	@MockBean
	DocumentCategoryRepository documentCategoryRepository;

	@Autowired
	DocumentCategoryService documentCategoryService;

	@MockBean
	ObjectMapperUtil objectMapperUtil;
	
	@MockBean
	ModelMapper modelMapper;
	
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

	@Test(expected = DocumentCategoryFetchException.class)
	public void getAllDocumentCategorysFetchException() {
		Mockito.when(documentCategoryRepository.findAll(Mockito.eq(DocumentCategory.class))).thenThrow(DataRetrievalFailureException.class);
		documentCategoryService.getAllDocumentCategory();
	}
	
	@Test(expected = DocumentCategoryMappingException.class)
	public void getAllDocumentCategorysMappingException() {
		Mockito.when(documentCategoryRepository.findAll(DocumentCategory.class)).thenReturn(documentCategoryList);
		Mockito.when(objectMapperUtil.mapAll(documentCategoryList, DocumentCategoryDto.class)).thenThrow(IllegalArgumentException.class, ConfigurationException.class, MappingException.class);
		documentCategoryService.getAllDocumentCategory();
	}

	@Test(expected = DocumentCategoryNotFoundException.class)
	public void getAllDocumentCategoryNotFoundException() {
		Mockito.when(documentCategoryRepository.findAll()).thenReturn(new ArrayList<DocumentCategory>());
		documentCategoryService.getAllDocumentCategory();
	}

	@Test(expected = DocumentCategoryFetchException.class)
	public void getAllDocumentCategoryByLaguageCodeFetchException() {
		Mockito.when(documentCategoryRepository.findAllByLangCode(Mockito.anyString())).thenThrow(DataRetrievalFailureException.class);
		documentCategoryService.getAllDocumentCategoryByLaguageCode(Mockito.anyString());
	}
	
	@Test(expected = DocumentCategoryMappingException.class)
	public void getAllDocumentCategoryByLaguageCodeMappingException() {
		Mockito.when(documentCategoryRepository.findAllByLangCode(Mockito.anyString())).thenReturn(documentCategoryList);
		Mockito.when(objectMapperUtil.mapAll(documentCategoryList, DocumentCategoryDto.class)).thenThrow(IllegalArgumentException.class, ConfigurationException.class, MappingException.class);
		documentCategoryService.getAllDocumentCategoryByLaguageCode(Mockito.anyString());
	}

	@Test(expected = DocumentCategoryNotFoundException.class)
	public void getAllDocumentCategoryByLaguageCodeNotFound() {
		Mockito.when(documentCategoryRepository.findAllByLangCode(Mockito.anyString())).thenReturn(new ArrayList<DocumentCategory>());
		documentCategoryService.getAllDocumentCategoryByLaguageCode(Mockito.anyString());
	}

	@Test(expected = DocumentCategoryFetchException.class)
	public void getDocumentCategoryByCodeAndLangCodeFetchException() {
		Mockito.when(documentCategoryRepository.findByCodeAndLangCode(Mockito.anyString(), Mockito.anyString())).thenThrow(DataRetrievalFailureException.class);
		documentCategoryService.getDocumentCategoryByCodeAndLangCode(Mockito.anyString(), Mockito.anyString());
	}
	
	@Test(expected = DocumentCategoryMappingException.class)
	public void getDocumentCategoryByCodeAndLangCodeMappingException() {
		Mockito.when(documentCategoryRepository.findByCodeAndLangCode(Mockito.anyString(), Mockito.anyString())).thenReturn(documentCategory1);
		Mockito.when(modelMapper.map(documentCategory1, DocumentCategoryDto.class)).thenThrow(IllegalArgumentException.class, ConfigurationException.class, MappingException.class);
		documentCategoryService.getDocumentCategoryByCodeAndLangCode(Mockito.anyString(), Mockito.anyString());
	}

	@Test(expected = DocumentCategoryNotFoundException.class)
	public void getDocumentCategoryByCodeAndLangCodeNotFoundException() {
		Mockito.when(documentCategoryRepository.findByCodeAndLangCode(Mockito.anyString(), Mockito.anyString())).thenReturn(null);
		documentCategoryService.getDocumentCategoryByCodeAndLangCode(Mockito.anyString(), Mockito.anyString());
	}
}
