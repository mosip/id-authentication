package io.mosip.preregistration.documents.test.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.preregistration.documents.controller.DocumentUploader;
import io.mosip.preregistration.documents.dto.DocumentDto;
import io.mosip.preregistration.documents.service.DocumentUploadService;

@RunWith(SpringRunner.class)
@WebMvcTest(DocumentUploader.class)
public class DocumentUploaderTest {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private MockMvc mockMvc;

	private MockMultipartFile multipartFile, jsonMultiPart;

	@MockBean
	private DocumentUploadService service;

	Map<String, String> response = null;

	@Before
	public void setUp() {
		DocumentDto documentDto = new DocumentDto("98076543218976", "address", "POA", "PDF", "SAVE", "ENG", "KISHAN",
				"KISHAN");

		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("sample.pdf").getFile());
		ObjectMapper mapper = new ObjectMapper();
		try {
			System.out.println("%%%%% " + file.getAbsolutePath());
			multipartFile = new MockMultipartFile("file", "sample.pdf", "application/pdf", new FileInputStream(file));

			jsonMultiPart = new MockMultipartFile("documentString", "", "application/json",
					mapper.writeValueAsString(documentDto).getBytes());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}

		response = new HashMap<String, String>();
		response.put("DocumentId", "1");
		response.put("Status", "Draft");
		Mockito.when(service.uploadDoucment(this.multipartFile, documentDto)).thenReturn(response);
	}

	@Test
	public void successTest() throws Exception {
		
		this.mockMvc
				.perform(MockMvcRequestBuilders.multipart("/v0.1/pre-registration/registration/documents")
						.file(this.jsonMultiPart).file(this.multipartFile))
				.andExpect(status().isOk());

	}

}
