package io.mosip.kernel.templatemanager.freemarker.builder;

import java.io.File;
import java.io.IOException;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.SoftCacheStorage;
import freemarker.template.Configuration;
import io.mosip.kernel.core.spi.templatemanager.MosipTemplateManager;
import io.mosip.kernel.templatemanager.freemarker.constant.TemplateManagerExceptionCodeConstant;
import io.mosip.kernel.templatemanager.freemarker.exception.TemplateConfigurationException;
import io.mosip.kernel.templatemanager.freemarker.impl.TemplateManagerImpl;
import lombok.Getter;

/**
 * TemplateConfigureBuilder will build the @See {@link MosipTemplateManager}
 * with the configuration either custom or default.
 * 
 * @author Abhishek Kumar
 * @since 2018-10-5
 * @version 1.0.0
 */
@Getter
public class TemplateConfigureBuilder {
	private static final String FILE = "file";
	private static final String CLASSPATH = "classpath";
	private String resourceLoader = CLASSPATH;
	private String templatePath = ".";
	private boolean cache = true;
	private String defaultEncoding = "UTF-8";

	/**
	 * Method for overriding the resourceloader
	 * 
	 * @param resourceLoader
	 *            the resourceLoader will specify from where to load templates,
	 *            default is classpath
	 * @return {@link TemplateConfigureBuilder}
	 */
	public TemplateConfigureBuilder resourceLoader(String resourceLoader) {
		this.resourceLoader = resourceLoader;
		return this;
	}

	/**
	 * Method for overriding the template location
	 * 
	 * @param templatePath
	 *            location of templates dafault is .
	 * @return {@link TemplateConfigureBuilder}
	 */
	public TemplateConfigureBuilder resourcePath(String templatePath) {
		this.templatePath = templatePath;
		return this;
	}

	/**
	 * Method to disable or enable cache
	 * 
	 * @param cache
	 *            template cache, default is true;
	 * @return {@link TemplateConfigureBuilder}
	 */
	public TemplateConfigureBuilder enableCache(boolean cache) {
		this.cache = cache;
		return this;
	}

	/**
	 * Method for setting up encoding type
	 * 
	 * @param defaultEncoding
	 *            template content encoding ,default is UTF-8
	 * @return TemplateConfigureBuilder
	 */
	public TemplateConfigureBuilder encodingType(String defaultEncoding) {
		this.defaultEncoding = defaultEncoding;
		return this;
	}

	/**
	 * Method to build the @see {@link MosipTemplateManager} with required
	 * configuration
	 * 
	 * @return {@link MosipTemplateManager}
	 */
	public MosipTemplateManager build() {
		Configuration configuration = new Configuration(Configuration.VERSION_2_3_23);
		configuration.setDefaultEncoding(defaultEncoding);
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
