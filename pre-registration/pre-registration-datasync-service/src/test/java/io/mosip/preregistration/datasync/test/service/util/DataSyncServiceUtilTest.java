package io.mosip.preregistration.datasync.test.service.util;

import static org.junit.Assert.assertEquals;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
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

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.preregistration.core.common.dto.BookingRegistrationDTO;
import io.mosip.preregistration.core.common.dto.DemographicResponseDTO;
import io.mosip.preregistration.core.common.dto.DocumentMultipartResponseDTO;
import io.mosip.preregistration.core.common.dto.ExceptionJSONInfoDTO;
import io.mosip.preregistration.core.common.dto.MainListResponseDTO;
import io.mosip.preregistration.core.common.dto.MainRequestDTO;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.common.dto.PreRegIdsByRegCenterIdDTO;
import io.mosip.preregistration.core.common.dto.PreRegIdsByRegCenterIdResponseDTO;
import io.mosip.preregistration.core.config.LoggerConfiguration;
import io.mosip.preregistration.core.exception.InvalidRequestParameterException;
import io.mosip.preregistration.core.util.AuditLogUtil;
import io.mosip.preregistration.datasync.dto.DataSyncRequestDTO;
import io.mosip.preregistration.datasync.dto.PreRegArchiveDTO;
import io.mosip.preregistration.datasync.dto.ReverseDataSyncRequestDTO;
import io.mosip.preregistration.datasync.dto.ReverseDatasyncReponseDTO;
import io.mosip.preregistration.datasync.entity.InterfaceDataSyncEntity;
import io.mosip.preregistration.datasync.entity.ProcessedPreRegEntity;
import io.mosip.preregistration.datasync.repository.InterfaceDataSyncRepo;
import io.mosip.preregistration.datasync.repository.ProcessedDataSyncRepo;
import io.mosip.preregistration.datasync.service.util.DataSyncServiceUtil;

@RunWith(SpringRunner.class)
@SpringBootTest
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
	@MockBean
	RestTemplateBuilder restTemplateBuilder;

	@Autowired
	DataSyncServiceUtil serviceUtil;

	@MockBean
	AuditLogUtil auditLogUtil;
	
	

	@Value("${ver}")
	String versionUrl;

	@Value("${id}")
	String idUrl;

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

	private ObjectMapper mapper = new ObjectMapper();

	private String dateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

	private static Logger log = LoggerConfiguration.logConfig(DataSyncServiceUtil.class);

	String resTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(new Date());
	
	ExceptionJSONInfoDTO errlist = new ExceptionJSONInfoDTO();
	ExceptionJSONInfoDTO exceptionJSONInfo = new ExceptionJSONInfoDTO("", "");
	
	String preId = "23587986034785";
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
	List<DocumentMultipartResponseDTO> responsestatusDto = new ArrayList<>();
	DemographicResponseDTO demographicResponseDTO = new DemographicResponseDTO();
	BookingRegistrationDTO bookingRegistrationDTO = new BookingRegistrationDTO();
	PreRegArchiveDTO preRegArchiveDTO = new PreRegArchiveDTO();
	
	@Test
	public void validateDataSyncRequestTest() {
		dataSyncRequestDTO.setRegClientId("1005");
		dataSyncRequestDTO.setFromDate("2018-01-17 00:00:00");
		dataSyncRequestDTO.setToDate("2018-12-17 00:00:00");
		dataSyncRequestDTO.setUserId("256752365832");
		boolean status = serviceUtil.validateDataSyncRequest(dataSyncRequestDTO);
		assertEquals(status, true);
	}

	@Test(expected = InvalidRequestParameterException.class)
	public void invalidRegCntrIdTest() {
		dataSyncRequestDTO.setRegClientId(null);
		serviceUtil.validateDataSyncRequest(dataSyncRequestDTO);

	}

	@Test(expected = InvalidRequestParameterException.class)
	public void invalidFromDateTest() {
		dataSyncRequestDTO.setRegClientId("1005");
		dataSyncRequestDTO.setFromDate(null);
		serviceUtil.validateDataSyncRequest(dataSyncRequestDTO);

	}

	@Test(expected = InvalidRequestParameterException.class)
	public void invalidToDateTest() {
		dataSyncRequestDTO.setRegClientId("1005");
		dataSyncRequestDTO.setFromDate("2018-01-17 00:00:00");
		dataSyncRequestDTO.setToDate(null);
		serviceUtil.validateDataSyncRequest(dataSyncRequestDTO);

	}

	@Test(expected = InvalidRequestParameterException.class)
	public void invalidUserIdTest() {
		dataSyncRequestDTO.setUserId(null);
		serviceUtil.validateDataSyncRequest(dataSyncRequestDTO);

	}

//	@Test
//	public void validateReverseDataSyncRequestTest() {
//		List<String> preRegistrationIds = new ArrayList<>();
//		preRegistrationIds.add(preId);
//		reverseDataSyncRequestDTO.setPreRegistrationIds(preRegistrationIds);
//		reverseDataSyncRequestDTO.setLangCode("AR");
//		reverseDataSyncRequestDTO.setCreatedBy("5766477466");
//		reverseDataSyncRequestDTO.setCreatedDateTime(date);
//		reverseDataSyncRequestDTO.setUpdateBy("5766477466");
//		reverseDataSyncRequestDTO.setUpdateDateTime(date);
//
//		boolean status = serviceUtil.validateReverseDataSyncRequest(reverseDataSyncRequestDTO);
//		assertEquals(status, true);
//	}

	@Test(expected = InvalidRequestParameterException.class)
	public void invalidPreIDTest() {
		reverseDataSyncRequestDTO.setPreRegistrationIds(null);
		serviceUtil.validateReverseDataSyncRequest(reverseDataSyncRequestDTO);

	}

	@Test(expected = InvalidRequestParameterException.class)
	public void invalidLangCodeTest() {
		List<String> preRegistrationIds = new ArrayList<>();
		preRegistrationIds.add(preId);
		reverseDataSyncRequestDTO.setPreRegistrationIds(preRegistrationIds);
		reverseDataSyncRequestDTO.setLangCode(null);
		serviceUtil.validateReverseDataSyncRequest(reverseDataSyncRequestDTO);

	}

	@Test(expected = InvalidRequestParameterException.class)
	public void invalidCrByTest() {
		List<String> preRegistrationIds = new ArrayList<>();
		preRegistrationIds.add(preId);
		reverseDataSyncRequestDTO.setPreRegistrationIds(preRegistrationIds);
		reverseDataSyncRequestDTO.setLangCode("AR");
		reverseDataSyncRequestDTO.setCreatedBy(null);
		serviceUtil.validateReverseDataSyncRequest(reverseDataSyncRequestDTO);

	}

	@Test(expected = InvalidRequestParameterException.class)
	public void invalidCrDateTimeTest() {
		List<String> preRegistrationIds = new ArrayList<>();
		preRegistrationIds.add(preId);
		reverseDataSyncRequestDTO.setPreRegistrationIds(preRegistrationIds);
		reverseDataSyncRequestDTO.setLangCode("AR");
		reverseDataSyncRequestDTO.setCreatedBy("5766477466");
		reverseDataSyncRequestDTO.setCreatedDateTime(null);
		serviceUtil.validateReverseDataSyncRequest(reverseDataSyncRequestDTO);

	}

	@Test(expected = InvalidRequestParameterException.class)
	public void invalidUpdatedByTest() {
		List<String> preRegistrationIds = new ArrayList<>();
		preRegistrationIds.add(preId);
		reverseDataSyncRequestDTO.setPreRegistrationIds(preRegistrationIds);
		reverseDataSyncRequestDTO.setLangCode("AR");
		reverseDataSyncRequestDTO.setCreatedBy("5766477466");
		reverseDataSyncRequestDTO.setCreatedDateTime(date);
		reverseDataSyncRequestDTO.setUpdateBy(null);
		reverseDataSyncRequestDTO.setUpdateDateTime(date);
		serviceUtil.validateReverseDataSyncRequest(reverseDataSyncRequestDTO);

	}

	@Test(expected = InvalidRequestParameterException.class)
	public void invalidUpdatedDatetimeTest() {
		List<String> preRegistrationIds = new ArrayList<>();
		preRegistrationIds.add(preId);
		reverseDataSyncRequestDTO.setPreRegistrationIds(preRegistrationIds);
		reverseDataSyncRequestDTO.setLangCode("AR");
		reverseDataSyncRequestDTO.setCreatedBy("5766477466");
		reverseDataSyncRequestDTO.setCreatedDateTime(date);
		reverseDataSyncRequestDTO.setUpdateBy("5766477466");
		reverseDataSyncRequestDTO.setUpdateDateTime(null);
		serviceUtil.validateReverseDataSyncRequest(reverseDataSyncRequestDTO);

	}
	
	@Test
	public void callGetPreIdsRestServiceTest() {
		String fromDate="2018-01-17 00:00:00";
		String toDate="2019-01-17 00:00:00";
		preRegIds.add("23587986034785");
		RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
		Mockito.when(restTemplateBuilder.build()).thenReturn(restTemplate);
		MainListResponseDTO mainResponseDTO=new MainListResponseDTO();
		mainResponseDTO.setStatus(true);
		mainResponseDTO.setResTime(resTime);
		mainResponseDTO.setErr(exceptionJSONInfo);
		mainResponseDTO.setResponse(preRegIds);
		ResponseEntity<MainListResponseDTO> respEntity=new ResponseEntity<>(mainResponseDTO, HttpStatus.OK);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
				Mockito.eq(MainListResponseDTO.class))).thenReturn(respEntity);
		List<String> list =serviceUtil.callGetPreIdsRestService(fromDate, toDate);
		assertEquals(list.size(), preRegIds.size());
	}
	
//	@Test(expected=DemographicGetDetailsException.class)
//	public void demographicGetDetailsExceptionTest() {
//		String fromDate="2018-01-17 00:00:00";
//		String toDate="2019-01-17 00:00:00";
//		preRegIds.add("23587986034785");
//		RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
//		Mockito.when(restTemplateBuilder.build()).thenReturn(restTemplate);
//		MainListResponseDTO mainResponseDTO=new MainListResponseDTO();
//		mainResponseDTO.setStatus(false);
//		mainResponseDTO.setResTime(resTime);
//		mainResponseDTO.setErr(exceptionJSONInfo);
//		mainResponseDTO.setResponse(preRegIds);
//		Mockito.when(restTemplateBuilder.build()).thenReturn(restTemplate);
//		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
//				Mockito.eq(MainListResponseDTO.class))).thenReturn(null);
//		List<String> list =serviceUtil.callGetPreIdsRestService(fromDate, toDate);
//		
//	}
	
	@Test
	public void callGetPreIdsByRegCenterIdRestServiceTest() {
		idResponseDTO.setPreRegistrationIds(preRegIds);
		idResponseDTO.setRegistrationCenterId("1005");
		List<PreRegIdsByRegCenterIdResponseDTO> list=new ArrayList<>();
		list.add(idResponseDTO);
		RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
		Mockito.when(restTemplateBuilder.build()).thenReturn(restTemplate);
		MainListResponseDTO mainResponseDTO=new MainListResponseDTO();
		mainResponseDTO.setStatus(true);
		mainResponseDTO.setResTime(resTime);
		mainResponseDTO.setErr(exceptionJSONInfo);
		mainResponseDTO.setResponse(list);
		ResponseEntity<MainListResponseDTO> respEntity=new ResponseEntity<>(mainResponseDTO, HttpStatus.OK);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.POST), Mockito.any(),
				Mockito.eq(MainListResponseDTO.class))).thenReturn(respEntity);
		PreRegIdsByRegCenterIdResponseDTO response = serviceUtil.callGetPreIdsByRegCenterIdRestService("1005", preRegIds);
		assertEquals("1005", response.getRegistrationCenterId());
	}
	
	@Test
	public void prepareRequestParamMapTest() {
		Map<String, String> inputValidation = new HashMap<>();
		MainRequestDTO<DataSyncRequestDTO> datasyncReqDto = new MainRequestDTO<>();
		dataSyncRequestDTO.setRegClientId("1005");
		dataSyncRequestDTO.setFromDate("2018-01-17 00:00:00");
		dataSyncRequestDTO.setToDate("2018-12-17 00:00:00");
		dataSyncRequestDTO.setUserId("256752365832");

		datasyncReqDto.setId(idUrl);
		datasyncReqDto.setVer(versionUrl);
		datasyncReqDto.setReqTime(new Timestamp(System.currentTimeMillis()));
		datasyncReqDto.setRequest(dataSyncRequestDTO);
		
		inputValidation=serviceUtil.prepareRequestParamMap(datasyncReqDto);
		
	}
	
	@Test
	public void callGetDocRestServiceTest() {
		multipartResponseDTOs.setPrereg_id("23587986034785");
		multipartResponseDTOs.setDoc_name("Address.pdf");
		multipartResponseDTOs.setDoc_id("1234");
		multipartResponseDTOs.setDoc_cat_code("POA");
		responsestatusDto.add(multipartResponseDTOs);
		
		RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
		Mockito.when(restTemplateBuilder.build()).thenReturn(restTemplate);
		MainListResponseDTO mainResponseDTO=new MainListResponseDTO();
		mainResponseDTO.setStatus(true);
		mainResponseDTO.setResTime(resTime);
		mainResponseDTO.setErr(exceptionJSONInfo);
		mainResponseDTO.setResponse(responsestatusDto);
		ResponseEntity<MainListResponseDTO> respEntity=new ResponseEntity<>(mainResponseDTO, HttpStatus.OK);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
				Mockito.eq(MainListResponseDTO.class))).thenReturn(respEntity);
		List<DocumentMultipartResponseDTO> response = serviceUtil.callGetDocRestService(preId);
		assertEquals(multipartResponseDTOs.getDoc_name(), response.get(0).getDoc_name());
	}
	
	@Test
	public void callGetPreRegInfoRestServiceTest() {
		demographicResponseDTO.setPreRegistrationId(preId);
		List<DemographicResponseDTO> list=new ArrayList<>();
		list.add(demographicResponseDTO);
		RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
		Mockito.when(restTemplateBuilder.build()).thenReturn(restTemplate);
		MainListResponseDTO mainResponseDTO=new MainListResponseDTO();
		mainResponseDTO.setStatus(true);
		mainResponseDTO.setResTime(resTime);
		mainResponseDTO.setErr(exceptionJSONInfo);
		mainResponseDTO.setResponse(list);
		ResponseEntity<MainListResponseDTO> respEntity=new ResponseEntity<>(mainResponseDTO, HttpStatus.OK);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
				Mockito.eq(MainListResponseDTO.class))).thenReturn(respEntity);
		DemographicResponseDTO response = serviceUtil.callGetPreRegInfoRestService(preId);
		assertEquals(demographicResponseDTO.getPreRegistrationId(), response.getPreRegistrationId());
	}
	
	@Test
	public void callGetAppointmentDetailsRestServiceTest() {
		bookingRegistrationDTO.setRegistrationCenterId("1005");
		RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
		Mockito.when(restTemplateBuilder.build()).thenReturn(restTemplate);
		MainResponseDTO responseDTO=new MainResponseDTO();
		responseDTO.setStatus(true);
		responseDTO.setResTime(resTime);
		responseDTO.setErr(exceptionJSONInfo);
		responseDTO.setResponse(bookingRegistrationDTO);
		ResponseEntity<MainResponseDTO> respEntity=new ResponseEntity<>(responseDTO, HttpStatus.OK);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
				Mockito.eq(MainResponseDTO.class))).thenReturn(respEntity);
		BookingRegistrationDTO response = serviceUtil.callGetAppointmentDetailsRestService(preId);
		assertEquals(bookingRegistrationDTO.getRegistrationCenterId(), response.getRegistrationCenterId());
	}
	
//	@Test
//	public void preparePreRegArchiveDTOTest() {
//		demographicResponseDTO.setPreRegistrationId(preId);
//		bookingRegistrationDTO.setRegistrationCenterId("1005");
//		bookingRegistrationDTO.setRegDate(resTime);
//		
//		serviceUtil.preparePreRegArchiveDTO(demographicResponseDTO,bookingRegistrationDTO);
//		
//	} 
	private JSONObject jsonObject;
	private JSONParser parser = null;
	
//	@Test
//	public void archivingFilesTest() throws FileNotFoundException, IOException, ParseException {
//		parser = new JSONParser();
//
//		ClassLoader classLoader = getClass().getClassLoader();
//		File file = new File(classLoader.getResource("pre-registration.json").getFile());
//		jsonObject = (JSONObject) parser.parse(new FileReader(file));
//		
//		demographicResponseDTO.setPreRegistrationId(preId);
//		demographicResponseDTO.setDemographicDetails(jsonObject);
//		
//		bookingRegistrationDTO.setRegistrationCenterId("1005");
//		bookingRegistrationDTO.setRegDate(resTime);
//		
//		multipartResponseDTOs.setPrereg_id("23587986034785");
//		multipartResponseDTOs.setDoc_name("Address.pdf");
//		multipartResponseDTOs.setDoc_id("1234");
//		multipartResponseDTOs.setDoc_cat_code("POA");
//		multipartResponseDTOs.setMultipartFile(MultipartFile);
//		responsestatusDto.add(multipartResponseDTOs);
//		serviceUtil.archivingFiles(demographicResponseDTO,bookingRegistrationDTO,responsestatusDto);
//	}
	
	@Test
	public void storeReverseDataSyncTest() {
		InterfaceDataSyncEntity interfaceDataSyncEntity=new InterfaceDataSyncEntity();
		interfaceDataSyncEntity.setCreatedBy("Sanober Noor");
		interfaceDataSyncEntity.setCreatedDate(null);
		interfaceDataSyncEntity.setDeleted(true);
		interfaceDataSyncEntity.setDelTime(null);
		interfaceDataSyncEntity.setIpprlst_PK(null);
		interfaceDataSyncEntity.setLangCode("eng");
		interfaceDataSyncEntity.setUpdatedBy("sanober");
		interfaceDataSyncEntity.setUpdatedDate(null);
		List<InterfaceDataSyncEntity> entityList=new ArrayList<>();
	entityList.add(interfaceDataSyncEntity);
	ProcessedPreRegEntity processedPreRegEntity=new ProcessedPreRegEntity();
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
		List<ProcessedPreRegEntity> processedEntityList=new ArrayList<>();
		processedEntityList.add(processedPreRegEntity);
		
		Mockito.when(interfaceDataSyncRepo.saveAll(Mockito.any())).thenReturn(entityList);
		ReverseDatasyncReponseDTO reponse=serviceUtil.storeReverseDataSync(entityList, processedEntityList);
	
		assertEquals("1", reponse.getCountOfStoredPreRegIds());
	}
	
}
