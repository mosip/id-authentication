package io.mosip.registration.test.dao.impl;

import static org.junit.Assert.assertEquals;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.mosip.registration.dao.impl.DocumentCategoryDAOImpl;
import io.mosip.registration.entity.DocumentCategory;
import io.mosip.registration.entity.GenericId;
import io.mosip.registration.repositories.DocumentCategoryRepository;

public class DocumentCategoryDAOImplTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@InjectMocks
	private DocumentCategoryDAOImpl registrationDocumentCategoryDAOImpl;
	@Mock
	private DocumentCategoryRepository registrationDocumentCategoryRepository;

	@Test
	public void test() {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		DocumentCategory documentCategory = new DocumentCategory();
		documentCategory.setDescription("description");
		documentCategory.setName("name");
		documentCategory.setCreatedTimesZone(timestamp);
		documentCategory.setCreatedBy("createdBy");
		documentCategory.setDeletedTimesZone(timestamp);
		documentCategory.setLanguageCode("languageCode");
		GenericId genericId = new GenericId();
		genericId.setActive(true);
		genericId.setCode("code");
		documentCategory.setGenericId(genericId);
		List<DocumentCategory> list = new ArrayList<>();
		list.add(documentCategory);

		Mockito.when(registrationDocumentCategoryRepository.findAll()).thenReturn(list);

		assertEquals(list, registrationDocumentCategoryDAOImpl.getDocumentCategories());

	}

}
