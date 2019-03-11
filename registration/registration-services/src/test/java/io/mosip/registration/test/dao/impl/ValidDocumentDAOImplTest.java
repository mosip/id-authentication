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
import io.mosip.registration.entity.ApplicantValidDocument;
import io.mosip.registration.entity.id.ApplicantValidDocumentID;
import io.mosip.registration.entity.id.GenericId;
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

		ApplicantValidDocument validDocument = new ApplicantValidDocument();
		ApplicantValidDocumentID validDocumentId=new ApplicantValidDocumentID();
		validDocumentId.setDocCatCode("D101");
		validDocumentId.setDocTypeCode("DC101");
		validDocumentId.setAppTypeCode("007");
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
		List<ApplicantValidDocument> list = new ArrayList<>();
		list.add(validDocument);

		Mockito.when(validDocumentRepository.findAll()).thenReturn(list);

		assertEquals(list, validDocumentDAOImpl.getValidDocuments());

	}

}
