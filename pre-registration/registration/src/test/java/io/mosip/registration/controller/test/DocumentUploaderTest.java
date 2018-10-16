package io.mosip.registration.controller.test;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.registration.dto.DocumentDto;
import io.mosip.registration.service.DocumentUploadService;

@RunWith(SpringRunner.class)
@WebMvcTest(DocumentUploaderTest.class)
public class DocumentUploaderTest {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private MockMvc mockMvc;
	
	private MockMultipartFile multipartFile, jsonMultiPart;
	
	@Mock
	private DocumentUploadService service;
	
	@Before
	public void setUp() {
		ClassLoader classLoader = getClass().getClassLoader();
		
		DocumentDto documentDto = new DocumentDto("98745632155997", "12345678996325", "address", "POA", ".pdf", "SAVE",
				"ENG", "Kishan", "Kishan", true);

		File jsonFile = new File("src/test/resources");
		File file = new File(classLoader.getResource("sample.pdf").getFile());
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.writeValue(jsonFile, documentDto);
		} catch (JsonGenerationException e) {
			logger.error(e.getMessage());
		} catch (JsonMappingException e) {
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		try {
			multipartFile = new MockMultipartFile("file", "file-name.data", "text/plain", "some other type".getBytes());
			jsonMultiPart = new MockMultipartFile( "documentString","",  "application/json",  mapper.writeValueAsString(documentDto).getBytes());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		
		
		Mockito.when(service.uploadDoucment(this.multipartFile, documentDto)).thenReturn(true);
	}

	@Test
	public void successTest() throws Exception {

		
		this.mockMvc.perform(MockMvcRequestBuilders.multipart("/v0.1/pre-registration/registration/document/upload")
				.file(this.jsonMultiPart).file(this.multipartFile)).andExpect(status().isOk());

	}

}
