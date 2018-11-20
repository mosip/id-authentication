package io.mosip.pregistration.datasync.test.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.Timestamp;
import java.util.ArrayList;
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
import io.mosip.pregistration.datasync.dto.ExceptionJSONInfo;
import io.mosip.pregistration.datasync.dto.ResponseDTO;
import io.mosip.pregistration.datasync.service.DataSyncService;

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

	@Before
	public void setUp() {
		preId = "29107415046379";
		status = "true";
		resTime = new Timestamp(System.currentTimeMillis());

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void successRetrievePreidsTest() throws Exception {

		exceptionJSONInfo = new ExceptionJSONInfo("", "");
		List responseList = new ArrayList<>();
		responseDto.setStatus(status);
		errlist.add(exceptionJSONInfo);
		responseDto.setErr(errlist);
		responseDto.setResTime(resTime);
		responseDto.setResponse(responseList);

		Mockito.when(dataSyncService.getPreRegistration(preId)).thenReturn(responseDto);
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/v0.1/pre-registration/data-sync/datasync")
				.contentType(MediaType.APPLICATION_JSON).param("preId", "29107415046379");
		mockMvc.perform(requestBuilder).andExpect(status().isOk());

	}

	// @Test(expected = RecordNotFoundException.class)
	@Test(expected = Exception.class)
	public void failureRetrievePreidsTest() throws Exception {

		Mockito.when(dataSyncService.getPreRegistration("1")).thenThrow(Exception.class);
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/v0.1/pre-registration/data-sync/datasync")
				.contentType(MediaType.APPLICATION_JSON).param("preId", "");
		try {
			mockMvc.perform(requestBuilder).andExpect(status().isBadRequest());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
