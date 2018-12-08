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
import java.util.TimeZone;

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

import io.mosip.kernel.core.util.DateUtils;
import io.mosip.pregistration.datasync.code.StatusCodes;
import io.mosip.pregistration.datasync.dto.DataSyncDTO;
import io.mosip.pregistration.datasync.dto.DataSyncRequestDTO;
import io.mosip.pregistration.datasync.dto.DataSyncResponseDTO;
import io.mosip.pregistration.datasync.dto.ExceptionJSONInfo;
import io.mosip.pregistration.datasync.dto.PreRegArchiveDTO;
import io.mosip.pregistration.datasync.dto.PreRegistrationIdsDTO;
import io.mosip.pregistration.datasync.dto.ReverseDataSyncDTO;
import io.mosip.pregistration.datasync.dto.ReverseDataSyncRequestDTO;
import io.mosip.pregistration.datasync.entity.DocumentEntity;
import io.mosip.pregistration.datasync.entity.InterfaceDataSyncTablePK;
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
	DataSyncResponseDTO<Object> dataSyncResponseDTO = new DataSyncResponseDTO<>();
	Timestamp resTime = new Timestamp(System.currentTimeMillis());
	PreRegistrationEntity demography = new PreRegistrationEntity();
	List<DocumentEntity> docEntityList = new ArrayList<>();
	DataSyncDTO requestDto = new DataSyncDTO();
	PreRegistrationIdsDTO responseDataSyncDTO = new PreRegistrationIdsDTO();
	List<PreRegistrationEntity> userDetails = new ArrayList<>();
	DataSyncRequestDTO dataSyncRequestDTO = new DataSyncRequestDTO();
	List<ExceptionJSONInfo> ex = new ArrayList<>();
	private JSONObject jsonTestObject;
	ReverseDataSyncDTO reverseDto = new ReverseDataSyncDTO();

	byte[] pFile = null;

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

		List<Object> responseList = new ArrayList<>();
		dataSyncResponseDTO.setStatus("true");
		errlist.add(exceptionJSONInfo);
		dataSyncResponseDTO.setErr(errlist);
		dataSyncResponseDTO.setResTime(resTime);
		dataSyncResponseDTO.setResponse(responseList);

		ArrayList<String> list = new ArrayList<>();
		list.add("1234");
		responseDataSyncDTO.setPreRegistrationIds(list);
		responseDataSyncDTO.setTransactionId("1111");

		PreRegistrationEntity preRegistrationEntity = new PreRegistrationEntity();
		preRegistrationEntity.setCreateDateTime(times);
		preRegistrationEntity.setPreRegistrationId("1234");
		userDetails.add(preRegistrationEntity);

		Date date1 = dateFormat.parse("08/10/2018");
		Date date2 = dateFormat.parse("01/11/2018");
		long time1 = date1.getTime();
		long time2 = date2.getTime();
		Timestamp from = new Timestamp(time1);
		Timestamp to = new Timestamp(time2);

		dataSyncRequestDTO.setRegClientId("59276903416082");
		dataSyncRequestDTO.setFromDate(from);
		dataSyncRequestDTO.setToDate(to);
		dataSyncRequestDTO.setUserId("256752365832");

		dataSyncResponseDTO.setResponse(responseDataSyncDTO);
		dataSyncResponseDTO.setErr(ex);
		dataSyncResponseDTO.setStatus("True");
		dataSyncResponseDTO.setResTime(new Timestamp(System.currentTimeMillis()));

		List<String> preRegIds = new ArrayList<String>();
		preRegIds.add("12345");
		ReverseDataSyncRequestDTO request = new ReverseDataSyncRequestDTO();
		request.setPre_registration_ids(preRegIds);

		reverseDto.setRequest(request);
		reverseDto.setReqTime(resTime);
	}

	@Test
	public void successGetPreRegistration() throws Exception {
		PreRegArchiveDTO preRegArchiveDTO = new PreRegArchiveDTO();
		dataSyncResponseDTO = new DataSyncResponseDTO<>();
		dataSyncResponseDTO.setStatus("true");

		preRegArchiveDTO.setZipBytes(pFile);
		preRegArchiveDTO.setFileName(demography.getPreRegistrationId().toString());
		dataSyncResponseDTO.setResponse(preRegArchiveDTO);
		dataSyncResponseDTO.setErr(errlist);
		dataSyncResponseDTO.setResTime(resTime);

		Mockito.when(dataSyncRepository.findDemographyByPreId(preid)).thenReturn(demography);
		Mockito.when(dataSyncRepository.findDocumentByPreId(preid)).thenReturn(docEntityList);

		DataSyncResponseDTO<PreRegArchiveDTO> response = dataSyncService.getPreRegistration(preid);

		assertEquals(response.getResponse().getFileName(), preRegArchiveDTO.getFileName());
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
	public void retrieveAllPreRegIdsWithTodateTest() throws ParseException {

		Mockito.when(dataSyncRepo.findBycreateDateTimeBetween(ArgumentMatchers.any(), ArgumentMatchers.any()))
				.thenReturn(userDetails);

		DataSyncResponseDTO<PreRegistrationIdsDTO> actualRes = dataSyncService.retrieveAllPreRegid(dataSyncRequestDTO);
		assertEquals(actualRes.getStatus(), dataSyncResponseDTO.getStatus());
	}

	@Test
	public void retrieveAllPreRegIdsWithoutTodateTest() throws ParseException {
		
		Date fromDate = dataSyncRequestDTO.getFromDate();
		
		final String ISO_FORMAT = "yyyy-MM-dd HH:mm:ss";
		final SimpleDateFormat sdf = new SimpleDateFormat(ISO_FORMAT);
		final TimeZone utc = TimeZone.getTimeZone("UTC");
		sdf.setTimeZone(utc);
		Date myDate = DateUtils.parseDefaultUTCToDate(sdf.format(fromDate).toString());

		DataSyncRequestDTO dataSyncRequestDTO1 = new DataSyncRequestDTO();
		dataSyncRequestDTO1.setRegClientId("59276903416082");
		dataSyncRequestDTO1.setFromDate(myDate);
		dataSyncRequestDTO1.setToDate(null);
		dataSyncRequestDTO1.setUserId("256752365832");


		Mockito.when(dataSyncRepo.findBycreateDateTimeBetween(
				ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(userDetails);
		
		DataSyncResponseDTO<PreRegistrationIdsDTO> actualRes = dataSyncService.retrieveAllPreRegid(dataSyncRequestDTO1);
		assertEquals(actualRes.getStatus(), dataSyncResponseDTO.getStatus());
	}

	@Test(expected = RecordNotFoundForDateRange.class)
	public void retrieveAllPreRegIdsFailure() throws ParseException {
		RecordNotFoundForDateRange exception = new RecordNotFoundForDateRange();
		Mockito.when(dataSyncRepo.findBycreateDateTimeBetween(null, null)).thenThrow(exception);
		DataSyncResponseDTO<PreRegistrationIdsDTO> responseDSDTO = dataSyncService
				.retrieveAllPreRegid(dataSyncRequestDTO);
		assertEquals(responseDSDTO.getErr().get(0).toString(), ex.get(0).toString());
	}

	@Test(expected = TablenotAccessibleException.class)
	public void retriveAllPreRegIdTableNotAccessCheck() throws ParseException {
		TablenotAccessibleException exception = new TablenotAccessibleException();
		Mockito.when(dataSyncRepo.findBycreateDateTimeBetween(ArgumentMatchers.any(), ArgumentMatchers.any()))
				.thenThrow(exception);
		dataSyncService.retrieveAllPreRegid(dataSyncRequestDTO);

	}

	@Test
	public void storeConsumePreIdsSuccessTest() {

		DataSyncResponseDTO<String> expRes = new DataSyncResponseDTO<>();
		expRes.setErr(null);
		expRes.setResponse(StatusCodes.PRE_REGISTRATION_IDS_STORED_SUCESSFULLY.toString());
		ReverseDataSyncEntity reverseDataSyncEntity = new ReverseDataSyncEntity();
		PreRegistrationProcessedEntity processedEntity = new PreRegistrationProcessedEntity();
		System.out.println("expRes:" + expRes);
		dataSyncResponseDTO.setResponse(StatusCodes.PRE_REGISTRATION_IDS_STORED_SUCESSFULLY.toString());
		dataSyncResponseDTO.setStatus("true");
		dataSyncResponseDTO.setResTime(resTime);
		dataSyncResponseDTO.setErr(errlist);

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
		System.out.println("actRes:" + actRes);
		assertEquals(actRes.getResponse().toString(), expRes.getResponse().toString());

	}

	@Test(expected = ReverseDataFailedToStoreException.class)
	public void reverseDataSyncFailureTest() {
		ReverseDataSyncEntity reverseDataSyncEntity = new ReverseDataSyncEntity();
		List<ReverseDataSyncEntity> savedList = new ArrayList<>();
		reverseDataSyncEntity.setLangCode("AR");
		reverseDataSyncEntity.setCrBy("5766477466");

		InterfaceDataSyncTablePK ipprlst_PK = new InterfaceDataSyncTablePK();
		ipprlst_PK.setPreregId("12345");
		ipprlst_PK.setReceivedDtimes(resTime);

		reverseDataSyncEntity.setIpprlst_PK(ipprlst_PK);

		savedList.add(reverseDataSyncEntity);
		ReverseDataFailedToStoreException exception = new ReverseDataFailedToStoreException();

		// ReverseDataSyncRequestDTO requestDTO=new ReverseDataSyncRequestDTO();

		Mockito.when(dataSyncRepository.saveAll(null)).thenThrow(exception);
		Mockito.when(reverseDataSyncRepo.existsById(ArgumentMatchers.any())).thenReturn(true);
		Mockito.when(reverseDataSyncRepo.save(ArgumentMatchers.any())).thenReturn(true);
		DataSyncResponseDTO<String> actRes = dataSyncService.storeConsumedPreRegistrations(reverseDto);
		System.out.println("actRes:" + actRes);
		assertEquals(actRes.getErr().get(0).toString(), ex.get(0).toString());
	}

}
