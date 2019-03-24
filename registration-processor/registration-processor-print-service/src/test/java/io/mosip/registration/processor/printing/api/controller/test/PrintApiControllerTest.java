package io.mosip.registration.processor.printing.api.controller.test;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.mosip.kernel.core.util.DateUtils;
import io.mosip.registration.processor.core.constant.IdType;
import io.mosip.registration.processor.core.spi.print.service.PrintService;
import io.mosip.registration.processor.printing.api.controller.PrintApiController;
import io.mosip.registration.processor.printing.api.dto.PrintRequest;
import io.mosip.registration.processor.printing.api.dto.PrintResponse;
import io.mosip.registration.processor.printing.api.dto.RequestDTO;
import io.mosip.registration.processor.printing.api.dto.ResponseDTO;
import io.mosip.registration.processor.printing.api.util.PrintServiceRequestValidator;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PrintApiControllerTest {

	@InjectMocks
	private PrintApiController printapicontroller = new PrintApiController();

	@MockBean
	private PrintService<Map<String, byte[]>> printservice;

	@Mock
	private Environment env;

	@Autowired
	private MockMvc mockMvc;

	@Mock
	private PrintServiceRequestValidator validator;

	private String json;

	private Map<String, byte[]> map = new HashMap<>();

	@Before
	public void setup() throws JsonProcessingException {
		when(env.getProperty("mosip.registration.processor.print.service.id")).thenReturn("mosip.registration.print");
		when(env.getProperty("mosip.registration.processor.datetime.pattern"))
				.thenReturn("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		when(env.getProperty("mosip.registration.processor.application.version")).thenReturn("1.0");

		PrintRequest request = new PrintRequest();
		request.setId("mosip.registration.print");
		request.setVersion("1.0");
		request.setRequesttime(DateUtils.getUTCCurrentDateTimeString("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
		RequestDTO dto = new RequestDTO();
		dto.setIdtype(IdType.RID);
		dto.setIdValue("10011100110026920190313153010");
		request.setRequest(dto);
		Gson gson = new GsonBuilder().serializeNulls().create();
		json = gson.toJson(request);

		PrintResponse response = new PrintResponse();
		response.setId("mosip.registration.print");
		response.setVersion("1.0");
		response.setResponsetime("2019-03-15T09:08:38.548Z");
		ResponseDTO dto2 = new ResponseDTO();
		byte[] pdfbyte = "pdf bytes".getBytes();
		dto2.setFile(pdfbyte);
		response.setResponse(dto2);
		map.put("uinPdf", pdfbyte);
	}

	@Test
	public void testpdfSuccess() throws Exception {
		Mockito.when(printservice.getDocuments(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(map);

		this.mockMvc.perform(post("/registration-processor/print/v0.1").accept(MediaType.APPLICATION_JSON_VALUE)
				.contentType(MediaType.APPLICATION_JSON_VALUE).content(json)).andExpect(status().isOk());
	}

//	@Test
//	public void testPdfFailure() throws Exception {
//		this.mockMvc.perform(post("/registration-processor/print/v1.0").accept(MediaType.APPLICATION_JSON_VALUE)
//				.contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isBadRequest());
//	}
}
