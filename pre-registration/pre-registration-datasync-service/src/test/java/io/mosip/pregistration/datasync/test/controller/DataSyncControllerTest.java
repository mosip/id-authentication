package io.mosip.pregistration.datasync.test.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import io.mosip.pregistration.datasync.code.StatusCodes;
import io.mosip.pregistration.datasync.controller.DataSyncController;
import io.mosip.pregistration.datasync.dto.DataSyncDTO;
import io.mosip.pregistration.datasync.dto.DataSyncRequestDTO;
import io.mosip.pregistration.datasync.dto.DataSyncResponseDTO;
import io.mosip.pregistration.datasync.dto.ExceptionJSONInfo;
import io.mosip.pregistration.datasync.dto.PreRegArchiveDTO;
import io.mosip.pregistration.datasync.dto.PreRegistrationIdsDTO;
import io.mosip.pregistration.datasync.dto.ReverseDataSyncDTO;
import io.mosip.pregistration.datasync.dto.ReverseDataSyncRequestDTO;
import io.mosip.pregistration.datasync.service.DataSyncService;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

/**
 * @author M1046129
 *
 */
@RunWith(SpringRunner.class)
@WebMvcTest(DataSyncController.class)
public class DataSyncControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private DataSyncService dataSyncService;

	String preId = "";
	List<ExceptionJSONInfo> errlist = new ArrayList<>();
	ExceptionJSONInfo exceptionJSONInfo = null;
	String status = "";
	@SuppressWarnings("rawtypes")
	DataSyncResponseDTO responseDto = new DataSyncResponseDTO<>();
	Timestamp resTime = null;
	String filename = "";
	byte[] bytes = null;
	ReverseDataSyncDTO reverseDataSyncDTO = new ReverseDataSyncDTO();
	private Object jsonObject = null;
	private Object jsonObjectRev = null;

	@SuppressWarnings("deprecation")
	@Before
	public void setUp() throws FileNotFoundException, IOException, ParseException, URISyntaxException {
		preId = "29107415046379";
		status = "true";
		resTime = new Timestamp(System.currentTimeMillis());
		bytes = new byte[1024];
		filename = "Doc.pdf";

		ReverseDataSyncRequestDTO requestDTO = new ReverseDataSyncRequestDTO();
		List<String> pre_registration_ids = new ArrayList<>();
		pre_registration_ids.add("75391783729406");
		pre_registration_ids.add("75391783729407");
		pre_registration_ids.add("75391783729408");
		requestDTO.setPre_registration_ids(pre_registration_ids);
		reverseDataSyncDTO.setRequest(requestDTO);

		preId = "29107415046379";
		status = "true";
		resTime = new Timestamp(System.currentTimeMillis());

		ClassLoader classLoader = getClass().getClassLoader();
		JSONParser parser = new JSONParser();

		URI dataSyncUri = new URI(
				classLoader.getResource("data-sync.json").getFile().trim().replaceAll("\\u0020", "%20"));
		File file = new File(dataSyncUri.getPath());
		jsonObject = parser.parse(new FileReader(file));

		URI reverseDataSyncUri = new URI(
				classLoader.getResource("reverse-data-sync.json").getFile().trim().replaceAll("\\u0020", "%20"));
		File file1 = new File(reverseDataSyncUri.getPath());

		jsonObjectRev = parser.parse(new FileReader(file1));

	}

	@SuppressWarnings("unchecked")
	@Test
	public void successRetrievePreidsTest() throws Exception {

		exceptionJSONInfo = new ExceptionJSONInfo("", "");
		PreRegArchiveDTO responseList = new PreRegArchiveDTO();
		responseList.setZipBytes(bytes);
		responseList.setFileName(filename);
		errlist.add(exceptionJSONInfo);
		responseDto.setResponse(responseList);

		Mockito.when(dataSyncService.getPreRegistration(preId)).thenReturn(responseDto);
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/v0.1/pre-registration/data-sync/datasync")
				.contentType(MediaType.APPLICATION_JSON).param("preId", "29107415046379");
		mockMvc.perform(requestBuilder).andExpect(status().isOk());

	}

	@SuppressWarnings("unchecked")
	@Test
	public void retrieveAllpregIdSuccessTest() throws Exception {
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Date date1 = dateFormat.parse("01/01/2011");
		Date date2 = dateFormat.parse("01/01/2013");
		long time1 = date1.getTime();
		long time2 = date2.getTime();
		Timestamp from = new Timestamp(time1);
		Timestamp to = new Timestamp(time2);

		DataSyncDTO dataSyncDTO = new DataSyncDTO();
		DataSyncRequestDTO dataSyncRequestDTO = new DataSyncRequestDTO();
		dataSyncRequestDTO.setRegClientId("59276903416082");
		dataSyncRequestDTO.setFromDate("01/01/2011");
		dataSyncRequestDTO.setToDate("01/01/2013");
		dataSyncRequestDTO.setUserId("Officer");
		dataSyncDTO.setId("mosip.pre-registration.datasync");
		dataSyncDTO.setDataSyncRequestDto(dataSyncRequestDTO);
		dataSyncDTO.setReqTime(new Timestamp(System.currentTimeMillis()));
		dataSyncDTO.setVer("1.0");

		PreRegistrationIdsDTO preRegistrationIdsDTO = new PreRegistrationIdsDTO();
		ArrayList<String> list = new ArrayList<>();
		list.add("1");

		preRegistrationIdsDTO.setPreRegistrationIds(list);
		preRegistrationIdsDTO.getTransactionId();
//		@SuppressWarnings("rawtypes")
//		DataSyncResponseDTO<PreRegistrationIdsDTO> responseDto = new DataSyncResponseDTO();
		
		responseDto.setErr(null);
		responseDto.setStatus("true");
		responseDto.setResTime(new Timestamp(System.currentTimeMillis()));
		responseDto.setResponse(preRegistrationIdsDTO);

		Mockito.when(dataSyncService.retrieveAllPreRegid(Mockito.any())).thenReturn(responseDto);
		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/v0.1/pre-registration/data-sync/datasync")
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8")
				.accept(MediaType.APPLICATION_JSON_VALUE).content(jsonObject.toString());
		System.out.println(requestBuilder);

		mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void reverseDatasyncSuccessTest() throws Exception {
		DataSyncResponseDTO<String> responseDto = new DataSyncResponseDTO<>();	
		List responseList = new ArrayList<>();
		responseList.add(StatusCodes.PRE_REGISTRATION_IDS_STORED_SUCESSFULLY.toString());
		responseDto.setErr(null);
		responseDto.setStatus("true");
		responseDto.setResTime(new Timestamp(System.currentTimeMillis()));
		responseDto.setResponse(StatusCodes.PRE_REGISTRATION_IDS_STORED_SUCESSFULLY.toString());
		Mockito.when(dataSyncService.storeConsumedPreRegistrations(Mockito.any())).thenReturn(responseDto);
		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/v0.1/pre-registration/data-sync/reverseDataSync")
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8")
				.accept(MediaType.APPLICATION_JSON_VALUE).content(jsonObjectRev.toString());
		System.out.println(requestBuilder);

		mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}

}
