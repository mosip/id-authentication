package io.mosip.preregistration.datasync.test.service;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.core.exception.ParseException;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.preregistration.core.common.dto.BookingRegistrationDTO;
import io.mosip.preregistration.core.common.dto.DemographicResponseDTO;
import io.mosip.preregistration.core.common.dto.DocumentMultipartResponseDTO;
import io.mosip.preregistration.core.common.dto.ExceptionJSONInfoDTO;
import io.mosip.preregistration.core.common.dto.MainRequestDTO;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.common.dto.PreRegIdsByRegCenterIdResponseDTO;
import io.mosip.preregistration.core.util.AuditLogUtil;
import io.mosip.preregistration.datasync.dto.DataSyncRequestDTO;
import io.mosip.preregistration.datasync.dto.PreRegArchiveDTO;
import io.mosip.preregistration.datasync.dto.PreRegistrationIdsDTO;
import io.mosip.preregistration.datasync.dto.ReverseDataSyncRequestDTO;
import io.mosip.preregistration.datasync.dto.ReverseDatasyncReponseDTO;
import io.mosip.preregistration.datasync.repository.InterfaceDataSyncRepo;
import io.mosip.preregistration.datasync.repository.ProcessedDataSyncRepo;
import io.mosip.preregistration.datasync.service.DataSyncService;
import io.mosip.preregistration.datasync.service.util.DataSyncServiceUtil;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DataSyncServiceTest {

	@Mock
	private InterfaceDataSyncRepo interfaceDataSyncRepo;

	@Mock
	private ProcessedDataSyncRepo processedDataSyncRepo;

	@Autowired
	private DataSyncService dataSyncService;

	@MockBean
	RestTemplateBuilder restTemplateBuilder;

	/**
	 * Autowired reference for $link{DataSyncServiceUtil}
	 */
	@MockBean
	DataSyncServiceUtil serviceUtil;

	@MockBean
	AuditLogUtil auditLogUtil;

	String preid = "61720179614289";
	ExceptionJSONInfoDTO errlist = new ExceptionJSONInfoDTO();
	ExceptionJSONInfoDTO exceptionJSONInfo = new ExceptionJSONInfoDTO("", "");
	MainResponseDTO<PreRegistrationIdsDTO> dataSyncResponseDTO = new MainResponseDTO<>();
	MainResponseDTO<String> storeResponseDTO = new MainResponseDTO<>();

	byte[] pFile = null;

	@Value("${ver}")
	String versionUrl;

	@Value("${id}")
	String idUrl;

	// new
	String preId = "23587986034785";
	String fromDate = "2018-01-17 00:00:00";
	String toDate = "2019-01-17 00:00:00";
	DocumentMultipartResponseDTO multipartResponseDTOs = new DocumentMultipartResponseDTO();
	List<DocumentMultipartResponseDTO> list2 = new ArrayList<>();
	BookingRegistrationDTO bookingRegistrationDTO = new BookingRegistrationDTO();
	DemographicResponseDTO demography = new DemographicResponseDTO();
	PreRegArchiveDTO archiveDTO = new PreRegArchiveDTO();
	MainResponseDTO<PreRegArchiveDTO> mainResponseDTO = new MainResponseDTO<>();

	List<String> preregIds = new ArrayList<>();
	PreRegIdsByRegCenterIdResponseDTO preRegIdsByRegCenterIdResponseDTO = new PreRegIdsByRegCenterIdResponseDTO();
	MainRequestDTO<DataSyncRequestDTO> datasyncReqDto = new MainRequestDTO<>();
	DataSyncRequestDTO dataSyncRequestDTO = new DataSyncRequestDTO();
	PreRegistrationIdsDTO preRegistrationIdsDTO = new PreRegistrationIdsDTO();
	Map<String, String> requestMap = new HashMap<>();
	Map<String, String> requiredRequestMap = new HashMap<>();

	private String dateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	String resTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(new Date());

	MainRequestDTO<ReverseDataSyncRequestDTO> reverseRequestDTO = new MainRequestDTO<>();
	MainResponseDTO<ReverseDatasyncReponseDTO> reverseResponseDTO = new MainResponseDTO<>();
	ReverseDataSyncRequestDTO reverseDataSyncRequestDTO = new ReverseDataSyncRequestDTO();
	ReverseDatasyncReponseDTO reverseDatasyncReponse = new ReverseDatasyncReponseDTO();

	@Before
	public void setUp() throws URISyntaxException, IOException, org.json.simple.parser.ParseException, ParseException,
			java.text.ParseException {

		// ClassLoader classLoader = getClass().getClassLoader();
		// JSONParser parser = new JSONParser();
		// URI uri = new URI(
		// classLoader.getResource("pre-registration-test.json").getFile().trim().replaceAll("\\u0020",
		// "%20"));
		// File jsonFileTest = new File(uri.getPath());
		// jsonTestObject = (JSONObject) parser.parse(new FileReader(jsonFileTest));
		// pFile = Files.readAllBytes(jsonFileTest.toPath());

		// DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		// Date date = dateFormat.parse("08/10/2018");
		// long time = date.getTime();
		// Timestamp times = new Timestamp(time);

		// File file = new File(classLoader.getResource("Doc.pdf").getFile());
		// uri = new
		// URI(classLoader.getResource("Doc.pdf").getFile().trim().replaceAll("\\u0020",
		// "%20"));
		// file = new File(uri.getPath());
		// dFile = Files.readAllBytes(file.toPath());

		// DocumentEntity documentEntity = new DocumentEntity(1, "75391783729406",
		// "Doc.pdf", "address", "POA", "PDF",
		// dFile, "Draft", "ENG", "Jagadishwari", new
		// Timestamp(System.currentTimeMillis()), "Jagadishwari",
		// new Timestamp(System.currentTimeMillis()));
		//
		// docEntityList.add(documentEntity);

		// PreRegistrationEntity preRegistrationEntity = new PreRegistrationEntity();
		// preRegistrationEntity.setCreateDateTime(times);
		// preRegistrationEntity.setPreRegistrationId("23587986034785");
		// userDetails.add(preRegistrationEntity);

		// Date date1 = dateFormat.parse("08/10/2018");
		// Date date2 = dateFormat.parse("01/11/2018");
		// long time1 = date1.getTime();
		// long time2 = date2.getTime();
		// Timestamp from = new Timestamp(time1);
		// Timestamp to = new Timestamp(time2);

		List<String> preRegIds = new ArrayList<String>();
		preRegIds.add("23587986034785");
		ReverseDataSyncRequestDTO request = new ReverseDataSyncRequestDTO();
		// request.setPre_registration_ids(preRegIds);

		// reverseDto.setReqTime(new Date());
		// reverseDto.setRequest(request);

		MockitoAnnotations.initMocks(this);

		// new
		requiredRequestMap.put("id", idUrl);
		requiredRequestMap.put("ver", versionUrl);

		multipartResponseDTOs.setPrereg_id("23587986034785");
		multipartResponseDTOs.setDoc_name("Address.pdf");
		multipartResponseDTOs.setDoc_id("1234");
		multipartResponseDTOs.setDoc_cat_code("POA");

		list2.add(multipartResponseDTOs);

		bookingRegistrationDTO.setRegDate("2018-01-17 00:00:00");
		bookingRegistrationDTO.setRegistrationCenterId("1005");
		bookingRegistrationDTO.setSlotFromTime(fromDate);
		bookingRegistrationDTO.setSlotToTime(toDate);

		demography.setPreRegistrationId(preId);
		demography.setCreatedBy("Rajath");
		demography.setStatusCode("SAVE");
		demography.setLangCode("12L");
		demography.setPreRegistrationId(preid);
		demography.setDemographicDetails(null);

		archiveDTO.setZipBytes(null);
		archiveDTO.setFileName(demography.getPreRegistrationId().toString());

		mainResponseDTO.setStatus(true);
		mainResponseDTO.setResTime(resTime);
		mainResponseDTO.setResponse(archiveDTO);
		mainResponseDTO.setErr(errlist);

		preRegIds.add(preId);

		preRegIdsByRegCenterIdResponseDTO.setPreRegistrationIds(preregIds);
		preRegIdsByRegCenterIdResponseDTO.setRegistrationCenterId("1005");

		dataSyncRequestDTO.setRegClientId("1005");
		dataSyncRequestDTO.setFromDate("2018-01-17 00:00:00");
		dataSyncRequestDTO.setToDate("2018-12-17 00:00:00");
		dataSyncRequestDTO.setUserId("256752365832");

		datasyncReqDto.setId(idUrl);
		datasyncReqDto.setVer(versionUrl);
		datasyncReqDto.setReqTime(new Timestamp(System.currentTimeMillis()));
		datasyncReqDto.setRequest(dataSyncRequestDTO);

		Map<String, String> list = new HashMap<>();
		list.put(preId, "2018-12-28T13:04:53.117Z");
		preRegistrationIdsDTO.setPreRegistrationIds(list);
		preRegistrationIdsDTO.setTransactionId("1111");
		preRegistrationIdsDTO.setCountOfPreRegIds("1");

		dataSyncResponseDTO.setResponse(preRegistrationIdsDTO);
		dataSyncResponseDTO.setErr(null);
		dataSyncResponseDTO.setStatus(Boolean.TRUE);
		dataSyncResponseDTO.setResTime("2019-02-12T10:54:53.131Z");

		Date date = new Timestamp(System.currentTimeMillis());

		requestMap.put("id", datasyncReqDto.getId());
		requestMap.put("ver", datasyncReqDto.getVer());
		requestMap.put("reqTime", DateUtils.formatDate(date, dateTimeFormat));
		requestMap.put("request", datasyncReqDto.getRequest().toString());

		List<String> preRegistrationIds = new ArrayList<>();
		preRegistrationIds.add(preid);
		reverseDataSyncRequestDTO.setPreRegistrationIds(preRegistrationIds);
		reverseDataSyncRequestDTO.setLangCode("AR");
		reverseDataSyncRequestDTO.setCreatedBy("5766477466");
		reverseDataSyncRequestDTO.setUpdateBy("5766477466");

		reverseRequestDTO.setRequest(reverseDataSyncRequestDTO);
		reverseRequestDTO.setReqTime(new Timestamp(System.currentTimeMillis()));
		reverseRequestDTO.setId(idUrl);
		reverseRequestDTO.setVer(versionUrl);

		reverseDatasyncReponse.setTransactionId("1111");
		reverseDatasyncReponse.setAlreadyStoredPreRegIds(preId);
		reverseDatasyncReponse.setCountOfStoredPreRegIds("1");
	}

	@Test
	public void successGetPreRegistrationTest() throws Exception {

		Mockito.when(serviceUtil.callGetPreRegInfoRestService(Mockito.anyString())).thenReturn(demography);
		Mockito.when(serviceUtil.callGetDocRestService(Mockito.anyString())).thenReturn(list2);
		Mockito.when(serviceUtil.callGetAppointmentDetailsRestService(Mockito.anyString()))
				.thenReturn(bookingRegistrationDTO);
		Mockito.when(serviceUtil.archivingFiles(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(archiveDTO);
		MainResponseDTO<PreRegArchiveDTO> response = dataSyncService.getPreRegistrationData(preId);

		assertEquals(response.getResponse().getPreRegistrationId(), archiveDTO.getPreRegistrationId());
	}

	@Test
	public void successRetrieveAllPreRegIdTest() throws Exception {
		Mockito.when(serviceUtil.prepareRequestParamMap(Mockito.any())).thenReturn(requestMap);
		Mockito.when(serviceUtil.validateDataSyncRequest(Mockito.any())).thenReturn(true);
		Mockito.when(serviceUtil.callGetPreIdsRestService(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(preregIds);
		Mockito.when(serviceUtil.callGetPreIdsByRegCenterIdRestService(Mockito.anyString(), Mockito.any()))
				.thenReturn(preRegIdsByRegCenterIdResponseDTO);
		Mockito.when(serviceUtil.getLastUpdateTimeStamp(Mockito.any())).thenReturn(preRegistrationIdsDTO);
		MainResponseDTO<PreRegistrationIdsDTO> response = dataSyncService.retrieveAllPreRegIds(datasyncReqDto);

		assertEquals(preRegistrationIdsDTO.getCountOfPreRegIds(), response.getResponse().getCountOfPreRegIds());
	}

	@Test
	public void successStoreConsumedPreRegistrationsTest() throws Exception {
		Mockito.when(serviceUtil.prepareRequestParamMap(Mockito.any())).thenReturn(requestMap);
		Mockito.when(serviceUtil.validateReverseDataSyncRequest(Mockito.any())).thenReturn(true);
		Mockito.when(serviceUtil.reverseDateSyncSave(Mockito.any(), Mockito.any())).thenReturn(reverseDatasyncReponse);
		reverseResponseDTO = dataSyncService.storeConsumedPreRegistrations(reverseRequestDTO);

		assertEquals(reverseDatasyncReponse.getAlreadyStoredPreRegIds(),
				reverseResponseDTO.getResponse().getAlreadyStoredPreRegIds());
	}

}
