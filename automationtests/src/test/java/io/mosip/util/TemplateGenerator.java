package io.mosip.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.log.NullLogChute;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.runtime.resource.loader.FileResourceLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.dbdto.TemplateDto;
import io.mosip.service.ApplicationLibrary;
import io.restassured.response.Response;

/**
 * The Class TemplateGenerator.
 * 
 * @author M1048358 Alok
 */
@Component
public class TemplateGenerator {

	/** The reg proc logger. */
	private static Logger logger = Logger.getLogger(TemplateGenerator.class);
	
	/** The Constant TEMPLATES. */
	private static final String TEMPLATES = "templates";

	/** The resource loader. */
	private String resourceLoader = "classpath";

	/** The template path. */
	private String templatePath = ".";

	/** The cache. */
	private boolean cache = Boolean.TRUE;

	/** The default encoding. */
	private String defaultEncoding = StandardCharsets.UTF_8.name();


	/**
	 * Gets the template.
	 *
	 * @param templateTypeCode
	 *            the template type code
	 * @param attributes
	 *            the attributes
	 * @param langCode
	 *            the lang code
	 * @return the template
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 */
	public InputStream getTemplate(String templateTypeCode, Map<String, Object> attributes, String langCode){

		try {
			Response actualResponse = null;
			ApplicationLibrary applicationLibrary = new ApplicationLibrary();
			String id_url = "/v1/masterdata/templates/templatetypecodes/";
			/*List<String> pathSegments = new ArrayList<>();
			pathSegments.add(TEMPLATES);
			pathSegments.add(langCode);
			pathSegments.add(templateTypeCode);*/
			/*TemplateResponseDto template = (TemplateResponseDto) restClientService.getApi(ApiName.MASTER, pathSegments,
					"", "", TemplateResponseDto.class);*/
			List<TemplateDto> templateDtoList = new ArrayList<>();
			String fileTextValue = null;
			 actualResponse = applicationLibrary.GetRequestNoParameter(id_url+templateTypeCode);
			logger.info("actual response : "+actualResponse.asString());
			Map<String,Map<String,String>>response = actualResponse.jsonPath().get("response");
			
			for(Map.Entry<String,Map<String,String>> entry : response.entrySet()){
				if(entry.getKey().matches("templates")){
					List<Map<String,String>> templates = (List<Map<String,String>>) entry.getValue();
					for(Map<String,String> temp : templates){
						for(Map.Entry<String,String> itr : temp.entrySet()){
							if(temp.get("langCode").matches(langCode)){
								fileTextValue = itr.getValue();
								logger.info("fileTextValue : "+fileTextValue);
								break;
							}
						}
					}
				}
			}
			

			InputStream fileTextStream = null;
			if (fileTextValue != null) {
				InputStream stream = new ByteArrayInputStream(fileTextValue.getBytes());
				fileTextStream = getTemplateManager().merge(stream, attributes);
			}
			return fileTextStream;

		} catch (Exception e) {
			logger.error("exception ", e);
		}
		return null;
	}

	/**
	 * Gets the template manager.
	 *
	 * @return the template manager
	 */
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
