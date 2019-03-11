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
		validDocument.setCrBy("createdBy");
		validDocument.setLangCode("langCode");
		List<ApplicantValidDocument> list = new ArrayList<>();
		list.add(validDocument);

		Mockito.when(validDocumentRepository.findByValidDocumentIdAppTypeCodeAndDocumentCategoryCodeAndLangCode(Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString())).thenReturn(list);

		assertEquals(list, validDocumentDAOImpl.getValidDocuments("NFR", "POA", "eng"));

	}

}
