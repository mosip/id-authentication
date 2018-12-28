package io.mosip.pregistration.datasync.test.service;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
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
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import io.mosip.pregistration.datasync.dto.DataSyncDTO;
import io.mosip.pregistration.datasync.dto.DataSyncRequestDTO;
import io.mosip.pregistration.datasync.dto.DataSyncResponseDTO;
import io.mosip.pregistration.datasync.dto.ExceptionJSONInfoDTO;
import io.mosip.pregistration.datasync.dto.MainRequestDTO;
import io.mosip.pregistration.datasync.dto.PreRegistrationIdsDTO;
import io.mosip.pregistration.datasync.dto.ResponseDTO;
import io.mosip.pregistration.datasync.dto.ReverseDataSyncDTO;
import io.mosip.pregistration.datasync.dto.ReverseDataSyncRequestDTO;
import io.mosip.pregistration.datasync.entity.InterfaceDataSyncTablePK;
import io.mosip.pregistration.datasync.entity.PreRegistrationProcessedEntity;
import io.mosip.pregistration.datasync.entity.ReverseDataSyncEntity;
import io.mosip.pregistration.datasync.errorcodes.ErrorCodes;
import io.mosip.pregistration.datasync.errorcodes.ErrorMessages;
import io.mosip.pregistration.datasync.exception.RecordNotFoundForDateRange;
import io.mosip.pregistration.datasync.exception.ReverseDataFailedToStoreException;
import io.mosip.pregistration.datasync.repository.DataSyncRepository;
import io.mosip.pregistration.datasync.repository.ReverseDataSyncRepo;
import io.mosip.pregistration.datasync.service.DataSyncService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DataSyncServiceTest {

	@Mock
	private DataSyncRepository dataSyncRepository;

	@Mock
	private ReverseDataSyncRepo reverseDataSyncRepo;

	@Autowired
	private DataSyncService dataSyncService;

	@Value("${preRegResourceUrl}")
	private String preRegResourceUrl;

	@Value("${docRegResourceUrl}")
	private String docRegResourceUrl;

	@MockBean
	RestTemplateBuilder restTemplateBuilder;

	String preid = "";
	List<ExceptionJSONInfoDTO> errlist = new ArrayList<>();
	ExceptionJSONInfoDTO exceptionJSONInfo = new ExceptionJSONInfoDTO("", "");
	DataSyncResponseDTO<PreRegistrationIdsDTO> dataSyncResponseDTO = new DataSyncResponseDTO<>();
	DataSyncResponseDTO<String> storeResponseDTO = new DataSyncResponseDTO<>();
	String resTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(new Date());
	DataSyncDTO requestDto = new DataSyncDTO();
	PreRegistrationIdsDTO preRegistrationIdsDTO = new PreRegistrationIdsDTO();
	DataSyncRequestDTO dataSyncRequestDTO = new DataSyncRequestDTO();
	// List<ExceptionJSONInfoDTO> ex = new ArrayList<>();
	private JSONObject jsonTestObject;
	ReverseDataSyncDTO reverseDto = new ReverseDataSyncDTO();

	byte[] pFile = null;

	private Object toDate;

	private Object fromDate;

	@Before
	public void setUp() throws URISyntaxException, IOException, org.json.simple.parser.ParseException, ParseException {
		preid = "23587986034785";

		ClassLoader classLoader = getClass().getClassLoader();
		JSONParser parser = new JSONParser();
		URI uri = new URI(
				classLoader.getResource("pre-registration-test.json").getFile().trim().replaceAll("\\u0020", "%20"));
		File jsonFileTest = new File(uri.getPath());
		jsonTestObject = (JSONObject) parser.parse(new FileReader(jsonFileTest));
		pFile = Files.readAllBytes(jsonFileTest.toPath());

		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Date date = dateFormat.parse("08/10/2018");
		long time = date.getTime();
		Timestamp times = new Timestamp(time);
		// demography = new PreRegistrationEntity();
		// demography.setCr_appuser_id("Rajath");
		// demography.setCreateDateTime(times);
		// demography.setStatusCode("SAVE");
		// demography.setLangCode("12L");
		// demography.setPreRegistrationId(preid);
		// demography.setApplicantDetailJson(jsonTestObject.toString().getBytes("UTF-8"));

		byte[] dFile = null;

		File file = new File(classLoader.getResource("Doc.pdf").getFile());
		uri = new URI(classLoader.getResource("Doc.pdf").getFile().trim().replaceAll("\\u0020", "%20"));
		file = new File(uri.getPath());
		dFile = Files.readAllBytes(file.toPath());

		// DocumentEntity documentEntity = new DocumentEntity(1, "75391783729406",
		// "Doc.pdf", "address", "POA", "PDF",
		// dFile, "Draft", "ENG", "Jagadishwari", new
		// Timestamp(System.currentTimeMillis()), "Jagadishwari",
		// new Timestamp(System.currentTimeMillis()));
		//
		// docEntityList.add(documentEntity);

		List<Object> responseList = new ArrayList<>();
		dataSyncResponseDTO.setStatus("true");
		dataSyncResponseDTO.setErr(exceptionJSONInfo);
		dataSyncResponseDTO.setResTime(resTime);

		Map<String,String> list = new HashMap<>();
		list.put("1","2018-12-28T13:04:53.117Z");
		preRegistrationIdsDTO.setPreRegistrationIds(list);
		preRegistrationIdsDTO.setTransactionId("1111");
		dataSyncResponseDTO.setResponse(preRegistrationIdsDTO);

		// PreRegistrationEntity preRegistrationEntity = new PreRegistrationEntity();
		// preRegistrationEntity.setCreateDateTime(times);
		// preRegistrationEntity.setPreRegistrationId("23587986034785");
		// userDetails.add(preRegistrationEntity);

		Date date1 = dateFormat.parse("08/10/2018");
		Date date2 = dateFormat.parse("01/11/2018");
		long time1 = date1.getTime();
		long time2 = date2.getTime();
		Timestamp from = new Timestamp(time1);
		Timestamp to = new Timestamp(time2);

		dataSyncRequestDTO.setRegClientId("59276903416082");
		dataSyncRequestDTO.setFromDate(from.toString());
		dataSyncRequestDTO.setToDate(to.toString());
		dataSyncRequestDTO.setUserId("256752365832");
		// ex.add(exceptionJSONInfo);
		dataSyncResponseDTO.setResponse(preRegistrationIdsDTO);
		dataSyncResponseDTO.setErr(null);
		dataSyncResponseDTO.setStatus("True");
		dataSyncResponseDTO.setResTime(resTime);

		List<String> preRegIds = new ArrayList<String>();
		preRegIds.add("23587986034785");
		ReverseDataSyncRequestDTO request = new ReverseDataSyncRequestDTO();
		request.setPre_registration_ids(preRegIds);

		reverseDto.setReqTime(new Date());
		reverseDto.setRequest(request);

		MockitoAnnotations.initMocks(this);
		// preRegResourceUrl="http://localhost:9093/v0.1/pre-registration/applicationDataByDateTime";
	}

	// @Test
	// public void successGetPreRegistration() throws Exception {
	// PreRegArchiveDTO preRegArchiveDTO = new PreRegArchiveDTO();
	// dataSyncResponseDTO = new DataSyncResponseDTO<>();
	// dataSyncResponseDTO.setStatus("true");
	//
	// preRegArchiveDTO.setZipBytes(pFile);
	// //
	// preRegArchiveDTO.setFileName(demography.getPreRegistrationId().toString());
	// dataSyncResponseDTO.setResponse(preRegArchiveDTO);
	// dataSyncResponseDTO.setErr(errlist);
	// dataSyncResponseDTO.setResTime(resTime);
	//
	// Mockito.when(dataSyncRepository.findDemographyByPreId(preid)).thenReturn(demography);
	// Mockito.when(dataSyncRepository.findDocumentByPreId(preid)).thenReturn(docEntityList);
	//
	// DataSyncResponseDTO<PreRegArchiveDTO> response =
	// dataSyncService.getPreRegistration(preid);
	//
	// assertEquals(response.getResponse().getFileName(),
	// preRegArchiveDTO.getFileName());
	// }

	// @Test(expected = DataSyncRecordNotFoundException.class)
	// public void failureGetPreRegistration() throws Exception {
	// DataSyncRecordNotFoundException exception = new
	// DataSyncRecordNotFoundException(
	// ErrorMessages.RECORDS_NOT_FOUND_FOR_REQUESTED_PREREGID.toString());
	// Mockito.when(dataSyncRepository.findDemographyByPreId(null)).thenThrow(exception);
	// dataSyncService.getPreRegistration(" ");
	// }

	// @Test(expected = ZipFileCreationException.class)
	// public void failurezipcreation() throws Exception {
	// ZipFileCreationException exception = new ZipFileCreationException(
	// ErrorMessages.FAILED_TO_CREATE_A_ZIP_FILE.toString());
	// demography.setApplicantDetailJson(null);
	// Mockito.when(dataSyncRepository.findDemographyByPreId(preid)).thenReturn(demography);
	// Mockito.when(dataSyncRepository.findDocumentByPreId(preid)).thenReturn(docEntityList);
	// Mockito.when(dataSyncService.getPreRegistration(preid)).thenThrow(exception);
	// }

	@Test
	public void storeConsumePreIdsSuccessTest() {

		DataSyncResponseDTO<String> expRes = new DataSyncResponseDTO<>();
		expRes.setErr(null);
		expRes.setResponse(ErrorMessages.PRE_REGISTRATION_IDS_STORED_SUCESSFULLY.toString());
		ReverseDataSyncEntity reverseDataSyncEntity = new ReverseDataSyncEntity();
		PreRegistrationProcessedEntity processedEntity = new PreRegistrationProcessedEntity();
		storeResponseDTO.setResponse(ErrorMessages.PRE_REGISTRATION_IDS_STORED_SUCESSFULLY.toString());
		storeResponseDTO.setStatus("true");
		storeResponseDTO.setResTime(resTime);
		storeResponseDTO.setErr(null);

		reverseDataSyncEntity.setLangCode("AR");
		reverseDataSyncEntity.setCrBy("5766477466");
		processedEntity.setLangCode("AR");
		processedEntity.setCrBy("5766477466");
		processedEntity.setStatusCode("");
		List<ReverseDataSyncEntity> savedList = new ArrayList<>();
		savedList.add(reverseDataSyncEntity);

		Mockito.when(dataSyncRepository.saveAll(ArgumentMatchers.any())).thenReturn(savedList);
		Mockito.when(reverseDataSyncRepo.existsById(preid)).thenReturn(Mockito.anyBoolean());
		Mockito.when(reverseDataSyncRepo.save(processedEntity)).thenReturn(processedEntity);
		DataSyncResponseDTO<String> actRes = dataSyncService.storeConsumedPreRegistrations(reverseDto);
		assertEquals(actRes.getResponse().toString(), expRes.getResponse().toString());

	}

	@Test(expected = ReverseDataFailedToStoreException.class)
	public void reverseDataSyncFailureTest() {
		ReverseDataSyncEntity reverseDataSyncEntity = new ReverseDataSyncEntity();
		List<ReverseDataSyncEntity> savedList = new ArrayList<>();
		reverseDataSyncEntity.setLangCode("AR");
		reverseDataSyncEntity.setCrBy("5766477466");

		DataSyncResponseDTO<String> expRes = new DataSyncResponseDTO<>();
		expRes.setErr(null);
		expRes.setStatus("false");

		InterfaceDataSyncTablePK ipprlst_PK = new InterfaceDataSyncTablePK();
		ipprlst_PK.setPreregId("23587986034785");
		ipprlst_PK.setReceivedDtimes(new Timestamp(System.currentTimeMillis()));

		reverseDataSyncEntity.setIpprlst_PK(ipprlst_PK);

		savedList.add(reverseDataSyncEntity);
		ReverseDataFailedToStoreException exception = new ReverseDataFailedToStoreException(
				ErrorCodes.PRG_REVESE_DATA_SYNC_001.toString(),
				ErrorMessages.FAILED_TO_STORE_PRE_REGISTRATION_IDS.toString());

		List<String> preRegIds = new ArrayList<String>();
		ReverseDataSyncRequestDTO request = new ReverseDataSyncRequestDTO();
		request.setPre_registration_ids(preRegIds);

		reverseDto.setReqTime(new Date());
		reverseDto.setRequest(request);

		Mockito.when(dataSyncRepository.saveAll(null)).thenThrow(exception);
		Mockito.when(reverseDataSyncRepo.existsById(ArgumentMatchers.any())).thenReturn(true);
		Mockito.when(reverseDataSyncRepo.save(ArgumentMatchers.any())).thenReturn(true);
		DataSyncResponseDTO<String> actRes = dataSyncService.storeConsumedPreRegistrations(reverseDto);
		System.out.println("size 1: " + actRes.getStatus());
		System.out.println("size 2: " + expRes.getStatus());
		assertEquals(actRes.getStatus(), expRes.getStatus());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void retrieveAllPreRegIdsWithTodateTest() throws ParseException {

		RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
		Mockito.when(restTemplateBuilder.build()).thenReturn(restTemplate);

		List<String> responseList = new ArrayList<String>();
		ResponseDTO responseDTO = new ResponseDTO<>();
		responseList.add("23587986034785");
		responseDTO.setStatus("true");
		responseDTO.setErr(null);
		responseDTO.setResponse(responseList);
		ResponseEntity<ResponseDTO> resp = new ResponseEntity<>(responseDTO, HttpStatus.OK);
		Mockito.when(restTemplate.exchange(ArgumentMatchers.anyString(), ArgumentMatchers.any(HttpMethod.class),
				ArgumentMatchers.any(), ArgumentMatchers.<Class<ResponseDTO>>any())).thenReturn(resp);

		PreRegistrationIdsDTO preRegResponse = new PreRegistrationIdsDTO();
		Map<String,String> listOfPreIds = new HashMap<>();
		listOfPreIds.put("23587986034785","2018-12-28T13:04:53.117Z");
		
		preRegResponse.setPreRegistrationIds(listOfPreIds);
		preRegResponse.setTransactionId("09876543");
		
		MainRequestDTO<DataSyncRequestDTO> mainReq = new MainRequestDTO<>();
		mainReq.setId("mosip.pre-registration.datasync");
		mainReq.setVer("1.0");
		mainReq.setReqTime(new Date());
		mainReq.setRequest(dataSyncRequestDTO);
		DataSyncResponseDTO<PreRegistrationIdsDTO> actualRes = dataSyncService.retrieveAllPreRegIds(mainReq);
		assertEquals(actualRes.getResponse().getPreRegistrationIds().get(0),
				dataSyncResponseDTO.getResponse().getPreRegistrationIds().get(0));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void retrieveAllPreRegIdsWithoutTodateTest() throws ParseException {

		RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
		Mockito.when(restTemplateBuilder.build()).thenReturn(restTemplate);

		dataSyncRequestDTO.setToDate("");

		List<String> responseList = new ArrayList<String>();
		ResponseDTO responseDTO = new ResponseDTO<>();
		responseList.add("23587986034785");
		responseDTO.setStatus("true");
		responseDTO.setErr(null);
		responseDTO.setResponse(responseList);
		ResponseEntity<ResponseDTO> resp = new ResponseEntity<>(responseDTO, HttpStatus.OK);
		Mockito.when(restTemplate.exchange(ArgumentMatchers.anyString(), ArgumentMatchers.any(HttpMethod.class),
				ArgumentMatchers.any(), ArgumentMatchers.<Class<ResponseDTO>>any())).thenReturn(resp);

		PreRegistrationIdsDTO preRegResponse = new PreRegistrationIdsDTO();
		Map<String,String> listOfPreIds = new HashMap<>();
		listOfPreIds.put("23587986034785","2018-12-28T13:04:53.117Z");
		
		preRegResponse.setPreRegistrationIds(listOfPreIds);
		preRegResponse.setTransactionId("09876543");
		
		MainRequestDTO<DataSyncRequestDTO> mainReq = new MainRequestDTO<>();
		mainReq.setId("mosip.pre-registration.datasync");
		mainReq.setVer("1.0");
		mainReq.setReqTime(new Date());
		mainReq.setRequest(dataSyncRequestDTO);
		
		DataSyncResponseDTO<PreRegistrationIdsDTO> actualRes = dataSyncService.retrieveAllPreRegIds(mainReq);
		assertEquals(actualRes.getResponse().getPreRegistrationIds().get(0),
				dataSyncResponseDTO.getResponse().getPreRegistrationIds().get(0));
	}

//	@SuppressWarnings({ "unchecked", "rawtypes" })
//	@Test(expected = RecordNotFoundForDateRange.class)
//	public void retrieveAllPreRegIdsFailure() throws ParseException {
//		RecordNotFoundForDateRange exception = new RecordNotFoundForDateRange(ErrorCodes.PRG_DATA_SYNC_001.toString(),
//				ErrorMessages.RECORDS_NOT_FOUND_FOR_DATE_RANGE.toString());
//
//		RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
//		Mockito.when(restTemplateBuilder.build()).thenReturn(restTemplate);
//
//		dataSyncRequestDTO.setFromDate("");
//		dataSyncRequestDTO.setToDate("");
//
//		List<String> responseList = new ArrayList<String>();
//		ResponseDTO responseDTO = new ResponseDTO<>();
//		responseList.add("23587986034785");
//		responseDTO.setStatus("true");
//		responseDTO.setErr(null);
//		responseDTO.setResponse(responseList);
//		ResponseEntity<ResponseDTO> resp = new ResponseEntity<>(responseDTO, HttpStatus.OK);
//		Mockito.when(restTemplate.exchange(ArgumentMatchers.anyString(), ArgumentMatchers.any(HttpMethod.class),
//				ArgumentMatchers.any(), ArgumentMatchers.<Class<ResponseDTO>>any())).thenReturn(resp);
//
//		DataSyncResponseDTO<PreRegistrationIdsDTO> responseDSDTO = dataSyncService
//				.retrieveAllPreRegid(dataSyncRequestDTO);
//		assertEquals(responseDSDTO.getErr().get(0).toString(), errlist.get(0).toString());
//	}
	
	//
	// @Test(expected = TablenotAccessibleException.class)
	// public void retriveAllPreRegIdTableNotAccessCheck() throws ParseException {
	// TablenotAccessibleException exception = new TablenotAccessibleException();
	// Mockito.when(dataSyncRepo.findBycreateDateTimeBetween(ArgumentMatchers.any(),
	// ArgumentMatchers.any()))
	// .thenThrow(exception);
	// dataSyncService.retrieveAllPreRegid(dataSyncRequestDTO);
	//
	// }

}
