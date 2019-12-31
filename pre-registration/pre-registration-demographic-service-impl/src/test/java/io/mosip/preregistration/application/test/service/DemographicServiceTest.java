package io.mosip.preregistration.application.test.service;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.auth.adapter.model.AuthUserDetails;
import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.idgenerator.spi.PridGenerator;
import io.mosip.kernel.core.idobjectvalidator.constant.IdObjectValidatorSupportedOperations;
import io.mosip.kernel.core.idobjectvalidator.exception.IdObjectIOException;
import io.mosip.kernel.core.idobjectvalidator.exception.IdObjectValidationFailedException;
import io.mosip.kernel.core.idobjectvalidator.spi.IdObjectValidator;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.preregistration.application.DemographicTestApplication;
import io.mosip.preregistration.booking.serviceimpl.service.BookingServiceIntf;
import io.mosip.preregistration.core.code.AuditLogVariables;
import io.mosip.preregistration.core.code.StatusCodes;
import io.mosip.preregistration.core.common.dto.AuditRequestDto;
import io.mosip.preregistration.core.common.dto.BookingRegistrationDTO;
import io.mosip.preregistration.core.common.dto.DeleteBookingDTO;
import io.mosip.preregistration.core.common.dto.DemographicResponseDTO;
import io.mosip.preregistration.core.common.dto.DocumentDeleteDTO;
import io.mosip.preregistration.core.common.dto.DocumentDeleteResponseDTO;
import io.mosip.preregistration.core.common.dto.DocumentMultipartResponseDTO;
import io.mosip.preregistration.core.common.dto.ExceptionJSONInfoDTO;
import io.mosip.preregistration.core.common.dto.MainRequestDTO;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.common.dto.PreRegIdsByRegCenterIdDTO;
import io.mosip.preregistration.core.common.dto.PreRegistartionStatusDTO;
import io.mosip.preregistration.core.common.dto.identity.DemographicIdentityRequestDTO;
import io.mosip.preregistration.core.common.dto.identity.Identity;
import io.mosip.preregistration.core.common.dto.identity.IdentityJsonValues;
import io.mosip.preregistration.core.common.entity.DemographicEntity;
import io.mosip.preregistration.core.exception.HashingException;
import io.mosip.preregistration.core.exception.InvalidRequestParameterException;
import io.mosip.preregistration.core.exception.PreIdInvalidForUserIdException;
import io.mosip.preregistration.core.exception.RecordFailedToDeleteException;
import io.mosip.preregistration.core.exception.TableNotAccessibleException;
import io.mosip.preregistration.core.util.AuditLogUtil;
import io.mosip.preregistration.core.util.CryptoUtil;
import io.mosip.preregistration.core.util.HashUtill;
import io.mosip.preregistration.core.util.RequestValidator;
import io.mosip.preregistration.demographic.dto.DeletePreRegistartionDTO;
import io.mosip.preregistration.demographic.dto.DemographicCreateResponseDTO;
import io.mosip.preregistration.demographic.dto.DemographicMetadataDTO;
import io.mosip.preregistration.demographic.dto.DemographicRequestDTO;
import io.mosip.preregistration.demographic.dto.DemographicUpdateResponseDTO;
import io.mosip.preregistration.demographic.dto.DemographicViewDTO;
import io.mosip.preregistration.demographic.dto.PridFetchResponseDto;
import io.mosip.preregistration.demographic.errorcodes.ErrorCodes;
import io.mosip.preregistration.demographic.errorcodes.ErrorMessages;
import io.mosip.preregistration.demographic.exception.BookingDeletionFailedException;
import io.mosip.preregistration.demographic.exception.DemographicServiceException;
import io.mosip.preregistration.demographic.exception.RecordFailedToUpdateException;
import io.mosip.preregistration.demographic.exception.RecordNotFoundException;
import io.mosip.preregistration.demographic.exception.RecordNotFoundForPreIdsException;
import io.mosip.preregistration.demographic.exception.system.SystemIllegalArgumentException;
import io.mosip.preregistration.demographic.repository.DemographicRepository;
import io.mosip.preregistration.demographic.service.DemographicServiceIntf;
import io.mosip.preregistration.demographic.service.util.DemographicServiceUtil;
import io.mosip.preregistration.document.service.DocumentServiceIntf;

/**
 * Test class to test the PreRegistration Service methods
 * 
 * @author Rajath KR
 * @author Sanober Noor
 * @author Ravi C Balaji
 * @since 1.0.0
 * 
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { DemographicTestApplication.class })
public class DemographicServiceTest {

	/**
	 * Autowired reference for $link{DemographicService}
	 */
	@Autowired
	private DemographicServiceIntf preRegistrationService;

	/**
	 * Mocking the DemographicRepository bean
	 */
	@MockBean
	private DemographicRepository demographicRepository;

	/**
	 * Mocking the RestTemplateBuilder bean
	 */
	@MockBean(name="restTemplate")
	RestTemplate restTemplate;

	/**
	 * Mocking the PridGenerator bean
	 */
	@MockBean
	private PridGenerator<String> pridGenerator;

	/**
	 * Mocking the JsonValidatorImpl bean
	 */
	@MockBean(name = "idObjectValidator")
	private IdObjectValidator jsonValidator;

	/**
	 * Autowired reference for $link{DemographicServiceUtil}
	 */
	@Mock
	DemographicServiceUtil serviceUtil;

	JSONParser parser = new JSONParser();

	@MockBean
	private DocumentServiceIntf documentServiceIntf;

	@MockBean
	private BookingServiceIntf bookingServiceIntf;

	@MockBean
	private AuditLogUtil auditLogUtil;

	@MockBean
	private CryptoUtil cryptoUtil;

	@MockBean
	private RequestValidator requestValidator;

	@Mock
	private DemographicEntity entity;

	String userId = "";

	List<DemographicEntity> userEntityDetails = new ArrayList<>();
	List<DemographicViewDTO> responseViewList = new ArrayList<DemographicViewDTO>();
	private DemographicViewDTO preRegistrationViewDTO;
	private DemographicEntity preRegistrationEntity;
	private JSONObject jsonObject;
	private JSONObject jsonTestObject;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	File fileCr = null;
	File fileUp = null;
	MainRequestDTO<DemographicRequestDTO> request = null;
	DemographicRequestDTO createPreRegistrationDTO = null;
	DemographicResponseDTO demographicResponseDTO = null;
	DemographicCreateResponseDTO demographicResponseForCreateDTO = null;
	DemographicUpdateResponseDTO demographicResponseForUpdateDTO = null;
	DemographicIdentityRequestDTO demographicIdentityRequestDTO = new DemographicIdentityRequestDTO();
	Identity identity = new Identity();
	IdentityJsonValues identityJsonValues = new IdentityJsonValues();
	boolean requestValidatorFlag = false;
	Map<String, String> requestMap = new HashMap<>();
	Map<String, String> requiredRequestMap = new HashMap<>();
	LocalDateTime times = null;
	BookingRegistrationDTO bookingRegistrationDTO;
	MainResponseDTO<DemographicResponseDTO> responseDTO = null;
	MainResponseDTO<DemographicCreateResponseDTO> responseCreateDTO = null;
	AuditRequestDto auditRequestDto = new AuditRequestDto();

	@Value("${version}")
	String versionUrl;

	/**
	 * Reference for ${createId} from property file
	 */
	@Value("${mosip.preregistration.demographic.create.id}")
	private String createId;

	/**
	 * Reference for ${updateId} from property file
	 */
	@Value("${mosip.preregistration.demographic.update.id}")
	private String updateId;

	/**
	 * Reference for ${retrieveId} from property file
	 */
	@Value("${mosip.preregistration.demographic.retrieve.basic.id}")
	private String retrieveId;
	/**
	 * Reference for ${retrieveDetailsId} from property file
	 */
	@Value("${mosip.preregistration.demographic.retrieve.details.id}")
	private String retrieveDetailsId;

	/**
	 * Reference for ${retrieveStatusId} from property file
	 */
	@Value("${mosip.preregistration.demographic.retrieve.status.id}")
	private String retrieveStatusId;

	/**
	 * Reference for ${deleteId} from property file
	 */
	@Value("${mosip.preregistration.demographic.delete.id}")
	private String deleteId;

	/**
	 * Reference for ${updateStatusId} from property file
	 */
	@Value("${mosip.preregistration.demographic.update.status.id}")
	private String updateStatusId;

	@Value("${mosip.pregistration.pagesize}")
	private String pageSize;

	/**
	 * Reference for ${dateId} from property file
	 */
	@Value("${mosip.preregistration.demographic.retrieve.date.id}")
	private String dateId;

	LocalDate fromDate = LocalDate.now();
	LocalDate toDate = LocalDate.now();
	@Autowired
	private ObjectMapper mapper;
	JSONArray fullname;
	LocalDateTime encryptionDateTime = DateUtils.getUTCCurrentDateTime();
	DemographicServiceIntf spyDemographicService;
	String preId = "";
	String identityMappingJson = "";

	MainResponseDTO<DocumentMultipartResponseDTO> documentResultDto = new MainResponseDTO<>();
	DocumentMultipartResponseDTO documentMultipartResponseDTO = new DocumentMultipartResponseDTO();

	@Mock
	private AuthUserDetails authUserDetails;

	@Mock
	SecurityContextHolder securityContextHolder;

	/**
	 * @throws ParseException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws org.json.simple.parser.ParseException
	 * @throws URISyntaxException
	 */
	@Before
	public void setup() throws ParseException, FileNotFoundException, IOException,
			org.json.simple.parser.ParseException, URISyntaxException {
		ReflectionTestUtils.setField(preRegistrationService, "jsonValidator", jsonValidator);
		preRegistrationEntity = new DemographicEntity();
		ClassLoader classLoader = getClass().getClassLoader();
		URI uri = new URI(
				classLoader.getResource("pre-registration-crby.json").getFile().trim().replaceAll("\\u0020", "%20"));
		fileCr = new File(uri.getPath());
		uri = new URI(
				classLoader.getResource("pre-registration-upby.json").getFile().trim().replaceAll("\\u0020", "%20"));
		fileUp = new File(uri.getPath());

		File file = new File(classLoader.getResource("pre-registration-test.json").getFile());
		jsonObject = (JSONObject) parser.parse(new FileReader(file));

		File fileTest = new File(classLoader.getResource("pre-registration-test.json").getFile());
		jsonTestObject = (JSONObject) parser.parse(new FileReader(fileTest));

		identityMappingJson = "{\r\n" + "	\"identity\": {\r\n" + "		\"name\": {\r\n"
				+ "			\"value\": \"fullName\",\r\n" + "			\"isMandatory\" : true\r\n" + "		},\r\n"
				+ "		\"proofOfAddress\": {\r\n" + "			\"value\" : \"proofOfAddress\"\r\n" + "		},\r\n"
				+ "		\"postalCode\": {\r\n" + "			\"value\" : \"postalCode\"\r\n" + "		}\r\n" + "	}\r\n"
				+ "}  ";

		times = LocalDateTime.now();
		preRegistrationEntity.setCreateDateTime(times);
		preRegistrationEntity.setCreatedBy("9988905444");
		preRegistrationEntity.setStatusCode("Pending_Appointment");
		preRegistrationEntity.setUpdateDateTime(times);

		preRegistrationEntity.setPreRegistrationId("98746563542672");
		preRegistrationEntity
				.setDemogDetailHash(HashUtill.hashUtill(jsonTestObject.toJSONString().getBytes()).toString());
		userEntityDetails.add(preRegistrationEntity);

		logger.info("Entity " + preRegistrationEntity);

		preRegistrationViewDTO = new DemographicViewDTO();
		preRegistrationViewDTO.setStatusCode("Pending_Appointment");
		preRegistrationViewDTO.setPreRegistrationId("98746563542672");
		responseViewList.add(preRegistrationViewDTO);

		createPreRegistrationDTO = new DemographicRequestDTO();
		createPreRegistrationDTO.setDemographicDetails(jsonObject);
		preId = "98746563542672";

		request = new MainRequestDTO<DemographicRequestDTO>();
		request.setId("mosip.pre-registration.demographic.create");
		request.setVersion("1.0");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		mapper.setDateFormat(df);
		mapper.setTimeZone(TimeZone.getDefault());
		request.setRequesttime(df.parse("2019-01-22T07:22:57.186Z"));
		request.setRequesttime(new Timestamp(System.currentTimeMillis()));
		request.setRequest(createPreRegistrationDTO);

		bookingRegistrationDTO = new BookingRegistrationDTO();
		bookingRegistrationDTO.setRegDate("2018-12-10");
		bookingRegistrationDTO.setRegistrationCenterId("1");
		bookingRegistrationDTO.setSlotFromTime("09:00");
		bookingRegistrationDTO.setSlotToTime("09:13");

		requestMap.put("version", versionUrl);

		requiredRequestMap.put("ver", versionUrl);

		responseDTO = new MainResponseDTO<DemographicResponseDTO>();
		responseCreateDTO = new MainResponseDTO<DemographicCreateResponseDTO>();

		responseDTO.setResponsetime(serviceUtil.getCurrentResponseTime());

		responseDTO.setErrors(null);
		responseCreateDTO.setResponsetime(serviceUtil.getCurrentResponseTime());
		responseCreateDTO.setErrors(null);

		auditRequestDto.setActionTimeStamp(LocalDateTime.now(ZoneId.of("UTC")));
		auditRequestDto.setApplicationId(AuditLogVariables.MOSIP_1.toString());
		auditRequestDto.setApplicationName(AuditLogVariables.PREREGISTRATION.toString());
		auditRequestDto.setCreatedBy(AuditLogVariables.SYSTEM.toString());
		auditRequestDto.setHostIp(auditLogUtil.getServerIp());
		auditRequestDto.setHostName(auditLogUtil.getServerName());
		auditRequestDto.setId(AuditLogVariables.NO_ID.toString());
		auditRequestDto.setIdType(AuditLogVariables.PRE_REGISTRATION_ID.toString());
		auditRequestDto.setSessionUserId(AuditLogVariables.SYSTEM.toString());
		auditRequestDto.setSessionUserName(AuditLogVariables.SYSTEM.toString());
		AuthUserDetails applicationUser = Mockito.mock(AuthUserDetails.class);
		Authentication authentication = Mockito.mock(Authentication.class);
		SecurityContext securityContext = Mockito.mock(SecurityContext.class);
		Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);
		Mockito.when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(applicationUser);
		spyDemographicService = Mockito.spy(preRegistrationService);

		userId = "9988905444";
		identityJsonValues.setIsMandatory(true);
		identityJsonValues.setValue("fullName");
		identity.setName(identityJsonValues);
		identityJsonValues.setIsMandatory(true);
		identityJsonValues.setValue("postalCode");
		identity.setPostalCode(identityJsonValues);
		identityJsonValues.setIsMandatory(true);
		identityJsonValues.setValue("proofOfAddress");
		identity.setProofOfAddress(identityJsonValues);

		demographicIdentityRequestDTO.setIdentity(identity);

		documentMultipartResponseDTO.setDocCatCode("POA");
		documentMultipartResponseDTO.setDocName("abc.pdf");
		documentMultipartResponseDTO.setDocTypCode("RNC");
		documentMultipartResponseDTO.setLangCode("fra");
		documentMultipartResponseDTO.setDocumentId("a11n3hbr3o30a2");
		documentResultDto.setResponse(documentMultipartResponseDTO);
	}

	@Test
	public void getPreRegistrationTest() {
		byte[] encryptedDemographicDetails = jsonTestObject.toJSONString().getBytes();// { 1, 0, 1, 0, 1, 0 };
		preRegistrationEntity.setApplicantDetailJson(encryptedDemographicDetails);
		preRegistrationEntity.setDemogDetailHash(HashUtill.hashUtill(preRegistrationEntity.getApplicantDetailJson()));
		Mockito.when(demographicRepository.findBypreRegistrationId("98746563542672")).thenReturn(preRegistrationEntity);
		Mockito.when(cryptoUtil.decrypt(Mockito.any(), Mockito.any())).thenReturn(jsonObject.toString().getBytes());
		MainResponseDTO<DemographicResponseDTO> res = preRegistrationService.getDemographicData("98746563542672");
		assertEquals("98746563542672", res.getResponse().getPreRegistrationId());
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void successSaveImplTest() throws Exception {

		byte[] encryptedDemographicDetails = jsonTestObject.toJSONString().getBytes();// { 1, 0, 1, 0, 1, 0 };
		requestMap.put("id", createId);
		Mockito.when(serviceUtil.prepareRequestMap(request)).thenReturn(requestMap);
		Mockito.when(cryptoUtil.encrypt(Mockito.any(), Mockito.any())).thenReturn(encryptedDemographicDetails);
        PridFetchResponseDto dto=new PridFetchResponseDto();
        dto.setPrid("67547447647457");
		preRegistrationEntity.setApplicantDetailJson(encryptedDemographicDetails);
		Mockito.when(serviceUtil.generateId()).thenReturn("67547447647457");
		Mockito.when(jsonValidator.validateIdObject(jsonObject.toString(),
				IdObjectValidatorSupportedOperations.NEW_REGISTRATION)).thenReturn(true);
		Mockito.when(demographicRepository.save(Mockito.any())).thenReturn(preRegistrationEntity);
		demographicResponseForCreateDTO = new DemographicCreateResponseDTO();
		demographicResponseForCreateDTO.setDemographicDetails(jsonObject);
		demographicResponseForCreateDTO.setPreRegistrationId("67547447647457");
		demographicResponseForCreateDTO
				.setCreatedDateTime(serviceUtil.getLocalDateString(LocalDateTime.now(ZoneId.of("UTC"))));
		demographicResponseForCreateDTO.setStatusCode("Pending_Appointment");
		createPreRegistrationDTO = new DemographicRequestDTO();
		createPreRegistrationDTO.setDemographicDetails(jsonObject);
		createPreRegistrationDTO.setLangCode("fra");
		request.setRequest(createPreRegistrationDTO);
		List<DemographicCreateResponseDTO> listOfCreatePreRegistrationDTO = new ArrayList<>();
		ResponseWrapper<PridFetchResponseDto> pridRes = new ResponseWrapper<>();
		pridRes.setResponse(dto);
		ResponseEntity<ResponseWrapper<PridFetchResponseDto>> res = new ResponseEntity<>(pridRes,
				HttpStatus.OK);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
				Mockito.eq(new ParameterizedTypeReference<ResponseWrapper<PridFetchResponseDto>>() {
				}))).thenReturn(res);
		Mockito.when(serviceUtil.generateId()).thenReturn("98746563542672");
	
		listOfCreatePreRegistrationDTO.add(demographicResponseForCreateDTO);
		responseCreateDTO.setResponse(demographicResponseForCreateDTO);

		Mockito.when(cryptoUtil.decrypt(Mockito.any(), Mockito.any())).thenReturn(jsonObject.toString().getBytes());
		MainResponseDTO<DemographicCreateResponseDTO> actualRes = preRegistrationService.addPreRegistration(request);
		assertEquals(actualRes.getResponse().getStatusCode(), responseCreateDTO.getResponse().getStatusCode());
	}

	/**
	 * @throws Exception
	 */
	@Test(expected = DemographicServiceException.class)
	public void httpServerErrorSaveTest() throws Exception {

		byte[] encryptedDemographicDetails = jsonTestObject.toJSONString().getBytes();// { 1, 0, 1, 0, 1, 0 };
		HttpServerErrorException errorException = new HttpServerErrorException(HttpStatus.BAD_GATEWAY, "httpError");

		requestMap.put("id", createId);
		Mockito.when(serviceUtil.prepareRequestMap(request)).thenReturn(requestMap);
		Mockito.when(cryptoUtil.encrypt(Mockito.any(), Mockito.any())).thenReturn(encryptedDemographicDetails);

		preRegistrationEntity.setApplicantDetailJson(encryptedDemographicDetails);
		Mockito.when(serviceUtil.generateId()).thenReturn("67547447647457");
		Mockito.when(jsonValidator.validateIdObject(jsonObject.toString(),
				IdObjectValidatorSupportedOperations.NEW_REGISTRATION)).thenReturn(true);
		Mockito.when(demographicRepository.save(Mockito.any())).thenThrow(errorException);
		demographicResponseForCreateDTO = new DemographicCreateResponseDTO();
		demographicResponseForCreateDTO.setDemographicDetails(jsonObject);
		demographicResponseForCreateDTO.setPreRegistrationId("67547447647457");
		demographicResponseForCreateDTO
				.setCreatedDateTime(serviceUtil.getLocalDateString(LocalDateTime.now(ZoneId.of("UTC"))));
		demographicResponseForCreateDTO.setStatusCode("Pending_Appointment");
		createPreRegistrationDTO = new DemographicRequestDTO();
		createPreRegistrationDTO.setDemographicDetails(jsonObject);
		createPreRegistrationDTO.setLangCode("fra");
		request.setRequest(createPreRegistrationDTO);
		List<DemographicCreateResponseDTO> listOfCreatePreRegistrationDTO = new ArrayList<>();
		listOfCreatePreRegistrationDTO.add(demographicResponseForCreateDTO);
		responseCreateDTO.setResponse(demographicResponseForCreateDTO);
		ResponseWrapper<PridFetchResponseDto> pridRes = new ResponseWrapper<>();
		PridFetchResponseDto dto= new PridFetchResponseDto();
		dto.setPrid("98746563542672");
		pridRes.setResponse(dto);
		ResponseEntity<ResponseWrapper<PridFetchResponseDto>> res = new ResponseEntity<>(pridRes,
				HttpStatus.OK);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
				Mockito.eq(new ParameterizedTypeReference<ResponseWrapper<PridFetchResponseDto>>() {
				}))).thenReturn(res);
		Mockito.when(serviceUtil.generateId()).thenReturn("98746563542672");

		Mockito.when(cryptoUtil.decrypt(Mockito.any(), Mockito.any())).thenReturn(jsonObject.toString().getBytes());
		MainResponseDTO<DemographicCreateResponseDTO> actualRes = preRegistrationService.addPreRegistration(request);
		assertEquals(actualRes.getResponse().getStatusCode(), responseCreateDTO.getResponse().getStatusCode());
	}

	/**
	 * @throws Exception
	 */
	@Test(expected = TableNotAccessibleException.class)
	public void saveFailureCheck() throws Exception {
		DataAccessLayerException exception = new DataAccessLayerException(ErrorCodes.PRG_PAM_APP_002.toString(),
				ErrorMessages.PRE_REGISTRATION_TABLE_NOT_ACCESSIBLE.toString(), null);
		byte[] encryptedDemographicDetails = jsonTestObject.toJSONString().getBytes();// { 1, 0, 1, 0, 1, 0 };
		requestMap.put("id", createId);
		Mockito.when(serviceUtil.prepareRequestMap(request)).thenReturn(requestMap);
		Mockito.when(cryptoUtil.encrypt(Mockito.any(), Mockito.any())).thenReturn(encryptedDemographicDetails);

		preRegistrationEntity.setApplicantDetailJson(encryptedDemographicDetails);
		Mockito.when(jsonValidator.validateIdObject(Mockito.any(), Mockito.any())).thenReturn(true);
		Mockito.when(cryptoUtil.decrypt(Mockito.any(), Mockito.any())).thenReturn(jsonObject.toString().getBytes());
		ResponseWrapper<PridFetchResponseDto> pridRes = new ResponseWrapper<>();
		PridFetchResponseDto dto= new PridFetchResponseDto();
		dto.setPrid("98746563542672");
		pridRes.setResponse(dto);
		ResponseEntity<ResponseWrapper<PridFetchResponseDto>> res = new ResponseEntity<>(pridRes,
				HttpStatus.OK);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
				Mockito.eq(new ParameterizedTypeReference<ResponseWrapper<PridFetchResponseDto>>() {
				}))).thenReturn(res);
		Mockito.when(serviceUtil.generateId()).thenReturn("98746563542672");
		Mockito.when(demographicRepository.save(Mockito.any())).thenThrow(exception);
		createPreRegistrationDTO = new DemographicRequestDTO();
		createPreRegistrationDTO.setDemographicDetails(jsonObject);
		createPreRegistrationDTO.setLangCode("fra");
		request.setRequest(createPreRegistrationDTO);
		preRegistrationService.addPreRegistration(request);
	}

	@Test
	public void successUpdateTest() throws Exception {
		byte[] encryptedDemographicDetails = jsonTestObject.toJSONString().getBytes();// { 1, 0, 1, 0, 1, 0 };
		requestMap.put("id", updateId);
		request.setId(updateId);
		Mockito.when(serviceUtil.prepareRequestMap(request)).thenReturn(requestMap);
		Mockito.when(cryptoUtil.encrypt(Mockito.any(), Mockito.any())).thenReturn(encryptedDemographicDetails);

		preRegistrationEntity.setApplicantDetailJson(encryptedDemographicDetails);
		preRegistrationEntity.setDemogDetailHash(HashUtill.hashUtill(preRegistrationEntity.getApplicantDetailJson()));
		Mockito.when(jsonValidator.validateIdObject(jsonTestObject.toString(),
				IdObjectValidatorSupportedOperations.NEW_REGISTRATION)).thenReturn(true);
		Mockito.when(demographicRepository.findBypreRegistrationId("98746563542672")).thenReturn(preRegistrationEntity);
		Mockito.when(cryptoUtil.decrypt(Mockito.any(), Mockito.any())).thenReturn(jsonObject.toString().getBytes());
		Mockito.when(demographicRepository.update(Mockito.any())).thenReturn(preRegistrationEntity);
		createPreRegistrationDTO = new DemographicRequestDTO();
		createPreRegistrationDTO.setDemographicDetails(jsonTestObject);
		createPreRegistrationDTO.setLangCode("eng");
		request.setRequest(createPreRegistrationDTO);
		MainResponseDTO<DemographicUpdateResponseDTO> res = preRegistrationService.updatePreRegistration(request, preId,
				userId);
		assertEquals("98746563542672", res.getResponse().getPreRegistrationId());
	}

	/*
	 * //@Test(expected = JsonValidationException.class) public void
	 * updateFailureCheck() throws Exception { HttpRequestException exception = new
	 * HttpRequestException(ErrorCodes.PRG_PAM_APP_007.name(),
	 * ErrorMessages.JSON_PARSING_FAILED.name()); byte[] encryptedDemographicDetails
	 * = { 1, 0, 1, 0, 1, 0 }; requestMap.put("id", updateId);
	 * request.setId(updateId);
	 * Mockito.when(serviceUtil.prepareRequestMap(request)).thenReturn(requestMap);
	 * Mockito.when(cryptoUtil.encrypt(Mockito.any(),
	 * Mockito.any())).thenReturn(encryptedDemographicDetails);
	 * 
	 * preRegistrationEntity.setApplicantDetailJson(encryptedDemographicDetails);
	 * Mockito.when(jsonValidator.validateIdObject(jsonTestObject.toString(),
	 * IdObjectValidatorSupportedOperations.NEW_REGISTRATION)).thenReturn(true);
	 * Mockito.when(cryptoUtil.decrypt(Mockito.any(),
	 * Mockito.any())).thenReturn(jsonObject.toString().getBytes());
	 * Mockito.when(demographicRepository.findBypreRegistrationId("98746563542672"))
	 * .thenReturn(preRegistrationEntity);
	 * 
	 * Mockito.when(demographicRepository.update(Mockito.any())).thenThrow(exception
	 * ); createPreRegistrationDTO = new DemographicRequestDTO();
	 * createPreRegistrationDTO.setDemographicDetails(jsonTestObject);
	 * createPreRegistrationDTO.setLangCode("eng");
	 * request.setRequest(createPreRegistrationDTO);
	 * preRegistrationService.updatePreRegistration(request, preId, userId); }
	 */

	@Test(expected = NullPointerException.class)
	public void createByDateFailureTest() throws Exception {
		InvalidRequestParameterException exception = new InvalidRequestParameterException(
				ErrorCodes.PRG_PAM_APP_012.toString(), ErrorMessages.MISSING_REQUEST_PARAMETER.toString(),
				responseCreateDTO);
		jsonObject = (JSONObject) parser.parse(new FileReader(fileCr));
		Mockito.when(jsonValidator.validateIdObject(jsonObject.toString(),
				IdObjectValidatorSupportedOperations.NEW_REGISTRATION)).thenReturn(true);

		preRegistrationEntity.setCreateDateTime(null);
		preRegistrationEntity.setCreatedBy("");
		preRegistrationEntity.setPreRegistrationId("");
		Mockito.when(demographicRepository.save(preRegistrationEntity)).thenThrow(exception);
		demographicResponseDTO = new DemographicResponseDTO();
		demographicResponseDTO.setDemographicDetails(jsonObject);
		demographicResponseDTO.setPreRegistrationId("");
		demographicResponseDTO.setCreatedBy("9988905444");
		demographicResponseDTO.setCreatedDateTime(serviceUtil.getLocalDateString(times));
		demographicResponseDTO.setStatusCode("Pending_Appointment");
		createPreRegistrationDTO = new DemographicRequestDTO();
		createPreRegistrationDTO.setDemographicDetails(jsonObject);
		createPreRegistrationDTO.setLangCode("fra");
		request.setRequest(createPreRegistrationDTO);
		List<DemographicResponseDTO> listOfCreatePreRegistrationDTO = new ArrayList<>();
		listOfCreatePreRegistrationDTO.add(demographicResponseDTO);
		ResponseWrapper<String> pridRes = new ResponseWrapper<>();
		pridRes.setResponse("98746563542672");
		ResponseEntity<ResponseWrapper<String>> res = new ResponseEntity<>(pridRes,
				HttpStatus.OK);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
				Mockito.eq(new ParameterizedTypeReference<ResponseWrapper<String>>() {
				}))).thenReturn(res);
		Mockito.when(serviceUtil.generateId()).thenReturn("98746563542672");
		responseDTO.setResponse(demographicResponseDTO);
		MainResponseDTO<DemographicCreateResponseDTO> actualRes = preRegistrationService.addPreRegistration(request);
		assertEquals(actualRes.getResponse().getStatusCode(), responseDTO.getResponse().getStatusCode());

	}

	/*
	 * @Test(expected = InvalidRequestParameterException.class) public void
	 * updateByDateFailureTest() throws Exception { InvalidRequestParameterException
	 * exception = new InvalidRequestParameterException(
	 * ErrorCodes.PRG_PAM_APP_012.toString(),
	 * ErrorMessages.MISSING_REQUEST_PARAMETER.toString(), responseDTO); jsonObject
	 * = (JSONObject) parser.parse(new FileReader(fileUp));
	 * Mockito.when(demographicRepository.findBypreRegistrationId(preId)).thenReturn
	 * (preRegistrationEntity);
	 * Mockito.when(jsonValidator.validateIdObject(Mockito.anyString(),
	 * IdObjectValidatorSupportedOperations.NEW_REGISTRATION)).thenThrow(exception);
	 * Mockito.when(demographicRepository.findBypreRegistrationId(preId)).thenReturn
	 * (preRegistrationEntity);
	 * Mockito.when(jsonValidator.validateIdObject(jsonObject.toString(),
	 * IdObjectValidatorSupportedOperations.NEW_REGISTRATION)).thenThrow(exception);
	 * Mockito.when(jsonValidator.validateIdObject(jsonObject.toString(),
	 * IdObjectValidatorSupportedOperations.NEW_REGISTRATION)).thenThrow(exception);
	 * MainResponseDTO<DemographicUpdateResponseDTO> res =
	 * preRegistrationService.updatePreRegistration(request, "", userId);
	 * assertEquals("1.0", res.getVersion()); }
	 */

	@Test(expected = PreIdInvalidForUserIdException.class)
	public void invalidUserTest() throws FileNotFoundException, IOException, org.json.simple.parser.ParseException,
			IdObjectIOException, IdObjectValidationFailedException {
		byte[] encryptedDemographicDetails = jsonTestObject.toJSONString().getBytes();// { 1, 0, 1, 0, 1, 0 };
		requestMap.put("id", updateId);
		request.setId(updateId);
		Mockito.when(serviceUtil.prepareRequestMap(request)).thenReturn(requestMap);
		Mockito.when(cryptoUtil.encrypt(Mockito.any(), Mockito.any())).thenReturn(encryptedDemographicDetails);

		preRegistrationEntity.setApplicantDetailJson(encryptedDemographicDetails);
		preRegistrationEntity.setDemogDetailHash(HashUtill.hashUtill(preRegistrationEntity.getApplicantDetailJson()));
		Mockito.when(jsonValidator.validateIdObject(jsonTestObject.toString(),
				IdObjectValidatorSupportedOperations.NEW_REGISTRATION)).thenReturn(true);
		Mockito.when(demographicRepository.findBypreRegistrationId("98746563542672")).thenReturn(preRegistrationEntity);
		Mockito.when(cryptoUtil.decrypt(Mockito.any(), Mockito.any())).thenReturn(jsonObject.toString().getBytes());
		Mockito.when(demographicRepository.update(Mockito.any())).thenReturn(preRegistrationEntity);
		createPreRegistrationDTO = new DemographicRequestDTO();
		createPreRegistrationDTO.setDemographicDetails(jsonTestObject);
		createPreRegistrationDTO.setLangCode("eng");
		request.setRequest(createPreRegistrationDTO);
		MainResponseDTO<DemographicUpdateResponseDTO> res = preRegistrationService.updatePreRegistration(request, preId,
				"1234");

		assertEquals("1.0", res.getVersion());
	}

	@Test(expected = DemographicServiceException.class)
	public void httpServerErrorTestForUpdate() throws FileNotFoundException, IOException,
			org.json.simple.parser.ParseException, IdObjectIOException, IdObjectValidationFailedException {
		HttpServerErrorException errorException = new HttpServerErrorException(HttpStatus.BAD_GATEWAY, "httpError");
		byte[] encryptedDemographicDetails = jsonTestObject.toJSONString().getBytes();// { 1, 0, 1, 0, 1, 0 };
		requestMap.put("id", updateId);
		request.setId(updateId);
		Mockito.when(serviceUtil.prepareRequestMap(request)).thenReturn(requestMap);
		Mockito.when(cryptoUtil.encrypt(Mockito.any(), Mockito.any())).thenReturn(encryptedDemographicDetails);

		preRegistrationEntity.setApplicantDetailJson(encryptedDemographicDetails);
		preRegistrationEntity.setDemogDetailHash(HashUtill.hashUtill(preRegistrationEntity.getApplicantDetailJson()));
		Mockito.when(jsonValidator.validateIdObject(jsonTestObject.toString(),
				IdObjectValidatorSupportedOperations.NEW_REGISTRATION)).thenReturn(true);
		Mockito.when(demographicRepository.findBypreRegistrationId(Mockito.anyString())).thenThrow(errorException);
		Mockito.when(cryptoUtil.decrypt(Mockito.any(), Mockito.any())).thenReturn(jsonObject.toString().getBytes());
		Mockito.when(demographicRepository.update(Mockito.any())).thenReturn(preRegistrationEntity);
		createPreRegistrationDTO = new DemographicRequestDTO();
		createPreRegistrationDTO.setDemographicDetails(jsonTestObject);
		createPreRegistrationDTO.setLangCode("eng");
		request.setRequest(createPreRegistrationDTO);
		MainResponseDTO<DemographicUpdateResponseDTO> res = preRegistrationService.updatePreRegistration(request, preId,
				"1234");

		assertEquals("1.0", res.getVersion());
	}

	@Test
	public void getApplicationDetailsTest() throws ParseException {
		byte[] encryptedDemographicDetails = jsonTestObject.toJSONString().getBytes();// { 1, 0, 1, 0, 1, 0 };

		Mockito.when(cryptoUtil.encrypt(Mockito.any(), Mockito.any())).thenReturn(encryptedDemographicDetails);

		preRegistrationEntity.setApplicantDetailJson(encryptedDemographicDetails);
		MainResponseDTO<DemographicMetadataDTO> response = new MainResponseDTO<>();
		List<DemographicViewDTO> viewList = new ArrayList<>();
		DemographicViewDTO viewDto = new DemographicViewDTO();

		viewDto = new DemographicViewDTO();
		viewDto.setPreRegistrationId("98746563542672");
		viewDto.setStatusCode(preRegistrationEntity.getStatusCode());
		viewDto.setBookingMetadata(bookingRegistrationDTO);
		viewList.add(viewDto);
		DemographicMetadataDTO demographicMetadataDTO = new DemographicMetadataDTO();
		demographicMetadataDTO.setBasicDetails(viewList);
		response.setVersion("1.0");
		response.setResponse(demographicMetadataDTO);
		MainResponseDTO<BookingRegistrationDTO> bookingResultDto = new MainResponseDTO<>();
		BookingRegistrationDTO bookingResponse = new BookingRegistrationDTO();
		bookingResponse.setRegDate(LocalDate.now().toString());
		bookingResponse.setRegistrationCenterId("1");
		bookingResponse.setSlotFromTime("9:00:00");
		bookingResponse.setSlotToTime("10:00:00");
		bookingResultDto.setResponse(bookingResponse);

		Page<DemographicEntity> page = new PageImpl<>(userEntityDetails);
		ResponseEntity<MainResponseDTO<BookingRegistrationDTO>> resBook = new ResponseEntity<>(bookingResultDto,
				HttpStatus.OK);
		ResponseEntity<MainResponseDTO<DocumentMultipartResponseDTO>> resDoc = new ResponseEntity<>(documentResultDto,
				HttpStatus.OK);
		Mockito.when(cryptoUtil.decrypt(Mockito.any(), Mockito.any()))
				.thenReturn(userEntityDetails.get(0).getApplicantDetailJson());
		Mockito.when(demographicRepository.findByCreatedBy(userId, "Consumed")).thenReturn(userEntityDetails);
		Mockito.when(demographicRepository.findByCreatedByOrderByCreateDateTime(userId, StatusCodes.CONSUMED.getCode(),
				null)).thenReturn(page);
		Mockito.when(restTemplate.getForObject(Mockito.anyString(), Mockito.eq(String.class)))
				.thenReturn(identityMappingJson);
		MainResponseDTO<String> pridRes = new MainResponseDTO<>();
		pridRes.setResponse("98746563542672");
		ResponseEntity<MainResponseDTO<String>> res = new ResponseEntity<>(pridRes,
				HttpStatus.OK);
		Mockito.when(serviceUtil.getJson(Mockito.any())).thenReturn(identityMappingJson);
		Mockito.when(serviceUtil.generateId()).thenReturn("98746563542672");
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
				Mockito.eq(new ParameterizedTypeReference<MainResponseDTO<String>>() {
				}))).thenReturn(res);

		MainResponseDTO<DemographicMetadataDTO> actualRes = preRegistrationService.getAllApplicationDetails(userId, "");
		assertEquals(actualRes.getVersion(), response.getVersion());

	}

	@Test
	public void getApplicationDetailsWithoutDocTest() throws ParseException {
		byte[] encryptedDemographicDetails = jsonTestObject.toJSONString().getBytes();// { 1, 0, 1, 0, 1, 0 };

		Mockito.when(cryptoUtil.encrypt(Mockito.any(), Mockito.any())).thenReturn(encryptedDemographicDetails);

		preRegistrationEntity.setApplicantDetailJson(encryptedDemographicDetails);
		MainResponseDTO<DemographicMetadataDTO> response = new MainResponseDTO<>();
		List<DemographicViewDTO> viewList = new ArrayList<>();
		DemographicViewDTO viewDto = new DemographicViewDTO();

		viewDto = new DemographicViewDTO();
		viewDto.setPreRegistrationId("98746563542672");
		viewDto.setStatusCode(preRegistrationEntity.getStatusCode());
		viewDto.setBookingMetadata(bookingRegistrationDTO);
		viewList.add(viewDto);
		DemographicMetadataDTO demographicMetadataDTO = new DemographicMetadataDTO();
		demographicMetadataDTO.setBasicDetails(viewList);
		response.setVersion("1.0");
		response.setResponse(demographicMetadataDTO);
		MainResponseDTO<BookingRegistrationDTO> bookingResultDto = new MainResponseDTO<>();
		BookingRegistrationDTO bookingResponse = new BookingRegistrationDTO();
		bookingResponse.setRegDate(LocalDate.now().toString());
		bookingResponse.setRegistrationCenterId("1");
		bookingResponse.setSlotFromTime("9:00:00");
		bookingResponse.setSlotToTime("10:00:00");
		bookingResultDto.setResponse(bookingResponse);
		ExceptionJSONInfoDTO dto = new ExceptionJSONInfoDTO();
		List<ExceptionJSONInfoDTO> errors = new ArrayList<>();
		errors.add(dto);
		documentResultDto.setResponse(null);
		documentResultDto.setErrors(errors);
		Page<DemographicEntity> page = new PageImpl<>(userEntityDetails);
		ResponseEntity<MainResponseDTO<BookingRegistrationDTO>> resBook = new ResponseEntity<>(bookingResultDto,
				HttpStatus.OK);
		ResponseEntity<MainResponseDTO<DocumentMultipartResponseDTO>> resDoc = new ResponseEntity<>(documentResultDto,
				HttpStatus.OK);
		Mockito.when(cryptoUtil.decrypt(Mockito.any(), Mockito.any()))
				.thenReturn(userEntityDetails.get(0).getApplicantDetailJson());
		Mockito.when(demographicRepository.findByCreatedBy(userId, "Consumed")).thenReturn(userEntityDetails);
		Mockito.when(demographicRepository.findByCreatedByOrderByCreateDateTime(userId, StatusCodes.CONSUMED.getCode(),
				null)).thenReturn(page);
		Mockito.when(restTemplate.getForObject(Mockito.anyString(), Mockito.eq(String.class)))
				.thenReturn(identityMappingJson);
		Mockito.when(serviceUtil.getJson(Mockito.any())).thenReturn(identityMappingJson);
		// Mockito.when(serviceUtil.getPreregistrationIdentityJson()).thenReturn(demographicIdentityRequestDTO);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
				Mockito.eq(new ParameterizedTypeReference<MainResponseDTO<DocumentMultipartResponseDTO>>() {
				}), Mockito.anyMap())).thenReturn(resDoc);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
				Mockito.eq(new ParameterizedTypeReference<MainResponseDTO<BookingRegistrationDTO>>() {
				}), Mockito.anyMap())).thenReturn(resBook);

		MainResponseDTO<DemographicMetadataDTO> actualRes = preRegistrationService.getAllApplicationDetails(userId, "");
		assertEquals(actualRes.getVersion(), response.getVersion());

	}

	@Test
	public void getApplicationDetailsWithPageTest() throws ParseException {
		byte[] encryptedDemographicDetails = jsonTestObject.toJSONString().getBytes();// { 1, 0, 1, 0, 1, 0 };

		Mockito.when(cryptoUtil.encrypt(Mockito.any(), Mockito.any())).thenReturn(encryptedDemographicDetails);

		preRegistrationEntity.setApplicantDetailJson(encryptedDemographicDetails);
		String userId = "9988905444";
		MainResponseDTO<DemographicMetadataDTO> response = new MainResponseDTO<>();
		List<DemographicViewDTO> viewList = new ArrayList<>();
		DemographicViewDTO viewDto = new DemographicViewDTO();

		viewDto = new DemographicViewDTO();
		viewDto.setPreRegistrationId("98746563542672");
		viewDto.setStatusCode(preRegistrationEntity.getStatusCode());
		viewDto.setBookingMetadata(bookingRegistrationDTO);
		viewList.add(viewDto);
		DemographicMetadataDTO demographicMetadataDTO = new DemographicMetadataDTO();
		demographicMetadataDTO.setBasicDetails(viewList);
		response.setVersion("1.0");
		response.setResponse(demographicMetadataDTO);
		// response.setStatus(Boolean.FALSE);
		MainResponseDTO<BookingRegistrationDTO> bookingResultDto = new MainResponseDTO<>();
		BookingRegistrationDTO bookingResponse = new BookingRegistrationDTO();
		bookingResponse.setRegDate(LocalDate.now().toString());
		bookingResponse.setRegistrationCenterId("1");
		bookingResponse.setSlotFromTime("9:00:00");
		bookingResponse.setSlotToTime("10:00:00");
		bookingResultDto.setResponse(bookingResponse);
		Page<DemographicEntity> page = new PageImpl<>(userEntityDetails);
		ResponseEntity<MainResponseDTO<BookingRegistrationDTO>> resBook = new ResponseEntity<>(bookingResultDto,
				HttpStatus.OK);
		ResponseEntity<MainResponseDTO<DocumentMultipartResponseDTO>> resDoc = new ResponseEntity<>(documentResultDto,
				HttpStatus.OK);
		Mockito.when(cryptoUtil.decrypt(Mockito.any(), Mockito.any()))
				.thenReturn(userEntityDetails.get(0).getApplicantDetailJson());
		Mockito.when(demographicRepository.findByCreatedBy(userId, "Consumed")).thenReturn(userEntityDetails);
		Mockito.when(demographicRepository.findByCreatedByOrderByCreateDateTime(userId, StatusCodes.CONSUMED.getCode(),
				PageRequest.of(Integer.parseInt("1"), Integer.parseInt(pageSize)))).thenReturn(page);
		Mockito.when(restTemplate.getForObject(Mockito.anyString(), Mockito.eq(String.class)))
				.thenReturn(identityMappingJson);
		Mockito.when(serviceUtil.getJson(Mockito.any())).thenReturn(identityMappingJson);
		// Mockito.when(getPreregistrationIdentityJson()).thenReturn(demographicIdentityRequestDTO);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
				Mockito.eq(new ParameterizedTypeReference<MainResponseDTO<DocumentMultipartResponseDTO>>() {
				}), Mockito.anyMap())).thenReturn(resDoc);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
				Mockito.eq(new ParameterizedTypeReference<MainResponseDTO<BookingRegistrationDTO>>() {
				}), Mockito.anyMap())).thenReturn(resBook);
		MainResponseDTO<DemographicMetadataDTO> actualRes = preRegistrationService.getAllApplicationDetails(userId,
				"1");
		assertEquals(actualRes.getVersion(), response.getVersion());

	}

	@Test
	public void callGetAppointmentDetailsRestServiceTest1()
			throws ParseException, org.json.simple.parser.ParseException {
		byte[] encryptedDemographicDetails = jsonTestObject.toJSONString().getBytes();

		Mockito.when(cryptoUtil.encrypt(Mockito.any(), Mockito.any())).thenReturn(encryptedDemographicDetails);

		preRegistrationEntity.setApplicantDetailJson(encryptedDemographicDetails);
		String userId = "9988905444";
		MainResponseDTO<DemographicMetadataDTO> response = new MainResponseDTO<>();
		List<DemographicViewDTO> viewList = new ArrayList<>();
		DemographicViewDTO viewDto = new DemographicViewDTO();

		viewDto = new DemographicViewDTO();
		viewDto.setPreRegistrationId("98746563542672");
		viewDto.setStatusCode(preRegistrationEntity.getStatusCode());
		viewDto.setBookingMetadata(bookingRegistrationDTO);

		viewList.add(viewDto);

		DemographicMetadataDTO demographicMetadataDTO = new DemographicMetadataDTO();
		demographicMetadataDTO.setBasicDetails(viewList);
		response.setResponse(demographicMetadataDTO);
		response.setVersion("1.0");
		Page<DemographicEntity> page = new PageImpl<>(userEntityDetails);
		Mockito.when(cryptoUtil.decrypt(Mockito.any(), Mockito.any()))
				.thenReturn(userEntityDetails.get(0).getApplicantDetailJson());
		Mockito.when(demographicRepository.findByCreatedBy(userId, "Consumed")).thenReturn(userEntityDetails);
		Mockito.when(demographicRepository.findByCreatedByOrderByCreateDateTime(userId, StatusCodes.CONSUMED.getCode(),
				PageRequest.of(Integer.parseInt("0"), Integer.parseInt(pageSize)))).thenReturn(page);
		MainResponseDTO<BookingRegistrationDTO> dto = new MainResponseDTO<>();
		dto.setErrors(null);
		ResponseEntity<MainResponseDTO<BookingRegistrationDTO>> respEntity = new ResponseEntity<>(dto, HttpStatus.OK);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
				Mockito.eq(new ParameterizedTypeReference<MainResponseDTO<BookingRegistrationDTO>>() {
				}))).thenReturn(respEntity);
		MainResponseDTO<DemographicMetadataDTO> actualRes = preRegistrationService.getAllApplicationDetails(userId,
				"0");
		assertEquals(actualRes.getVersion(), response.getVersion());

	}

	@Test
	public void callGetAppointmentDetailsRestServiceTest() throws ParseException {
		byte[] encryptedDemographicDetails = jsonTestObject.toJSONString().getBytes();// { 1, 0, 1, 0, 1, 0 };

		Mockito.when(cryptoUtil.encrypt(Mockito.any(), Mockito.any())).thenReturn(encryptedDemographicDetails);

		preRegistrationEntity.setApplicantDetailJson(encryptedDemographicDetails);
		String userId = "9988905444";
		MainResponseDTO<DemographicMetadataDTO> response = new MainResponseDTO<>();
		List<DemographicViewDTO> viewList = new ArrayList<>();
		DemographicViewDTO viewDto = new DemographicViewDTO();

		viewDto = new DemographicViewDTO();
		viewDto.setPreRegistrationId("98746563542672");
		viewDto.setStatusCode(preRegistrationEntity.getStatusCode());
		viewDto.setBookingMetadata(bookingRegistrationDTO);
		response.setVersion("1.0");
		viewList.add(viewDto);
		DemographicMetadataDTO demographicMetadataDTO = new DemographicMetadataDTO();
		demographicMetadataDTO.setBasicDetails(viewList);
		response.setResponse(demographicMetadataDTO);
		MainResponseDTO<BookingRegistrationDTO> bookingResultDto = new MainResponseDTO<>();
		BookingRegistrationDTO bookingResponse = new BookingRegistrationDTO();
		bookingResponse.setRegDate("12/01/2018");
		bookingResponse.setRegistrationCenterId("1");
		bookingResponse.setSlotFromTime("9:00:00");
		bookingResponse.setSlotToTime("10:00:00");
		bookingResultDto.setResponse(bookingResponse);
		Page<DemographicEntity> page = new PageImpl<>(userEntityDetails);
		ResponseEntity<MainResponseDTO> res = new ResponseEntity<>(bookingResultDto, HttpStatus.OK);
		Mockito.when(cryptoUtil.decrypt(Mockito.any(), Mockito.any()))
				.thenReturn(userEntityDetails.get(0).getApplicantDetailJson());
		Mockito.when(demographicRepository.findByCreatedBy(userId, "Consumed")).thenReturn(userEntityDetails);
		Mockito.when(demographicRepository.findByCreatedByOrderByCreateDateTime(userId, StatusCodes.CONSUMED.getCode(),
				PageRequest.of(Integer.parseInt("1"), Integer.parseInt(pageSize)))).thenReturn(page);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
				Mockito.eq(MainResponseDTO.class))).thenReturn(res);
		MainResponseDTO<DemographicMetadataDTO> actualRes = preRegistrationService.getAllApplicationDetails(userId,
				null);
		assertEquals(actualRes.getVersion(), response.getVersion());

	}

	@Test(expected = RecordNotFoundException.class)
	public void getApplicationDetailsFailureTest() {
		String userId = "12345";
		userEntityDetails = null;
		Mockito.when(demographicRepository.findByCreatedBy(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(userEntityDetails);
		preRegistrationService.getAllApplicationDetails(userId, null);

	}

	// @Test(expected = RecordNotFoundException.class)
	// public void getApplicationDetailsWithPageFailureTest() {
	// String userId = "12345";
	// userEntityDetails = new ArrayList<>();
	// preRegistrationEntity.setCreateDateTime(times);
	// preRegistrationEntity.setCreatedBy("9988905444");
	// preRegistrationEntity.setStatusCode("Pending_Appointment");
	// preRegistrationEntity.setUpdateDateTime(times);
	//
	// preRegistrationEntity.setPreRegistrationId("98746563542672");
	// preRegistrationEntity
	// .setDemogDetailHash(HashUtill.hashUtill(jsonTestObject.toJSONString().getBytes()).toString());
	// preRegistrationEntity.setStatusCode(StatusCodes.CONSUMED.getCode());
	// userEntityDetails.add(preRegistrationEntity);
	// Mockito.when(demographicRepository.findByCreatedBy(Mockito.anyString(),
	// Mockito.anyString()))
	// .thenReturn(userEntityDetails);
	// Mockito.when(demographicRepository.findByCreatedByOrderByCreateDateTime(Mockito.anyString(),
	// Mockito.anyString(), pageable));
	// preRegistrationService.getAllApplicationDetails(userId, "0");
	//
	// }

	@Test(expected = SystemIllegalArgumentException.class)
	public void getApplicationDetailsIndexTest() {
		String userId = "12345";
		Mockito.when(demographicRepository.findByCreatedBy(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(userEntityDetails);
		preRegistrationService.getAllApplicationDetails(userId, "abc");

	}

	@Test(expected = InvalidRequestParameterException.class)
	public void getApplicationDetailsInvalidRequestTest() {
		InvalidRequestParameterException exception = new InvalidRequestParameterException(
				ErrorCodes.PRG_PAM_APP_012.name(), ErrorMessages.MISSING_REQUEST_PARAMETER.name(), responseCreateDTO);
		Mockito.when(demographicRepository.findByCreatedBy("", "")).thenThrow(exception);
		preRegistrationService.getAllApplicationDetails("", "0");
	}

	@Test(expected = RecordNotFoundException.class)
	public void getApplicationDetailsInvalidPageRequestTest() {
		Mockito.when(demographicRepository.findByCreatedBy(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(userEntityDetails);
		Mockito.when(demographicRepository.findByCreatedByOrderByCreateDateTime("9886442073", "Consumed",
				PageRequest.of(5, 6))).thenReturn(null);
		preRegistrationService.getAllApplicationDetails("9886442073", "5");
	}

	@Test
	public void getApplicationStatusTest() {
		String preId = "98746563542672";
		byte[] encryptedDemographicDetails = jsonTestObject.toJSONString().getBytes();// { 1, 0, 1, 0, 1, 0 };

		// Mockito.when(cryptoUtil.encrypt(Mockito.any(),Mockito.any())).thenReturn(encryptedDemographicDetails);

		preRegistrationEntity.setApplicantDetailJson(encryptedDemographicDetails);
		preRegistrationEntity.setDemogDetailHash(HashUtill.hashUtill(preRegistrationEntity.getApplicantDetailJson()));
		MainResponseDTO<PreRegistartionStatusDTO> response = new MainResponseDTO<>();
		List<PreRegistartionStatusDTO> statusList = new ArrayList<PreRegistartionStatusDTO>();
		PreRegistartionStatusDTO statusDto = new PreRegistartionStatusDTO();
		statusDto.setPreRegistartionId(preId);
		statusDto.setStatusCode("Pending_Appointment");
		// statusList.add(statusDto);
		response.setResponse(statusDto);

		Mockito.when(demographicRepository.findBypreRegistrationId(ArgumentMatchers.any()))
				.thenReturn(preRegistrationEntity);

		MainResponseDTO<PreRegistartionStatusDTO> actualRes = preRegistrationService.getApplicationStatus(preId,
				userId);
		assertEquals(response.getResponse().getStatusCode(), actualRes.getResponse().getStatusCode());

	}

	@Test(expected = HashingException.class)
	public void getApplicationStatusHashingExceptionTest() {
		String preId = "98746563542672";
		byte[] encryptedDemographicDetails = { 1, 0, 1, 0, 1, 0 };

		// Mockito.when(cryptoUtil.encrypt(Mockito.any(),Mockito.any())).thenReturn(encryptedDemographicDetails);

		preRegistrationEntity.setApplicantDetailJson(encryptedDemographicDetails);
		// preRegistrationEntity.setDemogDetailHash(new
		// String(HashUtill.hashUtill(preRegistrationEntity.getApplicantDetailJson())));
		MainResponseDTO<PreRegistartionStatusDTO> response = new MainResponseDTO<>();
		List<PreRegistartionStatusDTO> statusList = new ArrayList<PreRegistartionStatusDTO>();
		PreRegistartionStatusDTO statusDto = new PreRegistartionStatusDTO();
		statusDto.setPreRegistartionId(preId);
		statusDto.setStatusCode("Pending_Appointment");
		// statusList.add(statusDto);
		response.setResponse(statusDto);

		Mockito.when(demographicRepository.findBypreRegistrationId(ArgumentMatchers.any()))
				.thenReturn(preRegistrationEntity);

		MainResponseDTO<PreRegistartionStatusDTO> actualRes = preRegistrationService.getApplicationStatus(preId,
				userId);
		assertEquals(response.getResponse().getStatusCode(), actualRes.getResponse().getStatusCode());

	}

	@Test(expected = RecordNotFoundException.class)
	public void getApplicationStatusFailure() {
		String preId = "98746563542672";
		MainResponseDTO<PreRegistartionStatusDTO> response = new MainResponseDTO<>();
		List<PreRegistartionStatusDTO> statusList = new ArrayList<PreRegistartionStatusDTO>();
		PreRegistartionStatusDTO statusDto = new PreRegistartionStatusDTO();
		statusDto.setPreRegistartionId(preId);
		statusDto.setStatusCode("Pending_Appointment");
		// statusList.add(statusDto);
		response.setResponse(statusDto);

		Mockito.when(demographicRepository.findBypreRegistrationId(ArgumentMatchers.any())).thenReturn(null);

		MainResponseDTO<PreRegistartionStatusDTO> actualRes = preRegistrationService.getApplicationStatus(preId,
				userId);
		assertEquals(response.getResponse().getStatusCode(), actualRes.getResponse().getStatusCode());

	}

	@Test(expected = TableNotAccessibleException.class)
	public void getApplicationDetailsTransactionFailureCheck() throws Exception {
		String userId = "9988905444";
		DataAccessLayerException exception = new DataAccessLayerException(ErrorCodes.PRG_PAM_APP_002.toString(),
				ErrorMessages.PRE_REGISTRATION_TABLE_NOT_ACCESSIBLE.toString(), null);
		Mockito.when(demographicRepository.findByCreatedBy(Mockito.anyString(), Mockito.anyString()))
				.thenThrow(exception);
		preRegistrationService.getAllApplicationDetails(userId, "0");
	}

	@Test(expected = RecordNotFoundException.class)
	public void deleteRecordNotFoundTest() {
		RecordNotFoundException exception = new RecordNotFoundException(ErrorCodes.PRG_PAM_APP_005.name(),
				ErrorMessages.UNABLE_TO_FETCH_THE_PRE_REGISTRATION.name());
		String preRegId = "98746563542672";

		DocumentDeleteDTO deleteDTO = new DocumentDeleteDTO();
		deleteDTO.setDocument_Id(String.valueOf("1"));
		List<DocumentDeleteDTO> deleteAllList = new ArrayList<>();
		deleteAllList.add(deleteDTO);

		MainResponseDTO<DocumentDeleteDTO> delResponseDto = new MainResponseDTO<>();
		// delResponseDto.setStatus(Boolean.TRUE);
		delResponseDto.setErrors(null);
		delResponseDto.setResponse(deleteDTO);
		delResponseDto.setResponsetime(serviceUtil.getCurrentResponseTime());

		ResponseEntity<MainResponseDTO> res = new ResponseEntity<>(delResponseDto, HttpStatus.OK);
		Mockito.when(demographicRepository.findBypreRegistrationId(preRegId)).thenReturn(null);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.DELETE), Mockito.any(),
				Mockito.eq(MainResponseDTO.class))).thenThrow(exception);
		preRegistrationService.deleteIndividual(preRegId, userId);

	}

	@Test(expected = RecordFailedToDeleteException.class)
	public void deleteRecordFailedToDeleteException() throws Exception {
		String preRegId = "98746563542672";
		ExceptionJSONInfoDTO err = new ExceptionJSONInfoDTO("PRG_PAM_APP_004",
				ErrorMessages.FAILED_TO_DELETE_THE_PRE_REGISTRATION_RECORD.name());
		Mockito.when(demographicRepository.findBypreRegistrationId(preRegId)).thenReturn(preRegistrationEntity);
		DocumentDeleteResponseDTO deleteDTO = new DocumentDeleteResponseDTO();
		List<DocumentDeleteResponseDTO> deleteAllList = new ArrayList<>();
		deleteAllList.add(deleteDTO);
		MainResponseDTO<DeleteBookingDTO> delBookingResponseDTO = new MainResponseDTO<>();
		DeleteBookingDTO deleteBookingDTO = new DeleteBookingDTO();
		deleteBookingDTO.setPreRegistrationId("98746563542672");
		List<DeleteBookingDTO> list = new ArrayList<>();
		list.add(deleteBookingDTO);
		delBookingResponseDTO.setResponse(deleteBookingDTO);
		MainResponseDTO<DocumentDeleteResponseDTO> delResponseDto = new MainResponseDTO<>();
		List<ExceptionJSONInfoDTO> exceptionJSONInfoDTOs = new ArrayList<>();
		exceptionJSONInfoDTOs.add(err);

		delResponseDto.setErrors(null);
		delResponseDto.setResponse(deleteDTO);
		delResponseDto.setResponsetime(serviceUtil.getCurrentResponseTime());

		ResponseEntity<MainResponseDTO<DocumentDeleteResponseDTO>> res = new ResponseEntity<>(delResponseDto,
				HttpStatus.OK);
		ResponseEntity<MainResponseDTO<DeleteBookingDTO>> res1 = new ResponseEntity<>(delBookingResponseDTO,
				HttpStatus.OK);
		HttpHeaders headers = new HttpHeaders();
		MainResponseDTO<DocumentDeleteResponseDTO> deleteAllByPreId1 = new MainResponseDTO<>();
		deleteAllByPreId1.setErrors(fullname);
		Mockito.when(documentServiceIntf.deleteAllByPreId(Mockito.anyString())).thenReturn(deleteAllByPreId1);
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.DELETE), Mockito.any(),
				Mockito.eq(new ParameterizedTypeReference<MainResponseDTO<DocumentDeleteResponseDTO>>() {
				}), Mockito.anyMap())).thenReturn(res);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.DELETE), Mockito.any(),
				Mockito.eq(new ParameterizedTypeReference<MainResponseDTO<DeleteBookingDTO>>() {
				}), Mockito.anyMap())).thenReturn(res1);
		Mockito.when(demographicRepository.deleteByPreRegistrationId(preRegistrationEntity.getPreRegistrationId()))
				.thenReturn(0);
		preRegistrationService.deleteIndividual(preRegId, userId);
	}

	@Test(expected = BookingDeletionFailedException.class)
	public void deleteRecordRestCallException() throws Exception {
		String preRegId = "98746563542672";
		ExceptionJSONInfoDTO err = new ExceptionJSONInfoDTO("PRG_PAM_DOC_015", "");
		List<ExceptionJSONInfoDTO> errlist = new ArrayList<>();
		errlist.add(err);
		Mockito.when(demographicRepository.findBypreRegistrationId(preRegId)).thenReturn(preRegistrationEntity);
		preRegistrationEntity.setCreateDateTime(times);
		preRegistrationEntity.setCreatedBy("9988905444");
		preRegistrationEntity.setStatusCode("Booked");
		preRegistrationEntity.setUpdateDateTime(times);
		preRegistrationEntity.setApplicantDetailJson(jsonTestObject.toJSONString().getBytes());
		preRegistrationEntity.setPreRegistrationId("98746563542672");

		DocumentDeleteResponseDTO deleteDTO = new DocumentDeleteResponseDTO();
		List<DocumentDeleteResponseDTO> deleteAllList = new ArrayList<>();
		deleteAllList.add(deleteDTO);
		MainResponseDTO<DeleteBookingDTO> delBookingResponseDTO = new MainResponseDTO<>();
		DeleteBookingDTO deleteBookingDTO = new DeleteBookingDTO();
		deleteBookingDTO.setPreRegistrationId("98746563542672");
		List<DeleteBookingDTO> list = new ArrayList<>();
		list.add(deleteBookingDTO);
		delBookingResponseDTO.setResponse(deleteBookingDTO);
		delBookingResponseDTO.setErrors(errlist);
		MainResponseDTO<DocumentDeleteResponseDTO> delResponseDto = new MainResponseDTO<>();

		delResponseDto.setErrors(null);
		delResponseDto.setResponse(deleteDTO);
		delResponseDto.setResponsetime(serviceUtil.getCurrentResponseTime());

		ResponseEntity<MainResponseDTO<DocumentDeleteResponseDTO>> res = new ResponseEntity<>(delResponseDto,
				HttpStatus.OK);
		ResponseEntity<MainResponseDTO<DeleteBookingDTO>> res1 = new ResponseEntity<>(delBookingResponseDTO,
				HttpStatus.OK);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		MainResponseDTO<DocumentDeleteResponseDTO> deleteAllByPreId1 = new MainResponseDTO<>();
		deleteAllByPreId1.setErrors(null);
		Mockito.when(documentServiceIntf.deleteAllByPreId(Mockito.anyString())).thenReturn(deleteAllByPreId1);
		MainResponseDTO<DeleteBookingDTO> deleteBooking = new MainResponseDTO<>();
		deleteBooking.setErrors(errlist);
		Mockito.when(bookingServiceIntf.deleteBooking(preRegistrationEntity.getPreRegistrationId()))
				.thenReturn(deleteBooking);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.DELETE), Mockito.any(),
				Mockito.eq(new ParameterizedTypeReference<MainResponseDTO<DocumentDeleteResponseDTO>>() {
				}), Mockito.anyMap())).thenReturn(res);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.DELETE), Mockito.any(),
				Mockito.eq(new ParameterizedTypeReference<MainResponseDTO<DeleteBookingDTO>>() {
				}))).thenReturn(res1);
		Mockito.when(demographicRepository.deleteByPreRegistrationId(preRegistrationEntity.getPreRegistrationId()))
				.thenReturn(1);

		Mockito.when(demographicRepository.deleteByPreRegistrationId(preRegId)).thenReturn(0);
		preRegistrationService.deleteIndividual(preRegId, userId);

	}

	@Test
	public void deleteIndividualSuccessTest() {
		String preRegId = "98746563542672";
		preRegistrationEntity.setCreateDateTime(times);
		preRegistrationEntity.setCreatedBy("9988905444");
		preRegistrationEntity.setStatusCode("Booked");
		preRegistrationEntity.setUpdateDateTime(times);
		preRegistrationEntity.setApplicantDetailJson(jsonTestObject.toJSONString().getBytes());
		preRegistrationEntity.setPreRegistrationId("98746563542672");

		DocumentDeleteResponseDTO deleteDTO = new DocumentDeleteResponseDTO();
		List<DocumentDeleteResponseDTO> deleteAllList = new ArrayList<>();
		deleteAllList.add(deleteDTO);
		MainResponseDTO<DeleteBookingDTO> delBookingResponseDTO = new MainResponseDTO<>();
		DeleteBookingDTO deleteBookingDTO = new DeleteBookingDTO();
		deleteBookingDTO.setPreRegistrationId("98746563542672");
		List<DeleteBookingDTO> list = new ArrayList<>();
		list.add(deleteBookingDTO);
		delBookingResponseDTO.setResponse(deleteBookingDTO);
		MainResponseDTO<DocumentDeleteResponseDTO> delResponseDto = new MainResponseDTO<>();
		// delResponseDto.setStatus(Boolean.TRUE);
		delResponseDto.setErrors(null);
		delResponseDto.setResponse(deleteDTO);
		delResponseDto.setResponsetime(serviceUtil.getCurrentResponseTime());

		Mockito.when(demographicRepository.findBypreRegistrationId(preRegId)).thenReturn(preRegistrationEntity);

		ResponseEntity<MainResponseDTO<DocumentDeleteResponseDTO>> res = new ResponseEntity<>(delResponseDto,
				HttpStatus.OK);
		ResponseEntity<MainResponseDTO<DeleteBookingDTO>> res1 = new ResponseEntity<>(delBookingResponseDTO,
				HttpStatus.OK);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.DELETE), Mockito.any(),
				Mockito.eq(new ParameterizedTypeReference<MainResponseDTO<DocumentDeleteResponseDTO>>() {
				}))).thenReturn(res);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.DELETE), Mockito.any(),
				Mockito.eq(new ParameterizedTypeReference<MainResponseDTO<DeleteBookingDTO>>() {
				}))).thenReturn(res1);
		Mockito.when(demographicRepository.deleteByPreRegistrationId(preRegistrationEntity.getPreRegistrationId()))
				.thenReturn(1);

		MainResponseDTO<DeletePreRegistartionDTO> actualres = preRegistrationService.deleteIndividual(preRegId, userId);

		assertEquals("1.0", actualres.getVersion());

	}

	@Test
	public void deleteIndividualSuccessTest2() {
		String preRegId = "98746563542672";
		preRegistrationEntity.setCreateDateTime(times);
		preRegistrationEntity.setCreatedBy("9988905444");
		preRegistrationEntity.setStatusCode(StatusCodes.PENDING_APPOINTMENT.getCode());
		preRegistrationEntity.setUpdateDateTime(times);
		preRegistrationEntity.setApplicantDetailJson(jsonTestObject.toJSONString().getBytes());
		preRegistrationEntity.setPreRegistrationId("98746563542672");

		DocumentDeleteResponseDTO deleteDTO = new DocumentDeleteResponseDTO();
		List<DocumentDeleteResponseDTO> deleteAllList = new ArrayList<>();
		deleteAllList.add(deleteDTO);
		MainResponseDTO<DeleteBookingDTO> delBookingResponseDTO = new MainResponseDTO<>();
		DeleteBookingDTO deleteBookingDTO = new DeleteBookingDTO();
		deleteBookingDTO.setPreRegistrationId("98746563542672");
		List<DeleteBookingDTO> list = new ArrayList<>();
		list.add(deleteBookingDTO);
		delBookingResponseDTO.setResponse(deleteBookingDTO);
		MainResponseDTO<DocumentDeleteResponseDTO> delResponseDto = new MainResponseDTO<>();
		// delResponseDto.setStatus(Boolean.TRUE);
		delResponseDto.setErrors(null);
		delResponseDto.setResponse(deleteDTO);
		delResponseDto.setResponsetime(serviceUtil.getCurrentResponseTime());

		Mockito.when(demographicRepository.findBypreRegistrationId(preRegId)).thenReturn(preRegistrationEntity);

		ResponseEntity<MainResponseDTO<DocumentDeleteResponseDTO>> res = new ResponseEntity<>(delResponseDto,
				HttpStatus.OK);
		ResponseEntity<MainResponseDTO<DeleteBookingDTO>> res1 = new ResponseEntity<>(delBookingResponseDTO,
				HttpStatus.OK);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.DELETE), Mockito.any(),
				Mockito.eq(new ParameterizedTypeReference<MainResponseDTO<DocumentDeleteResponseDTO>>() {
				}), Mockito.anyMap())).thenReturn(res);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.DELETE), Mockito.any(),
				Mockito.eq(new ParameterizedTypeReference<MainResponseDTO<DeleteBookingDTO>>() {
				}), Mockito.anyMap())).thenReturn(res1);
		Mockito.when(demographicRepository.deleteByPreRegistrationId(preRegistrationEntity.getPreRegistrationId()))
				.thenReturn(1);

		MainResponseDTO<DeletePreRegistartionDTO> actualres = preRegistrationService.deleteIndividual(preRegId, userId);

		assertEquals("1.0", actualres.getVersion());

	}

	@Test
	public void updateByPreIdTest() {
		Mockito.when(demographicRepository.findBypreRegistrationId("98746563542672")).thenReturn(preRegistrationEntity);
		requestMap.put("id", createId);
		request.setId(updateId);
		Mockito.when(serviceUtil.prepareRequestMap(request)).thenReturn(requestMap);
		createPreRegistrationDTO = new DemographicRequestDTO();
		createPreRegistrationDTO.setDemographicDetails(jsonTestObject);
		createPreRegistrationDTO.setLangCode("eng");
		request.setRequest(createPreRegistrationDTO);
		Mockito.when(cryptoUtil.decrypt(Mockito.any(), Mockito.any())).thenReturn(jsonObject.toString().getBytes());
		preRegistrationService.updatePreRegistration(request, preId, userId);
	}

	// @Test(expected = DocumentFailedToDeleteException.class)
	// public void deleteIndividualRestExceptionTest() throws Exception {
	// String preRegId = "98746563542672";
	// preRegistrationEntity.setCreateDateTime(times);
	// preRegistrationEntity.setCreatedBy("9988905444");
	// preRegistrationEntity.setStatusCode("Booked");
	// preRegistrationEntity.setUpdateDateTime(times);
	// preRegistrationEntity.setApplicantDetailJson(jsonTestObject.toJSONString().getBytes());
	// preRegistrationEntity.setPreRegistrationId("98746563542672");
	//
	// DocumentDeleteResponseDTO deleteDTO = new DocumentDeleteResponseDTO();
	// List<DocumentDeleteResponseDTO> deleteAllList = new ArrayList<>();
	// deleteAllList.add(deleteDTO);
	//
	// MainResponseDTO<DocumentDeleteResponseDTO> delResponseDto = new
	// MainResponseDTO<>();
	// delResponseDto.setErrors(null);
	// delResponseDto.setResponse(deleteDTO);
	//
	// MainResponseDTO<DocumentDeleteResponseDTO> deleteAllByPreId1 = new
	// MainResponseDTO<>();
	// deleteAllByPreId1.setErrors(fullname);
	// Mockito.when(documentServiceIntf.deleteAllByPreId(Mockito.anyString())).thenReturn(deleteAllByPreId1);
	// Mockito.when(demographicRepository.findBypreRegistrationId(preRegId)).thenReturn(preRegistrationEntity);
	//
	// ResponseEntity<MainResponseDTO<DocumentDeleteResponseDTO>> res = new
	// ResponseEntity<>(delResponseDto,
	// HttpStatus.OK);
	//
	// HttpHeaders headers = new HttpHeaders();
	// headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
	//
	// MainResponseDTO<DocumentDeleteResponseDTO> deleteAllByPreId = new
	// MainResponseDTO<>();
	// deleteAllByPreId.setErrors(fullname);
	// Mockito.when(documentServiceIntf.deleteAllByPreId(Mockito.anyString())).thenReturn(deleteAllByPreId);
	// Mockito.when(demographicRepository.deleteByPreRegistrationId(preRegistrationEntity.getPreRegistrationId()))
	// .thenReturn(1);
	//
	// MainResponseDTO<DeletePreRegistartionDTO> actualres =
	// preRegistrationService.deleteIndividual(preRegId, userId);
	//
	// // assertEquals(true, actualres.isStatus());
	//
	// }

	@Test(expected = RecordNotFoundException.class)
	public void RecordNotFoundExceptionTest() {
		Mockito.when(demographicRepository.findBypreRegistrationId("98746563542672")).thenReturn(null);
		requestMap.put("id", createId);
		request.setId(updateId);
		Mockito.when(serviceUtil.prepareRequestMap(request)).thenReturn(requestMap);
		createPreRegistrationDTO = new DemographicRequestDTO();
		createPreRegistrationDTO.setDemographicDetails(jsonTestObject);
		createPreRegistrationDTO.setLangCode("eng");
		request.setRequest(createPreRegistrationDTO);
		Mockito.when(cryptoUtil.decrypt(Mockito.any(), Mockito.any())).thenReturn(jsonObject.toString().getBytes());
		preRegistrationService.updatePreRegistration(request, preId, userId);
	}

	@Test(expected = HashingException.class)
	public void getPreRegistrationHashingExceptionTest() {
		byte[] encryptedDemographicDetails = { 1, 0, 1, 0, 1, 0 };

		// Mockito.when(cryptoUtil.encrypt(Mockito.any(),Mockito.any())).thenReturn(encryptedDemographicDetails);

		preRegistrationEntity.setApplicantDetailJson(encryptedDemographicDetails);
		// preRegistrationEntity.setDemogDetailHash(new
		// String(HashUtill.hashUtill(preRegistrationEntity.getApplicantDetailJson())));
		Mockito.when(demographicRepository.findBypreRegistrationId("98746563542672")).thenReturn(preRegistrationEntity);
		Mockito.when(cryptoUtil.decrypt(Mockito.any(), Mockito.any())).thenReturn(jsonObject.toString().getBytes());
		MainResponseDTO<DemographicResponseDTO> res = preRegistrationService.getDemographicData("98746563542672");
		assertEquals("98746563542672", res.getResponse().getPreRegistrationId());
	}

	@Test(expected = RecordNotFoundException.class)
	public void getPreRegistrationFailureTest() {
		Mockito.when(demographicRepository.findBypreRegistrationId("98746563542672")).thenReturn(null);
		preRegistrationService.getDemographicData("98746563542672");
	}

	@Test
	public void updatePreRegistrationStatusTest() {
		Mockito.when(demographicRepository.findBypreRegistrationId("98746563542672")).thenReturn(preRegistrationEntity);
		MainResponseDTO<String> res = preRegistrationService.updatePreRegistrationStatus("98746563542672", "Booked",
				userId);
	}

	@Test(expected = RecordNotFoundException.class)
	public void updatePreRegistrationStatusFailureTest1() {
		Mockito.when(demographicRepository.findBypreRegistrationId("98746563542672")).thenReturn(null);
		preRegistrationService.updatePreRegistrationStatus("98746563542672", "Booked", userId);
	}

	@Test(expected = RecordFailedToUpdateException.class)
	public void updatePreRegistrationStatusFailureTest2() {
		Mockito.when(demographicRepository.findBypreRegistrationId("98746563542672")).thenReturn(preRegistrationEntity);
		preRegistrationService.updatePreRegistrationStatus("98746563542672", "NA", userId);
	}

	// @Test
	// public void getApplicationByDateTest() {
	// List<String> list = new ArrayList<>();
	//
	// LocalDate fromDate = LocalDate.now();
	// LocalDate toDate = LocalDate.now();
	// MainListResponseDTO<String> response = new MainListResponseDTO<>();
	// List<String> preIds = new ArrayList<>();
	// List<DemographicEntity> details = new ArrayList<>();
	// // DemographicEntity entity = new DemographicEntity();
	// entity.setPreRegistrationId("98746563542672");
	// details.add(entity);
	// byte[] sampleAppJson = "JSON".getBytes();
	// preIds.add("98746563542672");
	// response.setResponse(preIds);
	// response.setVersion("1.0");
	// // response.setStatus(true);
	// Mockito.doReturn(list).when(spyDemographicService).getPreRegistrationByDateEntityCheck(details);
	// String dateFormat = "yyyy-MM-dd HH:mm:ss";
	// Date myFromDate;
	// // myFromDate = DateUtils.parseToDate(URLDecoder.decode(fromDate, "UTF-8"),
	// // dateFormat);
	//
	// // Date myToDate = DateUtils.parseToDate(URLDecoder.decode(toDate, "UTF-8"),
	// // dateFormat);
	// LocalDateTime fromLocaldate = fromDate.atStartOfDay();
	//
	// LocalDateTime toLocaldate = toDate.atTime(23, 59, 59);
	// Mockito.when(demographicRepository.findBycreateDateTimeBetween(fromLocaldate,
	// toLocaldate)).thenReturn(details);
	// Mockito.when(entity.getApplicantDetailJson()).thenReturn(sampleAppJson);
	// MainListResponseDTO<String> actualRes =
	// spyDemographicService.getPreRegistrationByDate(fromDate, toDate);
	// assertEquals(actualRes.getVersion(), response.getVersion());
	//
	// }

	// /**
	// * @throws Exception
	// */
	// //@Test(expected = DateParseException.class)
	// public void getBydateFailureParseCheck() throws Exception {
	// DateParseException exception = new
	// DateParseException(ErrorCodes.PRG_PAM_APP_011.name(),
	// ErrorMessages.UNSUPPORTED_DATE_FORMAT.name());
	//
	// MainListResponseDTO<String> response = new MainListResponseDTO<>();
	// List<String> preIds = new ArrayList<>();
	// List<DemographicEntity> details = new ArrayList<>();
	// DemographicEntity entity = new DemographicEntity();
	// entity.setPreRegistrationId("98746563542672");
	// details.add(entity);
	//
	// preIds.add("98746563542672");
	// response.setResponse(preIds);
	//
	// LocalDateTime fromLocaldate = fromDate.atStartOfDay();
	//
	// LocalDateTime toLocaldate = toDate.atTime(23, 59, 59);
	//
	// Mockito.when(demographicRepository.findBycreateDateTimeBetween(fromLocaldate,
	// toLocaldate))
	// .thenThrow(exception);
	// preRegistrationService.getPreRegistrationByDate(fromDate, toDate);
	//
	// }

	@Test
	public void callGetUpdatedDateTimeRestServiceTest() throws ParseException, org.json.simple.parser.ParseException {
		MainResponseDTO<Map<String, String>> dto = new MainResponseDTO<>();
		List<String> preIds = new ArrayList<>();
		preIds.add("98746563542672");
		PreRegIdsByRegCenterIdDTO preRegIdsByRegCenterIdDTO = new PreRegIdsByRegCenterIdDTO();
		preRegIdsByRegCenterIdDTO.setPreRegistrationIds(preIds);

		dto.setErrors(null);
		dto.setResponsetime(LocalDateTime.now().toString());
		dto.setVersion("1.0");
		Map<String, String> map = new HashMap<>();
		map.put("98746563542672", LocalDateTime.now().toString());

		dto.setResponse(map);
		List<String> statusCodes = new ArrayList<>();
		statusCodes.add(StatusCodes.BOOKED.getCode());
		statusCodes.add(StatusCodes.EXPIRED.getCode());
		Mockito.when(demographicRepository.findByStatusCodeInAndPreRegistrationIdIn(statusCodes, preIds))
				.thenReturn(userEntityDetails);
		ResponseEntity<MainResponseDTO> respEntity = new ResponseEntity<>(dto, HttpStatus.OK);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.POST), Mockito.any(),
				Mockito.eq(MainResponseDTO.class))).thenThrow(RestClientException.class);
		MainResponseDTO<Map<String, String>> actualRes = preRegistrationService
				.getUpdatedDateTimeForPreIds(preRegIdsByRegCenterIdDTO);
		assertEquals(actualRes.getVersion(), dto.getVersion());

	}

	@Test(expected = RecordNotFoundForPreIdsException.class)
	public void invalidPreidFailureTest() {
		List<String> preIds = new ArrayList<>();
		preIds.add("");
		PreRegIdsByRegCenterIdDTO preRegIdsByRegCenterIdDTO = new PreRegIdsByRegCenterIdDTO();
		preRegIdsByRegCenterIdDTO.setPreRegistrationIds(null);
		List<String> statusCodes = new ArrayList<>();
		statusCodes.add(StatusCodes.BOOKED.getCode());
		statusCodes.add(StatusCodes.EXPIRED.getCode());
		Mockito.when(demographicRepository.findByStatusCodeInAndPreRegistrationIdIn(statusCodes, preIds))
				.thenReturn(userEntityDetails);
		preRegistrationService.getUpdatedDateTimeForPreIds(preRegIdsByRegCenterIdDTO);

	}

	@Test(expected = RecordNotFoundForPreIdsException.class)
	public void recordeNotFoundTest() {
		List<String> preIds = new ArrayList<>();
		preIds.add("userEntityDetails");
		PreRegIdsByRegCenterIdDTO preRegIdsByRegCenterIdDTO = new PreRegIdsByRegCenterIdDTO();
		preRegIdsByRegCenterIdDTO.setPreRegistrationIds(preIds);
		userEntityDetails = null;
		List<String> statusCodes = new ArrayList<>();
		statusCodes.add(StatusCodes.BOOKED.getCode());
		statusCodes.add(StatusCodes.EXPIRED.getCode());
		Mockito.when(demographicRepository.findByStatusCodeInAndPreRegistrationIdIn(statusCodes, preIds))
				.thenReturn(userEntityDetails);
		preRegistrationService.getUpdatedDateTimeForPreIds(preRegIdsByRegCenterIdDTO);

	}

	@Test(expected = RecordNotFoundForPreIdsException.class)
	public void recordeNotForPreIdFoundTest() {
		List<String> preIds = new ArrayList<>();
		preIds.add("userEntityDetails");
		PreRegIdsByRegCenterIdDTO preRegIdsByRegCenterIdDTO = new PreRegIdsByRegCenterIdDTO();
		preRegIdsByRegCenterIdDTO.setPreRegistrationIds(null);
		userEntityDetails = null;
		List<String> statusCodes = new ArrayList<>();
		statusCodes.add(StatusCodes.BOOKED.getCode());
		statusCodes.add(StatusCodes.EXPIRED.getCode());
		Mockito.when(demographicRepository.findByStatusCodeInAndPreRegistrationIdIn(statusCodes, preIds))
				.thenReturn(userEntityDetails);
		preRegistrationService.getUpdatedDateTimeForPreIds(preRegIdsByRegCenterIdDTO);

	}

}