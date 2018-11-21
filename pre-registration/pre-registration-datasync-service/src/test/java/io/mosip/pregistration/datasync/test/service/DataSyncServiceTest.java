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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.pregistration.datasync.code.StatusCodes;
import io.mosip.pregistration.datasync.dto.ExceptionJSONInfo;
import io.mosip.pregistration.datasync.dto.ResponseDTO;
import io.mosip.pregistration.datasync.entity.DocumentEntity;
import io.mosip.pregistration.datasync.entity.PreRegistrationEntity;
import io.mosip.pregistration.datasync.exception.DataSyncRecordNotFoundException;
import io.mosip.pregistration.datasync.repository.DataSyncRepository;
import io.mosip.pregistration.datasync.service.DataSyncService;

/**
 * @author M1046129
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DataSyncServiceTest {

	@Mock
	private DataSyncRepository dataSyncRepository;

	@InjectMocks
	private DataSyncService dataSyncService;

	String preid = "";

	List<ExceptionJSONInfo> errlist = new ArrayList<>();
	ExceptionJSONInfo exceptionJSONInfo = new ExceptionJSONInfo("", "");
	String status = "true";
	@SuppressWarnings("rawtypes")
	ResponseDTO responseDto = new ResponseDTO<>();
	Timestamp resTime = new Timestamp(System.currentTimeMillis());
	PreRegistrationEntity preRegistrationEntity = new PreRegistrationEntity();
	List<DocumentEntity> docEntity = new ArrayList<>();
	private DocumentEntity entity;

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
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void successGetPreRegistration() {

		Mockito.when(dataSyncRepository.findDemographyByPreId(preid)).thenReturn(preRegistrationEntity);
		Mockito.when(dataSyncRepository.findDocumentByPreId(preid)).thenReturn(docEntity);
		ResponseDTO response = new ResponseDTO<>();
		try {
			response = dataSyncService.getPreRegistration(preid);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(response.getStatus(), responseDto.getStatus());
	}

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

}
