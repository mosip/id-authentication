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
import org.modelmapper.ConfigurationException;
import org.modelmapper.MappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.masterdata.dto.TemplateDto;
import io.mosip.kernel.masterdata.entity.Template;
import io.mosip.kernel.masterdata.exception.TemplateFetchException;
import io.mosip.kernel.masterdata.exception.TemplateMappingException;
import io.mosip.kernel.masterdata.exception.TemplateNotFoundException;
import io.mosip.kernel.masterdata.repository.TemplateRepository;
import io.mosip.kernel.masterdata.service.TemplateService;
import io.mosip.kernel.masterdata.utils.ObjectMapperUtil;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@SuppressWarnings("unchecked")
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

	@Test(expected = TemplateFetchException.class)
	public void getAllTemplateFetchExceptionTest() {
		Mockito.when(templateRepository.findAll(Mockito.eq(Template.class)))
				.thenThrow(DataRetrievalFailureException.class);

		templateService.getAllTemplate();
	}

	@Test(expected = TemplateMappingException.class)
	public void getAllTemplateMappingExceptionTest() {
		Mockito.when(templateRepository.findAll(Template.class)).thenReturn(templateList);

		Mockito.when(objectMapperUtil.mapAll(templateList, TemplateDto.class))
				.thenThrow(IllegalArgumentException.class, ConfigurationException.class, MappingException.class);

		templateService.getAllTemplate();
	}

	@Test(expected = TemplateNotFoundException.class)
	public void getAllTemplateNotFoundExceptionTest() {
		templateList = new ArrayList<>();
		Mockito.when(templateRepository.findAll(Mockito.eq(Template.class))).thenReturn(templateList);
		templateService.getAllTemplate();
	}

	@Test(expected = TemplateFetchException.class)
	public void getAllTemplateByLanguageCodeFetchExceptionTest() {
		Mockito.when(templateRepository.findAllByLanguageCode(Mockito.anyString()))
				.thenThrow(DataRetrievalFailureException.class);

		templateService.getAllTemplateByLanguageCode("HIN");
	}

	@Test(expected = TemplateMappingException.class)
	public void getAllTemplateByLanguageCodeMappingExceptionTest() {

		Mockito.when(templateRepository.findAllByLanguageCode(Mockito.anyString())).thenReturn(templateList);
		Mockito.when(objectMapperUtil.mapAll(templateList, TemplateDto.class)).thenThrow(IllegalArgumentException.class,
				ConfigurationException.class, MappingException.class);

		templateService.getAllTemplateByLanguageCode("HIN");
	}

	@Test(expected = TemplateNotFoundException.class)
	public void getAllTemplateByLanguageCodeNotFoundExceptionTest() {
		templateList = new ArrayList<>();
		Mockito.when(templateRepository.findAllByLanguageCode(Mockito.anyString())).thenReturn(templateList);

		templateService.getAllTemplateByLanguageCode("HIN");
	}

	@Test(expected = TemplateFetchException.class)
	public void getAllTemplateByLanguageCodeAndTemplateTypeCodeFetchExceptionTest() {
		Mockito.when(
				templateRepository.findAllByLanguageCodeAndTemplateTypeCode(Mockito.anyString(), Mockito.anyString()))
				.thenThrow(DataRetrievalFailureException.class);
		templateService.getAllTemplateByLanguageCodeAndTemplateTypeCode("HIN", "EMAIL");
	}

	@Test(expected = TemplateMappingException.class)
	public void getAllTemplateByLanguageCodeAndTemplateTypeCodeMappingExceptionTest() {
		Mockito.when(
				templateRepository.findAllByLanguageCodeAndTemplateTypeCode(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(templateList);
		Mockito.when(objectMapperUtil.mapAll(templateList, TemplateDto.class)).thenThrow(IllegalArgumentException.class,
				ConfigurationException.class, MappingException.class);
		templateService.getAllTemplateByLanguageCodeAndTemplateTypeCode("HIN", "EMAIL");
	}

	@Test(expected = TemplateNotFoundException.class)
	public void getAllTemplateByLanguageCodeAndTemplateTypeCodeNotFoundExceptionTest() {
		templateList = new ArrayList<>();
		Mockito.when(
				templateRepository.findAllByLanguageCodeAndTemplateTypeCode(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(templateList);
		templateService.getAllTemplateByLanguageCodeAndTemplateTypeCode("HIN", "EMAIL");
	}
}
