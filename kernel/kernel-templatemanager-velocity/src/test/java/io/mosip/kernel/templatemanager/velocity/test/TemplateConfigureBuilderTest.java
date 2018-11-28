package io.mosip.kernel.templatemanager.velocity.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.core.templatemanager.spi.TemplateManagerBuilder;
import io.mosip.kernel.templatemanager.velocity.builder.TemplateManagerBuilderImpl;

@RunWith(SpringRunner.class)
@SpringBootTest(classes= {TemplateManagerBuilderImpl.class})
public class TemplateConfigureBuilderTest {
	
	@Autowired
	private TemplateManagerBuilder templateManagerBuilder;

	@Test
	public void buildDefaultConfiguration() {
		TemplateManagerBuilderImpl builder=(TemplateManagerBuilderImpl) templateManagerBuilder;
		builder.build();
	
		System.out.println("1getDefaultEncoding " + builder.getDefaultEncoding());
		System.out.println("1getResourceLoader " + builder.getResourceLoader());
		System.out.println("1getTemplatePath " +  builder.getTemplatePath());
		
		
		assertEquals("UTF-8", builder.getDefaultEncoding());
		assertEquals("classpath", builder.getResourceLoader());
		assertEquals(".", builder.getTemplatePath());
		assertTrue(builder.isCache());

	}

	@Test
	public void buildWithCustomConfiguration() {
		TemplateManagerBuilderImpl builder = (TemplateManagerBuilderImpl) new TemplateManagerBuilderImpl().encodingType("UTF-16").enableCache(false)
				.resourcePath("/templates").resourceLoader("classpath");
		builder.build();
		
		
		System.out.println("2getDefaultEncoding " + builder.getDefaultEncoding());
		System.out.println("2getResourceLoader " + builder.getResourceLoader());
		System.out.println("2getTemplatePath " +  builder.getTemplatePath());
		
		assertEquals("UTF-16", builder.getDefaultEncoding());
		assertEquals("classpath", builder.getResourceLoader());
		assertEquals("/templates", builder.getTemplatePath());
		assertFalse(builder.isCache());
		builder.build();
	}
}
