package io.mosip.registration.test.dao.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.mosip.registration.dao.impl.ValidDocumentDAOImpl;
import io.mosip.registration.entity.ValidDocument;
import io.mosip.registration.entity.id.GenericId;
import io.mosip.registration.entity.id.ValidDocumentId;
import io.mosip.registration.repositories.ValidDocumentRepository;

public class ValidDocumentDAOImplTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@InjectMocks
	private ValidDocumentDAOImpl validDocumentDAOImpl;
	@Mock
	private ValidDocumentRepository validDocumentRepository;

	@Test
	public void test() {

		ValidDocument validDocument = new ValidDocument();
		ValidDocumentId validDocumentId=new ValidDocumentId();
		validDocumentId.setDocCategoryCode("D101");
		validDocumentId.setDocTypeCode("DC101");
		validDocumentId.setApplicantCode("007");
		validDocument.setValidDocumentId(validDocumentId);
		//validDocument.setDescription("description");
		//validDocument.setCreatedTimesZone(new Timestamp(new Date().getTime()));
		validDocument.setCrBy("createdBy");
		//validDocument.setDeletedTimesZone(new Timestamp(new Date().getTime()));
		validDocument.setLangCode("langCode");
		GenericId genericId = new GenericId();
		genericId.setActive(true);
		genericId.setCode("code");
		//validDocument.setGenericId(genericId);
		List<ValidDocument> list = new ArrayList<>();
		list.add(validDocument);

		Mockito.when(validDocumentRepository.findAll()).thenReturn(list);

		assertEquals(list, validDocumentDAOImpl.getValidDocuments());

	}

}
