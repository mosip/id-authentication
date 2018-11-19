package io.mosip.kernel.jsonvalidator.test;

import java.io.IOException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import io.mosip.kernel.jsonvalidator.exception.ConfigServerConnectionException;
import io.mosip.kernel.jsonvalidator.exception.FileIOException;
import io.mosip.kernel.jsonvalidator.exception.HttpRequestException;
import io.mosip.kernel.jsonvalidator.exception.JsonIOException;
import io.mosip.kernel.jsonvalidator.exception.JsonSchemaIOException;
import io.mosip.kernel.jsonvalidator.exception.JsonValidationProcessingException;
import io.mosip.kernel.jsonvalidator.validator.JsonValidator;

/**
 * 
 * @author Swati Raj
 * @since 1.0.0
 *
 */

@RunWith(MockitoJUnitRunner.class)
public class JsonValidatorInvalidConfigServerAddressExceptionTest {

	@InjectMocks
	JsonValidator jsonValidator;
	
	@Before
	public void setup() {
	    ReflectionTestUtils.setField(jsonValidator, "configServerFileStorageURL", "http://1.1.1.1:51000/*/default/DEV/");
	    ReflectionTestUtils.setField(jsonValidator, "propertySource", "CONFIG_SERVER");
	}
	

	@Test(expected = ConfigServerConnectionException.class)
	public void testForInvalidConfigServerAddress()
			throws HttpRequestException, JsonValidationProcessingException, JsonIOException, JsonSchemaIOException, FileIOException, IOException {
		JsonNode jsonSchemaNode = JsonLoader.fromResource("/valid-json.json");
		String jsonString = jsonSchemaNode.toString();
		String schemaName = "mosip-identity-json-schema.json";
		jsonValidator.validateJson(jsonString, schemaName);
	}
	

}
