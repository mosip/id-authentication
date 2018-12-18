package io.mosip.preregistration.application.test.service;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import io.mosip.kernel.core.jsonvalidator.exception.HttpRequestException;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.jsonvalidator.impl.JsonValidatorImpl;
import io.mosip.preregistration.application.dao.PreRegistrationDao;
import io.mosip.preregistration.application.dto.BookingRegistrationDTO;
import io.mosip.preregistration.application.dto.BookingResponseDTO;
import io.mosip.preregistration.application.dto.CreatePreRegistrationDTO;
import io.mosip.preregistration.application.dto.DeletePreRegistartionDTO;
import io.mosip.preregistration.application.dto.PreRegistartionStatusDTO;
import io.mosip.preregistration.application.dto.PreRegistrationViewDTO;
import io.mosip.preregistration.application.dto.ResponseDTO;
import io.mosip.preregistration.application.entity.PreRegistrationEntity;
import io.mosip.preregistration.application.errorcodes.ErrorCodes;
import io.mosip.preregistration.application.errorcodes.ErrorMessages;
import io.mosip.preregistration.application.exception.OperationNotAllowedException;
import io.mosip.preregistration.application.exception.RecordNotFoundException;
import io.mosip.preregistration.application.exception.system.JsonValidationException;
import io.mosip.preregistration.application.exception.system.SystemUnsupportedEncodingException;
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
	private JsonValidatorImpl jsonValidator;

	JSONParser parser = new JSONParser();

	@Autowired
	private PreRegistrationService preRegistrationService;

	List<PreRegistrationEntity> userDetails = new ArrayList<>();
	List<PreRegistrationViewDTO> response = new ArrayList<PreRegistrationViewDTO>();
	private PreRegistrationViewDTO responseDto;
	private PreRegistrationEntity preRegistrationEntity;
	private JSONObject jsonObject;
	private JSONObject jsonTestObject;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	File fileCr = null;
	File fileUp = null;

	@Before
	public void setup() throws ParseException, FileNotFoundException, IOException,
			org.json.simple.parser.ParseException, URISyntaxException {

		preRegistrationEntity = new PreRegistrationEntity();
		ClassLoader classLoader = getClass().getClassLoader();
		URI uri = new URI(
				classLoader.getResource("pre-registration-crby.json").getFile().trim().replaceAll("\\u0020", "%20"));
		fileCr = new File(uri.getPath());
		uri = new URI(
				classLoader.getResource("pre-registration-upby.json").getFile().trim().replaceAll("\\u0020", "%20"));
		fileUp = new File(uri.getPath());

		File file = new File(classLoader.getResource("pre-registration.json").getFile());
		jsonObject = (JSONObject) parser.parse(new FileReader(file));
		File fileTest = new File(classLoader.getResource("pre-registration-test.json").getFile());
		jsonTestObject = (JSONObject) parser.parse(new FileReader(fileTest));

		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Date date = dateFormat.parse("08/10/2018");
		long time = date.getTime();
		Timestamp times = new Timestamp(time);
		preRegistrationEntity.setCreateDateTime(times);

		preRegistrationEntity.setStatusCode("Pending_Appointment");
		preRegistrationEntity.setUpdateDateTime(times);
		preRegistrationEntity.setApplicantDetailJson(jsonTestObject.toString().getBytes("UTF-8"));

		preRegistrationEntity.setPreRegistrationId("1234");
		userDetails.add(preRegistrationEntity);

		logger.info("Entity " + preRegistrationEntity);

		responseDto = new PreRegistrationViewDTO();

		responseDto.setFullname(null);
		responseDto.setStatusCode("Pending_Appointment");
		responseDto.setPreId("1234");
		response.add(responseDto);
	}

	@Test
	public void successSaveImplTest() throws Exception {
		logger.info("----------successful save of application in impl-------");
		Mockito.when(pridGenerator.generateId()).thenReturn("67547447647457");
		Mockito.when(jsonValidator.validateJson(jsonObject.toString(), "mosip-prereg-identity-json-schema.json"))
				.thenReturn(null);
		ResponseDTO<CreatePreRegistrationDTO> res = preRegistrationService.addPreRegistration(jsonObject.toString());
		assertEquals(res.getResponse().get(0).getPrId(), "67547447647457");
	}

	@Test
	public void successUpdateTest() throws Exception {
		logger.info("----------successful save of application in impl-------");
		Mockito.when(jsonValidator.validateJson(jsonObject.toString(), "mosip-prereg-identity-json-schema.json"))
				.thenReturn(null);
		ResponseDTO<CreatePreRegistrationDTO> res = preRegistrationService.addPreRegistration(jsonObject.toString());

		equals(res.getResponse().get(0).getPrId());
	}

	@Test(expected = TablenotAccessibleException.class)
	public void saveFailureCheck() throws Exception {
		DataAccessLayerException exception = new DataAccessLayerException(ErrorCodes.PRG_PAM_APP_002.toString(),
				ErrorMessages.PRE_REGISTRATION_TABLE_NOT_ACCESSIBLE.toString(), null);
		Mockito.when(jsonValidator.validateJson(jsonObject.toString(), "mosip-prereg-identity-json-schema.json"))
				.thenReturn(null);
		Mockito.when(preRegistrationDao.save(Mockito.any())).thenThrow(exception);

		preRegistrationService.addPreRegistration(jsonObject.toString());
	}

	@Test(expected = OperationNotAllowedException.class)
	public void createByDateFailureTest() throws Exception {

		OperationNotAllowedException exception = new OperationNotAllowedException(ErrorCodes.PRG_PAM_APP_010.toString(),
				ErrorMessages.UNABLE_TO_CREATE_THE_PRE_REGISTRATION.toString());
		jsonObject = (JSONObject) parser.parse(new FileReader(fileCr));
		Mockito.when(jsonValidator.validateJson(jsonObject.toString(), "mosip-prereg-identity-json-schema.json"))
				.thenReturn(null);
		ResponseDTO<CreatePreRegistrationDTO> res = preRegistrationService.addPreRegistration(jsonObject.toString());
		assertEquals(res.getStatus(), "false");
	}

	@Test(expected = OperationNotAllowedException.class)
	public void updateByDateFailureTest() throws Exception {
		String prid = "1234";
		OperationNotAllowedException exception = new OperationNotAllowedException(ErrorCodes.PRG_PAM_APP_010.toString(),
				ErrorMessages.UNABLE_TO_UPDATE_THE_PRE_REGISTRATION.toString());
		jsonObject = (JSONObject) parser.parse(new FileReader(fileUp));
		Mockito.when(jsonValidator.validateJson(jsonObject.toString(), "mosip-prereg-identity-json-schema.json"))
				.thenReturn(null);

		Mockito.when(preRegistrationRepository.findBypreRegistrationId(prid)).thenReturn(preRegistrationEntity);

		ResponseDTO<CreatePreRegistrationDTO> res = preRegistrationService.addPreRegistration(jsonObject.toString());
		assertEquals(res.getStatus(), "false");
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void getApplicationDetails() {

		RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
		Mockito.when(restTemplateBuilder.build()).thenReturn(restTemplate);
		String userId = "9988905444";
		ResponseDTO<PreRegistrationViewDTO> response = new ResponseDTO<>();
		List<PreRegistrationViewDTO> viewList = new ArrayList<>();
		PreRegistrationViewDTO viewDto = new PreRegistrationViewDTO();
		viewDto.setPreId("1234");
		viewDto.setFullname("Rupika");
		viewDto.setStatusCode("Pending_Appointment");
		viewList.add(viewDto);
		response.setResponse(viewList);
		BookingResponseDTO<BookingRegistrationDTO> resultDto = new BookingResponseDTO<>();

		ResponseEntity<BookingResponseDTO> res = new ResponseEntity<>(resultDto, HttpStatus.OK);
		Mockito.when(preRegistrationRepository.findByuserId(Mockito.anyString())).thenReturn(userDetails);

		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
				Mockito.eq(BookingResponseDTO.class))).thenReturn(res);

		ResponseDTO<PreRegistrationViewDTO> actualRes = preRegistrationService.getAllApplicationDetails(userId);
		assertEqualsList(actualRes.getResponse(), response.getResponse());

	}

	@Test(expected = RecordNotFoundException.class)
	public void getApplicationDetailsFailureTest() {
		String userId = "9988905444";
		ResponseDTO<PreRegistrationViewDTO> response = new ResponseDTO<>();
		List<PreRegistrationViewDTO> viewList = new ArrayList<>();
		PreRegistrationViewDTO viewDto = new PreRegistrationViewDTO();
		viewDto.setPreId("1234");
		viewDto.setFullname("Sanober");
		viewDto.setStatusCode("Pending_Appointment");
		viewList.add(viewDto);
		response.setResponse(viewList);
		Mockito.when(preRegistrationRepository.findByuserId(Mockito.anyString())).thenReturn(null);
		ResponseDTO<PreRegistrationViewDTO> actualRes = preRegistrationService.getAllApplicationDetails(userId);
		assertEqualsList(actualRes.getResponse(), response.getResponse());

	}

	@Test
	public void getApplicationStatusTest() {
		String preId = "1234";
		ResponseDTO<PreRegistartionStatusDTO> response = new ResponseDTO<>();
		List<PreRegistartionStatusDTO> statusList = new ArrayList<PreRegistartionStatusDTO>();
		PreRegistartionStatusDTO statusDto = new PreRegistartionStatusDTO();
		statusDto.setPreRegistartionId(preId);
		statusDto.setStatusCode("Pending_Appointment");
		statusList.add(statusDto);
		response.setResponse(statusList);

		Mockito.when(preRegistrationRepository.findBypreRegistrationId(ArgumentMatchers.any()))
				.thenReturn(preRegistrationEntity);

		ResponseDTO<PreRegistartionStatusDTO> actualRes = preRegistrationService.getApplicationStatus(preId);
		assertEquals(response.getResponse().get(0).getStatusCode(), actualRes.getResponse().get(0).getStatusCode());

	}

	/**
	* 
	 */
	@Test(expected = RecordNotFoundException.class)
	public void getApplicationStatusFailure() {
		String preId = "1234";
		ResponseDTO<PreRegistartionStatusDTO> response = new ResponseDTO<>();
		List<PreRegistartionStatusDTO> statusList = new ArrayList<PreRegistartionStatusDTO>();
		PreRegistartionStatusDTO statusDto = new PreRegistartionStatusDTO();
		statusDto.setPreRegistartionId(preId);
		statusDto.setStatusCode("Pending_Appointment");
		statusList.add(statusDto);
		response.setResponse(statusList);

		Mockito.when(preRegistrationRepository.findBypreRegistrationId(ArgumentMatchers.any())).thenReturn(null);

		ResponseDTO<PreRegistartionStatusDTO> actualRes = preRegistrationService.getApplicationStatus(preId);
		assertEquals(response.getResponse().get(0).getStatusCode(), actualRes.getResponse().get(0).getStatusCode());

	}

	@Test(expected = TablenotAccessibleException.class)
	public void getApplicationDetailsTransactionFailureCheck() throws Exception {
		String userId = "9988905444";
		DataAccessLayerException exception = new DataAccessLayerException(ErrorCodes.PRG_PAM_APP_002.toString(),
				ErrorMessages.PRE_REGISTRATION_TABLE_NOT_ACCESSIBLE.toString(), null);
		Mockito.when(preRegistrationRepository.findByuserId(ArgumentMatchers.any())).thenThrow(exception);
		preRegistrationService.getAllApplicationDetails(userId);
	}

	@Test(expected = TablenotAccessibleException.class)
	public void getApplicationStatusTransactionFailureCheck() throws Exception {
		String preId = "1234";
		TablenotAccessibleException exception = new TablenotAccessibleException(ErrorCodes.PRG_PAM_APP_002.toString(),
				ErrorMessages.PRE_REGISTRATION_TABLE_NOT_ACCESSIBLE.toString());
		Mockito.when(preRegistrationRepository.findBypreRegistrationId(ArgumentMatchers.any())).thenThrow(exception);
		preRegistrationService.getApplicationStatus(preId);
	}

	public void assertEqualsList(List<PreRegistrationViewDTO> list, List<PreRegistrationViewDTO> list2) {
		for (int i = 0; i < list2.size(); i++) {
			assertEquals(list2.get(i).toString(), list.get(i).toString());
		}
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void deleteIndividualSuccessTest() {
		RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
		Mockito.when(restTemplateBuilder.build()).thenReturn(restTemplate);
		String preRegId = "1";
		preRegistrationEntity.setPreRegistrationId("1");
		preRegistrationEntity.setStatusCode("Pending_Appointment");

		ResponseEntity<ResponseDTO> res = new ResponseEntity<>(HttpStatus.OK);
		Mockito.when(preRegistrationRepository.findBypreRegistrationId(preRegId)).thenReturn(preRegistrationEntity);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		HttpEntity<String> entity = new HttpEntity<String>(headers);

		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.DELETE), Mockito.any(),
				Mockito.eq(ResponseDTO.class))).thenReturn(res);

		Mockito.when(preRegistrationRepository.deleteByPreRegistrationId(preRegistrationEntity.getPreRegistrationId()))
				.thenReturn(1);

		ResponseDTO<DeletePreRegistartionDTO> actualres = preRegistrationService.deleteIndividual(preRegId);
		System.out.println("Out put " + actualres);
		assertEquals("true", actualres.getStatus());

	}

	@Test
	public void updateByPreIdTest() {
		Mockito.when(preRegistrationRepository.findBypreRegistrationId("1234")).thenReturn(preRegistrationEntity);
		preRegistrationService.addPreRegistration(jsonTestObject.toString());
	}

	@Test(expected = RecordNotFoundException.class)
	public void RecordNotFoundExceptionTest() {
		System.out.println("===========" + preRegistrationRepository.findById(PreRegistrationEntity.class, "1234")
				+ "===================");
		Mockito.when(preRegistrationRepository.findBypreRegistrationId("1234")).thenReturn(null);
		preRegistrationService.addPreRegistration(jsonTestObject.toString());
	}

	@Test(expected = JsonValidationException.class)
	public void updateFailureCheck() throws Exception {
		HttpRequestException exception = new HttpRequestException(ErrorCodes.PRG_PAM_APP_007.name(),
				ErrorMessages.JSON_VALIDATION_FAILED.name());
		Mockito.when(jsonValidator.validateJson(jsonObject.toString(), "mosip-prereg-identity-json-schema.json"))
				.thenReturn(null);
		Mockito.when(preRegistrationDao.save(Mockito.any())).thenThrow(exception);

		preRegistrationService.addPreRegistration(jsonObject.toString());
	}

	@Test
	public void getPreRegistrationTest() {
		Mockito.when(preRegistrationRepository.findBypreRegistrationId("1234")).thenReturn(preRegistrationEntity);
		preRegistrationService.getPreRegistration("1234");
	}

	@Test
	public void updatePreRegistrationStatusTest() {
		Mockito.when(preRegistrationRepository.findBypreRegistrationId("1234")).thenReturn(preRegistrationEntity);
		preRegistrationService.updatePreRegistrationStatus("1234", "Booked");
	}

	@Test
	public void getApplicationByDateTest() {
		String fromDate = "2018-12-06 09:49:29";
		String toDate = "2018-12-06 12:59:29";
		ResponseDTO<String> response = new ResponseDTO<>();
		List<String> preIds = new ArrayList<>();
		List<PreRegistrationEntity> details = new ArrayList<>();
		PreRegistrationEntity entity = new PreRegistrationEntity();
		entity.setPreRegistrationId("1234");
		details.add(entity);

		preIds.add("1234");
		response.setResponse(preIds);

		String dateFormat = "yyyy-MM-dd HH:mm:ss";
		Date myFromDate;
		try {
			myFromDate = DateUtils.parse(URLDecoder.decode(fromDate, "UTF-8"), dateFormat);

			Date myToDate = DateUtils.parse(URLDecoder.decode(toDate, "UTF-8"), dateFormat);

			Mockito.when(preRegistrationRepository.findBycreateDateTimeBetween(new Timestamp(myFromDate.getTime()),
					new Timestamp(myToDate.getTime()))).thenReturn(details);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (java.io.UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		ResponseDTO<String> actualRes = preRegistrationService.getPreRegistrationByDate(fromDate, toDate);
		assertEquals(response.getResponse().get(0), actualRes.getResponse().get(0));

	}

	@Test(expected = UnsupportedEncodingException.class)
	public void getBydateFailureCheck() throws Exception {
		SystemUnsupportedEncodingException exception = new SystemUnsupportedEncodingException(
				ErrorCodes.PRG_PAM_APP_009.name(), ErrorMessages.UNSUPPORTED_ENCODING_CHARSET.name());
		String fromDate = "2018-12-06 09:49:29";
		String toDate = "2018-12-06 12:59:29";
		ResponseDTO<String> response = new ResponseDTO<>();
		List<String> preIds = new ArrayList<>();
		List<PreRegistrationEntity> details = new ArrayList<>();
		PreRegistrationEntity entity = new PreRegistrationEntity();
		entity.setPreRegistrationId("1234");
		details.add(entity);

		preIds.add("1234");
		response.setResponse(preIds);

		String dateFormat = "yyyy-MM-dd HH:mm:ss";
		Date myFromDate;
		Date myToDate;

		myFromDate = DateUtils.parse(URLDecoder.decode(fromDate, "UTF-0"), dateFormat);

		myToDate = DateUtils.parse(URLDecoder.decode(toDate, "UTF-8"), dateFormat);

		Mockito.when(preRegistrationRepository.findBycreateDateTimeBetween(new Timestamp(myFromDate.getTime()),
				new Timestamp(myToDate.getTime()))).thenThrow(exception);
		preRegistrationService.getPreRegistrationByDate(fromDate, toDate);

	}

}