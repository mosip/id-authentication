package io.mosip.kernel.templatemanager.freemarker.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.templatemanager.freemarker.builder.TemplateConfigureBuilder;
import io.mosip.kernel.templatemanager.freemarker.exception.TemplateConfigurationException;

@RunWith(SpringRunner.class)
public class TemplateConfigureBuilderTest {

	@Test
	public void buildDefaultConfiguration() {
		TemplateConfigureBuilder builder = new TemplateConfigureBuilder();
		builder.build();
		assertEquals("UTF-8", builder.getDefaultEncoding());
		assertEquals("classpath", builder.getResourceLoader());
		assertEquals(".", builder.getTemplatePath());
		assertTrue(builder.isCache());

	}

	@Test(expected = TemplateConfigurationException.class)
	public void buildFileTemplateConfigurationFailure() {
		TemplateConfigureBuilder builder = new TemplateConfigureBuilder().resourceLoader("file")
				.resourcePath("/templates");
		builder.build();
		assertEquals("UTF-8", builder.getDefaultEncoding());
		assertEquals("file", builder.getResourceLoader());
		assertEquals(".", builder.getTemplatePath());
		assertTrue(builder.isCache());

	}

	@Test
	public void buildFileTemplateConfiguration() {
		TemplateConfigureBuilder builder = new TemplateConfigureBuilder().resourceLoader("file").resourcePath("/");
		builder.build();
		assertEquals("UTF-8", builder.getDefaultEncoding());
		assertEquals("file", builder.getResourceLoader());
		assertEquals("/", builder.getTemplatePath());
		assertTrue(builder.isCache());

	}

	@Test
	public void buildWithCustomConfiguration() {
		TemplateConfigureBuilder builder = new TemplateConfigureBuilder().encodingType("UTF-16").enableCache(false)
				.resourceLoader("classpath").resourcePath("/templates");
		builder.build();
		assertEquals("UTF-16", builder.getDefaultEncoding());
		assertEquals("classpath", builder.getResourceLoader());
		assertEquals("/templates", builder.getTemplatePath());
		assertFalse(builder.isCache());
		builder.build();
	}
}
