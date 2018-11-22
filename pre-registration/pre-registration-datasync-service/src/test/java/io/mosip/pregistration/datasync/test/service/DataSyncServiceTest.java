package io.mosip.pregistration.datasync.test.service;

import static org.junit.Assert.*;

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
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.pregistration.datasync.code.StatusCodes;
import io.mosip.pregistration.datasync.dto.DataSyncDTO;
import io.mosip.pregistration.datasync.dto.DataSyncRequestDTO;
import io.mosip.pregistration.datasync.dto.ExceptionJSONInfo;
import io.mosip.pregistration.datasync.dto.ResponseDTO;
import io.mosip.pregistration.datasync.dto.ResponseDataSyncDTO;
import io.mosip.pregistration.datasync.dto.ReverseDataSyncDTO;
import io.mosip.pregistration.datasync.dto.ReverseDataSyncRequestDTO;
import io.mosip.pregistration.datasync.entity.DocumentEntity;
import io.mosip.pregistration.datasync.entity.Ipprlst_PK;
import io.mosip.pregistration.datasync.entity.PreRegistrationEntity;
import io.mosip.pregistration.datasync.entity.PreRegistrationProcessedEntity;
import io.mosip.pregistration.datasync.entity.ReverseDataSyncEntity;
import io.mosip.pregistration.datasync.exception.DataSyncRecordNotFoundException;
import io.mosip.pregistration.datasync.exception.RecordNotFoundForDateRange;
import io.mosip.pregistration.datasync.exception.ReverseDataFailedToStoreException;
import io.mosip.pregistration.datasync.exception.ZipFileCreationException;
import io.mosip.pregistration.datasync.repository.DataSyncRepo;
import io.mosip.pregistration.datasync.repository.DataSyncRepository;
import io.mosip.pregistration.datasync.repository.ReverseDataSyncRepo;
import io.mosip.pregistration.datasync.service.DataSyncService;
import io.mosip.preregistration.core.exceptions.TablenotAccessibleException;

/**
 * @author M1046129
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DataSyncServiceTest {

	@Mock
	private DataSyncRepository dataSyncRepository;

	@Mock
	private DataSyncRepo dataSyncRepo;

	@Mock
	private ReverseDataSyncRepo reverseDataSyncRepo;

	@InjectMocks
	private DataSyncService dataSyncService;
	

	String preid = "";
	List<ExceptionJSONInfo> errlist = new ArrayList<>();
	ExceptionJSONInfo exceptionJSONInfo = new ExceptionJSONInfo("", "");
	@SuppressWarnings("rawtypes")
	ResponseDTO responseDto = new ResponseDTO<>();
	Timestamp resTime = new Timestamp(System.currentTimeMillis());
	PreRegistrationEntity demography = new PreRegistrationEntity();
	List<DocumentEntity> docEntityList = new ArrayList<>();
	DataSyncDTO requestDto = new DataSyncDTO();
	ResponseDataSyncDTO responseDataSyncDTO = new ResponseDataSyncDTO();
	List<PreRegistrationEntity> userDetails = new ArrayList<>();
	DataSyncRequestDTO dataSyncRequestDTO = new DataSyncRequestDTO();
	List<ResponseDataSyncDTO> responseDataSyncList = new ArrayList<>();
	List<ExceptionJSONInfo> ex = new ArrayList<>();
	private JSONObject jsonTestObject;
	ReverseDataSyncDTO reverseDto = new ReverseDataSyncDTO();

	byte[] pFile = null;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Before
	public void setUp() throws URISyntaxException, IOException, org.json.simple.parser.ParseException, ParseException {
		preid = "75391783729406";

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
		demography = new PreRegistrationEntity();
		demography.setCr_appuser_id("Rajath");
		demography.setCreateDateTime(times);
		demography.setStatusCode("SAVE");
		demography.setLangCode("12L");
		demography.setPreRegistrationId(preid);
		demography.setApplicantDetailJson(jsonTestObject.toString().getBytes("UTF-8"));

		byte[] dFile = null;

		File file = new File(classLoader.getResource("Doc.pdf").getFile());
		uri = new URI(classLoader.getResource("Doc.pdf").getFile().trim().replaceAll("\\u0020", "%20"));
		file = new File(uri.getPath());
		dFile = Files.readAllBytes(file.toPath());

		DocumentEntity documentEntity = new DocumentEntity(1, "75391783729406", "Doc.pdf", "address", "POA", "PDF",
				dFile, "Draft", "ENG", "Jagadishwari", new Timestamp(System.currentTimeMillis()), "Jagadishwari",
				new Timestamp(System.currentTimeMillis()));

		docEntityList.add(documentEntity);

		List responseList = new ArrayList<>();
		responseDto.setStatus("true");
		errlist.add(exceptionJSONInfo);
		responseDto.setErr(errlist);
		responseDto.setResTime(resTime);
		responseDto.setResponse(responseList);

		ArrayList<String> list = new ArrayList<>();
		list.add("1234");
		responseDataSyncDTO.setPreRegistrationIds(list);
		responseDataSyncDTO.setTransactionId("1111");

		PreRegistrationEntity preRegistrationEntity = new PreRegistrationEntity();
		preRegistrationEntity.setCreateDateTime(times);
		preRegistrationEntity.setPreRegistrationId("1234");
		userDetails.add(preRegistrationEntity);

		Date date1 = dateFormat.parse("01/01/2011");
		Date date2 = dateFormat.parse("01/01/2013");
		long time1 = date1.getTime();
		long time2 = date2.getTime();
		Timestamp from = new Timestamp(time1);
		Timestamp to = new Timestamp(time2);

		dataSyncRequestDTO.setRegClientId("59276903416082");
		dataSyncRequestDTO.setFromDate(from);
		dataSyncRequestDTO.setToDate(to);
		dataSyncRequestDTO.setUserId("Officer");
		responseDataSyncList.add(responseDataSyncDTO);
		responseDto.setResponse(responseDataSyncList);
		responseDto.setErr(ex);
		responseDto.setStatus("True");
		responseDto.setResTime(new Timestamp(System.currentTimeMillis()));

		List<String> preRegIds = new ArrayList<String>();
		preRegIds.add("12345");
		ReverseDataSyncRequestDTO request = new ReverseDataSyncRequestDTO();
		request.setPre_registration_ids(preRegIds);

		reverseDto.setRequest(request);
		reverseDto.setId("12345872876387");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void successGetPreRegistration() throws Exception {
		List responseList = new ArrayList<>();
		responseDto = new ResponseDTO<>();
		responseDto.setStatus("true");

		responseList.add(pFile);
		responseList.add(demography.getPreRegistrationId().toString());
		responseDto.setResponse(responseList);
		responseDto.setErr(errlist);
		responseDto.setResTime(resTime);

		Mockito.when(dataSyncRepository.findDemographyByPreId(preid)).thenReturn(demography);
		Mockito.when(dataSyncRepository.findDocumentByPreId(preid)).thenReturn(docEntityList);

		ResponseDTO response = dataSyncService.getPreRegistration(preid);

		assertEquals(response.getResponse().get(1), responseDto.getResponse().get(1));
	}

	@Test(expected = DataSyncRecordNotFoundException.class)
	public void failureGetPreRegistration() throws Exception {
		DataSyncRecordNotFoundException exception = new DataSyncRecordNotFoundException(
				StatusCodes.RECORDS_NOT_FOUND_FOR_REQUESTED_PREREGID.toString());
		Mockito.when(dataSyncRepository.findDemographyByPreId(null)).thenThrow(exception);
		dataSyncService.getPreRegistration(" ");
	}

	@Test(expected = ZipFileCreationException.class)
	public void failurezipcreation() throws Exception {
		ZipFileCreationException exception = new ZipFileCreationException(
				StatusCodes.FAILED_TO_CREATE_A_ZIP_FILE.toString());
		demography.setApplicantDetailJson(null);
		Mockito.when(dataSyncRepository.findDemographyByPreId(preid)).thenReturn(demography);
		Mockito.when(dataSyncRepository.findDocumentByPreId(preid)).thenReturn(docEntityList);
		Mockito.when(dataSyncService.getPreRegistration(preid)).thenThrow(exception);
	}

	@Test
	public void retrieveAllPreRegIdsSuccess() throws ParseException {

		Mockito.when(dataSyncRepo.findBycreateDateTimeBetween(ArgumentMatchers.any(), ArgumentMatchers.any()))
				.thenReturn(userDetails);

		ResponseDTO<ResponseDataSyncDTO> actualRes = dataSyncService.retrieveAllPreRegid(dataSyncRequestDTO);
		assertEquals(actualRes.getStatus(), responseDto.getStatus());
	}

	@Test(expected = RecordNotFoundForDateRange.class)
	public void retrieveAllPreRegIdsFailure() {
		RecordNotFoundForDateRange exception = new RecordNotFoundForDateRange();
		Mockito.when(dataSyncRepo.findBycreateDateTimeBetween(null, null))
				.thenThrow(exception);
		ResponseDTO<ResponseDataSyncDTO> responseDSDTO = dataSyncService.retrieveAllPreRegid(dataSyncRequestDTO);
		assertEquals(responseDSDTO.getErr().get(0).toString(), ex.get(0).toString());
	}

	@Test(expected = TablenotAccessibleException.class)
	public void retriveAllPreRegIdTableNotAccessCheck() {
		TablenotAccessibleException exception = new TablenotAccessibleException();
		Mockito.when(dataSyncRepo.findBycreateDateTimeBetween(ArgumentMatchers.any(), ArgumentMatchers.any()))
				.thenThrow(exception);
		dataSyncService.retrieveAllPreRegid(dataSyncRequestDTO);

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void storeConsumePreIdsSuccessTest() {

		List responseList = new ArrayList<>();
		responseList.add(StatusCodes.PRE_REGISTRATION_IDS_STORED_SUCESSFULLY.toString());

		ResponseDTO<ReverseDataSyncDTO> expRes = new ResponseDTO<>();
		expRes.setErr(null);
		expRes.setResponse(responseList);
		ReverseDataSyncEntity reverseDataSyncEntity = new ReverseDataSyncEntity();
		PreRegistrationProcessedEntity processedEntity=new PreRegistrationProcessedEntity();
		System.out.println("expRes:" + expRes);
		responseDto.setResponse(responseList);
		responseDto.setStatus("true");
		responseDto.setResTime(resTime);
		responseDto.setErr(errlist);

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
		ResponseDTO<ReverseDataSyncDTO> actRes = dataSyncService.storeConsumedPreRegistrations(reverseDto);
		System.out.println("actRes:" + actRes);
		assertEquals(actRes.getResponse().toString(), expRes.getResponse().toString());

	}

	@Test(expected = ReverseDataFailedToStoreException.class)
	public void reverseDataSyncFailureTest() {
		ReverseDataSyncEntity reverseDataSyncEntity = new ReverseDataSyncEntity();
		List<ReverseDataSyncEntity> savedList = new ArrayList<>();
		reverseDataSyncEntity.setLangCode("AR");
		reverseDataSyncEntity.setCrBy("5766477466");
		
		Ipprlst_PK ipprlst_PK=new Ipprlst_PK();
		ipprlst_PK.setPrereg_id("12345");
		ipprlst_PK.setReceived_dtimes(resTime);
		
		reverseDataSyncEntity.setIpprlst_PK(ipprlst_PK);
		
		savedList.add(reverseDataSyncEntity);
		ReverseDataFailedToStoreException exception = new ReverseDataFailedToStoreException();

		Mockito.when(dataSyncRepository.saveAll(null)).thenThrow(exception);
		Mockito.when(reverseDataSyncRepo.existsById(ArgumentMatchers.any())).thenReturn(true);
		Mockito.when(reverseDataSyncRepo.save(ArgumentMatchers.any())).thenReturn(true);
		ResponseDTO<ReverseDataSyncDTO> actRes = dataSyncService.storeConsumedPreRegistrations(reverseDto);
		System.out.println("actRes:" + actRes);
		assertEquals(actRes.getErr().get(0).toString(), ex.get(0).toString());
	}

}
