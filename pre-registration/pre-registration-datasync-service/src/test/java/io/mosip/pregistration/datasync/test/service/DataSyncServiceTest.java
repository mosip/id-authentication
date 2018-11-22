package io.mosip.pregistration.datasync.test.service;

import static org.junit.Assert.*;

import java.io.File;
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
import io.mosip.pregistration.datasync.entity.DocumentEntity;
import io.mosip.pregistration.datasync.entity.PreRegistrationEntity;
import io.mosip.pregistration.datasync.exception.DataSyncRecordNotFoundException;
import io.mosip.pregistration.datasync.exception.RecordNotFoundForDateRange;
import io.mosip.pregistration.datasync.repository.DataSyncRepo;
import io.mosip.pregistration.datasync.repository.DataSyncRepository;
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

	@InjectMocks
	private DataSyncService dataSyncService;

	String preid = "";

	List<ExceptionJSONInfo> errlist = new ArrayList<>();
	ExceptionJSONInfo exceptionJSONInfo = new ExceptionJSONInfo("", "");
	String status = "True";
	@SuppressWarnings("rawtypes")
	ResponseDTO responseDto = new ResponseDTO<>();
	Timestamp resTime = new Timestamp(System.currentTimeMillis());
	PreRegistrationEntity preRegistrationEntity = new PreRegistrationEntity();
	List<DocumentEntity> docEntity = new ArrayList<>();
	private DocumentEntity entity;
	DataSyncDTO requestDto = new DataSyncDTO();
	
	ResponseDataSyncDTO responseDataSyncDTO = new ResponseDataSyncDTO();
	List<PreRegistrationEntity> userDetails = new ArrayList<>();
	DataSyncRequestDTO dataSyncRequestDTO = new DataSyncRequestDTO();
	List<ResponseDataSyncDTO> responseDataSyncList=new ArrayList<>();
	
	List<ExceptionJSONInfo> ex =new ArrayList<>();
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Before
	public void setUp() throws ParseException, URISyntaxException, IOException {
		preid = "75391783729406";
		preRegistrationEntity = new PreRegistrationEntity();

		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Date date = dateFormat.parse("08/10/2018");
		long time = date.getTime();
		Timestamp times = new Timestamp(time);
		preRegistrationEntity.setCr_appuser_id("Rajath");
		preRegistrationEntity.setCreateDateTime(times);
		preRegistrationEntity.setStatusCode("SAVE");
		preRegistrationEntity.setLangCode("12L");
		preRegistrationEntity.setPreRegistrationId(preid);
		byte[] bFile = null;
		ClassLoader classLoader = getClass().getClassLoader();

		File file = new File(classLoader.getResource("Doc.pdf").getFile());

		URI uri = new URI(classLoader.getResource("Doc.pdf").getFile().trim().replaceAll("\\u0020", "%20"));
		file = new File(uri.getPath());
		bFile = Files.readAllBytes(file.toPath());

		entity = new DocumentEntity(1, "75391783729406", "Doc.pdf", "address", "POA", "PDF", bFile, "Draft", "ENG",
				"Jagadishwari", new Timestamp(System.currentTimeMillis()), "Jagadishwari",
				new Timestamp(System.currentTimeMillis()));

		docEntity.add(entity);

		List responseList = new ArrayList<>();
		responseDto.setStatus(status);
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
	}

//	@SuppressWarnings("rawtypes")
//	@Test
//	public void successGetPreRegistration() {
//
//		Mockito.when(dataSyncRepository.findDemographyByPreId(preid)).thenReturn(preRegistrationEntity);
//		Mockito.when(dataSyncRepository.findDocumentByPreId(preid)).thenReturn(docEntity);
//		ResponseDTO response = new ResponseDTO<>();
//		try {
//			response = dataSyncService.getPreRegistration(preid);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		assertEquals(response.getStatus(), responseDto.getStatus());
//	}

	@Test(expected = DataSyncRecordNotFoundException.class)
	public void failureGetDemographyForPreIdTest() throws Exception {
		DataSyncRecordNotFoundException exception = new DataSyncRecordNotFoundException(
				StatusCodes.RECORDS_NOT_FOUND_FOR_REQUESTED_PREREGID.toString());
		Mockito.when(dataSyncRepository.findDemographyByPreId(" ")).thenThrow(exception);
		dataSyncService.getPreRegistration(" ");
	}

	// @Test(expected = DocumentNotFoundException.class)
	// public void failureGetDocForPreIdTest() {
	// DocumentNotFoundException exception = new
	// DocumentNotFoundException(StatusCodes.DOCUMENT_IS_MISSING.toString());
	// Mockito.when(dataSyncRepository.findDemographyByPreId(preid)).thenReturn(preRegistrationEntity);
	// Mockito.when(dataSyncRepository.findDocumentByPreId(ArgumentMatchers.any())).thenThrow(exception);
	// try {
	// dataSyncService.getPreRegistration(preid);
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }

	
	@Test
	public void retrieveAllPreRegIdsSuccess() throws ParseException {

		Mockito.when(dataSyncRepo.findBycreateDateTimeBetween(ArgumentMatchers.any(), ArgumentMatchers.any()))
				.thenReturn(userDetails);

		ResponseDTO<ResponseDataSyncDTO> actualRes = dataSyncService.retrieveAllPreRegid(dataSyncRequestDTO);
		//logger.info("ResponseDto:" + actualRes);
		System.out.println("ResponseDto:" + responseDto);
		assertEquals(actualRes.getStatus(), responseDto.getStatus());
	}

	@Test(expected = RecordNotFoundForDateRange.class)
	public void retrieveAllPreRegIdsFailure() {
		RecordNotFoundForDateRange exception=new RecordNotFoundForDateRange();
		Mockito.when(dataSyncRepo.findBycreateDateTimeBetween(ArgumentMatchers.any(), ArgumentMatchers.any()))
				.thenThrow(exception);
		ResponseDTO<ResponseDataSyncDTO> responseDSDTO = dataSyncService.retrieveAllPreRegid(dataSyncRequestDTO);
		System.out.println("ResponseDto:" + responseDto);
		assertEquals(responseDSDTO.getErr().get(0).toString(), ex.get(0).toString());
	}

	@Test(expected = TablenotAccessibleException.class)
	public void retriveAllPreRegIdTableNotAccessCheck() {
		TablenotAccessibleException exception=new TablenotAccessibleException();

		Mockito.when(dataSyncRepo.findBycreateDateTimeBetween(ArgumentMatchers.any(), ArgumentMatchers.any()))
				.thenThrow(exception);
		dataSyncService.retrieveAllPreRegid(dataSyncRequestDTO);

	}
}
