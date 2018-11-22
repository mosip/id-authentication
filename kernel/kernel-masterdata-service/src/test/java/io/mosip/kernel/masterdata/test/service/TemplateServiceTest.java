package io.mosip.kernel.masterdata.test.service;

/**
 * @author Neha
 * @since 1.0.0
 */
import static org.junit.Assert.assertEquals;

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
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.masterdata.dto.TemplateDto;
import io.mosip.kernel.masterdata.entity.Template;
import io.mosip.kernel.masterdata.repository.TemplateRepository;
import io.mosip.kernel.masterdata.service.TemplateService;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TemplateServiceTest {

	@MockBean
	private TemplateRepository templateRepository;

	@Autowired
	private TemplateService templateService;

	private List<Template> templateList = new ArrayList<>();
	
	private List<TemplateDto> templateDtoList;

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

	@Test
	public void getAllTemplateTest() {
		Mockito.when(templateRepository.findAllByIsActiveTrueAndIsDeletedFalse(Template.class)).thenReturn(templateList);
		templateDtoList = templateService.getAllTemplate();
		
		assertEquals(templateList.get(0).getId(), templateDtoList.get(0).getId());
		assertEquals(templateList.get(0).getName(), templateDtoList.get(0).getName());
	}
	
	@Test
	public void getAllTemplateByLanguageCodeTest() {
		Mockito.when(templateRepository.findAllByLanguageCodeAndIsActiveTrueAndIsDeletedFalse(Mockito.anyString())).thenReturn(templateList);
		templateDtoList = templateService.getAllTemplateByLanguageCode(Mockito.anyString());
		
		assertEquals(templateList.get(0).getId(), templateDtoList.get(0).getId());
		assertEquals(templateList.get(0).getName(), templateDtoList.get(0).getName());
	}
	
	@Test
	public void getAllTemplateByLanguageCodeAndTemplateTypeCodeTest() {
		Mockito.when(templateRepository.findAllByLanguageCodeAndTemplateTypeCodeAndIsActiveTrueAndIsDeletedFalse(Mockito.anyString(), Mockito.anyString())).thenReturn(templateList);
		templateDtoList = templateService.getAllTemplateByLanguageCodeAndTemplateTypeCode(Mockito.anyString(), Mockito.anyString());
		
		assertEquals(templateList.get(0).getId(), templateDtoList.get(0).getId());
		assertEquals(templateList.get(0).getName(), templateDtoList.get(0).getName());
	}
}
