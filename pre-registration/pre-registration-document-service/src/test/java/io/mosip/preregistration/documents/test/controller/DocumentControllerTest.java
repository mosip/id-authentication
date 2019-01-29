package io.mosip.preregistration.documents.test.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.util.JsonUtils;
import io.mosip.preregistration.core.common.dto.MainListResponseDTO;
import io.mosip.preregistration.documents.code.DocumentStatusMessages;
import io.mosip.preregistration.documents.controller.DocumentController;
import io.mosip.preregistration.documents.dto.DocumentRequestDTO;
import io.mosip.preregistration.documents.dto.DocumentResponseDTO;
import io.mosip.preregistration.documents.entity.DocumentEntity;
import io.mosip.preregistration.documents.service.DocumentService;
import io.mosip.preregistration.documents.service.util.DocumentServiceUtil;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.FilesystemCephAdapterImpl;

/**
 * Test class to test the DocumentUploader Controller methods
 * 
 * @author Rajath KR
 * @author Tapaswini Bahera
 * @author Jagadishwari S
 * @author Kishan Rathore
 * @since 1.0.0
 * 
 */
@RunWith(SpringRunner.class)
@WebMvcTest(DocumentController.class)
public class DocumentControllerTest {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Autowired reference for {@link #MockMvc}
	 */
	@Autowired
	private MockMvc mockMvc;

	//private MockMultipartFile multipartFile, jsonMultiPart;

	/**
	 * Creating Mock Bean for DocumentUploadService
	 */
	@MockBean
	private DocumentService service;

	@MockBean
	private DocumentServiceUtil serviceutil;

	/**
	 * Creating Mock Bean for FilesystemCephAdapterImpl
	 */
	@MockBean
	private FilesystemCephAdapterImpl ceph;

	List<DocumentEntity> DocumentList = new ArrayList<>();
	Map<String, String> response = null;

	String documentId;
	boolean flag;
	String json;
	String jsonDTO = "";

	Map<String, String> map = new HashMap<>();
	MainListResponseDTO responseCopy = new MainListResponseDTO<>();
	MainListResponseDTO responseDelete = new MainListResponseDTO<>();
	MainListResponseDTO<DocumentResponseDTO> responseMain = new MainListResponseDTO<>();
	DocumentRequestDTO documentDto = null;
	List<DocumentResponseDTO> docResponseDtos=new ArrayList<>();

	/**
	 * @throws IOException
	 */
	@Before
	public void setUp() throws IOException {

		documentDto = new DocumentRequestDTO("59276903416082", "POA", "address", "pdf", "Pending-Appoinment",
				new Date(), "ENG", "Kishan");

		json = "{\r\n" + "	\"id\": \"osip.pre-registration.document.upload\",\r\n" + "	\"ver\": \"1.0\",\r\n"
				+ "	\"reqTime\": \"2018-10-17T07:22:57.086Z\",\r\n" + "	\"request\": {\r\n"
				+ "		\"pre_registartion_id\": \"59276903416082\",\r\n" + "\"doc_cat_code\": \"POA\",\r\n"
				+ "		\"doc_typ_code\": \"address\",\r\n" + "\"doc_file_format\": \"pdf\",\r\n"
				+ "		\"status_code\": \"Pending-Appoinment\",\r\n" + "\"upload_by\": \"9217148168\",\r\n"
				+ "		\"upload_date_time\": \"2018-10-17T07:22:57.086Z\",\r\n" + "\"lang_code\": \"ENG\",\r\n"+"	}\r\n" + "}";
		
		ObjectMapper mapper = new ObjectMapper();
		

		response = new HashMap<String, String>();
		response.put("DocumentId", "1");
		response.put("Status", "Pending_Appoinment");
		documentId = response.get("DocumentId");
		flag = true;

		List responseCopyList = new ArrayList<>();
		responseCopyList.add(DocumentStatusMessages.DOCUMENT_UPLOAD_SUCCESSFUL);
		responseCopy.setResponse(responseCopyList);

		List responseDeleteList = new ArrayList<>();
		responseCopyList.add(DocumentStatusMessages.DOCUMENT_DELETE_SUCCESSFUL);
		responseDelete.setResponse(responseDeleteList);

		DocumentResponseDTO responseDto=new DocumentResponseDTO();
		responseDto.setDocumentCat("POA");
		responseDto.setDocumnetId("12345");
		responseDto.setPreRegistrationId("123546987412563");
		
		
		docResponseDtos.add(responseDto);
		responseMain.setStatus(true);
		responseMain.setResponse(docResponseDtos);
	}

	@Test
	public void successSave() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("Doc.pdf").getFile());
		try {
			URI uri = new URI(classLoader.getResource("Doc.pdf").getFile().trim().replaceAll("\\u0020", "%20"));
			file = new File(uri.getPath());
		} catch (URISyntaxException e2) {
			e2.printStackTrace();
		}
		try {
			MockMultipartFile multipartFile = new MockMultipartFile("file", "Doc.pdf", "application/pdf", new FileInputStream(file));

			MockMultipartFile jsonMultiPart = new MockMultipartFile("json", "json", "application/json", json.getBytes());
			
			String documentStringDTO=JsonUtils.javaObjectToJsonString(documentDto);
			Mockito.doReturn(true).when(ceph).storeFile(Mockito.any(), Mockito.any(), Mockito.any());
			//Mockito.when(service.createDoc(documentDto, multipartFile)).thenReturn(docResponseDtos);
			Mockito.when(service.uploadDoucment(Mockito.any(), Mockito.any())).thenReturn(responseMain);
			this.mockMvc.perform(MockMvcRequestBuilders.multipart("/v0.1/pre-registration/documents")
					.file(jsonMultiPart).file(multipartFile)).andExpect(status().isOk());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}


	}

	/**
	 * @throws Exception
	 */
	@Test
	public void successDelete() throws Exception {
		Mockito.when(service.deleteDocument(documentId)).thenReturn(responseCopy);
		RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/v0.1/pre-registration/deleteDocument")
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8")
				.accept(MediaType.APPLICATION_JSON_VALUE).param("documentId", documentId);
		mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void getAllDocumentforPreidTest() throws Exception {
		Mockito.when(service.getAllDocumentForPreId("48690172097498")).thenReturn(responseCopy);
		mockMvc.perform(get("/v0.1/pre-registration/getDocument").contentType(MediaType.APPLICATION_JSON_VALUE)
				.param("pre_registration_id", "48690172097498")).andExpect(status().isOk());
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void deletetAllDocumentByPreidTest() throws Exception {
		Mockito.when(service.deleteAllByPreId("48690172097498")).thenReturn(responseDelete);
		RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/v0.1/pre-registration/deleteAllByPreRegId")
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8")
				.accept(MediaType.APPLICATION_JSON_VALUE).param("pre_registration_id", "48690172097498");
		mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void copyDocumentTest() throws Exception {
		Mockito.when(service.copyDoucment("POA", "48690172097498", "1234567891")).thenReturn(responseCopy);
		mockMvc.perform(post("/v0.1/pre-registration/copyDocuments").contentType(MediaType.APPLICATION_JSON_VALUE)
				.param("catCode", "POA").param("sourcePrId", "48690172097498").param("destinationPreId", "1234567891"))
				.andExpect(status().isOk());
	}

	/**
	 * @throws Exception
	 */
	@Test(expected = Exception.class)
	public void FailuregetAllDocumentforPreidTest() throws Exception {
		Mockito.when(service.getAllDocumentForPreId("2")).thenThrow(Exception.class);
		mockMvc.perform(get("/v0.1/pre-registration/getDocument").contentType(MediaType.APPLICATION_JSON_VALUE)
				.param("preId", "2")).andExpect(status().isInternalServerError());

	}

	/**
	 * @throws Exception
	 */
	@Test(expected = Exception.class)
	public void FailurecopyDocumentTest() throws Exception {
		Mockito.when(service.copyDoucment(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
				.thenThrow(Exception.class);

		mockMvc.perform(post("/v0.1/pre-registration/copyDocuments").contentType(MediaType.APPLICATION_JSON_VALUE)
				.param("catCype", Mockito.anyString()).param("sourcePrId", Mockito.anyString())
				.param("destinationPreId", Mockito.anyString())).andExpect(status().isBadRequest());

	}

}
