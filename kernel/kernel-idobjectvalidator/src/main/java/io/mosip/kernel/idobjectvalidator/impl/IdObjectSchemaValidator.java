package io.mosip.kernel.idobjectvalidator.impl;

import static io.mosip.kernel.core.idobjectvalidator.constant.IdObjectValidatorErrorConstant.ID_OBJECT_PARSING_FAILED;
import static io.mosip.kernel.core.idobjectvalidator.constant.IdObjectValidatorErrorConstant.ID_OBJECT_VALIDATION_FAILED;
import static io.mosip.kernel.core.idobjectvalidator.constant.IdObjectValidatorErrorConstant.INVALID_INPUT_PARAMETER;
import static io.mosip.kernel.core.idobjectvalidator.constant.IdObjectValidatorErrorConstant.MISSING_INPUT_PARAMETER;
import static io.mosip.kernel.core.idobjectvalidator.constant.IdObjectValidatorErrorConstant.SCHEMA_IO_EXCEPTION;
import static io.mosip.kernel.core.idobjectvalidator.constant.IdObjectValidatorPropertySourceConstant.APPLICATION_CONTEXT;
import static io.mosip.kernel.core.idobjectvalidator.constant.IdObjectValidatorPropertySourceConstant.CONFIG_SERVER;
import static io.mosip.kernel.core.idobjectvalidator.constant.IdObjectValidatorPropertySourceConstant.LOCAL;
import static io.mosip.kernel.idobjectvalidator.constant.IdObjectValidatorConstant.APPLICATION_ID;
import static io.mosip.kernel.idobjectvalidator.constant.IdObjectValidatorConstant.ERROR;
import static io.mosip.kernel.idobjectvalidator.constant.IdObjectValidatorConstant.FIELD_LIST;
import static io.mosip.kernel.idobjectvalidator.constant.IdObjectValidatorConstant.INSTANCE;
import static io.mosip.kernel.idobjectvalidator.constant.IdObjectValidatorConstant.PATH_SEPERATOR;
import static io.mosip.kernel.idobjectvalidator.constant.IdObjectValidatorConstant.POINTER;
import static io.mosip.kernel.idobjectvalidator.constant.IdObjectValidatorConstant.ROOT_PATH;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.idobjectvalidator.constant.IdObjectValidatorSupportedOperations;
import io.mosip.kernel.core.idobjectvalidator.exception.IdObjectIOException;
import io.mosip.kernel.core.idobjectvalidator.exception.IdObjectValidationFailedException;
import io.mosip.kernel.core.idobjectvalidator.spi.IdObjectValidator;
import io.mosip.kernel.core.util.StringUtils;

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

	private static final String OPERATION = "operation";

	/** The mapper. */
	@Autowired
	private ObjectMapper mapper;

	/** The env. */
	@Autowired
	private Environment env;

	/** The Constant MISSING. */
	private static final String MISSING = "missing";

	/** The Constant UNWANTED. */
	private static final String UNWANTED = "unwanted";

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
	 * @throws IdObjectIOException
	 *             the id object IO exception
	 */
	@PostConstruct
	public void loadSchema() throws IdObjectIOException {
		try {
			if (APPLICATION_CONTEXT.getPropertySource().equals(propertySource)) {
				schema = JsonLoader.fromURL(new URL(configServerFileStorageURL + schemaName));
			}
		} catch (IOException e) {
			ExceptionUtils.logRootCause(e);
			throw new IdObjectIOException(SCHEMA_IO_EXCEPTION, e);
		}
	}

	/**
	 * Validates a JSON object passed as string with the schema provided.
	 *
	 * @param idObject            JSON as string that has to be Validated against the schema.
	 * @param operation the operation
	 * @return JsonValidationResponseDto containing 'valid' variable as boolean and
	 *         'warnings' arraylist
	 * @throws IdObjectValidationFailedException             JsonValidationProcessingException
	 * @throws IdObjectIOException             JsonIOException
	 * @throws HttpRequestException             HttpRequestException
	 * @throws NullJsonNodeException             NullJsonNodeException
	 * @throws ConfigServerConnectionException             ConfigServerConnectionException
	 */
	@Override
	public boolean validateIdObject(Object idObject, IdObjectValidatorSupportedOperations operation)
			throws IdObjectValidationFailedException, IdObjectIOException {
		JsonNode jsonObjectNode = null;
		JsonNode jsonSchemaNode = null;
		ProcessingReport report = null;
		try {
			jsonObjectNode = mapper.readTree(mapper.writeValueAsString(idObject));
			jsonSchemaNode = getJsonSchemaNode();
			final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
			final JsonSchema jsonSchema = factory.getJsonSchema(jsonSchemaNode);
			report = jsonSchema.validate(jsonObjectNode);

			List<ServiceError> errorList = new ArrayList<>();
			if (!report.isSuccess()) {
				report.forEach(processingMessage -> {
					if (processingMessage.getLogLevel().toString().equals(ERROR.getValue())) {
						JsonNode processingMessageAsJson = processingMessage.asJson();
						if (processingMessageAsJson.has(INSTANCE.getValue())
								&& processingMessageAsJson.get(INSTANCE.getValue()).has(POINTER.getValue())) {
							if (processingMessageAsJson.has(UNWANTED)
									&& !processingMessageAsJson.get(UNWANTED).isNull()) {
								errorList.add(new ServiceError(INVALID_INPUT_PARAMETER.getErrorCode(),
										buildErrorMessage(processingMessageAsJson, INVALID_INPUT_PARAMETER.getMessage(),
												UNWANTED)));
							} else if (processingMessageAsJson.has(MISSING)
									&& !processingMessageAsJson.get(MISSING).isNull()) {
								errorList.add(new ServiceError(MISSING_INPUT_PARAMETER.getErrorCode(),
										buildErrorMessage(processingMessageAsJson, MISSING_INPUT_PARAMETER.getMessage(),
												MISSING)));
							}
						}
					}
				});
			}
			validateMandatoryFields(jsonObjectNode, operation, errorList);
			if (!errorList.isEmpty()) {
				throw new IdObjectValidationFailedException(ID_OBJECT_VALIDATION_FAILED, errorList);
			}
			return report.isSuccess();
		} catch (IOException e) {
			ExceptionUtils.logRootCause(e);
			throw new IdObjectIOException(ID_OBJECT_PARSING_FAILED, e);
		} catch (ProcessingException e) {
			ExceptionUtils.logRootCause(e);
			throw new IdObjectIOException(ID_OBJECT_VALIDATION_FAILED, e);
		}
	}

	/**
	 * Validate mandatory fields.
	 *
	 * @param jsonObjectNode the json object node
	 * @param operation the operation
	 * @param errorList the error list
	 * @throws IdObjectIOException the id object IO exception
	 */
	private void validateMandatoryFields(JsonNode jsonObjectNode, IdObjectValidatorSupportedOperations operation,
			List<ServiceError> errorList) throws IdObjectIOException {
		if (Objects.isNull(operation)) {
			throw new IdObjectIOException(MISSING_INPUT_PARAMETER.getErrorCode(),
					String.format(MISSING_INPUT_PARAMETER.getMessage(), OPERATION));
		}
		String appId = env.getProperty(APPLICATION_ID.getValue());
		if (Objects.isNull(appId)) {
			throw new IdObjectIOException(MISSING_INPUT_PARAMETER.getErrorCode(),
					String.format(MISSING_INPUT_PARAMETER.getMessage(), APPLICATION_ID.getValue()));
		}
		String fields = env.getProperty(String.format(FIELD_LIST.getValue(), appId, operation.getOperation()));
		Optional.ofNullable(fields).ifPresent(fieldList -> 
			Arrays.asList(StringUtils.split(fields, ',')).parallelStream().map(StringUtils::normalizeSpace)
				.forEach(field -> {
					List<String> fieldNames = Arrays.asList(field.split("\\|"));
					if (!jsonObjectNode.hasNonNull(ROOT_PATH.getValue()) || fieldNames.parallelStream()
							.noneMatch(fieldName -> jsonObjectNode.get(ROOT_PATH.getValue()).hasNonNull(fieldName))) {
						errorList.add(new ServiceError(MISSING_INPUT_PARAMETER.getErrorCode(),
								String.format(MISSING_INPUT_PARAMETER.getMessage(),
										fieldNames
											.parallelStream()
											.map(fieldName -> ROOT_PATH.getValue()
													.concat(PATH_SEPERATOR.getValue()).concat(fieldName))
											.collect(Collectors.joining(" | ")))));
					}
				})
		);
	}

	/**
	 * Builds the error message.
	 *
	 * @param processingMessageAsJson the processing message as json
	 * @param messageBody the message body
	 * @param field the field
	 * @return the string
	 */
	private String buildErrorMessage(JsonNode processingMessageAsJson, String messageBody, String field) {
		return String.format(messageBody,
				StringUtils.strip(
						processingMessageAsJson.get(INSTANCE.getValue()).get(POINTER.getValue()).asText()
								+ PATH_SEPERATOR.getValue()
								+ StringUtils.removeAll(processingMessageAsJson.get(field).toString(), "[\\[\"\\]]"),
						"/"));
	}

	/**
	 * Gets the json schema node.
	 *
	 * @return the json schema node
	 * @throws IdObjectIOException the id object IO exception
	 */
	private JsonNode getJsonSchemaNode() throws IdObjectIOException {
		JsonNode jsonSchemaNode = null;
		/*
		 * If the property source selected is CONFIG_SERVER. In this scenario schema is
		 * coming from Config Server, whose location has to be mentioned in the
		 * bootstrap.properties by the application using this JSON validator API.
		 */
		if (CONFIG_SERVER.getPropertySource().equals(propertySource)) {
			try {
				// creating a JsonSchema node against which the JSON object will be validated.
				jsonSchemaNode = JsonLoader.fromURL(new URL(configServerFileStorageURL + schemaName));
			} catch (IOException e) {
				ExceptionUtils.logRootCause(e);
				throw new IdObjectIOException(SCHEMA_IO_EXCEPTION, e);
			}
		}
		// If the property source selected is local. In this scenario schema is coming
		// from local resource location.
		else if (LOCAL.getPropertySource().equals(propertySource)) {
			try {
				jsonSchemaNode = JsonLoader.fromResource(PATH_SEPERATOR.getValue() + schemaName);
			} catch (IOException e) {
				ExceptionUtils.logRootCause(e);
				throw new IdObjectIOException(SCHEMA_IO_EXCEPTION.getErrorCode(), SCHEMA_IO_EXCEPTION.getMessage(),
						e.getCause());
			}
		} else if (APPLICATION_CONTEXT.getPropertySource().equals(propertySource)) {
			jsonSchemaNode = schema;
		}
		return jsonSchemaNode;
	}
}