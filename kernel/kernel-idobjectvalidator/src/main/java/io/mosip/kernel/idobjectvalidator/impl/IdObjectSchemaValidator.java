package io.mosip.kernel.idobjectvalidator.impl;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

import io.mosip.kernel.core.idobjectvalidator.constant.IdObjectValidatorErrorConstant;
import io.mosip.kernel.core.idobjectvalidator.constant.IdObjectValidatorPropertySourceConstant;
import io.mosip.kernel.core.idobjectvalidator.constant.IdObjectValidatorConstant;
import io.mosip.kernel.core.idobjectvalidator.exception.ConfigServerConnectionException;
import io.mosip.kernel.core.idobjectvalidator.exception.FileIOException;
import io.mosip.kernel.core.idobjectvalidator.exception.HttpRequestException;
import io.mosip.kernel.core.idobjectvalidator.exception.IdObjectIOException;
import io.mosip.kernel.core.idobjectvalidator.exception.IdObjectSchemaIOException;
import io.mosip.kernel.core.idobjectvalidator.exception.IdObjectValidationProcessingException;
import io.mosip.kernel.core.idobjectvalidator.exception.NullJsonNodeException;
import io.mosip.kernel.core.idobjectvalidator.exception.UnidentifiedJsonException;
import io.mosip.kernel.core.idobjectvalidator.spi.IdObjectValidator;

/**
 * This class provides the implementation for JSON validation against the
 * schema.
 * 
 * @author Manoj SP
 * @author Swati Raj
 * @since 1.0.0
 * 
 */
@Component("schema")
@RefreshScope
public class IdObjectSchemaValidator implements IdObjectValidator {

	/** The config server file storage URL. */
	/*
	 * Address of Spring cloud config server for getting the schema file
	 */
	@Value("${mosip.kernel.idobjectvalidator.file-storage-uri}")
	private String configServerFileStorageURL;

	/** The schema name. */
	@Value("${mosip.kernel.idobjectvalidator.schema-name}")
	private String schemaName;

	/** The property source. */
	/*
	 * Property source from which schema file has to be taken, can be either
	 * CONFIG_SERVER or LOCAL
	 */
	@Value("${mosip.kernel.idobjectvalidator.property-source}")
	private String propertySource;

	/** The schema. */
	private JsonNode schema;

	/**
	 * Load schema.
	 *
	 * @throws IdObjectSchemaIOException
	 *             the id object schema IO exception
	 */
	@PostConstruct
	public void loadSchema() throws IdObjectSchemaIOException {
		try {
			if (IdObjectValidatorPropertySourceConstant.APPLICATION_CONTEXT.getPropertySource()
					.equals(propertySource)) {
				schema = JsonLoader.fromURL(new URL(configServerFileStorageURL + schemaName));
			}
		} catch (IOException e) {
			throw new IdObjectSchemaIOException(IdObjectValidatorErrorConstant.JSON_SCHEMA_IO_EXCEPTION.getErrorCode(),
					IdObjectValidatorErrorConstant.JSON_SCHEMA_IO_EXCEPTION.getMessage(), e);
		}
	}

	/**
	 * Validates a JSON object passed as string with the schema provided.
	 *
	 * @param idObject
	 *            JSON as string that has to be Validated against the schema.
	 * @return JsonValidationResponseDto containing 'valid' variable as boolean and
	 *         'warnings' arraylist
	 * @throws IdObjectValidationProcessingException
	 *             JsonValidationProcessingException
	 * @throws IdObjectIOException
	 *             JsonIOException
	 * @throws IdObjectSchemaIOException
	 *             JsonSchemaIOException
	 * @throws FileIOException
	 *             FileIOException
	 * @throws HttpRequestException
	 *             HttpRequestException
	 * @throws NullJsonNodeException
	 *             NullJsonNodeException
	 * @throws UnidentifiedJsonException
	 *             UnidentifiedJsonException
	 * @throws ConfigServerConnectionException
	 *             ConfigServerConnectionException
	 */
	@Override
	public boolean validateIdObject(Object idObject) throws IdObjectValidationProcessingException, IdObjectIOException,
			IdObjectSchemaIOException, FileIOException {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonObjectNode = null;
		JsonNode jsonSchemaNode = null;
		ProcessingReport report = null;
		ArrayList<String> reportWarnings = new ArrayList<>();
		try {
			// creating a JsonObject node from json string provided.
			jsonObjectNode = mapper.readTree(mapper.writeValueAsString(idObject));
		} catch (IOException e) {
			throw new IdObjectIOException(IdObjectValidatorErrorConstant.ID_OBJECT_IO_EXCEPTION.getErrorCode(),
					IdObjectValidatorErrorConstant.ID_OBJECT_IO_EXCEPTION.getMessage(), e.getCause());
		}
		if (jsonObjectNode == null) {
			throw new NullJsonNodeException(IdObjectValidatorErrorConstant.NULL_JSON_NODE_EXCEPTION.getErrorCode(),
					IdObjectValidatorErrorConstant.NULL_JSON_NODE_EXCEPTION.getMessage());
		}
		// getting a JsonSchema node from json schema Name provided.
		jsonSchemaNode = getJsonSchemaNode();

		final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
		try {
			final JsonSchema jsonSchema = factory.getJsonSchema(jsonSchemaNode);
			// Validating jsonObject against the schema and creating Processing Report
			report = jsonSchema.validate(jsonObjectNode);
		} catch (ProcessingException e) {
			throw new IdObjectValidationProcessingException(
					IdObjectValidatorErrorConstant.ID_OBJECT_VALIDATION_FAILED.getErrorCode(),
					IdObjectValidatorErrorConstant.ID_OBJECT_VALIDATION_FAILED.getMessage(), e);
		}

		// iterating over report to get each processingMessage
		report.forEach(processingMessage -> {
			// processingMessage object as JsonNode
			JsonNode processingMessageAsJson = processingMessage.asJson();
			// messageLevel variable to store level of message (eg: warning or error)
			String messageLevel = processingMessageAsJson.get(IdObjectValidatorConstant.LEVEL.getValue()).asText();
			// messageBody variable storing actual message.
			String messageBody = processingMessageAsJson.get(IdObjectValidatorConstant.MESSAGE.getValue()).asText();
			if (messageLevel.equals(IdObjectValidatorConstant.WARNING.getValue())) {
				reportWarnings.add(messageBody);
			} else if (messageLevel.equals(IdObjectValidatorConstant.ERROR.getValue())) {
				// getting the location of error in JSON string.
				if (processingMessageAsJson.has(IdObjectValidatorConstant.INSTANCE.getValue())
						&& processingMessageAsJson.get(IdObjectValidatorConstant.INSTANCE.getValue())
								.has(IdObjectValidatorConstant.POINTER.getValue())) {
					messageBody = messageBody + IdObjectValidatorConstant.AT.getValue()
							+ processingMessageAsJson.get(IdObjectValidatorConstant.INSTANCE.getValue())
									.get(IdObjectValidatorConstant.POINTER.getValue());
				}
				throw new UnidentifiedJsonException(
						IdObjectValidatorErrorConstant.ID_OBJECT_VALIDATION_FAILED.getErrorCode(), messageBody);
			}
		});

		return report.isSuccess();
	}

	/**
	 * Gets the json schema node.
	 *
	 * @return the json schema node
	 * @throws IdObjectSchemaIOException
	 *             the id object schema IO exception
	 * @throws FileIOException
	 *             the file IO exception
	 */
	private JsonNode getJsonSchemaNode() throws IdObjectSchemaIOException, FileIOException {
		JsonNode jsonSchemaNode = null;
		/*
		 * If the property source selected is CONFIG_SERVER. In this scenario schema is
		 * coming from Config Server, whose location has to be mentioned in the
		 * bootstrap.properties by the application using this JSON validator API.
		 */
		if (IdObjectValidatorPropertySourceConstant.CONFIG_SERVER.getPropertySource().equals(propertySource)) {
			try {
				// creating a JsonSchema node against which the JSON object will be validated.
				jsonSchemaNode = JsonLoader.fromURL(new URL(configServerFileStorageURL + schemaName));
			} catch (Exception e) {
				throw new IdObjectSchemaIOException(
						IdObjectValidatorErrorConstant.JSON_SCHEMA_IO_EXCEPTION.getErrorCode(),
						IdObjectValidatorErrorConstant.JSON_SCHEMA_IO_EXCEPTION.getMessage(), e.getCause());
			}
		}
		// If the property source selected is local. In this scenario schema is coming
		// from local resource location.
		else if (IdObjectValidatorPropertySourceConstant.LOCAL.getPropertySource().equals(propertySource)) {
			try {
				jsonSchemaNode = JsonLoader
						.fromResource(IdObjectValidatorConstant.PATH_SEPERATOR.getValue() + schemaName);
			} catch (IOException e) {
				throw new FileIOException(IdObjectValidatorErrorConstant.FILE_IO_EXCEPTION.getErrorCode(),
						IdObjectValidatorErrorConstant.FILE_IO_EXCEPTION.getMessage(), e.getCause());
			}
		} else if (IdObjectValidatorPropertySourceConstant.APPLICATION_CONTEXT.getPropertySource()
				.equals(propertySource)) {
			jsonSchemaNode = schema;
		}
		return jsonSchemaNode;
	}
}
