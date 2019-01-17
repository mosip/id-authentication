/* 
 * Copyright
 * 
 
package io.mosip.preregistration.transliteration.test.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import io.mosip.preregistration.transliteration.dto.MainResponseDTO;
import io.mosip.preregistration.transliteration.dto.TransliterationDTO;
import io.mosip.preregistration.transliteration.service.TransliterationService;


*//**
 * 
 * Test class to test the pre-registration transliteration Controller methods
 * 
 * @author Kishan rathore
 * @since 1.0.0
 *
 *//*
@RunWith(SpringRunner.class)
@WebMvcTest(TransliterationControllerTest.class)
public class TransliterationControllerTest {

	*//**
	 * Autowired reference for {@link #MockMvc}
	 *//*
	@Autowired
	private MockMvc mockMvc;

	*//**
	 * Creating Mock Bean for transliteration Service
	 *//*
	@MockBean
	private TransliterationService serviceImpl;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private Object jsonObject, failObject = null;

	*//**
	 * @throws FileNotFoundException
	 *             when file not found
	 * @throws IOException
	 *             on input error
	 * @throws ParseException
	 *             on json parsing error
	 *//*
	@Before
	public void setup() throws FileNotFoundException, IOException, ParseException {
		ClassLoader classLoader = getClass().getClassLoader();
		JSONParser parser = new JSONParser();
		File file = new File(classLoader.getResource("transliteration-application.json").getFile());
		jsonObject = parser.parse(new FileReader(file));

		File failFile = new File(classLoader.getResource("transliteration-application.json").getFile());
		failObject = parser.parse(new FileReader(failFile));
	}

	*//**
	 * @throws Exception on eoor
	 *//*
	@Test
	public void successTest() throws Exception {

		logger.info("----------Successful transliteration controller operation-------");
		MainResponseDTO<TransliterationDTO> response = new MainResponseDTO<>();
		TransliterationDTO dto = new TransliterationDTO();
		//response.setResponse(dto);
		Mockito.when(serviceImpl.translitratorService(Mockito.any())).thenReturn(response);
		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/v0.1/pre-registration/translitrate")
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8")
				.accept(MediaType.APPLICATION_JSON_VALUE).content(jsonObject.toString());
		logger.info("Resonse " + response);

		mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}

	*//**
	 * @throws Exception
	 *             on error
	 *//*
	@Test
	public void failureTest() throws Exception {
		logger.info("----------Unsuccessful transliteration controller operation-------");
		Mockito.doThrow(new IllegalParamException("ex")).when(serviceImpl).translitratorService(Mockito.any());

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/v0.1/pre-registration/translitrate")
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8")
				.accept(MediaType.APPLICATION_JSON_VALUE).content(failObject.toString());
		mockMvc.perform(requestBuilder).andExpect(status().isInternalServerError());
	}
}
*/