package io.mosip.preregistration.application.test.service;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
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
import io.mosip.preregistration.application.dto.BookingRegistrationDTO;
import io.mosip.preregistration.application.dto.BookingResponseDTO;
import io.mosip.preregistration.application.dto.CreateDemographicDTO;
import io.mosip.preregistration.application.dto.DeletePreRegistartionDTO;
import io.mosip.preregistration.application.dto.DemographicRequestDTO;
import io.mosip.preregistration.application.dto.DocumentDeleteDTO;
import io.mosip.preregistration.application.dto.PreRegistartionStatusDTO;
import io.mosip.preregistration.application.dto.PreRegistrationViewDTO;
import io.mosip.preregistration.application.dto.ResponseDTO;
import io.mosip.preregistration.application.entity.DemographicEntity;
import io.mosip.preregistration.application.errorcodes.ErrorCodes;
import io.mosip.preregistration.application.errorcodes.ErrorMessages;
import io.mosip.preregistration.application.exception.RecordNotFoundException;
import io.mosip.preregistration.application.exception.system.DateParseException;
import io.mosip.preregistration.application.exception.system.JsonValidationException;
import io.mosip.preregistration.application.exception.system.SystemUnsupportedEncodingException;
import io.mosip.preregistration.application.repository.DemographicRepository;
import io.mosip.preregistration.application.service.DemographicService;
import io.mosip.preregistration.application.service.util.DemographicServiceUtil;
import io.mosip.preregistration.core.exception.InvalidRequestParameterException;
import io.mosip.preregistration.core.exception.TablenotAccessibleException;

/**
 * Test class to test the PreRegistration Service methods
 * 
 * @author Rajath KR
 * @author Sanober Noor
 * @author Tapaswini Bahera
 * @author Jagadishwari S
 * @author Ravi C Balaji
 * @since 1.0.0
 * 
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemographicServiceTest {

	@MockBean
	private DemographicRepository demographicRepository;

	@MockBean
	RestTemplateBuilder restTemplateBuilder;

	@MockBean
	private PridGenerator<String> pridGenerator;

	@MockBean
	private JsonValidatorImpl jsonValidator;

	@Autowired
	DemographicServiceUtil serviceUtil;

	JSONParser parser = new JSONParser();

	@Autowired
	private DemographicService preRegistrationService;

	List<DemographicEntity> userEntityDetails = new ArrayList<>();
	List<PreRegistrationViewDTO> responseViewList = new ArrayList<PreRegistrationViewDTO>();
	private PreRegistrationViewDTO preRegistrationViewDTO;
	private DemographicEntity preRegistrationEntity;
	private JSONObject jsonObject;
	private JSONObject jsonTestObject;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	File fileCr = null;
	File fileUp = null;
	DemographicRequestDTO<CreateDemographicDTO> demographicRequestDTO = null;
	CreateDemographicDTO createPreRegistrationDTO = null;
	boolean requestValidatorFlag = false;
	Map<String, String> requestMap = new HashMap<>();
	Map<String, String> requiredRequestMap = new HashMap<>();
	Timestamp times = null;
	BookingRegistrationDTO bookingRegistrationDTO;
	ResponseDTO<CreateDemographicDTO> responseDTO = null;

	@Value("${ver}")
	String versionUrl;

	@Value("${id}")
	String idUrl;

	@Before
	public void setup() throws ParseException, FileNotFoundException, IOException,
			org.json.simple.parser.ParseException, URISyntaxException {

		preRegistrationEntity = new DemographicEntity();
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
		Date date = dateFormat.parse("17/12/2018");
		long time = date.getTime();
		times = new Timestamp(time);
		preRegistrationEntity.setCreateDateTime(times);
		preRegistrationEntity.setCreatedBy("9988905444");
		preRegistrationEntity.setStatusCode("Pending_Appointment");
		preRegistrationEntity.setUpdateDateTime(times);
		preRegistrationEntity.setApplicantDetailJson(jsonTestObject.toJSONString().getBytes());
		preRegistrationEntity.setPreRegistrationId("1234");
		userEntityDetails.add(preRegistrationEntity);

		logger.info("Entity " + preRegistrationEntity);

		preRegistrationViewDTO = new PreRegistrationViewDTO();
		preRegistrationViewDTO.setFullname(null);
		preRegistrationViewDTO.setStatusCode("Pending_Appointment");
		preRegistrationViewDTO.setPreId("1234");
		responseViewList.add(preRegistrationViewDTO);

		createPreRegistrationDTO = new CreateDemographicDTO();
		createPreRegistrationDTO.setDemographicDetails(jsonObject);
		createPreRegistrationDTO.setPreRegistrationId("1234");

		demographicRequestDTO = new DemographicRequestDTO<CreateDemographicDTO>();
		demographicRequestDTO.setId("mosip.pre-registration.demographic.create");
		demographicRequestDTO.setVer("1.0");
		demographicRequestDTO.setReqTime(new Timestamp(System.currentTimeMillis()));
		demographicRequestDTO.setRequest(createPreRegistrationDTO);

		bookingRegistrationDTO = new BookingRegistrationDTO();
		bookingRegistrationDTO.setReg_date("2018-12-10");
		bookingRegistrationDTO.setRegistration_center_id("1");
		bookingRegistrationDTO.setSlotFromTime("09:00");
		bookingRegistrationDTO.setSlotToTime("09:13");

		requestMap.put("id", demographicRequestDTO.getId());
		requestMap.put("ver", demographicRequestDTO.getVer());
		requestMap.put("reqTime", demographicRequestDTO.getReqTime().toString());
		requestMap.put("request", demographicRequestDTO.getRequest().toString());

		requiredRequestMap.put("id", idUrl);
		requiredRequestMap.put("ver", versionUrl);

		responseDTO = new ResponseDTO<CreateDemographicDTO>();
		responseDTO.setStatus("true");
		responseDTO.setResTime(times);
		responseDTO.setErr(null);
	}

	@Test
	public void successSaveImplTest() throws Exception {
		Mockito.when(pridGenerator.generateId()).thenReturn("67547447647457");
		Mockito.when(jsonValidator.validateJson(jsonObject.toString(), "mosip-prereg-identity-json-schema.json"))
				.thenReturn(null);
		Mockito.when(demographicRepository.save(Mockito.any())).thenReturn(preRegistrationEntity);
		createPreRegistrationDTO = new CreateDemographicDTO();
		createPreRegistrationDTO.setDemographicDetails(jsonObject);
		createPreRegistrationDTO.setPreRegistrationId("");
		createPreRegistrationDTO.setCreatedBy("9988905444");
		createPreRegistrationDTO.setCreatedDateTime(times);
		createPreRegistrationDTO.setStatusCode("Pending_Appointment");
		demographicRequestDTO.setRequest(createPreRegistrationDTO);
		List<CreateDemographicDTO> listOfCreatePreRegistrationDTO = new ArrayList<>();
		listOfCreatePreRegistrationDTO.add(createPreRegistrationDTO);
		responseDTO.setResponse(listOfCreatePreRegistrationDTO);

		ResponseDTO<CreateDemographicDTO> actualRes = preRegistrationService.addPreRegistration(demographicRequestDTO);
		assertEquals(actualRes.getResponse().get(0).getStatusCode(), responseDTO.getResponse().get(0).getStatusCode());
	}

	@Test(expected = TablenotAccessibleException.class)
	public void saveFailureCheck() throws Exception {
		DataAccessLayerException exception = new DataAccessLayerException(ErrorCodes.PRG_PAM_APP_002.toString(),
				ErrorMessages.PRE_REGISTRATION_TABLE_NOT_ACCESSIBLE.toString(), null);
		Mockito.when(jsonValidator.validateJson(jsonObject.toString(), "mosip-prereg-identity-json-schema.json"))
				.thenReturn(null);
		Mockito.when(demographicRepository.save(Mockito.any())).thenThrow(exception);
		createPreRegistrationDTO = new CreateDemographicDTO();
		createPreRegistrationDTO.setDemographicDetails(jsonObject);
		createPreRegistrationDTO.setPreRegistrationId("");
		createPreRegistrationDTO.setCreatedBy("9988905444");
		createPreRegistrationDTO.setCreatedDateTime(times);
		demographicRequestDTO.setRequest(createPreRegistrationDTO);
		preRegistrationService.addPreRegistration(demographicRequestDTO);
	}

	@Test
	public void successUpdateTest() throws Exception {
		Mockito.when(jsonValidator.validateJson(jsonTestObject.toString(), "mosip-prereg-identity-json-schema.json"))
				.thenReturn(null);
		Mockito.when(demographicRepository.findBypreRegistrationId("1234")).thenReturn(preRegistrationEntity);

		Mockito.when(demographicRepository.save(Mockito.any())).thenReturn(preRegistrationEntity);
		createPreRegistrationDTO = new CreateDemographicDTO();
		createPreRegistrationDTO.setDemographicDetails(jsonTestObject);
		createPreRegistrationDTO.setPreRegistrationId("1234");
		createPreRegistrationDTO.setCreatedBy("9988905444");
		createPreRegistrationDTO.setCreatedDateTime(times);
		createPreRegistrationDTO.setUpdatedBy("9988905444");
		createPreRegistrationDTO.setUpdatedDateTime(times);
		demographicRequestDTO.setRequest(createPreRegistrationDTO);
		ResponseDTO<CreateDemographicDTO> res = preRegistrationService.addPreRegistration(demographicRequestDTO);
		assertEquals("1234",res.getResponse().get(0).getPreRegistrationId());
	}

	@Test(expected = JsonValidationException.class)
	public void updateFailureCheck() throws Exception {
		HttpRequestException exception = new HttpRequestException(ErrorCodes.PRG_PAM_APP_007.name(),
				ErrorMessages.JSON_PARSING_FAILED.name());
		Mockito.when(jsonValidator.validateJson(jsonTestObject.toString(), "mosip-prereg-identity-json-schema.json"))
				.thenReturn(null);

		Mockito.when(demographicRepository.findBypreRegistrationId("1234")).thenReturn(preRegistrationEntity);

		Mockito.when(demographicRepository.save(Mockito.any())).thenThrow(exception);
		createPreRegistrationDTO = new CreateDemographicDTO();
		createPreRegistrationDTO.setDemographicDetails(jsonTestObject);
		createPreRegistrationDTO.setPreRegistrationId("1234");
		createPreRegistrationDTO.setUpdatedBy("9988905444");
		createPreRegistrationDTO.setUpdatedDateTime(times);
		createPreRegistrationDTO.setCreatedBy("9988905444");
		createPreRegistrationDTO.setCreatedDateTime(times);
		demographicRequestDTO.setRequest(createPreRegistrationDTO);
		preRegistrationService.addPreRegistration(demographicRequestDTO);
	}

	@Test(expected = InvalidRequestParameterException.class)
	public void createByDateFailureTest() throws Exception {
		InvalidRequestParameterException exception = new InvalidRequestParameterException(
				ErrorCodes.PRG_PAM_APP_012.toString(), ErrorMessages.MISSING_REQUEST_PARAMETER.toString());
		jsonObject = (JSONObject) parser.parse(new FileReader(fileCr));
		Mockito.when(jsonValidator.validateJson(jsonObject.toString(), "mosip-prereg-identity-json-schema.json"))
				.thenReturn(null);

		preRegistrationEntity.setCreateDateTime(null);
		preRegistrationEntity.setCreatedBy("");
		preRegistrationEntity.setPreRegistrationId("");
		Mockito.when(demographicRepository.save(preRegistrationEntity)).thenThrow(exception);

		createPreRegistrationDTO = new CreateDemographicDTO();
		createPreRegistrationDTO.setDemographicDetails(jsonObject);
		createPreRegistrationDTO.setPreRegistrationId("");
		createPreRegistrationDTO.setCreatedBy("");
		createPreRegistrationDTO.setCreatedDateTime(null);
		demographicRequestDTO.setRequest(createPreRegistrationDTO);
		List<CreateDemographicDTO> listOfCreatePreRegistrationDTO = new ArrayList<>();
		listOfCreatePreRegistrationDTO.add(createPreRegistrationDTO);
		responseDTO.setResponse(listOfCreatePreRegistrationDTO);
		ResponseDTO<CreateDemographicDTO> actualRes = preRegistrationService.addPreRegistration(demographicRequestDTO);
		assertEquals(actualRes.getResponse().get(0).getStatusCode(), responseDTO.getResponse().get(0).getStatusCode());

	}

	@Test(expected = InvalidRequestParameterException.class)
	public void updateByDateFailureTest() throws Exception {
		String prid = "1234";
		InvalidRequestParameterException exception = new InvalidRequestParameterException(
				ErrorCodes.PRG_PAM_APP_012.toString(), ErrorMessages.MISSING_REQUEST_PARAMETER.toString());
		jsonObject = (JSONObject) parser.parse(new FileReader(fileUp));
		Mockito.when(demographicRepository.findBypreRegistrationId(prid)).thenReturn(preRegistrationEntity);

		Mockito.when(jsonValidator.validateJson(jsonObject.toString(), "mosip-prereg-identity-json-schema.json"))
				.thenThrow(exception);
		ResponseDTO<CreateDemographicDTO> res = preRegistrationService.addPreRegistration(demographicRequestDTO);
		assertEquals("false",res.getStatus());
	}

	@Test
	public void getApplicationDetailsTest() throws org.json.simple.parser.ParseException {

		RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
		Mockito.when(restTemplateBuilder.build()).thenReturn(restTemplate);
		String userId = "9988905444";
		ResponseDTO<PreRegistrationViewDTO> response = new ResponseDTO<>();
		List<PreRegistrationViewDTO> viewList = new ArrayList<>();
		PreRegistrationViewDTO viewDto = new PreRegistrationViewDTO();

		viewDto = new PreRegistrationViewDTO();
		viewDto.setPreId("1234");
		viewDto.setFullname("rupika");
		viewDto.setStatusCode(preRegistrationEntity.getStatusCode());
		viewDto.setBookingRegistrationDTO(bookingRegistrationDTO);

		viewList.add(viewDto);
		response.setResponse(viewList);
		response.setStatus("true");
		BookingResponseDTO<BookingRegistrationDTO> bookingResultDto = new BookingResponseDTO<>();

		ResponseEntity<BookingResponseDTO> res = new ResponseEntity<>(bookingResultDto, HttpStatus.OK);

		Mockito.when(demographicRepository.findByCreatedBy(userId)).thenReturn(userEntityDetails);

		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
				Mockito.eq(BookingResponseDTO.class))).thenReturn(res);

		ResponseDTO<PreRegistrationViewDTO> actualRes = preRegistrationService.getAllApplicationDetails(userId);
		assertEquals(actualRes.getStatus(), response.getStatus());

	}

	@Test(expected = RecordNotFoundException.class)
	public void getApplicationDetailsFailureTest() {
		String userId = "12345";
		Mockito.when(demographicRepository.findByCreatedBy(Mockito.anyString()))
				.thenThrow(RecordNotFoundException.class);
		preRegistrationService.getAllApplicationDetails(userId);

	}

	@Test(expected = InvalidRequestParameterException.class)
	public void getApplicationDetailsInvalidRequestTest() {
		InvalidRequestParameterException exception = new InvalidRequestParameterException(
				ErrorCodes.PRG_PAM_APP_012.name(), ErrorMessages.MISSING_REQUEST_PARAMETER.name());
		Mockito.when(demographicRepository.findByCreatedBy("")).thenThrow(exception);
		preRegistrationService.getAllApplicationDetails("");
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

		Mockito.when(demographicRepository.findBypreRegistrationId(ArgumentMatchers.any()))
				.thenReturn(preRegistrationEntity);

		ResponseDTO<PreRegistartionStatusDTO> actualRes = preRegistrationService.getApplicationStatus(preId);
		assertEquals(response.getResponse().get(0).getStatusCode(), actualRes.getResponse().get(0).getStatusCode());

	}

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

		Mockito.when(demographicRepository.findBypreRegistrationId(ArgumentMatchers.any())).thenReturn(null);

		ResponseDTO<PreRegistartionStatusDTO> actualRes = preRegistrationService.getApplicationStatus(preId);
		assertEquals(response.getResponse().get(0).getStatusCode(), actualRes.getResponse().get(0).getStatusCode());

	}

	@Test(expected = TablenotAccessibleException.class)
	public void getApplicationDetailsTransactionFailureCheck() throws Exception {
		String userId = "9988905444";
		DataAccessLayerException exception = new DataAccessLayerException(ErrorCodes.PRG_PAM_APP_002.toString(),
				ErrorMessages.PRE_REGISTRATION_TABLE_NOT_ACCESSIBLE.toString(), null);
		Mockito.when(demographicRepository.findByCreatedBy(ArgumentMatchers.any())).thenThrow(exception);
		preRegistrationService.getAllApplicationDetails(userId);
	}

	@Test(expected = RecordNotFoundException.class)
	public void deleteRecordNotFoundTest() {
		RecordNotFoundException exception = new RecordNotFoundException(ErrorCodes.PRG_PAM_APP_005.name(),
				ErrorMessages.UNABLE_TO_FETCH_THE_PRE_REGISTRATION.name());
		RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
		Mockito.when(restTemplateBuilder.build()).thenReturn(restTemplate);
		String preRegId = "1234";

		DocumentDeleteDTO deleteDTO = new DocumentDeleteDTO();
		deleteDTO.setDocumnet_Id(String.valueOf("1"));
		List<DocumentDeleteDTO> deleteAllList = new ArrayList<>();
		deleteAllList.add(deleteDTO);

		ResponseDTO<DocumentDeleteDTO> delResponseDto = new ResponseDTO<>();
		delResponseDto.setStatus("true");
		delResponseDto.setErr(null);
		delResponseDto.setResponse(deleteAllList);
		delResponseDto.setResTime(new Timestamp(System.currentTimeMillis()));

		ResponseEntity<ResponseDTO> res = new ResponseEntity<>(delResponseDto, HttpStatus.OK);
		Mockito.when(demographicRepository.findBypreRegistrationId(preRegId)).thenReturn(null);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.DELETE), Mockito.any(),
				Mockito.eq(ResponseDTO.class))).thenThrow(exception);

		preRegistrationService.deleteIndividual(preRegId);

	}

	@Test
	public void deleteIndividualSuccessTest() {
		RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
		Mockito.when(restTemplateBuilder.build()).thenReturn(restTemplate);
		String preRegId = "1234";
		preRegistrationEntity.setCreateDateTime(times);
		preRegistrationEntity.setCreatedBy("9988905444");
		preRegistrationEntity.setStatusCode("Pending_Appointment");
		preRegistrationEntity.setUpdateDateTime(times);
		preRegistrationEntity.setApplicantDetailJson(jsonTestObject.toJSONString().getBytes());
		preRegistrationEntity.setPreRegistrationId("1234");

		DocumentDeleteDTO deleteDTO = new DocumentDeleteDTO();
		deleteDTO.setDocumnet_Id(String.valueOf("1"));
		List<DocumentDeleteDTO> deleteAllList = new ArrayList<>();
		deleteAllList.add(deleteDTO);

		ResponseDTO<DocumentDeleteDTO> delResponseDto = new ResponseDTO<>();
		delResponseDto.setStatus("true");
		delResponseDto.setErr(null);
		delResponseDto.setResponse(deleteAllList);
		delResponseDto.setResTime(new Timestamp(System.currentTimeMillis()));

		Mockito.when(demographicRepository.findBypreRegistrationId(preRegId)).thenReturn(preRegistrationEntity);

		ResponseEntity<ResponseDTO> res = new ResponseEntity<>(delResponseDto, HttpStatus.OK);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.DELETE), Mockito.any(),
				Mockito.eq(ResponseDTO.class))).thenReturn(res);

		Mockito.when(demographicRepository.deleteByPreRegistrationId(preRegistrationEntity.getPreRegistrationId()))
				.thenReturn(1);

		ResponseDTO<DeletePreRegistartionDTO> actualres = preRegistrationService.deleteIndividual(preRegId);

		assertEquals("true", actualres.getStatus());

	}

	@Test
	public void updateByPreIdTest() {
		Mockito.when(demographicRepository.findBypreRegistrationId("1234")).thenReturn(preRegistrationEntity);
		createPreRegistrationDTO = new CreateDemographicDTO();
		createPreRegistrationDTO.setDemographicDetails(jsonTestObject);
		createPreRegistrationDTO.setPreRegistrationId("1234");
		createPreRegistrationDTO.setUpdatedBy("9988905444");
		createPreRegistrationDTO.setUpdatedDateTime(times);
		createPreRegistrationDTO.setCreatedBy("9988905444");
		createPreRegistrationDTO.setCreatedDateTime(times);
		demographicRequestDTO.setRequest(createPreRegistrationDTO);
		preRegistrationService.addPreRegistration(demographicRequestDTO);
	}

	@Test(expected = RecordNotFoundException.class)
	public void RecordNotFoundExceptionTest() {
		Mockito.when(demographicRepository.findBypreRegistrationId("1234")).thenReturn(null);

		createPreRegistrationDTO = new CreateDemographicDTO();
		createPreRegistrationDTO.setDemographicDetails(jsonTestObject);
		createPreRegistrationDTO.setPreRegistrationId("1234");
		createPreRegistrationDTO.setUpdatedBy("9988905444");
		createPreRegistrationDTO.setUpdatedDateTime(times);
		createPreRegistrationDTO.setCreatedBy("9988905444");
		createPreRegistrationDTO.setCreatedDateTime(times);
		demographicRequestDTO.setRequest(createPreRegistrationDTO);

		preRegistrationService.addPreRegistration(demographicRequestDTO);
	}

	@Test
	public void getPreRegistrationTest() {
		Mockito.when(demographicRepository.findBypreRegistrationId("1234")).thenReturn(preRegistrationEntity);
		preRegistrationService.getDemographicData("1234");
	}

	@Test
	public void updatePreRegistrationStatusTest() {
		Mockito.when(demographicRepository.findBypreRegistrationId("1234")).thenReturn(preRegistrationEntity);
		preRegistrationService.updatePreRegistrationStatus("1234", "Booked");
	}

	@Test
	public void getApplicationByDateTest() {
		String fromDate = "2018-12-06 09:49:29";
		String toDate = "2018-12-06 12:59:29";
		ResponseDTO<String> response = new ResponseDTO<>();
		List<String> preIds = new ArrayList<>();
		List<DemographicEntity> details = new ArrayList<>();
		DemographicEntity entity = new DemographicEntity();
		entity.setPreRegistrationId("1234");
		details.add(entity);

		preIds.add("1234");
		response.setResponse(preIds);
		response.setStatus("true");

		String dateFormat = "yyyy-MM-dd HH:mm:ss";
		Date myFromDate;
		try {
			myFromDate = DateUtils.parseToDate(URLDecoder.decode(fromDate, "UTF-8"), dateFormat);

			Date myToDate = DateUtils.parseToDate(URLDecoder.decode(toDate, "UTF-8"), dateFormat);

			Mockito.when(demographicRepository.findBycreateDateTimeBetween(new Timestamp(myFromDate.getTime()),
					new Timestamp(myToDate.getTime()))).thenReturn(details);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (java.io.UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		ResponseDTO<String> actualRes = preRegistrationService.getPreRegistrationByDate(fromDate, toDate);
		assertEquals(actualRes.getStatus(), response.getStatus());

	}

	@Test(expected = SystemUnsupportedEncodingException.class)
	public void getBydateFailureCheck() throws Exception {
		SystemUnsupportedEncodingException exception = new SystemUnsupportedEncodingException(
				ErrorCodes.PRG_PAM_APP_009.name(), ErrorMessages.UNSUPPORTED_ENCODING_CHARSET.name());
		String fromDate = "2018-12-06 09:49:29";
		String toDate = "2018-12-06 12:59:29";
		ResponseDTO<String> response = new ResponseDTO<>();
		List<String> preIds = new ArrayList<>();
		List<DemographicEntity> details = new ArrayList<>();
		DemographicEntity entity = new DemographicEntity();
		entity.setPreRegistrationId("1234");
		details.add(entity);

		preIds.add("1234");
		response.setResponse(preIds);

		String dateFormat = "yyyy-MM-dd HH:mm:ss";
		Date myFromDate;
		Date myToDate;

		myFromDate = DateUtils.parseToDate(URLDecoder.decode(fromDate, "UTF-0"), dateFormat);

		myToDate = DateUtils.parseToDate(URLDecoder.decode(toDate, "UTF-8"), dateFormat);

		Mockito.when(demographicRepository.findBycreateDateTimeBetween(new Timestamp(myFromDate.getTime()),
				new Timestamp(myToDate.getTime()))).thenThrow(exception);
		preRegistrationService.getPreRegistrationByDate(fromDate, toDate);

	}

	@Test(expected = io.mosip.kernel.core.exception.ParseException.class)
	public void getBydateFailureParseCheck() throws Exception {
		DateParseException exception = new DateParseException(ErrorCodes.PRG_PAM_APP_011.name(),
				ErrorMessages.UNSUPPORTED_DATE_FORMAT.name());
		String fromDate = "2018-12-06 09:49:29";
		String toDate = "2018-12-06 12:59:29";
		ResponseDTO<String> response = new ResponseDTO<>();
		List<String> preIds = new ArrayList<>();
		List<DemographicEntity> details = new ArrayList<>();
		DemographicEntity entity = new DemographicEntity();
		entity.setPreRegistrationId("1234");
		details.add(entity);

		preIds.add("1234");
		response.setResponse(preIds);

		String dateFormat = "yyyy-MM-dd HH:mm:ss.SSS";
		Date myFromDate;
		Date myToDate;

		myFromDate = DateUtils.parseToDate(URLDecoder.decode(fromDate, "UTF-0"), dateFormat);

		myToDate = DateUtils.parseToDate(URLDecoder.decode(toDate, "UTF-8"), dateFormat);

		Mockito.when(demographicRepository.findBycreateDateTimeBetween(new Timestamp(myFromDate.getTime()),
				new Timestamp(myToDate.getTime()))).thenThrow(exception);
		preRegistrationService.getPreRegistrationByDate(fromDate, toDate);

	}

}