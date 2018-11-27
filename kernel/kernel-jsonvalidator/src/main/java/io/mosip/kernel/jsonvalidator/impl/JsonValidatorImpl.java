package io.mosip.kernel.jsonvalidator.impl;

import java.io.IOException;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

import io.mosip.kernel.core.jsonvalidator.exception.ConfigServerConnectionException;
import io.mosip.kernel.core.jsonvalidator.exception.FileIOException;
import io.mosip.kernel.core.jsonvalidator.exception.HttpRequestException;
import io.mosip.kernel.core.jsonvalidator.exception.JsonIOException;
import io.mosip.kernel.core.jsonvalidator.exception.JsonSchemaIOException;
import io.mosip.kernel.core.jsonvalidator.exception.JsonValidationProcessingException;
import io.mosip.kernel.core.jsonvalidator.exception.NullJsonNodeException;
import io.mosip.kernel.core.jsonvalidator.exception.NullJsonSchemaException;
import io.mosip.kernel.core.jsonvalidator.exception.UnidentifiedJsonException;
import io.mosip.kernel.core.jsonvalidator.model.ValidationReport;
import io.mosip.kernel.core.jsonvalidator.spi.JsonValidator;
import io.mosip.kernel.jsonvalidator.constant.JsonValidatorErrorConstant;
import io.mosip.kernel.jsonvalidator.constant.JsonValidatorPropertySourceConstant;
import io.mosip.kernel.jsonvalidator.constant.JsonValidatorReportConstant;

/**
 * This class provides the implementation for JSON validation against the
 * schema.
 * 
 * @author Swati Raj
 * @since 1.0.0
 * 
 */
@Component
public class JsonValidatorImpl implements JsonValidator{

	/*
	 * Address of Spring cloud config server for getting the schema file
	 */
	@Value("${mosip.kernel.jsonvalidator.file-storage-uri}")
	private String configServerFileStorageURL;

	/*
	 * Property source from which schema file has to be taken, can be either
	 * CONFIG_SERVER or LOCAL
	 */
	@Value("${mosip.kernel.jsonvalidator.property-source}")
	private String propertySource;

	/**
	 * Validates a JSON object passed as string with the schema provided
	 * 
	 * @param jsonString
	 *            JSON as string that has to be Validated against the schema.
	 * @param schemaName
	 *            name of the schema file against which JSON needs to be validated,
	 *            the schema file should be present in your config server storage or
	 *            local storage, which ever option is selected in properties file.
	 * @return ValidationReport containing 'valid' variable as boolean and
	 *         'warnings' arraylist
	 * @throws HttpRequestException
	 * @throws JsonValidationProcessingException
	 * @throws JsonIOException
	 * @throws NullJsonNodeException
	 * @throws UnidentifiedJsonException
	 * @throws JsonSchemaIOException
	 * @throws ConfigServerConnectionException
	 */

	public ValidationReport validateJson(String jsonString, String schemaName)
			throws JsonValidationProcessingException, JsonIOException, JsonSchemaIOException, FileIOException {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonObjectNode = null;
		JsonNode jsonSchemaNode = null;
		ProcessingReport report = null;
		ArrayList<String> reportWarnings = new ArrayList<>();
		try {
			/**
			 * creating a JsonObject node from json string provided.
			 */
			jsonObjectNode = mapper.readTree(jsonString);
		} catch (IOException e) {
			throw new JsonIOException(JsonValidatorErrorConstant.JSON_IO_EXCEPTION.getErrorCode(),
					JsonValidatorErrorConstant.JSON_IO_EXCEPTION.getMessage(), e.getCause());
		}
		if (jsonObjectNode == null) {
			throw new NullJsonNodeException(JsonValidatorErrorConstant.NULL_JSON_NODE_EXCEPTION.getErrorCode(),
					JsonValidatorErrorConstant.NULL_JSON_NODE_EXCEPTION.getMessage());
		}
		/**
		 * getting a JsonSchema node from json schema Name provided.
		 */
		jsonSchemaNode = getJsonSchemaNode(schemaName);

		if (jsonSchemaNode == null) {
			throw new NullJsonSchemaException(JsonValidatorErrorConstant.NULL_JSON_SCHEMA_EXCEPTION.getErrorCode(),
					JsonValidatorErrorConstant.NULL_JSON_SCHEMA_EXCEPTION.getMessage());
		}
		final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
		try {
			final JsonSchema jsonSchema = factory.getJsonSchema(jsonSchemaNode);

			/**
			 * Validating jsonObject against the schema and creating Processing Report
			 */

			report = jsonSchema.validate(jsonObjectNode);

		} catch (ProcessingException e) {
			throw new JsonValidationProcessingException(
					JsonValidatorErrorConstant.JSON_VALIDATION_PROCESSING_EXCEPTION.getErrorCode(),
					JsonValidatorErrorConstant.JSON_VALIDATION_PROCESSING_EXCEPTION.getMessage());
		}

		/**
		 * iterating over report to get each processingMessage
		 */
		report.forEach(processingMessage -> {
			/**
			 * processingMessage object as JsonNode
			 */
			JsonNode processingMessageAsJson = processingMessage.asJson();
			/**
			 * messageLevel variable to store level of message (eg: warning or error)
			 */
			String messageLevel = processingMessageAsJson.get(JsonValidatorReportConstant.LEVEL.getProperty()).asText();
			/**
			 * messageBody variable storing actual message.
			 */
			String messageBody = processingMessageAsJson.get(JsonValidatorReportConstant.MESSAGE.getProperty())
					.asText();
			if (messageLevel.equals(JsonValidatorReportConstant.WARNING.getProperty())) {
				reportWarnings.add(messageBody);
			} else if (messageLevel.equals(JsonValidatorReportConstant.ERROR.getProperty())) {

				/**
				 * getting the location of error in JSON string.
				 */
				if (processingMessageAsJson.has(JsonValidatorReportConstant.INSTANCE.getProperty())
						&& processingMessageAsJson.get(JsonValidatorReportConstant.INSTANCE.getProperty())
								.has(JsonValidatorReportConstant.POINTER.getProperty())) {
					messageBody = messageBody + JsonValidatorReportConstant.AT.getProperty()
							+ processingMessageAsJson.get(JsonValidatorReportConstant.INSTANCE.getProperty())
									.get(JsonValidatorReportConstant.POINTER.getProperty());
				}
				throw new UnidentifiedJsonException(
						JsonValidatorErrorConstant.UNIDENTIFIED_JSON_EXCEPTION.getErrorCode(), messageBody);
			}
		});

		ValidationReport validationResponse = new ValidationReport();
		validationResponse.setValid(report.isSuccess());
		validationResponse.setWarnings(reportWarnings);
		return validationResponse;
	}

	private JsonNode getJsonSchemaNode(String schemaName) throws JsonSchemaIOException, FileIOException {

		JsonNode jsonSchemaNode = null;
		ObjectMapper mapper = new ObjectMapper();

		/**
		 * If the property source selected is CONFIG_SERVER. In this scenario schema is
		 * coming from Config Server, whose location has to be mentioned in the
		 * bootstrap.properties by the application using this JSON validator API.
		 */
		if (JsonValidatorPropertySourceConstant.CONFIG_SERVER.getPropertySource().equals(propertySource)) {
			RestTemplate restTemplate = new RestTemplate();

			/**
			 * setting an error handler and overriding handleError method of
			 * DefaultResponseErrorHandler, such that if there is any error (status code
			 * other than 200) while requesting the schema from config server it will throw
			 * HttpRequestException.
			 * 
			 */
			restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {

				@Override
				protected void handleError(ClientHttpResponse response, HttpStatus statusCode) {
					throw new HttpRequestException(JsonValidatorErrorConstant.HTTP_REQUEST_EXCEPTION.getErrorCode(),
							JsonValidatorErrorConstant.HTTP_REQUEST_EXCEPTION.getMessage());
				}
			});
			try {
				String jsonSchemaAsString = restTemplate.getForObject(this.configServerFileStorageURL + schemaName,
						String.class);
				/**
				 * creating a JsonSchema node against which the JSON object will be validated.
				 */
				jsonSchemaNode = mapper.readTree(jsonSchemaAsString);
			} catch (ResourceAccessException e) {
				throw new ConfigServerConnectionException(
						JsonValidatorErrorConstant.CONFIG_SERVER_CONNECTION_EXCEPTION.getErrorCode(),
						JsonValidatorErrorConstant.CONFIG_SERVER_CONNECTION_EXCEPTION.getMessage());
			} catch (IOException e) {
				throw new JsonSchemaIOException(JsonValidatorErrorConstant.JSON_SCHEMA_IO_EXCEPTION.getErrorCode(),
						JsonValidatorErrorConstant.JSON_SCHEMA_IO_EXCEPTION.getMessage(), e.getCause());
			}
		}
		/**
		 * If the property source selected is local. In this scenario schema is coming
		 * from local resource location.
		 */
		else if (JsonValidatorPropertySourceConstant.LOCAL.getPropertySource().equals(propertySource)) {
			try {
				jsonSchemaNode = JsonLoader
						.fromResource(JsonValidatorReportConstant.PATH_SEPERATOR.getProperty() + schemaName);
			} catch (IOException e) {
				throw new FileIOException(JsonValidatorErrorConstant.FILE_IO_EXCEPTION.getErrorCode(),
						JsonValidatorErrorConstant.FILE_IO_EXCEPTION.getMessage(), e.getCause());
			}
		}
		return jsonSchemaNode;
	}
}
