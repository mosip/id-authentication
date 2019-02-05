package io.mosip.preregistration.datasync.test.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.parser.JSONParser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import io.mosip.preregistration.core.common.dto.MainRequestDTO;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.datasync.controller.DataSyncController;
import io.mosip.preregistration.datasync.dto.DataSyncRequestDTO;
import io.mosip.preregistration.datasync.dto.PreRegArchiveDTO;
import io.mosip.preregistration.datasync.dto.PreRegistrationIdsDTO;
import io.mosip.preregistration.datasync.dto.ReverseDataSyncRequestDTO;
import io.mosip.preregistration.datasync.dto.ReverseDatasyncReponseDTO;
import io.mosip.preregistration.datasync.errorcodes.ErrorMessages;
import io.mosip.preregistration.datasync.service.DataSyncService;

@RunWith(SpringRunner.class)
@WebMvcTest(DataSyncController.class)
public class DataSyncControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private DataSyncService dataSyncService;

	@MockBean
	RestTemplateBuilder restTemplateBuilder;

	Timestamp resTime = null;
	String filename = "";
	byte[] bytes = null;
	private Object jsonObject = null;
	private Object jsonObjectRev = null;

	DataSyncRequestDTO dataSyncRequestDTO = new DataSyncRequestDTO();
	PreRegistrationIdsDTO preRegistrationIdsDTO = new PreRegistrationIdsDTO();
	ReverseDataSyncRequestDTO reverseDataSyncRequestDTO = new ReverseDataSyncRequestDTO();
	ReverseDatasyncReponseDTO reverseDatasyncReponseDTO = new ReverseDatasyncReponseDTO();
	PreRegArchiveDTO preRegArchiveDTO = new PreRegArchiveDTO();
	MainRequestDTO<DataSyncRequestDTO> mainDataSyncRequestDTO = new MainRequestDTO<>();
	MainResponseDTO<PreRegArchiveDTO> mainPreRegArchiveDTO = new MainResponseDTO<>();
	MainResponseDTO<PreRegistrationIdsDTO> mainDataSyncResponseDTO = new MainResponseDTO<>();
	MainRequestDTO<ReverseDataSyncRequestDTO> mainReverseDataSyncRequestDTO = new MainRequestDTO<>();
	MainResponseDTO<ReverseDatasyncReponseDTO> mainReverseDataSyncResponseDTO = new MainResponseDTO<>();

	@Before
	public void setUp() throws URISyntaxException, FileNotFoundException, IOException, ParseException,
			org.json.simple.parser.ParseException {
		bytes = new byte[1024];
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

		dataSyncRequestDTO.setRegClientId("12");
		dataSyncRequestDTO.setFromDate("2019-01-01 00:00:00");
		dataSyncRequestDTO.setToDate("2019-01-31 00:00:00");
		dataSyncRequestDTO.setUserId("User1");
		mainDataSyncRequestDTO.setId("mosip.pre-registration.datasync");
		mainDataSyncRequestDTO.setVer("1.0");
		mainDataSyncRequestDTO.setReqTime(new Timestamp(System.currentTimeMillis()));
		mainDataSyncRequestDTO.setRequest(dataSyncRequestDTO);

		List<String> pre_registration_ids = new ArrayList<>();
		pre_registration_ids.add("97285429827016");
		reverseDataSyncRequestDTO.setPreRegistrationIds(pre_registration_ids);
		mainReverseDataSyncRequestDTO.setRequest(reverseDataSyncRequestDTO);

	}

	@Test
	public void successRetrievePreidsTest() throws Exception {
		preRegArchiveDTO.setAppointmentDate("2019-01-12");
		preRegArchiveDTO.setFileName("97285429827016.zip");
		preRegArchiveDTO.setPreRegistrationId("97285429827016");
		;
		preRegArchiveDTO.setRegistrationCenterId("12");
		preRegArchiveDTO.setTimeSlotFrom("09:23");
		preRegArchiveDTO.setTimeSlotTo("09:46");
		preRegArchiveDTO.setZipBytes(bytes);
		mainPreRegArchiveDTO.setResponse(preRegArchiveDTO);

		Mockito.when(dataSyncService.getPreRegistrationData("97285429827016")).thenReturn(mainPreRegArchiveDTO);
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/v0.1/pre-registration/data-sync/datasync")
				.contentType(MediaType.APPLICATION_JSON).param("pre_registration_id", "97285429827016");
		mockMvc.perform(requestBuilder).andExpect(status().isOk());

	}

	@Test
	public void retrieveAllpregIdSuccessTest() throws Exception {

		Map<String, String> list = new HashMap<>();
		list.put("97285429827016", "2019-01-17T13:24:53.419Z");
		list.put("56014280251746", "2019-01-17T13:24:51.665Z");
		list.put("63470164572136", "2019-01-17T13:24:52.203Z");
		list.put("25368956035901", "2019-01-17T13:24:52.753Z");
		preRegistrationIdsDTO.setPreRegistrationIds(list);
		mainDataSyncResponseDTO.setErr(null);
		mainDataSyncResponseDTO.setStatus(true);
		mainDataSyncResponseDTO.setResTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(new Date()));
		mainDataSyncResponseDTO.setResponse(preRegistrationIdsDTO);

		Mockito.when(dataSyncService.retrieveAllPreRegIds(mainDataSyncRequestDTO)).thenReturn(mainDataSyncResponseDTO);
		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.post("/v0.1/pre-registration/data-sync/retrieveAllPreRegIds")
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8")
				.accept(MediaType.APPLICATION_JSON_VALUE).content(jsonObject.toString());

		mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}

	@Test
	public void reverseDatasyncSuccessTest() throws Exception {

		reverseDatasyncReponseDTO.setAlreadyStoredPreRegIds("1");
		reverseDatasyncReponseDTO.setCountOfStoredPreRegIds("1");
		reverseDatasyncReponseDTO.setTransactionId("26fde349-0e56-11e9-99e1-f7683fbbce99");
		List<String> responseList = new ArrayList<>();
		responseList.add(ErrorMessages.PRE_REGISTRATION_IDS_STORED_SUCESSFULLY.toString());
		mainReverseDataSyncResponseDTO.setErr(null);
		mainReverseDataSyncResponseDTO.setStatus(true);
		mainReverseDataSyncResponseDTO
				.setResTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(new Date()));
		mainReverseDataSyncResponseDTO.setResponse(reverseDatasyncReponseDTO);
		Mockito.when(dataSyncService.storeConsumedPreRegistrations(mainReverseDataSyncRequestDTO))
				.thenReturn(mainReverseDataSyncResponseDTO);
		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/v0.1/pre-registration/data-sync/reverseDataSync")
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8")
				.accept(MediaType.APPLICATION_JSON_VALUE).content(jsonObjectRev.toString());
		System.out.println(requestBuilder);

		mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}
}