package io.mosip.kernel.templatemanager.builder;

import java.io.File;
import java.io.IOException;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.SoftCacheStorage;
import freemarker.template.Configuration;
import io.mosip.kernel.core.spi.templatemanager.MosipTemplateManager;
import io.mosip.kernel.templatemanager.constants.TemplateManagerExceptionCodeConstants;
import io.mosip.kernel.templatemanager.exception.TemplateConfigurationException;
import io.mosip.kernel.templatemanager.impl.TemplateManagerImpl;
import lombok.Getter;

@Getter
public class TemplateConfigureBuilder {
	private static final String FILE = "file";
	private static final String CLASSPATH = "classpath";
	private String resourceLoader = CLASSPATH;
	private String templatePath = ".";
	private boolean cache = true;
	private String defaultEncoding = "UTF-8";

	public TemplateConfigureBuilder resourceLoader(String resourceLoader) {
		this.resourceLoader = resourceLoader;
		return this;
	}

	public TemplateConfigureBuilder resourcePath(String templatePath) {
		this.templatePath = templatePath;
		return this;
	}

	public TemplateConfigureBuilder enableCache(boolean cache) {
		this.cache = cache;
		return this;
	}

	public TemplateConfigureBuilder encodingType(String defaultEncoding) {
		this.defaultEncoding = defaultEncoding;
		return this;
	}

	public MosipTemplateManager build() {
		Configuration configuration = new Configuration(Configuration.VERSION_2_3_23);
		configuration.setDefaultEncoding(defaultEncoding);
		if (FILE.equalsIgnoreCase(resourceLoader) && !templatePath.isEmpty()) {
			try {
				configuration.setTemplateLoader(new FileTemplateLoader(new File(templatePath)));
			} catch (IOException e) {
				throw new TemplateConfigurationException(
						TemplateManagerExceptionCodeConstants.TEMPLATE_CONFIGURATION_INVALID_DIR.getErrorCode(),
						TemplateManagerExceptionCodeConstants.TEMPLATE_CONFIGURATION_INVALID_DIR.getErrorMessage());
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
