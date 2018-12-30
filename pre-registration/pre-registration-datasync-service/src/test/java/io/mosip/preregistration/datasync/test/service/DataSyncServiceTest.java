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

import io.mosip.preregistration.datasync.dto.DataSyncRequestDTO;
import io.mosip.preregistration.datasync.dto.ExceptionJSONInfoDTO;
import io.mosip.preregistration.datasync.dto.MainRequestDTO;
import io.mosip.preregistration.datasync.dto.MainResponseDTO;
import io.mosip.preregistration.datasync.dto.PreRegistrationIdsDTO;
import io.mosip.preregistration.datasync.dto.ReverseDataSyncRequestDTO;
import io.mosip.preregistration.datasync.entity.InterfaceDataSyncEntity;
import io.mosip.preregistration.datasync.entity.InterfaceDataSyncTablePK;
import io.mosip.preregistration.datasync.entity.ProcessedPreRegEntity;
import io.mosip.preregistration.datasync.errorcodes.ErrorCodes;
import io.mosip.preregistration.datasync.errorcodes.ErrorMessages;
import io.mosip.preregistration.datasync.exception.ReverseDataFailedToStoreException;
import io.mosip.preregistration.datasync.repository.InterfaceDataSyncRepo;
import io.mosip.preregistration.datasync.repository.ProcessedDataSyncRepo;
import io.mosip.preregistration.datasync.service.DataSyncService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DataSyncServiceTest {

	@Mock
	private InterfaceDataSyncRepo interfaceDataSyncRepo;

	@Mock
	private ProcessedDataSyncRepo processedDataSyncRepo;

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
	MainResponseDTO<PreRegistrationIdsDTO> dataSyncResponseDTO = new MainResponseDTO<>();
	MainResponseDTO<String> storeResponseDTO = new MainResponseDTO<>();
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

		MainResponseDTO<String> expRes = new MainResponseDTO<>();
		expRes.setErr(null);
		expRes.setResponse(ErrorMessages.PRE_REGISTRATION_IDS_STORED_SUCESSFULLY.toString());
		InterfaceDataSyncEntity interfaceDataSyncEntity = new InterfaceDataSyncEntity();
		ProcessedPreRegEntity processedEntity = new ProcessedPreRegEntity();
		storeResponseDTO.setResponse(ErrorMessages.PRE_REGISTRATION_IDS_STORED_SUCESSFULLY.toString());
		storeResponseDTO.setStatus("true");
		storeResponseDTO.setResTime(resTime);
		storeResponseDTO.setErr(null);

		interfaceDataSyncEntity.setLangCode("AR");
		interfaceDataSyncEntity.setCrBy("5766477466");
		processedEntity.setLangCode("AR");
		processedEntity.setCrBy("5766477466");
		processedEntity.setStatusCode("");
		List<InterfaceDataSyncEntity> savedList = new ArrayList<>();
		savedList.add(interfaceDataSyncEntity);

		Mockito.when(interfaceDataSyncRepo.saveAll(ArgumentMatchers.any())).thenReturn(savedList);
		Mockito.when(processedDataSyncRepo.existsById(preid)).thenReturn(Mockito.anyBoolean());
		Mockito.when(processedDataSyncRepo.save(processedEntity)).thenReturn(processedEntity);
		MainResponseDTO<String> actRes = dataSyncService.storeConsumedPreRegistrations(reverseDto);
		assertEquals(actRes.getResponse().toString(), expRes.getResponse().toString());

	}

	@Test(expected = ReverseDataFailedToStoreException.class)
	public void reverseDataSyncFailureTest() {
		InterfaceDataSyncEntity interfaceDataSyncEntity = new InterfaceDataSyncEntity();
		List<InterfaceDataSyncEntity> savedList = new ArrayList<>();
		interfaceDataSyncEntity.setLangCode("AR");
		interfaceDataSyncEntity.setCrBy("5766477466");

		MainResponseDTO<String> expRes = new MainResponseDTO<>();
		expRes.setErr(null);
		expRes.setStatus("false");

		InterfaceDataSyncTablePK ipprlst_PK = new InterfaceDataSyncTablePK();
		ipprlst_PK.setPreregId("23587986034785");
		ipprlst_PK.setReceivedDtimes(new Timestamp(System.currentTimeMillis()));

		interfaceDataSyncEntity.setIpprlst_PK(ipprlst_PK);

		savedList.add(interfaceDataSyncEntity);
		ReverseDataFailedToStoreException exception = new ReverseDataFailedToStoreException(
				ErrorCodes.PRG_REVESE_DATA_SYNC_001.toString(),
				ErrorMessages.FAILED_TO_STORE_PRE_REGISTRATION_IDS.toString());

		List<String> preRegIds = new ArrayList<String>();
		ReverseDataSyncRequestDTO request = new ReverseDataSyncRequestDTO();
		request.setPre_registration_ids(preRegIds);

		reverseDto.setReqTime(new Date());
		reverseDto.setRequest(request);

		Mockito.when(interfaceDataSyncRepo.saveAll(null)).thenThrow(exception);
		Mockito.when(processedDataSyncRepo.existsById(ArgumentMatchers.any())).thenReturn(true);
		Mockito.when(processedDataSyncRepo.save(ArgumentMatchers.any())).thenReturn(true);
		MainResponseDTO<String> actRes = dataSyncService.storeConsumedPreRegistrations(reverseDto);
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
		MainResponseDTO responseDTO = new MainResponseDTO<>();
		responseList.add("23587986034785");
		responseDTO.setStatus("true");
		responseDTO.setErr(null);
		responseDTO.setResponse(responseList);
		ResponseEntity<MainResponseDTO> resp = new ResponseEntity<>(responseDTO, HttpStatus.OK);
		Mockito.when(restTemplate.exchange(ArgumentMatchers.anyString(), ArgumentMatchers.any(HttpMethod.class),
				ArgumentMatchers.any(), ArgumentMatchers.<Class<MainResponseDTO>>any())).thenReturn(resp);

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
		MainResponseDTO<PreRegistrationIdsDTO> actualRes = dataSyncService.retrieveAllPreRegIds(mainReq);
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
		MainResponseDTO responseDTO = new MainResponseDTO<>();
		responseList.add("23587986034785");
		responseDTO.setStatus("true");
		responseDTO.setErr(null);
		responseDTO.setResponse(responseList);
		ResponseEntity<MainResponseDTO> resp = new ResponseEntity<>(responseDTO, HttpStatus.OK);
		Mockito.when(restTemplate.exchange(ArgumentMatchers.anyString(), ArgumentMatchers.any(HttpMethod.class),
				ArgumentMatchers.any(), ArgumentMatchers.<Class<MainResponseDTO>>any())).thenReturn(resp);

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
		
		MainResponseDTO<PreRegistrationIdsDTO> actualRes = dataSyncService.retrieveAllPreRegIds(mainReq);
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
