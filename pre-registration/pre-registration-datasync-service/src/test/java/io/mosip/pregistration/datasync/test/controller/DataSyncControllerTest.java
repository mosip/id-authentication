package io.mosip.pregistration.datasync.test.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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

import io.mosip.pregistration.datasync.controller.DataSyncController;
import io.mosip.pregistration.datasync.dto.DataSyncDTO;
import io.mosip.pregistration.datasync.dto.DataSyncRequestDTO;
import io.mosip.pregistration.datasync.dto.ExceptionJSONInfo;
import io.mosip.pregistration.datasync.dto.ResponseDTO;
import io.mosip.pregistration.datasync.dto.ResponseDataSyncDTO;
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
	ResponseDTO responseDto = new ResponseDTO<>();
	Timestamp resTime = null;
	private Object jsonObject=null;
	
	@Before
	public void setUp() throws FileNotFoundException, IOException, ParseException {
		preId = "29107415046379";
		status = "true";
		resTime = new Timestamp(System.currentTimeMillis());
		
		ClassLoader classLoader = getClass().getClassLoader();
		JSONParser parser = new JSONParser();
		File file = new File(classLoader.getResource("data-sync.json").getFile());
		jsonObject = parser.parse(new FileReader(file));
		
	}

//	@SuppressWarnings({ "unchecked", "rawtypes" })
//	@Test
//	public void successRetrievePreidsTest() throws Exception {
//
//		exceptionJSONInfo = new ExceptionJSONInfo("", "");
//		List responseList = new ArrayList<>();
//		responseDto.setStatus(status);
//		errlist.add(exceptionJSONInfo);
//		responseDto.setErr(errlist);
//		responseDto.setResTime(resTime);
//		responseDto.setResponse(responseList);
//
//		Mockito.when(dataSyncService.getPreRegistration(preId)).thenReturn(responseDto);
//		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/v0.1/pre-registration/data-sync/datasync")
//				.contentType(MediaType.APPLICATION_JSON).param("preId", "29107415046379");
//		mockMvc.perform(requestBuilder).andExpect(status().isOk());
//
//	}

	// @Test(expected = RecordNotFoundException.class)
//	@Test(expected = Exception.class)
//	public void failureRetrievePreidsTest() throws Exception {
//
//		Mockito.when(dataSyncService.getPreRegistration("1")).thenThrow(Exception.class);
//		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/v0.1/pre-registration/data-sync/datasync")
//				.contentType(MediaType.APPLICATION_JSON).param("preId", "");
//		try {
//			mockMvc.perform(requestBuilder).andExpect(status().isBadRequest());
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}
	
	@Test
	public void retrieveAllpregIdSuccessTest() throws Exception {
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Date date1 = dateFormat.parse("01/01/2011");
		Date date2 = dateFormat.parse("01/01/2013");
		long time1 = date1.getTime();
		long time2 = date2.getTime();
		Timestamp from = new Timestamp(time1);
		Timestamp to = new Timestamp(time2);
		
		DataSyncDTO dataSyncDTO=new DataSyncDTO();
		DataSyncRequestDTO dataSyncRequestDTO= new DataSyncRequestDTO();
		dataSyncRequestDTO.setRegClientId("59276903416082");
		dataSyncRequestDTO.setFromDate(from);
		dataSyncRequestDTO.setToDate(to);
		dataSyncRequestDTO.setUserId("Officer");
		dataSyncDTO.setId("mosip.pre-registration.datasync");
		dataSyncDTO.setDataSyncRequestDto(dataSyncRequestDTO);
		dataSyncDTO.setReqTime(new Timestamp(System.currentTimeMillis()));
		dataSyncDTO.setVer("1.0");

		ResponseDataSyncDTO responseDataSyncDTO=new ResponseDataSyncDTO();
		List<ResponseDataSyncDTO> responseDataSyncList=new ArrayList<>();
		ArrayList<String> list=new ArrayList<>();
		list.add("1");
		
		responseDataSyncDTO.setPreRegistrationIds(list);
		responseDataSyncDTO.getTransactionId();
		ResponseDTO<ResponseDataSyncDTO> responseDto=new ResponseDTO();
		responseDataSyncList.add(responseDataSyncDTO);
		responseDto.setErr(null);
		responseDto.setStatus("true");
		responseDto.setResTime(new Timestamp(System.currentTimeMillis()));
		responseDto.setResponse(responseDataSyncList);
		
		
		 Mockito.when(dataSyncService.retrieveAllPreRegid(Mockito.any())).thenReturn(responseDto);
			RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/v0.1/pre-registration/data-sync/datasync")
					.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8").accept(MediaType.APPLICATION_JSON_VALUE)
					.content(jsonObject.toString());
			System.out.println(requestBuilder);
			
	        mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}

}
