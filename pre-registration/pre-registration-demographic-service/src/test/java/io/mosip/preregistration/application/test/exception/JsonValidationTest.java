package io.mosip.preregistration.application.test.exception;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.jsonvalidator.exception.FileIOException;
import io.mosip.kernel.jsonvalidator.exception.HttpRequestException;
import io.mosip.kernel.jsonvalidator.exception.JsonIOException;
import io.mosip.kernel.jsonvalidator.exception.JsonSchemaIOException;
import io.mosip.kernel.jsonvalidator.exception.JsonValidationProcessingException;
import io.mosip.kernel.jsonvalidator.validator.JsonValidator;
import io.mosip.preregistration.application.exception.system.JsonValidationException;
import io.mosip.preregistration.application.service.PreRegistrationService;


@RunWith(SpringRunner.class)
@SpringBootTest
public class JsonValidationTest {
	private static final String RECORD_NOT_FOUND="This is record format is invalid exception";

	@MockBean
	private PreRegistrationService 	preRegistrationService;

	@MockBean
	private JsonValidator jsonValidator;
	
	JSONParser parser = new JSONParser();
	private JSONObject jsonObject;
	
	@Test(expected=JsonValidationException.class)
	public void JsonNotValidException() throws FileNotFoundException, IOException, ParseException, HttpRequestException, JsonValidationProcessingException, JsonIOException, JsonSchemaIOException, FileIOException {


		
		ClassLoader classLoader = getClass().getClassLoader();

		File file = new File(classLoader.getResource("pre-registration.json").getFile());
		
		jsonObject = (JSONObject) parser.parse(new FileReader(file));
		
//		Mockito.when(jsonValidator.validateJson(jsonObject.toString(), "mosip-prereg-identity-json-schema.json"))
//		.thenReturn(null);
//	  preRegistrationService.addRegistration(jsonObject.toJSONString());
//				
	//	Mockito.when(jsonValidator.validateJson(jsonObject.toString(), "mosip-prereg-identity-json-schema.json")).thenThrow(HttpRequestException.class);
		
Mockito.when(preRegistrationService.addPreRegistration(jsonObject.toJSONString())).thenThrow(JsonValidationException.class);
preRegistrationService.addPreRegistration(jsonObject.toJSONString());
	
	}
}
