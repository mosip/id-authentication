package io.mosip.preregistration.documents.test.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.preregistration.documents.code.StatusCodes;
import io.mosip.preregistration.documents.controller.DocumentUploader;
import io.mosip.preregistration.documents.dto.DocumentDto;
import io.mosip.preregistration.documents.dto.ResponseDto;
import io.mosip.preregistration.documents.entity.DocumentEntity;
import io.mosip.preregistration.documents.exception.DocumentNotFoundException;
import io.mosip.preregistration.documents.service.DocumentUploadService;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RunWith(SpringRunner.class)
@WebMvcTest(DocumentUploader.class)
public class DocumentUploaderTest {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private MockMvc mockMvc;

	private MockMultipartFile multipartFile, jsonMultiPart;

	@MockBean
	private DocumentUploadService service;

	List<DocumentEntity> DocumentList = new ArrayList<>();
	Map<String, String> response = null;

	String documentId;
	boolean flag;

	Map<String, String> map = new HashMap<>();
	ResponseDto responseCopy = new ResponseDto<>();
	ResponseDto responseDelete = new ResponseDto<>();
	DocumentDto documentDto = null;

	@Before
	public void setUp() throws IOException {

		documentDto = new DocumentDto("59276903416082", "address", "POA", "pdf", "Pending-Appoinment",new Timestamp(System.currentTimeMillis()),"Jagadishwari");

		String json="{\r\n" + 
				"	\"id\": \"mosip.pre-registration.document.upload\",\r\n" + 
				"	\"ver\": \"1.0\",\r\n" + 
				"	\"reqTime\": \"2018-10-17T07:22:57.086+0000\",\r\n" + 
				"	\"request\": {\r\n" + 
				"		\"prereg_id\": \"59276903416082\",\r\n" + 
				"		\"doc_cat_code\": \"POA\",\r\n" + 
				"		\"doc_typ_code\": \"address\",\r\n" + 
				"		\"doc_file_format\": \"pdf\",\r\n" + 
				"		\"status_code\": \"Pending-Appoinment\",\r\n" + 
				"		\"upload_by\": \"9217148168\",\r\n" + 
				"		\"upload_DateTime\": \"2018-10-17T07:22:57.086+0000\"\r\n" + 
				"	}\r\n" + 
				"}";
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("Doc.pdf").getFile());
		try {
			URI uri = new URI(classLoader.getResource("Doc.pdf").getFile().trim().replaceAll("\\u0020", "%20"));
			file = new File(uri.getPath());
		} catch (URISyntaxException e2) {
			e2.printStackTrace();
		}
		ObjectMapper mapper = new ObjectMapper();
		try {
			multipartFile = new MockMultipartFile("file", "Doc.pdf", "application/pdf", new FileInputStream(file));

			jsonMultiPart = new MockMultipartFile("JsonString", "", "application/json",
					json.getBytes());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}

		response = new HashMap<String, String>();
		response.put("DocumentId", "1");
		response.put("Status", "Pending-Appoinment");
		documentId = response.get("DocumentId");
		flag = true;

		List responseCopyList = new ArrayList<>();
		responseCopyList.add(StatusCodes.DOCUMENT_UPLOAD_SUCCESSFUL);
		responseCopy.setResponse(responseCopyList);

		List responseDeleteList = new ArrayList<>();
		responseCopyList.add(StatusCodes.DOCUMENT_DELETE_SUCCESSFUL);
		responseDelete.setResponse(responseDeleteList);

	}

	@Test
	public void successSave() throws Exception {
		Mockito.when(service.uploadDoucment(multipartFile, documentDto)).thenReturn(responseCopy);
		this.mockMvc.perform(MockMvcRequestBuilders.multipart("/v0.1/pre-registration/documents")
				.file(this.jsonMultiPart).file(this.multipartFile)).andExpect(status().isOk());

	}

	@Test
	public void successDelete() throws Exception {
		Mockito.when(service.deleteDocument(documentId)).thenReturn(responseCopy);
		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.delete("/v0.1/pre-registration/delete_document")
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8")
				.accept(MediaType.APPLICATION_JSON_VALUE).param("documentId", documentId);
		mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}

	@Test
	public void getAllDocumentforPreidTest() throws Exception {
		Mockito.when(service.getAllDocumentForPreId("48690172097498")).thenReturn(responseCopy);
		mockMvc.perform(get("/v0.1/pre-registration/get_document")
				.contentType(MediaType.APPLICATION_JSON_VALUE).param("preId", "48690172097498"))
				.andExpect(status().isOk());
	}

	@Test
	public void deletetAllDocumentByPreidTest() throws Exception {
		Mockito.when(service.deleteAllByPreId("48690172097498")).thenReturn(responseDelete);
		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.delete("/v0.1/pre-registration/deleteAllByPreRegId")
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8")
				.accept(MediaType.APPLICATION_JSON_VALUE).param("preId", "48690172097498");
		mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}

	@Test
	public void copyDocumentTest() throws Exception {
		Mockito.when(service.copyDoucment("POA", "48690172097498", "1234567891")).thenReturn(responseCopy);
		mockMvc.perform(post("/v0.1/pre-registration/copy_documents")
				.contentType(MediaType.APPLICATION_JSON_VALUE).param("cat_code", "POA")
				.param("source_prId", "48690172097498").param("destination_preId", "1234567891"))
				.andExpect(status().isOk());

	}
	

	@Test(expected=Exception.class)
	public void FailuregetAllDocumentforPreidTest() throws Exception {
		Mockito.when(service.getAllDocumentForPreId("2")).thenThrow(Exception.class);
		mockMvc.perform(get("/v0.1/pre-registration/get_document")
				.contentType(MediaType.APPLICATION_JSON_VALUE).param("preId", "2"))
				.andExpect(status().isInternalServerError());

	}

	@Test(expected=Exception.class)
	public void FailurecopyDocumentTest() throws Exception {
		Mockito.when(service.copyDoucment(Mockito.anyString(),Mockito.anyString(), Mockito.anyString())).thenThrow(Exception.class);

		mockMvc.perform(
				post("/v0.1/pre-registration/copy_documents").contentType(MediaType.APPLICATION_JSON_VALUE)
						.param("cat_type", Mockito.anyString()).param("source_prId", Mockito.anyString()).param("destination_preId", Mockito.anyString()))
				.andExpect(status().isBadRequest());

	}

}
