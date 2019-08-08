 package io.mosip.registration.test.template;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.mosip.registration.dao.TemplateDao;
import io.mosip.registration.entity.Template;
import io.mosip.registration.entity.TemplateEmbeddedKeyCommonFields;
import io.mosip.registration.entity.TemplateFileFormat;
import io.mosip.registration.entity.TemplateType;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.template.impl.TemplateServiceImpl;

public class TemplateServiceTest {

	@Mock
	private TemplateDao templateDao;
	
	@InjectMocks
	private TemplateServiceImpl templateService;
	
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	
	public List<Template> getAllDummyTemplates(){
		List<Template> templates = new ArrayList<>();
		Template template = new Template();
		template.setName("AckTemplate");
		template.setTemplateTypCode("vel");
		template.setFileFormatCode("vel");
		template.setId("T01");
		template.setFileTxt("sample text");
		template.setLangCode("eng");
		template.setActive(true);
		templates.add(template);
		return templates;
	}
	
	public List<TemplateType> getAllDummyTemplateTypes(){
		List<TemplateType> templateTypes = new ArrayList<>();
		TemplateType templateType = new TemplateType();
		TemplateEmbeddedKeyCommonFields typePrimaryKey = new TemplateEmbeddedKeyCommonFields();
		typePrimaryKey.setCode("ackTemplate");
		typePrimaryKey.setLangCode("eng");
		templateType.setPkTmpltCode(typePrimaryKey);
		templateType.setActive(true);
		templateTypes.add(templateType);
		return templateTypes;
	}
	
	public List<TemplateFileFormat> getAllDummyFormats(){
		List<TemplateFileFormat> fileFormats = new ArrayList<>();
		TemplateFileFormat fileFormat = new TemplateFileFormat();
		TemplateEmbeddedKeyCommonFields fileFormatPK = new TemplateEmbeddedKeyCommonFields();
		fileFormatPK.setCode("vel");
		fileFormatPK.setLangCode("eng");
		fileFormat.setPkTfftCode(fileFormatPK);
		fileFormat.setActive(true);
		fileFormats.add(fileFormat);
		return fileFormats;
	}
	
	@Test
	public void getTemplatePositiveTest() {
		List<Template> templates = getAllDummyTemplates();
		when(templateDao.getAllTemplates("ackTemplate")).thenReturn(templates);
		List<TemplateType> templateTypes = getAllDummyTemplateTypes();
		TemplateEmbeddedKeyCommonFields typePrimaryKey = new TemplateEmbeddedKeyCommonFields();
		typePrimaryKey.setCode("ackTemplate");
		typePrimaryKey.setLangCode("eng");
		when(templateDao.getAllTemplateTypes("ackTemplate","eng")).thenReturn(templateTypes);
		List<TemplateFileFormat> fileFormats = getAllDummyFormats();
		when(templateDao.getAllTemplateFileFormats()).thenReturn(fileFormats);
		assertThat(templateService.getTemplate("ackTemplate", "eng"), is(templates.get(0)));
	}
	
	@Test
	public void getTemplateNegativeTest() {
		List<Template> templates = getAllDummyTemplates();
		when(templateDao.getAllTemplates("ackTemplate")).thenReturn(templates);
		List<TemplateType> templateTypes = new ArrayList<>();
		TemplateType templateType = new TemplateType();
		TemplateEmbeddedKeyCommonFields typePrimaryKey = new TemplateEmbeddedKeyCommonFields();
		typePrimaryKey.setCode("ackTemplate");
		typePrimaryKey.setLangCode("fr");
		templateType.setPkTmpltCode(typePrimaryKey);
		templateTypes.add(templateType);
		when(templateDao.getAllTemplateTypes("ackTemplate","eng")).thenReturn(templateTypes);
		List<TemplateFileFormat> fileFormats = getAllDummyFormats();
		when(templateDao.getAllTemplateFileFormats()).thenReturn(fileFormats);
		Template templ = new Template();
		assertThat(templateService.getTemplate("ackTemplate","eng"), is(templ));
	}
	
	@Test
	public void createReceiptTest() throws RegBaseCheckedException {
		Template template = new Template();
		template.setId("T01");
		template.setFileTxt("sample text");
		template.setLangCode("en");
		template.setActive(true);
		template.setName("AckTemplate");
		
		TemplateServiceImpl temp = new TemplateServiceImpl();
		TemplateServiceImpl spyTemp = Mockito.spy(temp);

	    Mockito.doReturn(template).when(spyTemp).getTemplate("ackTemplate", "eng"); 
	    String ack = spyTemp.getHtmlTemplate("ackTemplate", "eng");
	    
		assertNotNull(ack);
	}

	@SuppressWarnings("unchecked")
	@Test(expected = RegBaseUncheckedException.class)
	public void getHtmlTemplateExceptionTest() throws RegBaseCheckedException {
		TemplateServiceImpl temp = new TemplateServiceImpl();
		TemplateServiceImpl spyTemp = Mockito.spy(temp);

		when(spyTemp.getTemplate("ackTemplate", "eng")).thenThrow(RegBaseUncheckedException.class);
		
		String ack = spyTemp.getHtmlTemplate("ackTemplate", "eng");

		assertNull(ack);
	}
	
	@Test(expected = RegBaseCheckedException.class)
	public void getHtmlTemplateNullTest() throws RegBaseCheckedException {
		TemplateServiceImpl temp = new TemplateServiceImpl();
		TemplateServiceImpl spyTemp = Mockito.spy(temp);

		spyTemp.getHtmlTemplate("", "eng");
	}
	
	@Test(expected = RegBaseCheckedException.class)
	public void getHtmlTemplateLangCodeNullTest() throws RegBaseCheckedException {
		TemplateServiceImpl temp = new TemplateServiceImpl();
		TemplateServiceImpl spyTemp = Mockito.spy(temp);

		spyTemp.getHtmlTemplate("acktemplate", "");
	}
}
