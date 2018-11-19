package io.mosip.preregistration.application.test.service;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.idgenerator.spi.PridGenerator;
import io.mosip.kernel.jsonvalidator.dto.JsonValidatorResponseDto;
import io.mosip.kernel.jsonvalidator.validator.JsonValidator;
import io.mosip.preregistration.application.dao.PreRegistrationDao;
import io.mosip.preregistration.application.dto.CreateDto;
import io.mosip.preregistration.application.dto.ExceptionInfoDto;
import io.mosip.preregistration.application.dto.ResponseDto;
import io.mosip.preregistration.application.dto.ViewRegistrationResponseDto;
import io.mosip.preregistration.application.entity.PreRegistrationEntity;
import io.mosip.preregistration.application.exception.utils.PreRegistrationErrorMessages;
import io.mosip.preregistration.application.repository.PreRegistrationRepository;
import io.mosip.preregistration.application.service.PreRegistrationService;
import io.mosip.preregistration.core.exceptions.TablenotAccessibleException;


/**
 * Test class to test the ViewRegistrationService
 * 
 * @author M1037462 M1037717 since 1.0.0
 */

@RunWith(SpringRunner.class)
@SpringBootTest
// @SpringBootConfiguration
public class PreRegistrationServiceTest {


	@MockBean
	private PreRegistrationRepository preRegistrationRepository;

	@MockBean
	private PreRegistrationDao preRegistrationDao;

	
	@MockBean
	RestTemplateBuilder restTemplateBuilder;

	@MockBean
	private PridGenerator<String> pridGenerator;
	
	@MockBean
	private JsonValidator jsonValidator;
	
	JSONParser parser = new JSONParser();

	@Autowired
	private PreRegistrationService preRegistrationService;
	
//	@InjectMocks
//	private PreRegistrationService preRegistrationService = new PreRegistrationService();

	List<PreRegistrationEntity> userDetails = new ArrayList<>();
	List<ViewRegistrationResponseDto> response = new ArrayList<ViewRegistrationResponseDto>();
	ExceptionInfoDto exceptionInfoDto = new ExceptionInfoDto();
	List<ExceptionInfoDto> responseList = new ArrayList<>();
	private ViewRegistrationResponseDto responseDto;
	private PreRegistrationEntity preRegistrationEntity;
	private JSONObject jsonObject;
	private JSONObject jsonTestObject;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Before
	public void setup()
			throws ParseException, FileNotFoundException, IOException, org.json.simple.parser.ParseException {

		preRegistrationEntity = new PreRegistrationEntity();

		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Date date = dateFormat.parse("08/10/2018");
		long time = date.getTime();
		Timestamp times = new Timestamp(time);
		preRegistrationEntity.setCreateDateTime(times);



		preRegistrationEntity.setStatusCode("Pending_Appointment");
		preRegistrationEntity.setUpdateDateTime(times);


		preRegistrationEntity.setPreRegistrationId("1234");
		userDetails.add(preRegistrationEntity);

		logger.info("Entity " + preRegistrationEntity);

		responseDto = new ViewRegistrationResponseDto();

		responseDto.setFirstname(null);
		responseDto.setStatus_code("Pending_Appointment");
		responseDto.setPreId("1234");
		response.add(responseDto);
		exceptionInfoDto.setResponse(response);
		exceptionInfoDto.setStatus(true);
		responseList.add(exceptionInfoDto);

		 JsonValidatorResponseDto dto= new JsonValidatorResponseDto();
		 ClassLoader classLoader = getClass().getClassLoader();


		File file = new File(classLoader.getResource("pre-registration.json").getFile());
		jsonObject =   (JSONObject) parser.parse(new FileReader(file));
		File fileTest = new File(classLoader.getResource("pre-registration-test.json").getFile());
		jsonTestObject =   (JSONObject) parser.parse(new FileReader(fileTest));
	}

	@Test
	public void successSaveImplTest() throws Exception {
		logger.info("----------successful save of application in impl-------");
		Mockito.when(pridGenerator.generateId()).thenReturn("67547447647457");
		Mockito.when(jsonValidator.validateJson(jsonObject.toString(),"mosip-prereg-identity-json-schema.json")).thenReturn(null);
		ResponseDto<CreateDto> res = preRegistrationService.addRegistration(jsonObject.toString());
		assertEquals(res.getResponse().get(0).getPrId(), "67547447647457");
	}

	@Test
	public void successUpdateTest() throws Exception {
		logger.info("----------successful save of application in impl-------");
		Mockito.when(jsonValidator.validateJson(jsonObject.toString(),"mosip-prereg-identity-json-schema.json")).thenReturn(null);
		ResponseDto<CreateDto> res = preRegistrationService.addRegistration(jsonObject.toString());
		
		equals(res.getResponse().get(0).getPrId());
	}

	@Test(expected = TablenotAccessibleException.class)
	public void saveFailureCheck() throws Exception {
		DataAccessLayerException exception = new DataAccessLayerException("PRG_PAM‌_007",
				PreRegistrationErrorMessages.REGISTRATION_TABLE_NOTACCESSIBLE, null);
		Mockito.when(jsonValidator.validateJson(jsonObject.toString(),"mosip-prereg-identity-json-schema.json")).thenReturn(null);
		Mockito.when(preRegistrationDao.save(Mockito.any())).thenThrow(exception);
	
		preRegistrationService.addRegistration(jsonObject.toString());
	}

	@Test
	public void getApplicationDetails() {
		String userId = "9988905444";
		Mockito.when(preRegistrationRepository.findByuserId(ArgumentMatchers.any())).thenReturn(userDetails);
		List<ExceptionInfoDto> actualRes = preRegistrationService.getApplicationDetails(userId);
		assertEqualsList(actualRes, responseList);

	}

	@Test
	public void getApplicationStatus() {
		String groupId = "1232";

		Mockito.when(preRegistrationRepository.findBygroupId(ArgumentMatchers.any())).thenReturn(userDetails);
		Map<String, String> response = userDetails.stream().collect(
				Collectors.toMap(PreRegistrationEntity::getPreRegistrationId, PreRegistrationEntity::getStatusCode));
		Map<String, String> actualRes = preRegistrationService.getApplicationStatus(groupId);
		assertEquals(response, actualRes);

	}

	@Test(expected = TablenotAccessibleException.class)
	public void getApplicationDetailsTransactionFailureCheck() throws Exception {
		String userId = "9988905444";
		DataAccessLayerException exception = new DataAccessLayerException("PRG_PAM‌_007",
				PreRegistrationErrorMessages.REGISTRATION_TABLE_NOTACCESSIBLE, null);

		Mockito.when(preRegistrationRepository.findByuserId(ArgumentMatchers.any())).thenThrow(exception);
		preRegistrationService.getApplicationDetails(userId);
	}

	@Test(expected = TablenotAccessibleException.class)
	public void getApplicationStatusTransactionFailureCheck() throws Exception {
		String groupId = "1234";
		TablenotAccessibleException exception = new TablenotAccessibleException(
				PreRegistrationErrorMessages.REGISTRATION_TABLE_NOTACCESSIBLE);
		Mockito.when(preRegistrationRepository.findBygroupId(ArgumentMatchers.any())).thenThrow(exception);
		preRegistrationService.getApplicationStatus(groupId);
	}

	public void assertEqualsList(List<ExceptionInfoDto> actual, List<ExceptionInfoDto> expected) {
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).toString(), actual.get(i).toString());
		}
	}

	@SuppressWarnings("deprecation")
	@Test
	public void deleteIndividualSuccessTest() {
		RestTemplate restTemplate=Mockito.mock(RestTemplate.class);
	 Mockito.when(restTemplateBuilder.build()).thenReturn(restTemplate);
		String preRegId = "1";
        preRegistrationEntity.setPreRegistrationId("1");
		preRegistrationEntity.setStatusCode("Pending_Appointmemt");

		ResponseEntity<ResponseDto> res = new ResponseEntity<>(HttpStatus.OK);
		Mockito.when(preRegistrationRepository.findBypreRegistrationId(preRegId)).thenReturn(preRegistrationEntity);

		String resourceUrl = "http://localhost:9093//v0.1/pre-registration/registration/deleteAllByPreRegId";
		// UriComponentsBuilder builder =
		// UriComponentsBuilder.fromHttpUrl(resourceUrl).queryParam("preId", preRegId);
		// String uriBuilder = builder.build().encode().toUriString();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		//
		// ResponseEntity<String> responseEntity = restTemplate.exchange(uriBuilder,
		// HttpMethod.DELETE, entity,
		// String.class);

		System.out.println("rt"+restTemplate);
		Mockito.when(restTemplate.exchange(Mockito.anyString(),Mockito.eq(HttpMethod.DELETE),
				Mockito.any(), Mockito.eq(ResponseDto.class)))
				.thenReturn(res);

		Mockito.doNothing().when(preRegistrationRepository)
				.deleteByPreRegistrationId(preRegistrationEntity.getPreRegistrationId());

		System.out.println(preRegId);
		ResponseDto actualres = preRegistrationService.deleteIndividual(preRegId);
		System.out.println("Out put " + actualres);
		// assertEquals(expectedres.getStatus(), actualres.getStatus());

	}

	/*
	 * @Test(expected = OperationNotAllowedException.class) public void
	 * deleteDraftTest() {
	 * 
	 * String groupId = "33"; List<String> preregIds = Arrays.asList("1");
	 * RegistrationEntity applicant_Demographic = new RegistrationEntity();
	 * applicant_Demographic.setGroupId("33");
	 * applicant_Demographic.setPreRegistrationId("1");
	 * applicant_Demographic.setIsPrimary(true);
	 * applicant_Demographic.setStatusCode("update");
	 * 
	 * Mockito.when(registrationRepository.findByGroupIdAndPreRegistrationId(
	 * ArgumentMatchers.any(),
	 * ArgumentMatchers.any())).thenReturn(applicant_Demographic);
	 * doNothing().when(documentRepository).deleteAllByPreregId(
	 * applicant_Demographic.getPreRegistrationId());
	 * doNothing().when(registrationRepository).deleteByGroupIdAndPreRegistrationId(
	 * applicant_Demographic.getGroupId(),
	 * applicant_Demographic.getPreRegistrationId());
	 * registrationService.deleteIndividual(groupId, preregIds);
	 * 
	 * }
	 */

	/*
	 * @Test(expected = OperationNotAllowedException.class) public void
	 * deletePrimaryMemberTest() {
	 * 
	 * String groupId = "33"; List<String> preregIds = Arrays.asList("1");
	 * RegistrationEntity applicant_Demographic = new RegistrationEntity();
	 * applicant_Demographic.setGroupId("33");
	 * applicant_Demographic.setPreRegistrationId("1");
	 * applicant_Demographic.setIsPrimary(true);
	 * applicant_Demographic.setStatusCode("Draft");
	 * 
	 * Mockito.when(registrationRepository.findByGroupIdAndPreRegistrationId(
	 * ArgumentMatchers.any(),
	 * ArgumentMatchers.any())).thenReturn(applicant_Demographic);
	 * 
	 * doNothing().when(registrationRepository).deleteByGroupIdAndPreRegistrationId(
	 * applicant_Demographic.getGroupId(),
	 * applicant_Demographic.getPreRegistrationId());
	 * registrationService.deleteIndividual(groupId, preregIds);
	 * 
	 * }
	 */
	
	@Test
public void updateByPreIdTest() {
	Mockito.when(preRegistrationRepository.findById(PreRegistrationEntity.class,
						"1234")).thenReturn(preRegistrationEntity);
		preRegistrationService.addRegistration(jsonTestObject.toString());
	
	
}
}