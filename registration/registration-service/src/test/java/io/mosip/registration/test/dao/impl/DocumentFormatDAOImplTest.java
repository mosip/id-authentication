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

import io.mosip.registration.dao.impl.DocumentFormatDAOImpl;
import io.mosip.registration.entity.DocumentFormat;
import io.mosip.registration.entity.GenericId;
import io.mosip.registration.repositories.DocumentFormatRepository;

public class DocumentFormatDAOImplTest {
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@InjectMocks
	private DocumentFormatDAOImpl registationDocumentFormatDAOImpl;
	@Mock
	private DocumentFormatRepository registrationDocumentFormatRepository;

	@Test
	public void test() {
		DocumentFormat documentFormat = new DocumentFormat();
		documentFormat.setDescription("description");
		documentFormat.setName("name");
		documentFormat.setCreatedTimesZone(new Timestamp(new Date().getTime()));
		documentFormat.setCreatedBy("createdBy");
		documentFormat.setDeletedTimesZone(new Timestamp(new Date().getTime()));
		documentFormat.setLanguageCode("languageCode");
		GenericId genericId = new GenericId();
		genericId.setActive(true);
		genericId.setCode("code");
		documentFormat.setGenericId(genericId);
		List<DocumentFormat> list = new ArrayList<>();
		list.add(documentFormat);
		Mockito.when(registrationDocumentFormatRepository.findAll()).thenReturn(list);

		assertEquals(list, registationDocumentFormatDAOImpl.getDocumentFormats());
	}

}
