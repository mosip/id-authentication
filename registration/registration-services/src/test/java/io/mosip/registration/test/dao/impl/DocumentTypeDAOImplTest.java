package io.mosip.registration.test.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
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
import io.mosip.registration.entity.id.GenericId;
import io.mosip.registration.repositories.DocumentTypeRepository;

public class DocumentTypeDAOImplTest {
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@InjectMocks
	private DocumentTypeDAOImpl registrationDocumentTypeDAOImpl;
	@Mock
	private DocumentTypeRepository registrationDocumentTypeRepository;

	@Test
	public void getDocumentTypesTest() {

		List<DocumentType> list = new ArrayList<>();
		DocumentType documentType = new DocumentType();
		documentType.setName("name");
		GenericId genericId = new GenericId();
		genericId.setCode("code");
		genericId.setActive(true);
		//documentType.setGenericId(genericId);

		documentType.setCrBy("createdBy");
		//documentType.setCreatedTimesZone(new Timestamp(new Date().getTime()));

		//documentType.setDeleted(true);
		//documentType.setDeletedTimesZone(new Timestamp(new Date().getTime()));
		documentType.setUpdBy("updatedBy");
		//documentType.setUpdatedTimesZone(new Timestamp(new Date().getTime()));
		documentType.setLangCode("eng");

		list.add(documentType);
		Mockito.when(registrationDocumentTypeRepository.findAll()).thenReturn(list);
		assertEquals(list, registrationDocumentTypeDAOImpl.getDocumentTypes());

	}
	@Test
	public void getDocTypeByNameTest()
	{
		List<DocumentType> list=new ArrayList<>();
		Mockito.when(registrationDocumentTypeRepository.findByIsActiveTrueAndName(Mockito.anyString())).thenReturn(list);
		assertNotNull(registrationDocumentTypeDAOImpl.getDocTypeByName("docTypeName"));
	}
}
