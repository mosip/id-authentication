package io.mosip.kernel.templatemanager.velocity.builder;

import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.runtime.resource.loader.FileResourceLoader;

import io.mosip.kernel.core.spi.templatemanager.MosipTemplateManager;
import io.mosip.kernel.templatemanager.velocity.impl.TemplateManagerImpl;
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
	private String resourceLoader = "file, classpath";
	private String templatePath = ".";
	private boolean cache = Boolean.TRUE;
	private String defaultEncoding = StandardCharsets.UTF_8.name();

	/**
	 * Method for overriding the resourceloader, default is file and classpath
	 * 
	 * @param resourceLoader
	 *            the resourceLoader will specify from where to load templates
	 *            ,default value is file,classpath
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
	 *            as String , template location
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
	 *            as boolean , default is true
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
	 *            as String , default is UTF-8
	 * @return {@link TemplateConfigureBuilder}
	 */
	public TemplateConfigureBuilder encodingType(String defaultEncoding) {
		this.defaultEncoding = defaultEncoding;
		return this;
	}

	/**
	 * Method to build the {@link MosipTemplateManager} with required configuration
	 * 
	 * @return {@link MosipTemplateManager}
	 */
	public MosipTemplateManager build() {
		final Properties properties = new Properties();
		properties.put(RuntimeConstants.INPUT_ENCODING, defaultEncoding);
		properties.put(RuntimeConstants.OUTPUT_ENCODING, defaultEncoding);
		properties.put(RuntimeConstants.ENCODING_DEFAULT, defaultEncoding);
		properties.put(RuntimeConstants.RESOURCE_LOADER, resourceLoader);
		properties.put(RuntimeConstants.FILE_RESOURCE_LOADER_PATH, templatePath);
		properties.put(RuntimeConstants.FILE_RESOURCE_LOADER_CACHE, cache);
		properties.put("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		properties.put("file.resource.loader.class", FileResourceLoader.class.getName());
		VelocityEngine engine = new VelocityEngine(properties);
		engine.init();
		return new TemplateManagerImpl(engine);
	}
}
