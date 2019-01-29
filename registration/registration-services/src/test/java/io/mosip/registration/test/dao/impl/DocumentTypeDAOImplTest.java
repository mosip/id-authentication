package io.mosip.registration.test.dao.impl;

import static org.junit.Assert.assertEquals;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.mosip.registration.dao.impl.DocumentTypeDAOImpl;
import io.mosip.registration.entity.DocumentType;
import io.mosip.registration.entity.GenericId;
import io.mosip.registration.repositories.DocumentTypeRepository;

public class DocumentTypeDAOImplTest {
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@InjectMocks
	private DocumentTypeDAOImpl registrationDocumentTypeDAOImpl;
	@Mock
	private DocumentTypeRepository registrationDocumentTypeRepository;

	@Test
	public void test() {

		List<DocumentType> list = new ArrayList<>();
		DocumentType documentType = new DocumentType();
		documentType.setName("name");
		GenericId genericId = new GenericId();
		genericId.setCode("code");
		genericId.setActive(true);
		documentType.setGenericId(genericId);

		documentType.setCreatedBy("createdBy");
		documentType.setCreatedTimesZone(new Timestamp(new Date().getTime()));

		documentType.setDeleted(true);
		documentType.setDeletedTimesZone(new Timestamp(new Date().getTime()));
		documentType.setUpdatedBy("updatedBy");
		documentType.setUpdatedTimesZone(new Timestamp(new Date().getTime()));
		documentType.setLanguageCode("languageCode");

		list.add(documentType);
		Mockito.when(registrationDocumentTypeRepository.findAll()).thenReturn(list);
		assertEquals(list, registrationDocumentTypeDAOImpl.getDocumentTypes());

	}
}
