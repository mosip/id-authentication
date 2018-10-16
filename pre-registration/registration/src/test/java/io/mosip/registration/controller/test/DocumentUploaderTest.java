package io.mosip.registration.controller.test;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.FileInputStream;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

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
		DocumentDto documentDto = new DocumentDto("98745632155997", "12345678996325", "address", "POA", ".pdf", "SAVE",
				"ENG", "Kishan", "Kishan", true);

		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("sample.pdf").getFile());
		ObjectMapper mapper = new ObjectMapper();
		try {
			multipartFile = new MockMultipartFile("file", "sample.pdf", "application/pdf", new FileInputStream(file));
			jsonMultiPart = new MockMultipartFile("documentString", "", "application/json",
					mapper.writeValueAsString(documentDto).getBytes());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}

		Mockito.when(service.uploadDoucment(this.multipartFile, documentDto)).thenReturn(true);
	}

	@Test
	public void successTest() throws Exception {

		this.mockMvc.perform(MockMvcRequestBuilders.multipart("/v0.1/pre-registration/registration/upload")
				.file(this.jsonMultiPart).file(this.multipartFile)).andExpect(status().isOk());

	}

}
