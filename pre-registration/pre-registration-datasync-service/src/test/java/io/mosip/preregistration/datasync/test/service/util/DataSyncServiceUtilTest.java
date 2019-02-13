package io.mosip.preregistration.datasync.test.service.util;

import static org.junit.Assert.assertEquals;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.preregistration.core.common.dto.MainRequestDTO;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.config.LoggerConfiguration;
import io.mosip.preregistration.core.exception.InvalidRequestParameterException;
import io.mosip.preregistration.core.util.AuditLogUtil;
import io.mosip.preregistration.datasync.dto.DataSyncRequestDTO;
import io.mosip.preregistration.datasync.dto.ReverseDataSyncRequestDTO;
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

	String preId = "23587986034785";
	MainRequestDTO<?> requestDto = new MainRequestDTO<>();
	Date date = new Timestamp(System.currentTimeMillis());
	Map<String, String> requestMap = new HashMap<>();
	MainRequestDTO<DataSyncRequestDTO> datasyncReqDto = new MainRequestDTO<>();
	DataSyncRequestDTO dataSyncRequestDTO = new DataSyncRequestDTO();
	ReverseDataSyncRequestDTO reverseDataSyncRequestDTO = new ReverseDataSyncRequestDTO();

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
	
//	@Test
//	public void callGetPreIdsRestServiceTest() {
//		
//	}
	
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
	
}
