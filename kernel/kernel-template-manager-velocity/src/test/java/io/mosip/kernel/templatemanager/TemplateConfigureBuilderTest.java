package io.mosip.kernel.templatemanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.templatemanager.builder.TemplateConfigureBuilder;

@RunWith(SpringRunner.class)
public class TemplateConfigureBuilderTest {

	@Test
	public void buildDefaultConfiguration() {
		TemplateConfigureBuilder builder = new TemplateConfigureBuilder();
		builder.build();
		assertEquals("UTF-8", builder.getDefaultEncoding());
		assertEquals("file, classpath", builder.getResourceLoader());
		assertEquals(".", builder.getTemplatePath());
		assertTrue(builder.isCache());

	}

	@Test
	public void buildWithCustomConfiguration() {
		TemplateConfigureBuilder builder = new TemplateConfigureBuilder().encodingType("UTF-16").enableCache(false)
				.resourcePath("/templates").resourceLoader("classpath");
		builder.build();
		assertEquals("UTF-16", builder.getDefaultEncoding());
		assertEquals("classpath", builder.getResourceLoader());
		assertEquals("/templates", builder.getTemplatePath());
		assertFalse(builder.isCache());
		builder.build();
	}
}
