package io.mosip.registration.test.service;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.mosip.registration.dao.DocumentCategoryDAO;
import io.mosip.registration.service.doc.category.impl.DocumentCategoryServiceImpl;

public class DocumentCategoryServiceTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@Mock
	private DocumentCategoryDAO documentCategoryDAO;

	@InjectMocks
	private DocumentCategoryServiceImpl documentCategoryServiceImpl;

	@Test
	public void getDocumentCategoriesTest() {

		Mockito.when(documentCategoryDAO.getDocumentCategories()).thenReturn(new ArrayList<>());

		Assert.assertNotNull(documentCategoryServiceImpl.getDocumentCategories());

	}

	@Test
	public void getDocumentCategoriesByLangCodeTest() {
		Mockito.when(documentCategoryDAO.getDocumentCategoriesByLangCode("eng")).thenReturn(new ArrayList<>());

		Assert.assertNotNull(documentCategoryServiceImpl.getDocumentCategoriesByLangCode("eng"));

	}
}
