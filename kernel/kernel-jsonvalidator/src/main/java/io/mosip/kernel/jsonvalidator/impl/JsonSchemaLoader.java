package io.mosip.kernel.jsonvalidator.impl;

import java.io.IOException;
import java.net.URL;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;

import io.mosip.kernel.core.jsonvalidator.exception.JsonSchemaIOException;
import io.mosip.kernel.jsonvalidator.constant.JsonValidatorErrorConstant;
import io.mosip.kernel.jsonvalidator.constant.JsonValidatorPropertySourceConstant;

/**
 * @author Manoj SP
 *
 */
@Component
@RefreshScope
public class JsonSchemaLoader {
	
	@Value("${mosip.kernel.jsonvalidator.file-storage-uri}")
	private String configServerFileStorageURL;
	
	@Value("${mosip.kernel.jsonvalidator.schema-name}")
	private String schemaName;
	
	@Value("${mosip.kernel.jsonvalidator.property-source}")
	private String propertySource;
	
	private JsonNode schema;

	@PostConstruct
	public void loadSchema() throws JsonSchemaIOException {
		try {
			if (JsonValidatorPropertySourceConstant.APPLICATION_CONTEXT.getPropertySource().equals(propertySource)) {
				schema = JsonLoader.fromURL(new URL(configServerFileStorageURL + schemaName));
			}
		} catch (IOException e) {
			throw new JsonSchemaIOException(JsonValidatorErrorConstant.JSON_SCHEMA_IO_EXCEPTION.getErrorCode(),
					JsonValidatorErrorConstant.JSON_SCHEMA_IO_EXCEPTION.getMessage(), e);
		}
	}
	
	public JsonNode getSchema() {
		return schema;
	}
}