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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.core.virusscanner.spi.VirusScanner;
import io.mosip.preregistration.documents.code.StatusCodes;
import io.mosip.preregistration.documents.dto.DocResponseDto;
import io.mosip.preregistration.documents.dto.DocumentCopyDTO;
import io.mosip.preregistration.documents.dto.DocumentDeleteDTO;
import io.mosip.preregistration.documents.dto.DocumentDto;
import io.mosip.preregistration.documents.dto.DocumentGetAllDto;
import io.mosip.preregistration.documents.dto.ExceptionJSONInfoDTO;
import io.mosip.preregistration.documents.dto.ResponseDTO;
import io.mosip.preregistration.documents.entity.DocumentEntity;
import io.mosip.preregistration.documents.errorcodes.ErrorCodes;
import io.mosip.preregistration.documents.errorcodes.ErrorMessages;
import io.mosip.preregistration.documents.exception.DocumentFailedToCopyException;
import io.mosip.preregistration.documents.exception.DocumentFailedToUploadException;
import io.mosip.preregistration.documents.exception.DocumentNotFoundException;
import io.mosip.preregistration.documents.exception.DocumentNotValidException;
import io.mosip.preregistration.documents.exception.DocumentSizeExceedException;
import io.mosip.preregistration.documents.exception.DocumentVirusScanException;
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

	@Mock
	private VirusScanner<Boolean, String> virusScan;

	private MockMultipartFile mockMultipartFile;
	private MockMultipartFile mockMultipartFileSizeCheck;
	private MockMultipartFile mockMultipartFileExtnCheck;
	private MockMultipartFile mockMultipartSaveCheck;
	DocumentDto documentDto = new DocumentDto("48690172097498", "address", "POA", "PDF", "Pending_Appointment",
			new Timestamp(System.currentTimeMillis()), "Jagadishwari");
	DocumentDto dummyDto = new DocumentDto("48690172097499", "address", "POI", "PDF", "Pending_Appointment",
			new Timestamp(System.currentTimeMillis()), "Jagadishwari");
	private DocumentEntity entity;
	private DocumentEntity copyEntity;
	String documentId;
	String preId;
	private Map<String, String> map = new HashMap<>();
	boolean flag;
	ResponseDTO<DocumentDeleteDTO> responsedelete = new ResponseDTO<>();
	DocResponseDto docResp = new DocResponseDto();
	DocumentDeleteDTO documentDeleteDTO = null;
	ResponseDTO<DocumentGetAllDto> responseGetAllPreid = new ResponseDTO<>();
	ResponseDTO<DocResponseDto> responseUpload = new ResponseDTO<>();
	ResponseDTO<DocumentCopyDTO> responseCopy = new ResponseDTO<>();

	@Before
	public void setUp() throws URISyntaxException, FileNotFoundException, IOException {

		ClassLoader classLoader = getClass().getClassLoader();

		URI uri = new URI(classLoader.getResource("Doc.pdf").getFile().trim().replaceAll("\\u0020", "%20"));
		File file = new File(uri.getPath());
		byte[] bFile = Files.readAllBytes(file.toPath());

		URI uriExtCheck = new URI(classLoader.getResource("sample2.img").getFile().trim().replaceAll("\\u0020", "%20"));
		File fileExtCheck = new File(uriExtCheck.getPath());

		URI uriSaveCheck = new URI(classLoader.getResource("sample.pdf").getFile().trim().replaceAll("\\u0020", "%20"));
		File fileSaveCheck = new File(uriSaveCheck.getPath());

		mockMultipartFileSizeCheck = new MockMultipartFile("file", "SampleSizeTest.pdf", "mixed/multipart",
				new FileInputStream(file));

		mockMultipartFileExtnCheck = new MockMultipartFile("file", "sample2.img", "mixed/multipart",
				new FileInputStream(fileExtCheck));

		mockMultipartSaveCheck = new MockMultipartFile("file", "sample.pdf", "mixed/multipart",
				new FileInputStream(fileSaveCheck));

		mockMultipartFile = new MockMultipartFile("file", "Doc.pdf", "mixed/multipart", new FileInputStream(file));

		entity = new DocumentEntity(1, "48690172097498", "Doc.pdf", "address", "POA", "PDF", bFile,
				"Pending_Appointment", "ENG", "Jagadishwari", new Timestamp(System.currentTimeMillis()), "Jagadishwari",
				new Timestamp(System.currentTimeMillis()));

		copyEntity = new DocumentEntity(2, "48690172097499", "Doc.pdf", "address", "POA", "PDF", bFile,
				"Pending_Appointment", "ENG", "Jagadishwari", new Timestamp(System.currentTimeMillis()), "Jagadishwari",
				new Timestamp(System.currentTimeMillis()));

		map.put("DocumentId", "1");
		map.put("Status", "Pending_Appointment");
		documentId = map.get("DocumentId");
		flag = true;

		docEntity.add(entity);
		preId = "98076543218976";
	}

	@Test
	public void uploadDocumentSuccessTest() throws IOException {
		List<DocResponseDto> responseUploadList = new ArrayList<>();
		docResp.setResMsg(StatusCodes.DOCUMENT_UPLOAD_SUCCESSFUL.toString());
		responseUploadList.add(docResp);
		responseUpload.setResponse(responseUploadList);
		Mockito.when(virusScan.scanDocument(mockMultipartFile.getBytes())).thenReturn(true);
		Mockito.when(documentRepository.save(Mockito.any())).thenReturn(entity);
		ResponseDTO<DocResponseDto> responseDto = documentUploadService.uploadDoucment(mockMultipartFile, documentDto);
		assertEquals(responseDto.getResponse().get(0).getResMsg(), responseUpload.getResponse().get(0).getResMsg());
	}

	@Test(expected = DocumentVirusScanException.class)
	public void uploadDocumentVirusScanFailureTest() throws Exception {
		Mockito.when(virusScan.scanDocument(mockMultipartFile.getBytes())).thenReturn(false);
		documentUploadService.uploadDoucment(mockMultipartFile, documentDto);
	}

	/*@Test(expected = DocumentSizeExceedException.class)
	public void uploadDocumentSizeFailurTest() throws IOException {
		Mockito.when(virusScan.scanDocument(mockMultipartFileSizeCheck.getBytes())).thenReturn(true);
		documentUploadService.uploadDoucment(mockMultipartFileSizeCheck, documentDto);
	}*/

	@Test(expected = DocumentNotValidException.class)
	public void uploadDocumentExtnFailurTest() throws IOException {
		Mockito.when(virusScan.scanDocument(mockMultipartFileExtnCheck.getBytes())).thenReturn(true);
		documentUploadService.uploadDoucment(mockMultipartFileExtnCheck, documentDto);
	}

	@Test(expected = DocumentFailedToUploadException.class)
	public void uploadDocumentRepoFailurTest() throws IOException {
		Mockito.when(virusScan.scanDocument(mockMultipartSaveCheck.getBytes())).thenReturn(true);
		Mockito.when(documentRepository.save(entity)).thenReturn(null);
		documentUploadService.uploadDoucment(mockMultipartSaveCheck, documentDto);
	}

	@Test
	public void documentCopySuccessTest() {
		List<DocumentCopyDTO> docCopyList = new ArrayList<>();
		DocumentCopyDTO copyDcoResDto = new DocumentCopyDTO();
		copyDcoResDto.setSourcePreRegId("48690172097498");
		copyDcoResDto.setSourceDocumnetId("1");
		copyDcoResDto.setDestPreRegId("48690172097499");
		copyDcoResDto.setDestDocumnetId("2");
		docCopyList.add(copyDcoResDto);

		responseCopy.setStatus("true");
		responseCopy.setErr(null);
		responseCopy.setResponse(docCopyList);
		responseCopy.setResTime(new Timestamp(System.currentTimeMillis()));

		Mockito.when(documentRepository.findSingleDocument("48690172097498", "POA")).thenReturn(entity);
		Mockito.when(documentRepository.save(Mockito.any())).thenReturn(copyEntity);
		ResponseDTO<DocumentCopyDTO> responseDto = documentUploadService.copyDoucment("POA", "48690172097498",
				"48690172097499");
		assertEquals(responseDto.getResponse().get(0).getDestDocumnetId(),
				responseCopy.getResponse().get(0).getDestDocumnetId());
	}

	@Test(expected = DocumentNotFoundException.class)
	public void documentCopyFailureTest1() {
		Mockito.when(documentRepository.findSingleDocument("48690172097498", "POA")).thenReturn(null);
		documentUploadService.copyDoucment("POA", "48690172097498", "48690172097499");
	}

	@Test(expected = DocumentFailedToCopyException.class)
	public void documentCopyFailureTest2() {
		Mockito.when(documentRepository.findSingleDocument("48690172097498", "POA")).thenReturn(entity);
		Mockito.when(documentRepository.save(Mockito.any())).thenReturn(null);
		documentUploadService.copyDoucment("POA", "48690172097498", "48690172097499");
	}

	@Test
	public void getAllDocumentForPreIdSuccessTest() {
		List<DocumentGetAllDto> documentGetAllDtos = new ArrayList<>();
		List<DocumentEntity> documentEntities = new ArrayList<>();
		documentEntities.add(entity);

		DocumentGetAllDto allDocDto = new DocumentGetAllDto();
		allDocDto.setDoc_cat_code(entity.getDocCatCode());
		allDocDto.setDoc_file_format(entity.getDocFileFormat());
		allDocDto.setDoc_name(entity.getDocName());
		allDocDto.setDoc_id(Integer.toString(entity.getDocumentId()));
		allDocDto.setDoc_typ_code(entity.getDocTypeCode());
		allDocDto.setMultipartFile(entity.getDocStore());
		allDocDto.setPrereg_id(entity.getPreregId());
		documentGetAllDtos.add(allDocDto);

		ResponseDTO<DocumentGetAllDto> responseDto = new ResponseDTO<>();
		responseDto.setResponse(documentGetAllDtos);

		Mockito.when(documentRepository.findBypreregId("98076543218976")).thenReturn(documentEntities);

		ResponseDTO<DocumentGetAllDto> serviceResponseDto = documentUploadService
				.getAllDocumentForPreId("98076543218976");

		assertEquals(serviceResponseDto.getResponse().get(0).getDoc_id(), responseDto.getResponse().get(0).getDoc_id());
	}

	@Test
	public void getAllDocumentForPreIdTest() {
		List<DocumentGetAllDto> docCopyList = new ArrayList<>();
		DocumentGetAllDto getAllDto = new DocumentGetAllDto();
		getAllDto.setPrereg_id("48690172097498");
		docCopyList.add(getAllDto);
		responseGetAllPreid.setResponse(docCopyList);
		Mockito.when(documentRepository.findBypreregId(preId)).thenReturn(docEntity);
		ResponseDTO<DocumentGetAllDto> responseDto = documentUploadService.getAllDocumentForPreId(preId);
		assertEquals(responseDto.getResponse().get(0).getPrereg_id(),
				responseGetAllPreid.getResponse().get(0).getPrereg_id());
	}

	@Test(expected = DocumentNotFoundException.class)
	public void getAllDocumentForPreIdExceptionTest() {
		Mockito.when(documentRepository.findBypreregId("98076543218976")).thenReturn(null);
		documentUploadService.getAllDocumentForPreId("98076543218976");
	}

	@Test
	public void deleteDocumentSuccessTest() {
		List<DocumentDeleteDTO> deleteresponseList = new ArrayList<>();
		documentDeleteDTO = new DocumentDeleteDTO();
		documentDeleteDTO.setDocumnet_Id("1");
		documentDeleteDTO.setResMsg(StatusCodes.DOCUMENT_DELETE_SUCCESSFUL.toString());
		deleteresponseList.add(documentDeleteDTO);

		responsedelete.setResponse(deleteresponseList);
		Mockito.when(documentRepository.findBydocumentId(Integer.parseInt(documentId))).thenReturn(entity);
		Mockito.when(documentRepository.deleteAllBydocumentId(Integer.parseInt(documentId))).thenReturn(1);
		ResponseDTO<DocumentDeleteDTO> responseDto = documentUploadService.deleteDocument(documentId);
		assertEquals(responseDto.getResponse().get(0).getResMsg(), responsedelete.getResponse().get(0).getResMsg());
	}

	@Test
	public void deleteDocumentFailureTest() {
		ResponseDTO<DocumentDeleteDTO> delResponseDto = new ResponseDTO<>();
		ExceptionJSONInfoDTO documentErr = new ExceptionJSONInfoDTO(ErrorCodes.PRG_PAM_DOC_005.toString(),
				ErrorMessages.DOCUMENT_NOT_PRESENT.toString());
		delResponseDto.setStatus("false");
		delResponseDto.setErr(documentErr);

		Mockito.when(documentRepository.findBydocumentId(Mockito.anyInt())).thenReturn(null);

		ResponseDTO<DocumentDeleteDTO> responseDto = documentUploadService.deleteDocument(documentId);
		assertEquals(responseDto.getErr().getErrorCode(), delResponseDto.getErr().getErrorCode());
	}

	@Test
	public void deleteAllByPreIdSuccessTest() {
		List<DocumentDeleteDTO> deleteresponseList = new ArrayList<>();
		documentDeleteDTO = new DocumentDeleteDTO();
		documentDeleteDTO.setDocumnet_Id("1");
		documentDeleteDTO.setResMsg(StatusCodes.DOCUMENT_DELETE_SUCCESSFUL.toString());
		deleteresponseList.add(documentDeleteDTO);

		ResponseDTO<DocumentDeleteDTO> delResponseDto = new ResponseDTO<>();
		delResponseDto.setResponse(deleteresponseList);

		Mockito.when(documentRepository.findBypreregId(preId)).thenReturn(docEntity);
		Mockito.when(documentRepository.deleteAllBypreregId(preId)).thenReturn(docEntity);
		ResponseDTO<DocumentDeleteDTO> responseDto = documentUploadService.deleteAllByPreId(preId);
		assertEquals(responseDto.getResponse().get(0).getDocumnet_Id(),
				delResponseDto.getResponse().get(0).getDocumnet_Id());
	}

	@Test
	public void deleteAllByPreIdFailureTest() {
		ResponseDTO<DocumentDeleteDTO> delResponseDto = new ResponseDTO<>();
		ExceptionJSONInfoDTO documentErr = new ExceptionJSONInfoDTO(ErrorCodes.PRG_PAM_DOC_005.toString(),
				ErrorMessages.DOCUMENT_NOT_PRESENT.toString());
		delResponseDto.setStatus("false");
		delResponseDto.setErr(documentErr);

		Mockito.when(documentRepository.findBydocumentId(Mockito.anyInt())).thenReturn(null);

		ResponseDTO<DocumentDeleteDTO> responseDto = documentUploadService.deleteAllByPreId(preId);
		assertEquals(responseDto.getErr().getErrorCode(), delResponseDto.getErr().getErrorCode());
	}

}
