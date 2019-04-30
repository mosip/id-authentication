package io.mosip.kernel.core.jsonvalidator.spi;

import io.mosip.kernel.core.jsonvalidator.exception.ConfigServerConnectionException;
import io.mosip.kernel.core.jsonvalidator.exception.FileIOException;
import io.mosip.kernel.core.jsonvalidator.exception.HttpRequestException;
import io.mosip.kernel.core.jsonvalidator.exception.JsonIOException;
import io.mosip.kernel.core.jsonvalidator.exception.JsonSchemaIOException;
import io.mosip.kernel.core.jsonvalidator.exception.JsonValidationProcessingException;
import io.mosip.kernel.core.jsonvalidator.exception.NullJsonNodeException;
import io.mosip.kernel.core.jsonvalidator.exception.UnidentifiedJsonException;
import io.mosip.kernel.core.jsonvalidator.model.ValidationReport;

/**
 * TInterface JSON validation against the schema.
 * 
 * @author Swati Raj
 * @since 1.0.0
 * 
 */

public interface JsonValidator {
	/**
	 * Validates a JSON object passed as string with the schema provided
	 * 
	 * @param jsonString JSON as string that has to be Validated against the schema.
	 * 
	 * @return JsonValidationResponseDto containing 'valid' variable as boolean and
	 *         'warnings' arraylist
	 * @throws HttpRequestException              HttpRequestException
	 * @throws JsonValidationProcessingException JsonValidationProcessingException
	 * @throws JsonIOException                   JsonIOException
	 * @throws NullJsonNodeException             NullJsonNodeException
	 * @throws UnidentifiedJsonException         UnidentifiedJsonException
	 * @throws JsonSchemaIOException             JsonSchemaIOException
	 * @throws ConfigServerConnectionException   ConfigServerConnectionException
	 * @throws FileIOException                   FileIOException
	 */

	public ValidationReport validateJson(String jsonString)
			throws JsonValidationProcessingException, JsonIOException, JsonSchemaIOException, FileIOException;

}
