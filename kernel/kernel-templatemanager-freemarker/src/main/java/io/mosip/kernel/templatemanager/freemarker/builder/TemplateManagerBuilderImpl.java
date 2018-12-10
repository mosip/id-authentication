package io.mosip.kernel.templatemanager.freemarker.builder;

import java.io.File;
import java.io.IOException;

import org.springframework.stereotype.Component;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.SoftCacheStorage;
import freemarker.template.Configuration;
import io.mosip.kernel.core.templatemanager.exception.TemplateConfigurationException;
import io.mosip.kernel.core.templatemanager.spi.TemplateManager;
import io.mosip.kernel.core.templatemanager.spi.TemplateManagerBuilder;
import io.mosip.kernel.templatemanager.freemarker.constant.TemplateManagerExceptionCodeConstant;
import io.mosip.kernel.templatemanager.freemarker.impl.TemplateManagerImpl;
import lombok.Getter;

/**
 * TemplateConfigureBuilder will build the @See {@link TemplateManager} with the
 * configuration either custom or default.
 * 
 * @author Abhishek Kumar
 * @since 05-10-2018
 * @version 1.0.0
 */
@Getter
@Component
public class TemplateManagerBuilderImpl implements TemplateManagerBuilder {
	private static final String FILE = "file";
	private static final String CLASSPATH = "classpath";
	private String resourceLoader = CLASSPATH;
	private String templatePath = ".";
	private boolean cache = true;
	private String defaultEncoding = "UTF-8";

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.templatemanager.spi.TemplateManagerBuilder#
	 * resourceLoader(java.lang.String)
	 */
	@Override
	public TemplateManagerBuilderImpl resourceLoader(String resourceLoader) {
		this.resourceLoader = resourceLoader;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.core.templatemanager.spi.TemplateManagerBuilder#resourcePath(
	 * java.lang.String)
	 */
	@Override
	public TemplateManagerBuilderImpl resourcePath(String templatePath) {
		this.templatePath = templatePath;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.core.templatemanager.spi.TemplateManagerBuilder#enableCache(
	 * boolean)
	 */
	@Override
	public TemplateManagerBuilderImpl enableCache(boolean cache) {
		this.cache = cache;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.core.templatemanager.spi.TemplateManagerBuilder#encodingType(
	 * java.lang.String)
	 */
	@Override
	public TemplateManagerBuilderImpl encodingType(String defaultEncoding) {
		this.defaultEncoding = defaultEncoding;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.templatemanager.spi.TemplateManagerBuilder#build()
	 */
	@Override
	public TemplateManager build() {
		Configuration configuration = new Configuration(Configuration.VERSION_2_3_23);
		configuration.setDefaultEncoding(defaultEncoding);
		configuration.setLogTemplateExceptions(false);
		if (FILE.equalsIgnoreCase(resourceLoader) && !templatePath.isEmpty()) {
			try {
				configuration.setTemplateLoader(new FileTemplateLoader(new File(templatePath)));
			} catch (IOException e) {
				throw new TemplateConfigurationException(
						TemplateManagerExceptionCodeConstant.TEMPLATE_CONFIGURATION_INVALID_DIR.getErrorCode(),
						TemplateManagerExceptionCodeConstant.TEMPLATE_CONFIGURATION_INVALID_DIR.getErrorMessage());
			}

		}
		if (CLASSPATH.equalsIgnoreCase(resourceLoader)) {
			configuration.setTemplateLoader(new ClassTemplateLoader(this.getClass().getClassLoader(), templatePath));
		}
		if (cache)
			configuration.setCacheStorage(new SoftCacheStorage());

		return new TemplateManagerImpl(configuration);
	}
}
