package io.mosip.preregistration.datasync.test.service.util;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import io.mosip.preregistration.core.common.dto.BookingRegistrationDTO;
import io.mosip.preregistration.core.common.dto.DemographicResponseDTO;
import io.mosip.preregistration.core.common.dto.DocumentDTO;
import io.mosip.preregistration.core.common.dto.DocumentMultipartResponseDTO;
import io.mosip.preregistration.core.common.dto.DocumentsMetaData;
import io.mosip.preregistration.core.common.dto.ExceptionJSONInfoDTO;
import io.mosip.preregistration.core.common.dto.MainRequestDTO;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.common.dto.PreRegIdsByRegCenterIdDTO;
import io.mosip.preregistration.core.common.dto.PreRegIdsByRegCenterIdResponseDTO;
import io.mosip.preregistration.core.exception.InvalidRequestParameterException;
import io.mosip.preregistration.core.util.AuditLogUtil;
import io.mosip.preregistration.core.util.ValidationUtil;
import io.mosip.preregistration.datasync.DataSyncApplicationTest;
import io.mosip.preregistration.datasync.dto.DataSyncRequestDTO;
import io.mosip.preregistration.datasync.dto.PreRegArchiveDTO;
import io.mosip.preregistration.datasync.dto.ReverseDataSyncRequestDTO;
import io.mosip.preregistration.datasync.dto.ReverseDatasyncReponseDTO;
import io.mosip.preregistration.datasync.entity.InterfaceDataSyncEntity;
import io.mosip.preregistration.datasync.entity.ProcessedPreRegEntity;
import io.mosip.preregistration.datasync.errorcodes.ErrorCodes;
import io.mosip.preregistration.datasync.errorcodes.ErrorMessages;
import io.mosip.preregistration.datasync.exception.DemographicGetDetailsException;
import io.mosip.preregistration.datasync.exception.RecordNotFoundForDateRange;
import io.mosip.preregistration.datasync.repository.InterfaceDataSyncRepo;
import io.mosip.preregistration.datasync.repository.ProcessedDataSyncRepo;
import io.mosip.preregistration.datasync.service.util.DataSyncServiceUtil;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { DataSyncApplicationTest.class })
public class DataSyncServiceUtilTest {

	/**
	 * Autowired reference for {@link #DataSyncRepository}
	 */
	@MockBean
	private InterfaceDataSyncRepo interfaceDataSyncRepo;

	/**
	 * Autowired reference for {@link #ReverseDataSyncRepo}
	 */
	@MockBean
	private ProcessedDataSyncRepo processedDataSyncRepo;

	/**
	 * Autowired reference for {@link #RestTemplateBuilder}
	 */
	@Autowired
	DataSyncServiceUtil serviceUtil;

	@MockBean
	AuditLogUtil auditLogUtil;

	@MockBean
	ValidationUtil validationUtil;

	@MockBean
	RestTemplate restTemplate;

	/**
	 * Reference for ${mosip.id.preregistration.datasync.fetch.ids} from property
	 * file
	 */
	@Value("${mosip.id.preregistration.datasync.fetch.ids}")
	private String fetchAllId;

	/**
	 * Reference for ${mosip.id.preregistration.datasync.store} from property file
	 */
	@Value("${mosip.id.preregistration.datasync.store}")
	private String storeId;

	/**
	 * Reference for ${mosip.id.preregistration.datasync.fetch} from property file
	 */
	@Value("${mosip.id.preregistration.datasync.fetch}")
	private String fetchId;

	/**
	 * Reference for ${ver} from property file
	 */
	@Value("${version}")
	private String version;

	/**
	 * Reference for ${demographic.resource.url} from property file
	 */
	@Value("${demographic.resource.url}")
	private String demographicResourceUrl;

	/**
	 * Reference for ${document.resource.url} from property file
	 */
	@Value("${document.resource.url}")
	private String documentResourceUrl;

	/**
	 * Reference for ${booking.resource.url} from property file
	 */
	@Value("${booking.resource.url}")
	private String bookingResourceUrl;

	/**
	 * Reference for ${booking.resource.url} from property file
	 */
	@Value("${poa.url}")
	private String poaUrl;

	/**
	 * Reference for ${booking.resource.url} from property file
	 */
	@Value("${poi.url}")
	private String poiUrl;

	/**
	 * Reference for ${booking.resource.url} from property file
	 */
	@Value("${por.url}")
	private String porUrl;

	String resTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(new Date());

	ExceptionJSONInfoDTO errlist = new ExceptionJSONInfoDTO();
	ExceptionJSONInfoDTO exceptionJSONInfo = new ExceptionJSONInfoDTO("", "");

	String preId = "23587986034785";
	DocumentsMetaData documentsMetaData = new DocumentsMetaData();
	MainRequestDTO<?> requestDto = new MainRequestDTO<>();
	Date date = new Timestamp(System.currentTimeMillis());
	Map<String, String> requestMap = new HashMap<>();
	MainRequestDTO<DataSyncRequestDTO> datasyncReqDto = new MainRequestDTO<>();
	DataSyncRequestDTO dataSyncRequestDTO = new DataSyncRequestDTO();
	ReverseDataSyncRequestDTO reverseDataSyncRequestDTO = new ReverseDataSyncRequestDTO();
	PreRegIdsByRegCenterIdResponseDTO idResponseDTO = new PreRegIdsByRegCenterIdResponseDTO();
	PreRegIdsByRegCenterIdDTO preRegIdsByRegCenterIdDTO = new PreRegIdsByRegCenterIdDTO();
	List<String> preRegIds = new ArrayList<String>();
	DocumentMultipartResponseDTO multipartResponseDTOs = new DocumentMultipartResponseDTO();
	DocumentDTO documentDTO = new DocumentDTO();
	List<DocumentMultipartResponseDTO> responsestatusDto = new ArrayList<>();
	DemographicResponseDTO demographicResponseDTO = new DemographicResponseDTO();
	BookingRegistrationDTO bookingRegistrationDTO = new BookingRegistrationDTO();
	PreRegArchiveDTO preRegArchiveDTO = new PreRegArchiveDTO();
	File file;

	@Before
	public void setUp() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		URI uri = new URI(classLoader.getResource("Doc.pdf").getFile().trim().replaceAll("\\u0020", "%20"));
		file = new File(uri.getPath());
		// mockMultipartFile = new MockMultipartFile("file", "Doc.pdf",
		// "mixed/multipart", new FileInputStream(file));
	}

	@Test
	public void validateDataSyncRequestTest() {
		dataSyncRequestDTO.setRegistrationCenterId("1005");
		dataSyncRequestDTO.setFromDate("2018-01-17");
		dataSyncRequestDTO.setToDate("2018-12-17");
		boolean status = serviceUtil.validateDataSyncRequest(dataSyncRequestDTO, null);
		assertEquals(status, true);
	}

	@Test(expected = InvalidRequestParameterException.class)
	public void invalidRegCntrIdTest() {
		dataSyncRequestDTO.setRegistrationCenterId(null);
		serviceUtil.validateDataSyncRequest(dataSyncRequestDTO, null);

	}

	@Test(expected = InvalidRequestParameterException.class)
	public void invalidFromDateTest() {
		dataSyncRequestDTO.setRegistrationCenterId("1005");
		dataSyncRequestDTO.setFromDate(null);
		serviceUtil.validateDataSyncRequest(dataSyncRequestDTO, null);

	}
	
	@Test(expected = InvalidRequestParameterException.class)
	public void invalidToDateTest() {
		dataSyncRequestDTO.setRegistrationCenterId("1005");
		dataSyncRequestDTO.setFromDate("2019-02-10");
		dataSyncRequestDTO.setToDate("2019-00-1");
		serviceUtil.validateDataSyncRequest(dataSyncRequestDTO, null);

	}

	@Test
	public void validateReverseDataSyncRequestTest() {
		List<String> preRegistrationIds = new ArrayList<>();
		preRegistrationIds.add(preId);
		reverseDataSyncRequestDTO.setPreRegistrationIds(preRegistrationIds);
		boolean status = serviceUtil.validateReverseDataSyncRequest(reverseDataSyncRequestDTO, null);
		assertEquals(true, status);
	}

	@Test(expected = InvalidRequestParameterException.class)
	public void invalidPreIDTest() {
		reverseDataSyncRequestDTO.setPreRegistrationIds(null);
		serviceUtil.validateReverseDataSyncRequest(reverseDataSyncRequestDTO, null);

	}

	@Test
	public void callGetPreIdsRestServiceTest() {
		String fromDate = "2018-01-17";
		String toDate = "2019-01-17";
		preRegIds.add("23587986034785");
		MainResponseDTO<PreRegIdsByRegCenterIdResponseDTO> mainResponseDTO = new MainResponseDTO<>();
		PreRegIdsByRegCenterIdResponseDTO byRegCenterIdResponseDTO = new PreRegIdsByRegCenterIdResponseDTO();
		byRegCenterIdResponseDTO.setPreRegistrationIds(preRegIds);
		byRegCenterIdResponseDTO.setRegistrationCenterId("10001");
		mainResponseDTO.setResponsetime(resTime);
		mainResponseDTO.setErrors(null);
		mainResponseDTO.setResponse(byRegCenterIdResponseDTO);
		ResponseEntity<MainResponseDTO<PreRegIdsByRegCenterIdResponseDTO>> respEntity = new ResponseEntity<>(
				mainResponseDTO, HttpStatus.OK);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
				Mockito.eq(new ParameterizedTypeReference<MainResponseDTO<PreRegIdsByRegCenterIdResponseDTO>>() {
				}), Mockito.anyMap())).thenReturn(respEntity);
		PreRegIdsByRegCenterIdResponseDTO preRegIdsByRegCenterIdResponseDTO = serviceUtil
				.getBookedPreIdsByDateAndRegCenterIdRestService(fromDate, toDate, "10001");
		assertEquals(preRegIdsByRegCenterIdResponseDTO.getPreRegistrationIds().get(0), preRegIds.get(0));
	}

	@Test(expected = RecordNotFoundForDateRange.class)
	public void callGetBookedPreIdsRestServiceFailureTest() {
		String fromDate = "2018-01-17";
		String toDate = "2019-01-17";
		preRegIds.add("23587986034785");
		MainResponseDTO<PreRegIdsByRegCenterIdResponseDTO> mainResponseDTO = new MainResponseDTO<>();
		PreRegIdsByRegCenterIdResponseDTO byRegCenterIdResponseDTO = new PreRegIdsByRegCenterIdResponseDTO();
		byRegCenterIdResponseDTO.setPreRegistrationIds(preRegIds);
		byRegCenterIdResponseDTO.setRegistrationCenterId("10001");
		mainResponseDTO.setResponsetime(resTime);
		List<ExceptionJSONInfoDTO> exceptionJSONInfoDTOs = new ArrayList<>();
		exceptionJSONInfo.setErrorCode(ErrorCodes.PRG_DATA_SYNC_016.toString());
		exceptionJSONInfo.setMessage(ErrorMessages.BOOKING_NOT_FOUND.toString());
		exceptionJSONInfoDTOs.add(exceptionJSONInfo);
		mainResponseDTO.setErrors(exceptionJSONInfoDTOs);
		mainResponseDTO.setResponse(byRegCenterIdResponseDTO);
		Map<String, String> params = new HashMap<>();
		params.put("registrationCenterId", "10001");
		ResponseEntity<MainResponseDTO<PreRegIdsByRegCenterIdResponseDTO>> respEntity = new ResponseEntity<>(
				mainResponseDTO, HttpStatus.OK);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
				Mockito.eq(new ParameterizedTypeReference<MainResponseDTO<PreRegIdsByRegCenterIdResponseDTO>>() {
				}), Mockito.anyMap())).thenReturn(respEntity);
		serviceUtil.getBookedPreIdsByDateAndRegCenterIdRestService(fromDate, toDate, "10001");
	}

	@Test
	public void callGetPreIdsWithoutToDateRestServiceTest() {
		String fromDate = "2018-01-17";
		String toDate = null;
		preRegIds.add("23587986034785");
		MainResponseDTO<PreRegIdsByRegCenterIdResponseDTO> mainResponseDTO = new MainResponseDTO<>();
		PreRegIdsByRegCenterIdResponseDTO byRegCenterIdResponseDTO = new PreRegIdsByRegCenterIdResponseDTO();
		byRegCenterIdResponseDTO.setPreRegistrationIds(preRegIds);
		byRegCenterIdResponseDTO.setRegistrationCenterId("10001");
		mainResponseDTO.setResponsetime(resTime);
		mainResponseDTO.setErrors(null);
		mainResponseDTO.setResponse(byRegCenterIdResponseDTO);
		ResponseEntity<MainResponseDTO<PreRegIdsByRegCenterIdResponseDTO>> respEntity = new ResponseEntity<>(
				mainResponseDTO, HttpStatus.OK);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
				Mockito.eq(new ParameterizedTypeReference<MainResponseDTO<PreRegIdsByRegCenterIdResponseDTO>>() {
				}), Mockito.anyMap())).thenReturn(respEntity);
		PreRegIdsByRegCenterIdResponseDTO preRegIdsByRegCenterIdResponseDTO = serviceUtil
				.getBookedPreIdsByDateAndRegCenterIdRestService(fromDate, toDate, "10001");
		assertEquals(preRegIdsByRegCenterIdResponseDTO.getPreRegistrationIds().get(0), preRegIds.get(0));
	}

	// @Test(expected=DemographicGetDetailsException.class)
	// public void demographicGetDetailsExceptionTest() {
	// String fromDate="2018-01-17 00:00:00";
	// String toDate="2019-01-17 00:00:00";
	// preRegIds.add("23587986034785");
	// MainResponseDTO mainResponseDTO=new MainResponseDTO();
	// mainResponseDTO.setStatus(false);
	// mainResponseDTO.setResTime(resTime);
	// mainResponseDTO.setErr(exceptionJSONInfo);
	// mainResponseDTO.setResponse(preRegIds);
	// Mockito.when(restTemplateBuilder.build()).thenReturn(restTemplate);
	// Mockito.when(restTemplate.exchange(Mockito.anyString(),
	// Mockito.eq(HttpMethod.GET), Mockito.any(),
	// Mockito.eq(MainResponseDTO.class))).thenReturn(null);
	// List<String> list =serviceUtil.callGetPreIdsRestService(fromDate, toDate);
	//
	// }

	@Test
	public void callGetDocRestServiceTest() {

		multipartResponseDTOs.setDocName("RNC.pdf");
		multipartResponseDTOs.setDocumentId("1234");
		multipartResponseDTOs.setDocCatCode("POA");
		multipartResponseDTOs.setLangCode("ENG");

		responsestatusDto.add(multipartResponseDTOs);
		DocumentsMetaData documentsMetaData = new DocumentsMetaData();
		documentsMetaData.setDocumentsMetaData(responsestatusDto);
		MainResponseDTO<DocumentsMetaData> mainResponseDTO = new MainResponseDTO<>();
		mainResponseDTO.setResponsetime(resTime);
		mainResponseDTO.setErrors(null);
		mainResponseDTO.setResponse(documentsMetaData);
		ResponseEntity<MainResponseDTO<DocumentsMetaData>> respEntity = new ResponseEntity<>(mainResponseDTO,
				HttpStatus.OK);
		Map<String, String> params = new HashMap<>();
		params.put("preRegistrationId", preId);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
				Mockito.eq(new ParameterizedTypeReference<MainResponseDTO<DocumentsMetaData>>() {
				}), Mockito.anyMap())).thenReturn(respEntity);
		DocumentsMetaData response = serviceUtil.getDocDetails(preId);
		assertEquals(documentsMetaData.getDocumentsMetaData().get(0).getDocName(),
				response.getDocumentsMetaData().get(0).getDocName());
	}

	@Test
	public void callGetDocRestServiceTestFailure() {

		multipartResponseDTOs.setDocName("RNC.pdf");
		multipartResponseDTOs.setDocumentId("1234");
		multipartResponseDTOs.setDocCatCode("POA");
		multipartResponseDTOs.setLangCode("ENG");
		responsestatusDto.add(multipartResponseDTOs);
		DocumentsMetaData documentsMetaData = new DocumentsMetaData();
		documentsMetaData.setDocumentsMetaData(responsestatusDto);
		MainResponseDTO<DocumentsMetaData> mainResponseDTO = new MainResponseDTO<>();
		mainResponseDTO.setResponsetime(resTime);
		List<ExceptionJSONInfoDTO> exceptionJSONInfoDTOs = new ArrayList<>();
		exceptionJSONInfoDTOs.add(exceptionJSONInfo);
		mainResponseDTO.setErrors(exceptionJSONInfoDTOs);
		mainResponseDTO.setResponse(documentsMetaData);
		ResponseEntity<MainResponseDTO<DocumentsMetaData>> respEntity = new ResponseEntity<>(mainResponseDTO,
				HttpStatus.OK);
		Map<String, String> params = new HashMap<>();
		params.put("preRegistrationId", preId);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
				Mockito.eq(new ParameterizedTypeReference<MainResponseDTO<DocumentsMetaData>>() {
				}), Mockito.anyMap())).thenReturn(respEntity);
		serviceUtil.getDocDetails(preId);
	}

	@Test
	public void callGetPreRegInfoRestServiceTest() {
		demographicResponseDTO.setPreRegistrationId(preId);
		List<DemographicResponseDTO> list = new ArrayList<>();
		list.add(demographicResponseDTO);
		MainResponseDTO<DemographicResponseDTO> mainResponseDTO = new MainResponseDTO<>();
		mainResponseDTO.setResponsetime(resTime);
		mainResponseDTO.setErrors(null);
		mainResponseDTO.setResponse(demographicResponseDTO);
		ResponseEntity<MainResponseDTO<DemographicResponseDTO>> respEntity = new ResponseEntity<>(mainResponseDTO,
				HttpStatus.OK);
		Map<String, Object> params = new HashMap<>();
		params.put("preRegistrationId", preId);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
				Mockito.eq(new ParameterizedTypeReference<MainResponseDTO<DemographicResponseDTO>>() {
				}), Mockito.anyMap())).thenReturn(respEntity);

		DemographicResponseDTO response = serviceUtil.getPreRegistrationData(preId);
		assertEquals(demographicResponseDTO.getPreRegistrationId(), response.getPreRegistrationId());
	}

	@Test(expected = DemographicGetDetailsException.class)
	public void callGetPreRegInfoRestServiceFailureTest() {
		demographicResponseDTO.setPreRegistrationId(preId);
		List<DemographicResponseDTO> list = new ArrayList<>();
		list.add(demographicResponseDTO);
		MainResponseDTO<DemographicResponseDTO> mainResponseDTO = new MainResponseDTO<>();
		mainResponseDTO.setResponsetime(resTime);
		List<ExceptionJSONInfoDTO> exceptionJSONInfoDTOs = new ArrayList<>();
		ExceptionJSONInfoDTO exceptionJSONInfoDTO = new ExceptionJSONInfoDTO();
//		exceptionJSONInfoDTO.setErrorCode(ErrorCodes.PRG_PAM_APP_002.toString());
		exceptionJSONInfoDTO.setMessage(ErrorMessages.DEMOGRAPHIC_GET_RECORD_FAILED.toString());
		exceptionJSONInfoDTOs.add(exceptionJSONInfoDTO);
		mainResponseDTO.setErrors(exceptionJSONInfoDTOs);
		mainResponseDTO.setResponse(demographicResponseDTO);
		ResponseEntity<MainResponseDTO<DemographicResponseDTO>> respEntity = new ResponseEntity<>(mainResponseDTO,
				HttpStatus.OK);
		Map<String, Object> params = new HashMap<>();
		params.put("preRegistrationId", preId);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
				Mockito.eq(new ParameterizedTypeReference<MainResponseDTO<DemographicResponseDTO>>() {
				}), Mockito.anyMap())).thenReturn(respEntity);

		serviceUtil.getPreRegistrationData(preId);
	}

	@Test
	public void callGetAppointmentDetailsRestServiceTest() {
		bookingRegistrationDTO.setRegistrationCenterId("1005");
		MainResponseDTO<BookingRegistrationDTO> responseDTO = new MainResponseDTO<>();
		responseDTO.setResponsetime(resTime);
		responseDTO.setErrors(null);
		responseDTO.setResponse(bookingRegistrationDTO);
		ResponseEntity<MainResponseDTO<BookingRegistrationDTO>> respEntity = new ResponseEntity<>(responseDTO,
				HttpStatus.OK);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
				Mockito.eq(new ParameterizedTypeReference<MainResponseDTO<BookingRegistrationDTO>>() {
				}), Mockito.anyMap())).thenReturn(respEntity);

		BookingRegistrationDTO response = serviceUtil.getAppointmentDetails(preId);
		assertEquals(bookingRegistrationDTO.getRegistrationCenterId(), response.getRegistrationCenterId());
	}

	@Test(expected = DemographicGetDetailsException.class)
	public void callGetAppointmentDetailsRestFailureTest() {
		bookingRegistrationDTO.setRegistrationCenterId("1005");
		MainResponseDTO<BookingRegistrationDTO> responseDTO = new MainResponseDTO<>();
		responseDTO.setResponsetime(resTime);
		List<ExceptionJSONInfoDTO> exceptionJSONInfoDTOs = new ArrayList<>();
		ExceptionJSONInfoDTO exceptionJSONInfoDTO = new ExceptionJSONInfoDTO();
//		exceptionJSONInfoDTO.setErrorCode(ErrorCodes.PRG_PAM_APP_002.toString());
		exceptionJSONInfoDTO.setMessage(ErrorMessages.BOOKING_NOT_FOUND.toString());
		exceptionJSONInfoDTOs.add(exceptionJSONInfoDTO);
		responseDTO.setErrors(exceptionJSONInfoDTOs);
		responseDTO.setResponse(bookingRegistrationDTO);
		ResponseEntity<MainResponseDTO<BookingRegistrationDTO>> respEntity = new ResponseEntity<>(responseDTO,
				HttpStatus.OK);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
				Mockito.eq(new ParameterizedTypeReference<MainResponseDTO<BookingRegistrationDTO>>() {
				}), Mockito.anyMap())).thenReturn(respEntity);

		serviceUtil.getAppointmentDetails(preId);
	}

	@Test(expected = RecordNotFoundForDateRange.class)
	public void callGetAppointmentDetailsRestFailureTest2() {
		bookingRegistrationDTO.setRegistrationCenterId("1005");
		MainResponseDTO<BookingRegistrationDTO> responseDTO = new MainResponseDTO<>();
		responseDTO.setResponsetime(resTime);
		responseDTO.setErrors(null);
		bookingRegistrationDTO = null;
		responseDTO.setResponse(bookingRegistrationDTO);
		ResponseEntity<MainResponseDTO<BookingRegistrationDTO>> respEntity = new ResponseEntity<>(responseDTO,
				HttpStatus.OK);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
				Mockito.eq(new ParameterizedTypeReference<MainResponseDTO<BookingRegistrationDTO>>() {
				}), Mockito.anyMap())).thenReturn(respEntity);

		serviceUtil.getAppointmentDetails(preId);
	}

	@Test
	public void preparePreRegArchiveDTOTest() {
		demographicResponseDTO.setPreRegistrationId(preId);
		bookingRegistrationDTO.setRegistrationCenterId("1005");
		bookingRegistrationDTO.setRegDate(resTime);

		serviceUtil.preparePreRegArchiveDTO(demographicResponseDTO, bookingRegistrationDTO);

	}

	private JSONObject jsonObject;
	private JSONParser parser = null;

	@Test
	public void archivingFilesTest() throws FileNotFoundException, IOException, ParseException {
		parser = new JSONParser();

		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("pre-registration-test.json").getFile());
		jsonObject = (JSONObject) parser.parse(new FileReader(file));

		demographicResponseDTO.setPreRegistrationId(preId);
		demographicResponseDTO.setDemographicDetails(jsonObject);

		bookingRegistrationDTO.setRegistrationCenterId("1005");
		bookingRegistrationDTO.setRegDate(resTime);

		multipartResponseDTOs.setDocName("RNC.pdf");
		multipartResponseDTOs.setDocumentId("1234");
		multipartResponseDTOs.setDocCatCode("POA");
		multipartResponseDTOs.setLangCode("ENG");
		multipartResponseDTOs.setDocTypCode("RNC");
		responsestatusDto.add(multipartResponseDTOs);
		multipartResponseDTOs = new DocumentMultipartResponseDTO();
		multipartResponseDTOs.setDocName("CIN.pdf");
		multipartResponseDTOs.setDocumentId("1235");
		multipartResponseDTOs.setDocCatCode("POI");
		multipartResponseDTOs.setLangCode("ENG");
		multipartResponseDTOs.setDocTypCode("CIN");
		responsestatusDto.add(multipartResponseDTOs);
		multipartResponseDTOs = new DocumentMultipartResponseDTO();
		multipartResponseDTOs.setDocName("COB.pdf");
		multipartResponseDTOs.setDocumentId("4223");
		multipartResponseDTOs.setDocCatCode("POB");
		multipartResponseDTOs.setLangCode("ENG");
		multipartResponseDTOs.setDocTypCode("COB");
		responsestatusDto.add(multipartResponseDTOs);
		multipartResponseDTOs = new DocumentMultipartResponseDTO();
		multipartResponseDTOs.setDocName("drivingLicense.pdf");
		multipartResponseDTOs.setDocumentId("5324");
		multipartResponseDTOs.setLangCode("ENG");
		multipartResponseDTOs.setDocCatCode("POR");
		multipartResponseDTOs.setDocTypCode("CRN");
		documentDTO.setDocument(file.toString().getBytes());
		responsestatusDto.add(multipartResponseDTOs);
		documentsMetaData.setDocumentsMetaData(responsestatusDto);
		MainResponseDTO<DocumentDTO> mainResponseDTO = new MainResponseDTO<>();
		mainResponseDTO.setResponse(documentDTO);
		mainResponseDTO.setErrors(null);
		ResponseEntity<MainResponseDTO<DocumentDTO>> responseEntity = new ResponseEntity<>(mainResponseDTO,
				HttpStatus.OK);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
				Mockito.eq(new ParameterizedTypeReference<MainResponseDTO<DocumentDTO>>() {
				}), Mockito.anyMap())).thenReturn(responseEntity);
		Map<String, String> documentTypeMap = new HashMap<>();
		Mockito.when(validationUtil.getDocumentTypeNameByTypeCode(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(documentTypeMap);
		serviceUtil.archivingFiles(demographicResponseDTO, bookingRegistrationDTO, documentsMetaData);
	}

	@Test
	public void archivingFilesFailureTest() throws FileNotFoundException, IOException, ParseException {
		parser = new JSONParser();

		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("pre-registration-test.json").getFile());
		jsonObject = (JSONObject) parser.parse(new FileReader(file));

		demographicResponseDTO.setPreRegistrationId(preId);
		demographicResponseDTO.setDemographicDetails(jsonObject);

		bookingRegistrationDTO.setRegistrationCenterId("1005");
		bookingRegistrationDTO.setRegDate(resTime);

		multipartResponseDTOs.setDocName("CIN.pdf");
		multipartResponseDTOs.setDocumentId("1235");
		multipartResponseDTOs.setDocCatCode("POI");
		multipartResponseDTOs.setDocTypCode("RNC");
		multipartResponseDTOs.setLangCode("ENG");
		responsestatusDto.add(multipartResponseDTOs);
		// documentDTO.setDocument(file.toString().getBytes());
		documentDTO = null;
		responsestatusDto.add(multipartResponseDTOs);
		documentsMetaData.setDocumentsMetaData(responsestatusDto);
		MainResponseDTO<DocumentDTO> mainResponseDTO = new MainResponseDTO<>();
		mainResponseDTO.setResponse(documentDTO);
		ExceptionJSONInfoDTO dto = new ExceptionJSONInfoDTO();
		dto.setErrorCode("");
		dto.setMessage("");
		List<ExceptionJSONInfoDTO> dtos = new ArrayList<>();
		dtos.add(dto);
		mainResponseDTO.setErrors(dtos);
		ResponseEntity<MainResponseDTO<DocumentDTO>> responseEntity = new ResponseEntity<>(mainResponseDTO,
				HttpStatus.OK);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
				Mockito.eq(new ParameterizedTypeReference<MainResponseDTO<DocumentDTO>>() {
				}), Mockito.anyMap())).thenReturn(responseEntity);
		Map<String, String> documentTypeMap = new HashMap<>();
		Mockito.when(validationUtil.getDocumentTypeNameByTypeCode(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(documentTypeMap);
		serviceUtil.archivingFiles(demographicResponseDTO, bookingRegistrationDTO, documentsMetaData);
	}

	@Test
	public void reverseDateSyncSaveTest() {
		List<String> preIdList = new ArrayList<>();
		preIdList.add(preId);
		reverseDataSyncRequestDTO.setPreRegistrationIds(preIdList);
		PreRegIdsByRegCenterIdDTO preRegDTO = new PreRegIdsByRegCenterIdDTO();
		preRegDTO.setPreRegistrationIds(preIdList);
		MainRequestDTO<PreRegIdsByRegCenterIdDTO> mainRequestDTO = new MainRequestDTO<>();
		mainRequestDTO.setRequest(preRegDTO);
		Map<String, String> preIdMap = new HashMap<>();
		MainResponseDTO<Map<String, String>> mainResponseDTO = new MainResponseDTO<>();
		preIdMap.put(preId, LocalDateTime.now().toString());
		mainResponseDTO.setResponsetime(resTime);
		mainResponseDTO.setErrors(null);
		mainResponseDTO.setResponse(preIdMap);
		ResponseEntity<MainResponseDTO<Map<String, String>>> respEntity = new ResponseEntity<>(mainResponseDTO,
				HttpStatus.OK);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.POST), Mockito.any(),
				Mockito.eq(new ParameterizedTypeReference<MainResponseDTO<Map<String, String>>>() {
				}))).thenReturn(respEntity);

		ReverseDatasyncReponseDTO reverseDatasyncReponse = new ReverseDatasyncReponseDTO();
		reverseDatasyncReponse.setTransactionId("1111");
		List<String> preids = new ArrayList<>();
		preids.add("23587986034785");
		reverseDatasyncReponse.setPreRegistrationIds(preids);
		reverseDatasyncReponse.setCountOfStoredPreRegIds("1");
		serviceUtil.reverseDateSyncSave(date, reverseDataSyncRequestDTO, "9886442073");
	}

	@Test
	public void getLastUpdateTimeStampTest() {
		List<String> preIdList = new ArrayList<>();
		preIdList.add(preId);
		PreRegIdsByRegCenterIdDTO preRegDTO = new PreRegIdsByRegCenterIdDTO();
		preRegDTO.setPreRegistrationIds(preIdList);
		MainRequestDTO<PreRegIdsByRegCenterIdDTO> mainRequestDTO = new MainRequestDTO<>();
		mainRequestDTO.setRequest(preRegDTO);
		MainResponseDTO<Map<String, String>> mainResponseDTO = new MainResponseDTO<>();
		Map<String, String> preIdMap = new HashMap<>();
		preIdMap.put(preId, LocalDateTime.now().toString());
		mainResponseDTO.setResponsetime(resTime);
		mainResponseDTO.setErrors(null);
		mainResponseDTO.setResponse(preIdMap);
		ResponseEntity<MainResponseDTO<Map<String, String>>> respEntity = new ResponseEntity<>(mainResponseDTO,
				HttpStatus.OK);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.POST), Mockito.any(),
				Mockito.eq(new ParameterizedTypeReference<MainResponseDTO<Map<String, String>>>() {

				}))).thenReturn(respEntity);

		serviceUtil.getLastUpdateTimeStamp(preRegDTO);
	}

	@Test
	public void storeReverseDataSyncTest() {
		InterfaceDataSyncEntity interfaceDataSyncEntity = new InterfaceDataSyncEntity();
		interfaceDataSyncEntity.setCreatedBy("Sanober Noor");
		interfaceDataSyncEntity.setCreatedDate(null);
		interfaceDataSyncEntity.setDeleted(true);
		interfaceDataSyncEntity.setDelTime(null);
		interfaceDataSyncEntity.setIpprlst_PK(null);
		interfaceDataSyncEntity.setLangCode("eng");
		interfaceDataSyncEntity.setUpdatedBy("sanober");
		interfaceDataSyncEntity.setUpdatedDate(null);
		List<InterfaceDataSyncEntity> entityList = new ArrayList<>();
		entityList.add(interfaceDataSyncEntity);
		ProcessedPreRegEntity processedPreRegEntity = new ProcessedPreRegEntity();
		processedPreRegEntity.setCrBy("sanober Noor");
		processedPreRegEntity.setCrDate(null);
		processedPreRegEntity.setDeleted(true);
		processedPreRegEntity.setDelTime(null);
		processedPreRegEntity.setLangCode("eng");
		processedPreRegEntity.setPreRegistrationId("1234567890");
		processedPreRegEntity.setPreregTrnId("976543211324");
		processedPreRegEntity.setReceivedDTime(null);
		processedPreRegEntity.setStatusCode("");
		processedPreRegEntity.setUpBy("sanober");
		processedPreRegEntity.setUpdDate(null);
		List<ProcessedPreRegEntity> processedEntityList = new ArrayList<>();
		processedEntityList.add(processedPreRegEntity);

		Mockito.when(interfaceDataSyncRepo.saveAll(Mockito.any())).thenReturn(entityList);
		Mockito.when(processedDataSyncRepo.existsById(processedPreRegEntity.getPreRegistrationId())).thenReturn(false);
		Mockito.when(processedDataSyncRepo.save(Mockito.any())).thenReturn(processedPreRegEntity);
		ReverseDatasyncReponseDTO reponse = serviceUtil.storeReverseDataSync(entityList, processedEntityList);

		assertEquals("1", reponse.getCountOfStoredPreRegIds());
	}

}
