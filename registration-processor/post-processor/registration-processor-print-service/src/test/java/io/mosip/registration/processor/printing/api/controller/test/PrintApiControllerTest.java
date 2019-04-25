package io.mosip.registration.processor.printing.api.controller.test;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;

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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.mosip.registration.processor.core.spi.print.service.PrintService;
import io.mosip.registration.processor.core.token.validation.TokenValidator;
import io.mosip.registration.processor.printing.api.controller.PrintApiController;
import io.mosip.registration.processor.printing.api.util.PrintServiceRequestValidator;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = PrintServiceConfigTest.class)
@TestPropertySource(locations = "classpath:application.properties")
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

	@Mock
	private TokenValidator tokenValidator;
	
	private Map<String, byte[]> map = new HashMap<>();

	@Before
	public void setup() throws JsonProcessingException {
		when(env.getProperty("mosip.registration.processor.print.service.id")).thenReturn("mosip.registration.print");
		when(env.getProperty("mosip.registration.processor.datetime.pattern"))
				.thenReturn("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		when(env.getProperty("mosip.registration.processor.application.version")).thenReturn("1.0");
		doNothing().when(tokenValidator).validate(ArgumentMatchers.any(), ArgumentMatchers.any());
		
		byte[] pdfbyte = "pdf bytes".getBytes();
		map.put("uinPdf", pdfbyte);
	}

	@Test
	public void testpdfSuccess() throws Exception {
		Mockito.when(printservice.getDocuments(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(map);

		this.mockMvc.perform(post("/registration-processor/print/v0.1").accept(MediaType.APPLICATION_JSON_VALUE)
				.cookie(new Cookie("Authorization", "token")).contentType(MediaType.APPLICATION_JSON_VALUE)
				.content("{\r\n" + "  \"id\": \"mosip.registration.print\",\r\n" + "  \"request\": {\r\n"
						+ "    \"idValue\": \"10011100110026920190313153010\",\r\n" + "    \"idtype\": \"RID\"\r\n"
						+ "  },\r\n" + "  \"requesttime\": \"2019-03-15T09:08:38.548Z\",\r\n"
						+ "  \"version\": \"1.0\"\r\n" + "}"))
				.andExpect(status().isOk());
	}

	@Test
	public void testPdfFailure() throws Exception {
		this.mockMvc.perform(post("/registration-processor/print/v1.0").accept(MediaType.APPLICATION_JSON_VALUE)
				.contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isNotFound());
	}
}
