package io.mosip.registration.test.template;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.mosip.registration.dao.impl.TemplateDaoImpl;
import io.mosip.registration.entity.Template;
import io.mosip.registration.entity.TemplateEmbeddedKeyCommonFields;
import io.mosip.registration.entity.TemplateFileFormat;
import io.mosip.registration.entity.TemplateType;
import io.mosip.registration.repositories.TemplateFileFormatRepository;
import io.mosip.registration.repositories.TemplateRepository;
import io.mosip.registration.repositories.TemplateTypeRepository;

public class TemplateDaoImplTest {

	@Mock
	TemplateRepository<Template> templateRepository;
	
	@Mock
	TemplateTypeRepository<TemplateType> typeRepository;
	
	@Mock
	TemplateFileFormatRepository<TemplateFileFormat> fileFormatRepository;
	
	@InjectMocks
	TemplateDaoImpl templateDao;
	
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	
	@Test
	public void getTemplateTest() {
		List<Template> templates = new ArrayList<>();
		Template template = new Template();
		template.setId("T01");
		template.setFileTxt(new byte[1024]);
		template.setLangCode("en");
		template.setActive(true);
		templates.add(template);
		when(templateRepository.findByIsActiveTrue()).thenReturn(templates);
		assertThat(templateDao.getAllTemplates(), is(templates));
	}
	
	@Test
	public void getTemplateTypesTest() {
		List<TemplateType> templateTypes = new ArrayList<>();
		TemplateType templateType = new TemplateType();
		TemplateEmbeddedKeyCommonFields typePrimaryKey = new TemplateEmbeddedKeyCommonFields();
		typePrimaryKey.setCode("vel");
		typePrimaryKey.setLangCode("en");
		templateType.setPkTmpltCode(typePrimaryKey);
		templateType.setActive(true);
		templateTypes.add(templateType);
		when(typeRepository.findByIsActiveTrue()).thenReturn(templateTypes);
		assertThat(templateDao.getAllTemplateTypes(), is(templateTypes));
	}
	
	@Test
	public void getTemplateFileFormatsTest() {
		List<TemplateFileFormat> fileFormats = new ArrayList<>();
		TemplateFileFormat fileFormat = new TemplateFileFormat();
		TemplateEmbeddedKeyCommonFields fileFormatPK = new TemplateEmbeddedKeyCommonFields();
		fileFormatPK.setCode("vel");
		fileFormatPK.setLangCode("en");
		fileFormat.setPkTfftCode(fileFormatPK);
		fileFormat.setActive(true);
		fileFormats.add(fileFormat);
		when(fileFormatRepository.findByIsActiveTrue()).thenReturn(fileFormats);
		assertThat(templateDao.getAllTemplateFileFormats(), is(fileFormats));
	}
}
