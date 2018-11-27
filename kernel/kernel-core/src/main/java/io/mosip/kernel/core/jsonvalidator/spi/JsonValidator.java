package io.mosip.kernel.core.jsonvalidator.spi;

/*
*//**
	 * TInterface JSON validation against the schema.
	 * 
	 * @author Swati Raj
	 * @since 1.0.0
	 * 
	 */
/*
 * @Component public class JsonValidator {
 *//**
	 * Validates a JSON object passed as string with the schema provided
	 * 
	 * @param jsonString
	 *            JSON as string that has to be Validated against the schema.
	 * @param schemaName
	 *            name of the schema file against which JSON needs to be validated,
	 *            the schema file should be present in your config server storage or
	 *            local storage, which ever option is selected in properties file.
	 * @return JsonValidationResponseDto containing 'valid' variable as boolean and
	 *         'warnings' arraylist
	 * @throws HttpRequestException
	 * @throws JsonValidationProcessingException
	 * @throws JsonIOException
	 * @throws NullJsonNodeException
	 * @throws UnidentifiedJsonException
	 * @throws JsonSchemaIOException
	 * @throws ConfigServerConnectionException
	 *//*
		 * 
		 * 
		 * public JsonValidatorResponseDto validateJson(String jsonString, String
		 * schemaName) throws JsonValidationProcessingException, JsonIOException,
		 * JsonSchemaIOException, FileIOException;
		 * 
		 * }
		 */