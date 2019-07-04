package io.mosip.preregistration.datasync.test.service;

import static org.junit.Assert.assertEquals;

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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.auth.adapter.model.AuthUserDetails;
import io.mosip.kernel.core.exception.ParseException;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.preregistration.core.common.dto.BookingRegistrationDTO;
import io.mosip.preregistration.core.common.dto.DemographicResponseDTO;
import io.mosip.preregistration.core.common.dto.DocumentDTO;
import io.mosip.preregistration.core.common.dto.DocumentMultipartResponseDTO;
import io.mosip.preregistration.core.common.dto.DocumentsMetaData;
import io.mosip.preregistration.core.common.dto.ExceptionJSONInfoDTO;
import io.mosip.preregistration.core.common.dto.MainRequestDTO;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.common.dto.PreRegIdsByRegCenterIdResponseDTO;
import io.mosip.preregistration.core.exception.InvalidRequestParameterException;
import io.mosip.preregistration.core.util.AuditLogUtil;
import io.mosip.preregistration.datasync.DataSyncApplicationTest;
import io.mosip.preregistration.datasync.dto.DataSyncRequestDTO;
import io.mosip.preregistration.datasync.dto.PreRegArchiveDTO;
import io.mosip.preregistration.datasync.dto.PreRegistrationIdsDTO;
import io.mosip.preregistration.datasync.dto.ReverseDataSyncRequestDTO;
import io.mosip.preregistration.datasync.dto.ReverseDatasyncReponseDTO;
import io.mosip.preregistration.datasync.errorcodes.ErrorCodes;
import io.mosip.preregistration.datasync.errorcodes.ErrorMessages;
import io.mosip.preregistration.datasync.exception.DemographicGetDetailsException;
import io.mosip.preregistration.datasync.repository.InterfaceDataSyncRepo;
import io.mosip.preregistration.datasync.repository.ProcessedDataSyncRepo;
import io.mosip.preregistration.datasync.service.DataSyncService;
import io.mosip.preregistration.datasync.service.util.DataSyncServiceUtil;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { DataSyncApplicationTest.class })
public class DataSyncServiceTest {

	@Mock
	private InterfaceDataSyncRepo interfaceDataSyncRepo;

	@Mock
	private ProcessedDataSyncRepo processedDataSyncRepo;

	@Autowired
	@InjectMocks
	private DataSyncService dataSyncService;

	@SpyBean
	private DataSyncService spyDataSyncService;

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

	// new
	String preId = "23587986034785";
	String fromDate = "2018-01-17 00:00:00";
	String toDate = "2019-01-17 00:00:00";
	DocumentMultipartResponseDTO multipartResponseDTOs = new DocumentMultipartResponseDTO();
	List<DocumentMultipartResponseDTO> list2 = new ArrayList<>();
	DocumentsMetaData documentsMetaData = new DocumentsMetaData();
	DocumentDTO documentDTO = new DocumentDTO();
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

		//spyDataSyncService = Mockito.spy(dataSyncService);

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

		MockitoAnnotations.initMocks(this);

		AuthUserDetails applicationUser = Mockito.mock(AuthUserDetails.class);
		Authentication authentication = Mockito.mock(Authentication.class);
		SecurityContext securityContext = Mockito.mock(SecurityContext.class);
		Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);
		Mockito.when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(applicationUser);

		requiredRequestMap.put("version", version);

		multipartResponseDTOs.setDocName("Address.pdf");
		multipartResponseDTOs.setDocumentId("1234");
		multipartResponseDTOs.setDocCatCode("POA");
		multipartResponseDTOs.setLangCode("ENG");
		multipartResponseDTOs.setDocTypCode("RNC");
		list2.add(multipartResponseDTOs);
		documentsMetaData.setDocumentsMetaData(list2);

		bookingRegistrationDTO.setRegDate("2018-01-17 00:00:00");
		bookingRegistrationDTO.setRegistrationCenterId("1005");
		bookingRegistrationDTO.setSlotFromTime(fromDate);
		bookingRegistrationDTO.setSlotToTime(toDate);

		demography.setPreRegistrationId(preId);
		demography.setCreatedBy("Rajath");
		demography.setStatusCode("SAVE");
		demography.setLangCode("12L");
		demography.setPreRegistrationId(preid);

		byte[] demographicDetails = { 1, 0, 1, 0, 1, 0 };

		demography.setDemographicDetails(null);

		archiveDTO.setZipBytes(demographicDetails);
		archiveDTO.setFileName(demography.getPreRegistrationId().toString());

		mainResponseDTO.setResponsetime(resTime);
		mainResponseDTO.setResponse(archiveDTO);
		List<ExceptionJSONInfoDTO> exceptionJSONInfoDTOs = new ArrayList<>();
		exceptionJSONInfoDTOs.add(errlist);
		mainResponseDTO.setErrors(exceptionJSONInfoDTOs);

		preRegIds.add(preId);

		preRegIdsByRegCenterIdResponseDTO.setPreRegistrationIds(preregIds);
		preRegIdsByRegCenterIdResponseDTO.setRegistrationCenterId("1005");

		dataSyncRequestDTO.setRegistrationCenterId("1005");
		dataSyncRequestDTO.setFromDate("2018-01-17 00:00:00");
		dataSyncRequestDTO.setToDate("2018-12-17 00:00:00");

		datasyncReqDto.setId(fetchAllId);
		datasyncReqDto.setVersion(version);
		datasyncReqDto.setRequesttime(new Timestamp(System.currentTimeMillis()));
		datasyncReqDto.setRequest(dataSyncRequestDTO);

		Map<String, String> list = new HashMap<>();
		list.put(preId, "2018-12-28T13:04:53.117Z");
		preRegistrationIdsDTO.setPreRegistrationIds(list);
		preRegistrationIdsDTO.setTransactionId("1111");
		preRegistrationIdsDTO.setCountOfPreRegIds("1");

		dataSyncResponseDTO.setResponse(preRegistrationIdsDTO);
		dataSyncResponseDTO.setErrors(null);
		dataSyncResponseDTO.setResponsetime("2019-02-12T10:54:53.131Z");

		Date date = new Timestamp(System.currentTimeMillis());

		requestMap.put("id", datasyncReqDto.getId());
		requestMap.put("version", datasyncReqDto.getVersion());
		requestMap.put("requesttime", DateUtils.formatDate(date, dateTimeFormat));
		requestMap.put("request", datasyncReqDto.getRequest().toString());

		List<String> preRegistrationIds = new ArrayList<>();
		preRegistrationIds.add(preid);
		reverseDataSyncRequestDTO.setPreRegistrationIds(preRegistrationIds);

		reverseRequestDTO.setRequest(reverseDataSyncRequestDTO);
		reverseRequestDTO.setRequesttime(new Timestamp(System.currentTimeMillis()));
		reverseRequestDTO.setId(storeId);
		reverseRequestDTO.setVersion(version);

		reverseDatasyncReponse.setTransactionId("1111");
		List<String> preids = new ArrayList<>();
		preids.add("23587986034785");
		reverseDatasyncReponse.setPreRegistrationIds(preids);
		reverseDatasyncReponse.setCountOfStoredPreRegIds("1");
	}

	@Test
	public void successGetPreRegistrationTest() throws Exception {

		Mockito.when(serviceUtil.getPreRegistrationData(Mockito.anyString())).thenReturn(demography);
		Mockito.when(serviceUtil.getDocDetails(Mockito.anyString())).thenReturn(documentsMetaData);
		Mockito.when(serviceUtil.getDocBytesDetails(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(documentDTO);
		Mockito.when(serviceUtil.getAppointmentDetails(Mockito.anyString()))
				.thenReturn(bookingRegistrationDTO);
		Mockito.when(serviceUtil.archivingFiles(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(archiveDTO);
		Mockito.doNothing().when(spyDataSyncService).setAuditValues(Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		MainResponseDTO<PreRegArchiveDTO> response = dataSyncService.getPreRegistrationData(preId);

		assertEquals(response.getResponse().getPreRegistrationId(), archiveDTO.getPreRegistrationId());
	}

	@Test(expected = DemographicGetDetailsException.class)
	public void GetPreRegistrationTest1() throws Exception {
		DemographicGetDetailsException ex = new DemographicGetDetailsException(ErrorCodes.PRG_DATA_SYNC_007.toString(),
				ErrorMessages.DEMOGRAPHIC_GET_RECORD_FAILED.toString(), null);
		Mockito.when(serviceUtil.getPreRegistrationData(Mockito.anyString())).thenThrow(ex);
		dataSyncService.getPreRegistrationData(preId);
	}

	@Test
	public void successRetrieveAllPreRegIdTest() throws Exception {
		Mockito.when(serviceUtil.validateDataSyncRequest(Mockito.any(), Mockito.any())).thenReturn(true);
		Mockito.when(serviceUtil.getBookedPreIdsByDateAndRegCenterIdRestService(Mockito.any(), Mockito.any(),
				Mockito.anyString())).thenReturn(preRegIdsByRegCenterIdResponseDTO);
		Mockito.when(serviceUtil.getLastUpdateTimeStamp(Mockito.any())).thenReturn(preRegistrationIdsDTO);
		Mockito.doNothing().when(spyDataSyncService).setAuditValues(Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		MainResponseDTO<PreRegistrationIdsDTO> response = dataSyncService.retrieveAllPreRegIds(datasyncReqDto);

		assertEquals(preRegistrationIdsDTO.getCountOfPreRegIds(), response.getResponse().getCountOfPreRegIds());
	}

	@Test(expected = InvalidRequestParameterException.class)
	public void RetrieveAllPreRegIdTest1() throws Exception {
		InvalidRequestParameterException ex = new InvalidRequestParameterException(
				ErrorCodes.PRG_DATA_SYNC_009.toString(), ErrorMessages.INVALID_REGISTRATION_CENTER_ID.toString(), null);
		Mockito.when(serviceUtil.validateDataSyncRequest(Mockito.any(), Mockito.any())).thenThrow(ex);
		Mockito.doNothing().when(spyDataSyncService).setAuditValues(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		dataSyncService.retrieveAllPreRegIds(datasyncReqDto);
	}

	@Test
	public void successStoreConsumedPreRegistrationsTest() throws Exception {
		Mockito.when(serviceUtil.validateReverseDataSyncRequest(Mockito.any(), Mockito.any())).thenReturn(true);
		Mockito.when(serviceUtil.reverseDateSyncSave(Mockito.any(), Mockito.any(), Mockito.anyString()))
				.thenReturn(reverseDatasyncReponse);
		Mockito.doNothing().when(spyDataSyncService).setAuditValues(Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		reverseResponseDTO = dataSyncService.storeConsumedPreRegistrations(reverseRequestDTO);

		assertEquals(reverseDatasyncReponse.getPreRegistrationIds().size(),
				reverseResponseDTO.getResponse().getPreRegistrationIds().size());
	}

}
