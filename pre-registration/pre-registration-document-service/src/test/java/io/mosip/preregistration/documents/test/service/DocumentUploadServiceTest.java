package io.mosip.preregistration.documents.test.service;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.preregistration.documents.code.StatusCodes;
import io.mosip.preregistration.documents.dto.DocumentDto;
import io.mosip.preregistration.documents.dto.ExceptionJSONInfo;
import io.mosip.preregistration.documents.dto.ResponseDto;
import io.mosip.preregistration.documents.entity.DocumentEntity;
import io.mosip.preregistration.documents.exception.DocumentNotFoundException;
import io.mosip.preregistration.documents.exception.DocumentNotValidException;
import io.mosip.preregistration.documents.exception.DocumentSizeExceedException;
import io.mosip.preregistration.documents.repository.DocumentRepository;
import io.mosip.preregistration.documents.service.DocumentUploadService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DocumentUploadServiceTest {

	@InjectMocks
	private DocumentUploadService documentUploadService = new DocumentUploadService() {
		@Override
		public String getFileExtension() {
			// Document extension PDF
			return "PDF";
		}

		@Override
		public long getMaxFileSize() {
			// Max document size 5 Mb
			return 5 * 1024 * 1024;
		}
	};

	List<DocumentEntity> docEntity = new ArrayList<>();

	@Mock
	private DocumentRepository documentRepository;

	@Value("${max.file.size}")
	private int maxFileSize;

	@Value("${file.extension}")
	private String fileExtension;

	/**
	 * @return maximum file size defined.
	 */
	public long getMaxFileSize() {
		return (this.maxFileSize * 1024 * 1024);
	}

	/**
	 * @return defined document extension.
	 */
	public String getFileExtension() {
		return this.fileExtension;
	}

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private MockMultipartFile mockMultipartFile;
	private MockMultipartFile mockMultipartFileDummy;
	DocumentDto documentDto = new DocumentDto("48690172097498", "address", "POA", "PDF", "Draft", "ENG", "Jagadishwari",
			"Jagadishwari");
	DocumentDto dummyDto = new DocumentDto("48690172097499", "address", "POI", "PDF", "Draft", "ENG", "Jagadishwari",
			"Jagadishwari");
	private DocumentEntity entity;
	private DocumentEntity dummyEntity;
	String documentId;
	String preId;
	private Map<String, String> map = new HashMap<String, String>();
	boolean flag;
	ResponseDto responsedelete = new ResponseDto<>();
	ResponseDto<DocumentEntity> responseGetAllPreid = new ResponseDto<>();
	ResponseDto responseUpload = new ResponseDto<>();
	ResponseDto responseCopy = new ResponseDto<>();

	@Before
	public void setUp() throws URISyntaxException, FileNotFoundException, IOException {

		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("Doc.pdf").getFile());

		URI uri = new URI(classLoader.getResource("Doc.pdf").getFile().trim().replaceAll("\\u0020", "%20"));
		file = new File(uri.getPath());

		byte[] bFile = null;

		bFile = Files.readAllBytes(file.toPath());

		mockMultipartFileDummy = new MockMultipartFile("file", "sampleZip", "mixed/multipart",
				new FileInputStream(file));

		mockMultipartFile = new MockMultipartFile("file", "Doc.pdf", "mixed/multipart", new FileInputStream(file));
		
		entity = new DocumentEntity(1, "48690172097498", "Doc.pdf", "address", "POA", "PDF", bFile, "Draft", "ENG",
				"Jagadishwari", new Timestamp(System.currentTimeMillis()), "Jagadishwari",
				new Timestamp(System.currentTimeMillis()));

		dummyEntity = new DocumentEntity(1, "48690172097499", "sampleZip", "address", "POA", "PDF", bFile, "Draft",
				"ENG", "Jagadishwari", new Timestamp(System.currentTimeMillis()), "Jagadishwari",
				new Timestamp(System.currentTimeMillis()));

		map.put("DocumentId", "1");
		map.put("Status", "Draft");
		documentId = map.get("DocumentId");
		flag = true;

		docEntity.add(entity);
		preId = "98076543218976";

		ExceptionJSONInfo documentErr = null;
		List<ExceptionJSONInfo> err = new ArrayList<>();
		String status;

		List deleteresponseList = new ArrayList<>();
		deleteresponseList.add(StatusCodes.DOCUMENT_DELETE_SUCCESSFUL);
		responsedelete.setResponse(deleteresponseList);

		List responseUploadList = new ArrayList<>();
		responseUploadList.add(StatusCodes.DOCUMENT_UPLOAD_SUCCESSFUL);
		responseUpload.setResponse(responseUploadList);

		List responseCopyList = new ArrayList<>();
		responseCopyList.add(StatusCodes.DOCUMENT_UPLOAD_SUCCESSFUL);
		responseCopy.setResponse(responseCopyList);

		List responseGetList = new ArrayList<>();
		responseGetList.add(entity);
		responseGetAllPreid.setResponse(responseGetList);

	}

	@Test
	public void uploadDocument() {
		Mockito.when(documentRepository.save(Mockito.any())).thenReturn(entity);
		ResponseDto responseDto = documentUploadService.uploadDoucment(mockMultipartFile, documentDto);
		assertEquals(responseDto.getResponse().get(0).toString().trim(),
				responseUpload.getResponse().get(0).toString().trim());
	}

	@Test
	public void deleteDocument() {
		Mockito.when(documentRepository.findBydocumentId(Integer.parseInt(documentId))).thenReturn(entity);
		Mockito.when(documentRepository.deleteAllBydocumentId(Integer.parseInt(documentId))).thenReturn((long) 1);
		ResponseDto responseDto = documentUploadService.deleteDocument(documentId);
		assertEquals(responseDto.getResponse().get(0), responsedelete.getResponse().get(0));
	}

	@Test
	public void deleteAllByPreId() {
		Mockito.when(documentRepository.findBypreregId(preId)).thenReturn(docEntity);
		Mockito.when(documentRepository.deleteAllBypreregId(preId)).thenReturn(docEntity);
		ResponseDto responseDto = documentUploadService.deleteAllByPreId(preId);
		assertEquals(responseDto.getResponse().get(0), responsedelete.getResponse().get(0));
	}

	/*
	 * @Test public void documentCopy() { DocumentEntity documentEntity = entity;
	 * List<DocumentEntity> list = new ArrayList<DocumentEntity>();
	 * list.add(entity); List<String> list2 = new ArrayList<String>();
	 * list2.add("98745632155997");
	 * Mockito.when(documentRepository.save(entity)).thenReturn(documentEntity);
	 * Mockito.when(registrationRepositary.findBygroupIds(ArgumentMatchers.any())).
	 * thenReturn(list2);
	 * Mockito.when(documentRepository.findBypreregId(ArgumentMatchers.any())).
	 * thenReturn(list); Map<String, String> success =
	 * documentUploaderServiceImpl.uploadDoucment(mockMultipartFile, documentDto);
	 * 
	 * assertEquals(map, success);
	 * 
	 * }
	 */

	// @Test
	// public void documentCopy() {
	//
	// Mockito.when(documentRepository.save(entity)).thenReturn(entity);
	//// Mockito.when(documentRepository.findBygroupIds(ArgumentMatchers.any())).thenReturn(list2);
	//// Mockito.when(documentRepository.findBypreregId(ArgumentMatchers.any())).thenReturn(list);
	// Map<String, String> success =
	// documentUploaderServiceImpl.copyDoucment(cat_type, source, destination)
	//
	// assertEquals(map, success);
	//
	// }

	@Test
	public void documentCopy() {
		Mockito.when(documentRepository.findSingleDocument("48690172097498", "POA")).thenReturn(entity);
		Mockito.when(documentRepository.save(Mockito.any())).thenReturn(entity);
		ResponseDto responseDto = documentUploadService.copyDoucment("POA", "48690172097498", "48690172097499");
		assertEquals(responseDto.getResponse().get(0).toString().trim(),
				responseCopy.getResponse().get(0).toString().trim());

	}

	// @Test(expected = DocumentNotFoundException.class)
	// public void failureDocumentCopy() {
	//
	// DocumentNotFoundException exception = new DocumentNotFoundException();
	//
	// Mockito.when(documentRepository.findSingleDocument("1234567891234",
	// "POA")).thenThrow(exception);
	//
	// Mockito.when(documentRepository.save(Mockito.any())).thenThrow(exception);
	//
	// documentUploadService.copyDoucment("POA", "1234567891234", "98076543218976");
	//
	// }

	@Test
	public void getAllDocumentForPreIdTest() {
		Mockito.when(documentRepository.findBypreregId(preId)).thenReturn(docEntity);
		ResponseDto<DocumentEntity> responseDto = documentUploadService.getAllDocumentForPreId(preId);
		assertEquals(responseDto.getResponse().get(0).getPreregId(),
				responseGetAllPreid.getResponse().get(0).getPreregId());
	}

	@Test(expected = DocumentNotFoundException.class)
	public void failureGetAllDocumentForPreIdTest() {
		DocumentNotFoundException exception = new DocumentNotFoundException();
		Mockito.when(documentRepository.findBypreregId(" ")).thenThrow(exception);
		documentUploadService.getAllDocumentForPreId(" ");

	}

	@Test(expected = DocumentNotFoundException.class)
	public void getAllDocumentForPreIdExceptionTest() {
		Mockito.when(documentRepository.findBypreregId("98076543218976")).thenReturn(null);
		documentUploadService.getAllDocumentForPreId("98076543218976");
	}
	
	@Test(expected = DocumentNotValidException.class)
	public void uploadDocumentValidFailureCheck() {
		DocumentNotValidException exception = new DocumentNotValidException();
		Mockito.when(documentUploadService.uploadDoucment(mockMultipartFileDummy, dummyDto)).thenThrow(exception);
		documentUploadService.uploadDoucment(mockMultipartFileDummy, dummyDto);
	}


}
