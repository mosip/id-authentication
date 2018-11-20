package io.mosip.preregistration.application.test.exception;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.preregistration.application.exception.RecordNotFoundException;
import io.mosip.preregistration.application.service.PreRegistrationService;

/**
 * @author Sanober Noor
 *
 */
@RunWith(SpringRunner.class)
public class RecordNotFoundTest {
	
	
private static final String RECORD_NOT_FOUND="This is record format is invalid exception";

@Mock
private PreRegistrationService 	preRegistrationService;


JSONParser parser = new JSONParser();
private JSONObject jsonObject;
@Test
public void notFoundException() throws FileNotFoundException, IOException, ParseException {

	RecordNotFoundException recordNotFoundException = new RecordNotFoundException(RECORD_NOT_FOUND);

	
	ClassLoader classLoader = getClass().getClassLoader();

	File file = new File(classLoader.getResource("pre-registration.json").getFile());
	
	jsonObject = (JSONObject) parser.parse(new FileReader(file));
	
   Mockito.when(preRegistrationService.addRegistration(jsonObject.toJSONString()))
			.thenThrow(recordNotFoundException);
	try {

		preRegistrationService.addRegistration(jsonObject.toJSONString());
		fail();

	} catch (RecordNotFoundException e) {
		assertThat("This is record format is invalid exception",
				e.getErrorCode().equalsIgnoreCase("This is record format is invalid exception"));
		assertThat("Should throw record not found exception with correct messages",
				e.getErrorText().equalsIgnoreCase(RECORD_NOT_FOUND));
	}

}
}
