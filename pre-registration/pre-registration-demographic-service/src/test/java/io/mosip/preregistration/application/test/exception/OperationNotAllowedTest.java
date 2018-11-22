package io.mosip.preregistration.application.test.exception;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.preregistration.application.entity.PreRegistrationEntity;
import io.mosip.preregistration.application.exception.OperationNotAllowedException;
import io.mosip.preregistration.application.repository.PreRegistrationRepository;
import io.mosip.preregistration.application.service.PreRegistrationService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OperationNotAllowedTest {

	private static final String OPERATION_NOT_ALLOWED = "DELETE_OPERATION_NOT_ALLOWED_FOR_OTHERTHEN_DRAFT";

	@Autowired
	private PreRegistrationService preRegistrationService;

	@MockBean
	private PreRegistrationRepository preRegistrationRepository;
	private PreRegistrationEntity preRegistrationEntity = new PreRegistrationEntity();

	JSONParser parser = new JSONParser();
	private JSONObject jsonObject;

	private String preRegId;

	@Before
	public void setUp() throws java.text.ParseException, FileNotFoundException, IOException, ParseException {

		ClassLoader classLoader = getClass().getClassLoader();

		File file = new File(classLoader.getResource("pre-registration.json").getFile());
		jsonObject = (JSONObject) parser.parse(new FileReader(file));

		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Date date = dateFormat.parse("08/10/2018");
		long time = date.getTime();
		Timestamp times = new Timestamp(time);
		preRegistrationEntity.setCreateDateTime(times);

		preRegistrationEntity.setStatusCode("");
		preRegistrationEntity.setUpdateDateTime(times);
		preRegistrationEntity.setApplicantDetailJson(jsonObject.toString().getBytes("UTF-8"));

		preRegistrationEntity.setPreRegistrationId("1234");

		preRegId = preRegistrationEntity.getPreRegistrationId();
	}

	@Test(expected = OperationNotAllowedException.class)
	public void notAllowedTest() {

		Mockito.when(preRegistrationRepository.findBypreRegistrationId(preRegId)).thenReturn(preRegistrationEntity);
		preRegistrationService.deleteIndividual(preRegId);

	}

}
