package io.mosip.resident.config;

import java.nio.charset.StandardCharsets;
import java.util.Properties;

import javax.servlet.Filter;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.log.NullLogChute;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.runtime.resource.loader.FileResourceLoader;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.mosip.kernel.core.idvalidator.spi.RidValidator;
import io.mosip.kernel.core.idvalidator.spi.UinValidator;
import io.mosip.kernel.core.idvalidator.spi.VidValidator;
import io.mosip.kernel.core.templatemanager.spi.TemplateManager;
import io.mosip.kernel.idvalidator.rid.impl.RidValidatorImpl;
import io.mosip.kernel.idvalidator.uin.impl.UinValidatorImpl;
import io.mosip.kernel.idvalidator.vid.impl.VidValidatorImpl;
import io.mosip.kernel.templatemanager.velocity.impl.TemplateManagerImpl;


@Configuration
public class Config {
	private String defaultEncoding = StandardCharsets.UTF_8.name();
	/** The resource loader. */
	private String resourceLoader = "classpath";

	/** The template path. */
	private String templatePath = ".";

	/** The cache. */
	private boolean cache = Boolean.TRUE;


	@Bean
	public FilterRegistrationBean<Filter> registerReqResFilter() {
		FilterRegistrationBean<Filter> corsBean = new FilterRegistrationBean<>();
		corsBean.setFilter(getReqResFilter());
		corsBean.setOrder(1);
		return corsBean;
	}

	@Bean
	public Filter getReqResFilter() {
		return new ReqResFilter();
	}
	
	@Bean
	public VidValidator<String> vidValidator() {
		return new VidValidatorImpl();
	}
	
	@Bean
	public UinValidator<String> uinValidator() {
		return new UinValidatorImpl();
	}
	
	@Bean
	public RidValidator<String> ridValidator() {
		return new RidValidatorImpl();
	}
	
	@Bean
	public TemplateManager getTemplateManager() {
		final Properties properties = new Properties();
		properties.put(RuntimeConstants.INPUT_ENCODING, defaultEncoding);
		properties.put(RuntimeConstants.OUTPUT_ENCODING, defaultEncoding);
		properties.put(RuntimeConstants.ENCODING_DEFAULT, defaultEncoding);
		properties.put(RuntimeConstants.RESOURCE_LOADER, resourceLoader);
		properties.put(RuntimeConstants.FILE_RESOURCE_LOADER_PATH, templatePath);
		properties.put(RuntimeConstants.FILE_RESOURCE_LOADER_CACHE, cache);
		properties.put(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, NullLogChute.class.getName());
		properties.put("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		properties.put("file.resource.loader.class", FileResourceLoader.class.getName());
		VelocityEngine engine = new VelocityEngine(properties);
		engine.init();
		return new TemplateManagerImpl(engine);
	}
}
