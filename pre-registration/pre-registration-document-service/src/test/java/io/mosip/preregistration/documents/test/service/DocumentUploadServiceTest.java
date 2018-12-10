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
import io.mosip.preregistration.documents.dto.DocResponseDto;
import io.mosip.preregistration.documents.dto.DocumentDto;
import io.mosip.preregistration.documents.dto.DocumentGetAllDto;
import io.mosip.preregistration.documents.dto.ExceptionJSONInfo;
import io.mosip.preregistration.documents.dto.ResponseDto;
import io.mosip.preregistration.documents.entity.DocumentEntity;
import io.mosip.preregistration.documents.errorcodes.ErrorCodes;
import io.mosip.preregistration.documents.exception.DocumentNotFoundException;
import io.mosip.preregistration.documents.exception.DocumentNotValidException;
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
	DocumentDto documentDto = new DocumentDto("48690172097498", "address", "POA", "PDF", "Pending_Appointment",new Timestamp(System.currentTimeMillis()),
			"Jagadishwari");
	DocumentDto dummyDto = new DocumentDto("48690172097499", "address", "POI", "PDF", "Pending_Appointment", new Timestamp(System.currentTimeMillis()),
			"Jagadishwari");
	private DocumentEntity entity;
	private DocumentEntity dummyEntity;
	String documentId;
	String preId;
	private Map<String, String> map = new HashMap<String, String>();
	boolean flag;
	ResponseDto<DocResponseDto> responsedelete = new ResponseDto<DocResponseDto>();
	DocResponseDto docResp= new DocResponseDto();
	ResponseDto<DocumentGetAllDto> responseGetAllPreid = new ResponseDto<DocumentGetAllDto>();
	ResponseDto<DocResponseDto> responseUpload = new ResponseDto<DocResponseDto>();
	ResponseDto<DocResponseDto> responseCopy = new ResponseDto<DocResponseDto>();

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
		
		entity = new DocumentEntity(1, "48690172097498", "Doc.pdf", "address", "POA", "PDF", bFile, "Pending_Appointment", "ENG",
				"Jagadishwari", new Timestamp(System.currentTimeMillis()), "Jagadishwari",
				new Timestamp(System.currentTimeMillis()));

		dummyEntity = new DocumentEntity(1, "48690172097499", "sampleZip", "address", "POA", "PDF", bFile, "Pending_Appointment",
				"ENG", "Jagadishwari", new Timestamp(System.currentTimeMillis()), "Jagadishwari",
				new Timestamp(System.currentTimeMillis()));

		map.put("DocumentId", "1");
		map.put("Status", "Pending_Appointment");
		documentId = map.get("DocumentId");
		flag = true;

		docEntity.add(entity);
		preId = "98076543218976";

		ExceptionJSONInfo documentErr = null;
		List<ExceptionJSONInfo> err = new ArrayList<>();
		String status;

	}

	/*@Test
	public void uploadDocument() {
		List<DocResponseDto> responseUploadList = new ArrayList<>();
		docResp.setResMsg(StatusCodes.DOCUMENT_UPLOAD_SUCCESSFUL.toString());
		responseUploadList.add(docResp);
		responseUpload.setResponse(responseUploadList);
		Mockito.when(documentRepository.save(Mockito.any())).thenReturn(entity);
	    ResponseDto<DocResponseDto> responseDto = documentUploadService.uploadDoucment(mockMultipartFile, documentDto);
		logger.info("Response "+responseDto);
	    assertEquals(responseDto.getResponse().get(0).getResMsg(),
				responseUpload.getResponse().get(0).getResMsg());
	}*/

	@Test
	public void deleteDocument() {
		List<DocResponseDto> deleteresponseList = new ArrayList<>();
		docResp.setResMsg(StatusCodes.DOCUMENT_DELETE_SUCCESSFUL.toString());
		deleteresponseList.add(docResp);
		responsedelete.setResponse(deleteresponseList);
		Mockito.when(documentRepository.findBydocumentId(Integer.parseInt(documentId))).thenReturn(entity);
		Mockito.when(documentRepository.deleteAllBydocumentId(Integer.parseInt(documentId))).thenReturn((long) 1);
		ResponseDto<DocResponseDto> responseDto = documentUploadService.deleteDocument(documentId);
		assertEquals(responseDto.getResponse().get(0).getResMsg(), responsedelete.getResponse().get(0).getResMsg());
	}

	@Test
	public void deleteAllByPreId() {
		List<DocResponseDto> deleteresponseList = new ArrayList<>();
		docResp.setResMsg(StatusCodes.DOCUMENT_DELETE_SUCCESSFUL.toString());
		deleteresponseList.add(docResp);
		responsedelete.setResponse(deleteresponseList);
		Mockito.when(documentRepository.findBypreregId(preId)).thenReturn(docEntity);
		Mockito.when(documentRepository.deleteAllBypreregId(preId)).thenReturn(docEntity);
		ResponseDto<DocResponseDto> responseDto = documentUploadService.deleteAllByPreId(preId);
		assertEquals(responseDto.getResponse().get(0).getResMsg(), responsedelete.getResponse().get(0).getResMsg());
	}



	@Test
	public void documentCopy() {
		List<DocResponseDto> docCopyList = new ArrayList<>();
		docResp.setResMsg(StatusCodes.DOCUMENT_UPLOAD_SUCCESSFUL.toString());
		docCopyList.add(docResp);
		responseCopy.setResponse(docCopyList);
		Mockito.when(documentRepository.findSingleDocument("48690172097498", "POA")).thenReturn(entity);
		Mockito.when(documentRepository.save(Mockito.any())).thenReturn(entity);
		ResponseDto<DocResponseDto> responseDto = documentUploadService.copyDoucment("POA", "48690172097498", "48690172097499");
		assertEquals(responseDto.getResponse().get(0).getResMsg(),
				responseCopy.getResponse().get(0).getResMsg());

	}


	@Test
	public void getAllDocumentForPreIdTest() {
		List<DocumentGetAllDto> docCopyList = new ArrayList<>();
        DocumentGetAllDto getAllDto= new DocumentGetAllDto();
        getAllDto.setPrereg_id("48690172097498");
        docCopyList.add(getAllDto);
		responseGetAllPreid.setResponse(docCopyList);
		logger.info("Doc Dto "+getAllDto);
		Mockito.when(documentRepository.findBypreregId(preId)).thenReturn(docEntity);
		ResponseDto<DocumentGetAllDto> responseDto = documentUploadService.getAllDocumentForPreId(preId);
		assertEquals(responseDto.getResponse().get(0).getPrereg_id(),
				responseGetAllPreid.getResponse().get(0).getPrereg_id());
	}

	@Test(expected = DocumentNotFoundException.class)
	public void failureGetAllDocumentForPreIdTest() {
		ArrayList<ExceptionJSONInfo> err = new ArrayList<>();
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(ErrorCodes.PRG_PAM_DOC_007.toString(),
				StatusCodes.DOCUMENT_EXCEEDING_PERMITTED_SIZE.toString());
		err.add(errorDetails);
		ResponseDto errorRes = new ResponseDto<>();
		errorRes.setErr(err);
		errorRes.setStatus("False");
		errorRes.setResTime(new Timestamp(System.currentTimeMillis()));
		DocumentNotFoundException exception = new DocumentNotFoundException();
		Mockito.when(documentRepository.findBypreregId("1234577746395")).thenReturn(null);
		ResponseDto responseDto =documentUploadService.getAllDocumentForPreId("1234577746395");
		System.out.println(responseDto.getStatus());
	}

	@Test(expected = DocumentNotFoundException.class)
	public void getAllDocumentForPreIdExceptionTest() {
		Mockito.when(documentRepository.findBypreregId("98076543218976")).thenReturn(null);
		documentUploadService.getAllDocumentForPreId("98076543218976");
	}
	
	/*@Test(expected = DocumentNotValidException.class)
	public void uploadDocumentValidFailureCheck() {
		DocumentNotValidException exception = new DocumentNotValidException();
		Mockito.when(documentUploadService.uploadDoucment(mockMultipartFileDummy, dummyDto)).thenThrow(exception);
		documentUploadService.uploadDoucment(mockMultipartFileDummy, dummyDto);
	}*/


}
