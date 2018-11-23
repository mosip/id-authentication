package io.mosip.kernel.templatemanager.freemarker.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.core.templatemanager.exception.TemplateConfigurationException;
import io.mosip.kernel.core.templatemanager.spi.TemplateManagerBuilder;
import io.mosip.kernel.templatemanager.freemarker.builder.TemplateManagerBuilderImpl;

@RunWith(SpringRunner.class)

@SpringBootTest(classes= {TemplateManagerBuilderImpl.class})
public class TemplateConfigureBuilderTest {

	@Autowired
	private TemplateManagerBuilder templateManagerBuilder;
	
	@Test
	public void buildDefaultConfiguration() {
	 TemplateManagerBuilderImpl builder = new TemplateManagerBuilderImpl();
		builder.build();
		assertEquals("UTF-8", builder.getDefaultEncoding());
		assertEquals("classpath", builder.getResourceLoader());
		assertEquals(".", builder.getTemplatePath());
		assertTrue(builder.isCache());

	}

	@Test(expected = TemplateConfigurationException.class)
	public void buildFileTemplateConfigurationFailure() {
		TemplateManagerBuilderImpl builder = (TemplateManagerBuilderImpl) templateManagerBuilder.resourceLoader("file")
				.resourcePath("/templates");
		builder.build();
		assertEquals("UTF-8", builder.getDefaultEncoding());
		assertEquals("file", builder.getResourceLoader());
		assertEquals(".", builder.getTemplatePath());
		assertTrue(builder.isCache());

	}

	@Test
	public void buildFileTemplateConfiguration() {
		TemplateManagerBuilderImpl builder = new TemplateManagerBuilderImpl().resourceLoader("file").resourcePath("/");
		builder.build();
		assertEquals("UTF-8", builder.getDefaultEncoding());
		assertEquals("file", builder.getResourceLoader());
		assertEquals("/", builder.getTemplatePath());
		assertTrue(builder.isCache());

	}

	@Test
	public void buildWithCustomConfiguration() {
		TemplateManagerBuilderImpl builder = (TemplateManagerBuilderImpl) templateManagerBuilder.encodingType("UTF-16").enableCache(false)
				.resourceLoader("classpath").resourcePath("/templates");
		builder.build();
		assertEquals("UTF-16", builder.getDefaultEncoding());
		assertEquals("classpath", builder.getResourceLoader());
		assertEquals("/templates", builder.getTemplatePath());
		assertFalse(builder.isCache());
		builder.build();
	}
}
