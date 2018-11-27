package io.mosip.kernel.core.templatemanager.spi;

/**
 * TemplateManagerBuilder will build the {@link TemplateManager} with the
 * configuration either custom or default.
 * 
 * @author Abhishek Kumar
 * @version 1.0.0
 * @since 22-11-2018
 *
 */
public interface TemplateManagerBuilder {
	/**
	 * Method for overriding the resourceLoader, default is classPath
	 * 
	 * @param resourceLoader
	 *            the resourceLoader will specify from where to load templates
	 *            ,default value is classPath
	 * @return {@link TemplateManagerBuilder}
	 */
	TemplateManagerBuilder resourceLoader(String resourceLoader);

	/**
	 * Method for overriding the template location
	 * 
	 * @param templatePath
	 *            as String , template location
	 * @return {@link TemplateManagerBuilder}
	 */
	TemplateManagerBuilder resourcePath(String templatePath);

	/**
	 * Method to disable or enable cache
	 * 
	 * @param cache
	 *            cache template in memory , default is true
	 * @return {@link TemplateManagerBuilder}
	 */
	TemplateManagerBuilder enableCache(boolean cache);

	/**
	 * Method for setting up encoding type
	 * 
	 * @param defaultEncoding
	 *            template encoding type, default is UTF-8
	 * @return {@link TemplateManagerBuilder}
	 */
	TemplateManagerBuilder encodingType(String defaultEncoding);

	/**
	 * Method to build the {@link TemplateManager} with required configuration
	 * 
	 * @return {@link TemplateManager}
	 */
	TemplateManager build();
}
