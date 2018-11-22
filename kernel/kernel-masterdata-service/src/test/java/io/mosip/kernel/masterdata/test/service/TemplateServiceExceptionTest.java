package io.mosip.kernel.masterdata.test.service;

/**
 * @author Neha
 * @since 1.0.0
 */
import java.time.LocalDateTime;
import java.time.Month;
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
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.masterdata.entity.Template;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.TemplateRepository;
import io.mosip.kernel.masterdata.service.TemplateService;
import io.mosip.kernel.masterdata.utils.ObjectMapperUtil;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TemplateServiceExceptionTest {

	@MockBean
	private TemplateRepository templateRepository;

	@Autowired
	private TemplateService templateService;

	@MockBean
	private ObjectMapperUtil objectMapperUtil;

	private List<Template> templateList = new ArrayList<>();

	@Before
	public void setUp() {

		Template template = new Template();

		template.setId("3");
		template.setName("Email template");
		template.setFileFormatCode("xml");
		template.setTemplateTypeCode("EMAIL");
		template.setLanguageCode("HIN");
		template.setCreatedBy("Neha");
		template.setCreatedtimes(LocalDateTime.of(2018, Month.NOVEMBER, 12, 0, 0, 0));
		template.setIsActive(true);
		template.setIsDeleted(false);

		templateList.add(template);
	}

	@Test(expected = MasterDataServiceException.class)
	public void getAllTemplateFetchExceptionTest() {
		Mockito.when(templateRepository.findAllByIsActiveTrueAndIsDeletedFalse(Mockito.eq(Template.class)))
				.thenThrow(DataRetrievalFailureException.class);

		templateService.getAllTemplate();
	}

	@Test(expected = DataNotFoundException.class)
	public void getAllTemplateNotFoundExceptionTest() {
		templateList = new ArrayList<>();
		Mockito.when(templateRepository.findAllByIsActiveTrueAndIsDeletedFalse(Mockito.eq(Template.class)))
				.thenReturn(templateList);
		templateService.getAllTemplate();
	}

	@Test(expected = MasterDataServiceException.class)
	public void getAllTemplateByLanguageCodeFetchExceptionTest() {
		Mockito.when(templateRepository.findAllByLanguageCodeAndIsActiveTrueAndIsDeletedFalse(Mockito.anyString()))
				.thenThrow(DataRetrievalFailureException.class);

		templateService.getAllTemplateByLanguageCode("HIN");
	}

	@Test(expected = DataNotFoundException.class)
	public void getAllTemplateByLanguageCodeNotFoundExceptionTest() {
		templateList = new ArrayList<>();
		Mockito.when(templateRepository.findAllByLanguageCodeAndIsActiveTrueAndIsDeletedFalse(Mockito.anyString()))
				.thenReturn(templateList);

		templateService.getAllTemplateByLanguageCode("HIN");
	}

	@Test(expected = MasterDataServiceException.class)
	public void getAllTemplateByLanguageCodeAndTemplateTypeCodeFetchExceptionTest() {
		Mockito.when(templateRepository.findAllByLanguageCodeAndTemplateTypeCodeAndIsActiveTrueAndIsDeletedFalse(
				Mockito.anyString(), Mockito.anyString())).thenThrow(DataRetrievalFailureException.class);
		templateService.getAllTemplateByLanguageCodeAndTemplateTypeCode("HIN", "EMAIL");
	}

	@Test(expected = DataNotFoundException.class)
	public void getAllTemplateByLanguageCodeAndTemplateTypeCodeNotFoundExceptionTest() {
		templateList = new ArrayList<>();
		Mockito.when(templateRepository.findAllByLanguageCodeAndTemplateTypeCodeAndIsActiveTrueAndIsDeletedFalse(
				Mockito.anyString(), Mockito.anyString())).thenReturn(templateList);
		templateService.getAllTemplateByLanguageCodeAndTemplateTypeCode("HIN", "EMAIL");
	}
}
