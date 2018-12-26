package io.mosip.preregistration.documents.test.service;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.virusscanner.spi.VirusScanner;
import io.mosip.preregistration.documents.code.StatusCodes;
import io.mosip.preregistration.documents.dto.DocResponseDTO;
import io.mosip.preregistration.documents.dto.DocumentCopyDTO;
import io.mosip.preregistration.documents.dto.DocumentDTO;
import io.mosip.preregistration.documents.dto.DocumentDeleteDTO;
import io.mosip.preregistration.documents.dto.DocumentGetAllDTO;
import io.mosip.preregistration.documents.dto.ResponseDTO;
import io.mosip.preregistration.documents.entity.DocumentEntity;
import io.mosip.preregistration.documents.exception.DocumentFailedToCopyException;
import io.mosip.preregistration.documents.exception.DocumentFailedToDeleteException;
import io.mosip.preregistration.documents.exception.DocumentFailedToUploadException;
import io.mosip.preregistration.documents.exception.DocumentNotFoundException;
import io.mosip.preregistration.documents.exception.DocumentNotValidException;
import io.mosip.preregistration.documents.exception.DocumentSizeExceedException;
import io.mosip.preregistration.documents.exception.MandatoryFieldNotFoundException;
import io.mosip.preregistration.documents.repository.DocumentRepository;
import io.mosip.preregistration.documents.service.DocumentUploadService;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.FilesystemCephAdapterImpl;

/**
 * Test class to test the DocumentUploadService
 * 
 * @author Rajath KR
 * @author Tapaswini Bahera
 * @author Jagadishwari S
 * @author Kishan Rathore
 * @since 1.0.0
 * 
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DocumentUploadServiceTest {

	@Autowired
	private DocumentUploadService documentUploadService;

	@MockBean
	private FilesystemCephAdapterImpl ceph;

	List<DocumentEntity> docEntity = new ArrayList<>();

	@MockBean
	private DocumentRepository documentRepository;

	@Mock
	private VirusScanner<Boolean, String> virusScan;

	private MockMultipartFile mockMultipartFile;
	private MockMultipartFile mockMultipartFileSizeCheck;
	private MockMultipartFile mockMultipartFileExtnCheck;
	private MockMultipartFile mockMultipartSaveCheck;
	DocumentDTO documentDto = new DocumentDTO("48690172097498", "address", "POA", "PDF", "Pending_Appointment",
			new Timestamp(System.currentTimeMillis()), "Jagadishwari");
	DocumentDTO dummyDto = new DocumentDTO("48690172097499", "address", "POI", "PDF", "Pending_Appointment",
			new Timestamp(System.currentTimeMillis()), "Jagadishwari");
	private DocumentEntity entity;
	private DocumentEntity copyEntity;
	String documentId;
	String preId;
	private Map<String, String> map = new HashMap<>();
	boolean flag;
	ResponseDTO<DocumentDeleteDTO> responsedelete = new ResponseDTO<>();
	DocResponseDTO docResp = new DocResponseDTO();
	DocumentDeleteDTO documentDeleteDTO = null;
	ResponseDTO<DocumentGetAllDTO> responseGetAllPreid = new ResponseDTO<>();
	ResponseDTO<DocResponseDTO> responseUpload = new ResponseDTO<>();
	ResponseDTO<DocumentCopyDTO> responseCopy = new ResponseDTO<>();

	String docJson;
	String errJson;
	File file;

	@Before
	public void setUp() throws URISyntaxException, FileNotFoundException, IOException {

		docJson = "{\r\n" + "	\"id\": \"mosip.pre-registration.document.upload\",\r\n" + "	\"ver\": \"1.0\",\r\n"
				+ "	\"reqTime\": \"2018-12-22T08:28:23.057Z\",\r\n" + "	\"request\": {\r\n"
				+ "		\"prereg_id\": \"48690172097498\",\r\n" + "		\"doc_cat_code\": \"POA\",\r\n"
				+ "		\"doc_typ_code\": \"address\",\r\n" + "		\"doc_file_format\": \"pdf\",\r\n"
				+ "		\"status_code\": \"Pending-Appoinment\",\r\n" + "		\"upd_by\": \"9217148168\",\r\n"
				+ "		\"upload_DateTime\": \"2018-12-22T08:28:23.057Z\"\r\n" + "	}\r\n" + "}";
		
		errJson = "{\r\n" + "	\"id\": \"mosip.pre-registration.document.upload\",\r\n" + "	\"ver\": \"1.0\",\r\n"
				+ "	\"reqTime\": \"2018-12-22T08:28:23.057Z\",\r\n" + "	\"request\": {\r\n"
				+ "		\"prereg_id\": \"48690172097498\",\r\n" + "		\"doc_cat_code\": \"\",\r\n"
				+ "		\"doc_typ_code\": \"address\",\r\n" + "		\"doc_file_format\": \"pdf\",\r\n"
				+ "		\"status_code\": \"Pending-Appoinment\",\r\n" + "		\"upd_by\": \"9217148168\",\r\n"
				+ "		\"upload_DateTime\": \"2018-12-22T08:28:23.057Z\"\r\n" + "	}\r\n" + "}";

		ClassLoader classLoader = getClass().getClassLoader();

		URI uri = new URI(classLoader.getResource("Doc.pdf").getFile().trim().replaceAll("\\u0020", "%20"));
		file = new File(uri.getPath());
		byte[] bFile = Files.readAllBytes(file.toPath());

		URI uriExtCheck = new URI(classLoader.getResource("sample2.img").getFile().trim().replaceAll("\\u0020", "%20"));
		File fileExtCheck = new File(uriExtCheck.getPath());

		URI uriSaveCheck = new URI(classLoader.getResource("sample.pdf").getFile().trim().replaceAll("\\u0020", "%20"));
		File fileSaveCheck = new File(uriSaveCheck.getPath());
		
		URI uriFileSize = new URI(classLoader.getResource("SampleSizeTest.pdf").getFile().trim().replaceAll("\\u0020", "%20"));
		File SampleSizeTestFile = new File(uriFileSize.getPath());

		mockMultipartFileSizeCheck = new MockMultipartFile("file", "SampleSizeTest.pdf", "mixed/multipart",
				new FileInputStream(SampleSizeTestFile));

		mockMultipartFileExtnCheck = new MockMultipartFile("file", "sample2.img", "mixed/multipart",
				new FileInputStream(fileExtCheck));

		mockMultipartSaveCheck = new MockMultipartFile("file", "sample.pdf", "mixed/multipart",
				new FileInputStream(fileSaveCheck));

		mockMultipartFile = new MockMultipartFile("file", "Doc.pdf", "mixed/multipart", new FileInputStream(file));

		entity = new DocumentEntity(1, "48690172097498", "Doc.pdf", "address", "POA", "PDF", "Pending_Appointment",
				"ENG", "Jagadishwari", new Timestamp(System.currentTimeMillis()), "Jagadishwari",
				new Timestamp(System.currentTimeMillis()));

		copyEntity = new DocumentEntity(2, "48690172097499", "Doc.pdf", "address", "POA", "PDF", "Pending_Appointment",
				"ENG", "Jagadishwari", new Timestamp(System.currentTimeMillis()), "Jagadishwari",
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
		List<DocResponseDTO> responseUploadList = new ArrayList<>();
		docResp.setResMsg(StatusCodes.DOCUMENT_UPLOAD_SUCCESSFUL.toString());
		responseUploadList.add(docResp);
		responseUpload.setResponse(responseUploadList);
		Mockito.when(virusScan.scanDocument(mockMultipartFile.getBytes())).thenReturn(true);
		Mockito.doReturn(true).when(ceph).storeFile(Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.when(documentRepository.save(Mockito.any())).thenReturn(entity);
		ResponseDTO<DocResponseDTO> responseDto = documentUploadService.uploadDoucment(mockMultipartFile, docJson);
		assertEquals(responseDto.getResponse().get(0).getResMsg(), responseUpload.getResponse().get(0).getResMsg());
	}
	
	@Test(expected=MandatoryFieldNotFoundException.class)
	public void mandatoryFeildNotPresentTesst() throws IOException {
		Mockito.when(virusScan.scanDocument(mockMultipartFile.getBytes())).thenReturn(true);
		ResponseDTO<DocResponseDTO> responseDto = documentUploadService.uploadDoucment(mockMultipartFile, errJson);
	}

	/*
	 * @Test(expected = DocumentVirusScanException.class) public void
	 * uploadDocumentVirusScanFailureTest() throws Exception {
	 * Mockito.when(virusScan.scanDocument(mockMultipartFile.getBytes())).thenReturn
	 * (false); documentUploadService.uploadDoucment(mockMultipartFile,
	 * documentDto); }
	 */


	 @Test(expected = DocumentSizeExceedException.class) 
	 public void uploadDocumentSizeFailurTest() throws IOException {
	 Mockito.when(virusScan.scanDocument(mockMultipartFileSizeCheck.getBytes())).thenReturn(true);
	 documentUploadService.uploadDoucment(mockMultipartFileSizeCheck,docJson); 
	 }
	 

	@Test(expected = DocumentNotValidException.class)
	public void uploadDocumentExtnFailurTest() throws IOException {
		Mockito.when(virusScan.scanDocument(mockMultipartFileExtnCheck.getBytes())).thenReturn(true);
		documentUploadService.uploadDoucment(mockMultipartFileExtnCheck, docJson);
	}

	@Test(expected = DocumentFailedToUploadException.class)
	public void uploadDocumentRepoFailurTest() throws IOException {
		Mockito.when(virusScan.scanDocument(mockMultipartSaveCheck.getBytes())).thenReturn(true);
		Mockito.when(documentRepository.save(entity)).thenReturn(null);
		documentUploadService.uploadDoucment(mockMultipartSaveCheck, docJson);
	}

	@Test
	public void documentCopySuccessTest() throws Exception {
		List<DocumentCopyDTO> docCopyList = new ArrayList<>();
		DocumentCopyDTO copyDcoResDto = new DocumentCopyDTO();
		copyDcoResDto.setSourcePreRegId("48690172097498");
		copyDcoResDto.setSourceDocumnetId("1");
		copyDcoResDto.setDestPreRegId("48690172097499");
		copyDcoResDto.setDestDocumnetId("2");
		System.out.println("DocumentCopyDTO "+copyDcoResDto);
		docCopyList.add(copyDcoResDto);

		responseCopy.setStatus("true");
		responseCopy.setErr(null);
		responseCopy.setResponse(docCopyList);
		responseCopy.setResTime(new Timestamp(System.currentTimeMillis()));

		Mockito.when(documentRepository.findSingleDocument(Mockito.anyString(),Mockito.anyString())).thenReturn(entity);
		Mockito.when(documentRepository.save(Mockito.any())).thenReturn(copyEntity);
		InputStream sourceFile;
			sourceFile = new FileInputStream(file);
			Mockito.doReturn(sourceFile).when(ceph).getFile(Mockito.anyString(), Mockito.anyString());
			Mockito.doReturn(true).when(ceph).storeFile(Mockito.any(), Mockito.any(), Mockito.any());
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
	
	@Test(expected = DocumentFailedToCopyException.class)
	public void documentCopyFailureTest3() {
		Mockito.when(documentRepository.findSingleDocument("48690172097498", "POA")).thenReturn(entity);
		Mockito.when(documentRepository.save(Mockito.any())).thenThrow(DataAccessLayerException.class);
		documentUploadService.copyDoucment("POA", "48690172097498", "48690172097499");
	}
	
	@Test
	public void getAllDocumentForPreIdSuccessTest() throws Exception {
		List<DocumentGetAllDTO> documentGetAllDtos = new ArrayList<>();
		List<DocumentEntity> documentEntities = new ArrayList<>();
		documentEntities.add(entity);
		DocumentGetAllDTO allDocDto = new DocumentGetAllDTO();
		allDocDto.setDoc_cat_code(entity.getDocCatCode());
		allDocDto.setDoc_file_format(entity.getDocFileFormat());
		allDocDto.setDoc_name(entity.getDocName());
		allDocDto.setDoc_id(Integer.toString(entity.getDocumentId()));
		allDocDto.setDoc_typ_code(entity.getDocTypeCode());
		allDocDto.setPrereg_id(entity.getPreregId());
		System.out.println("DocumentGetAllDTO "+allDocDto);
		documentGetAllDtos.add(allDocDto);

		ResponseDTO<DocumentGetAllDTO> responseDto = new ResponseDTO<>();
		responseDto.setResponse(documentGetAllDtos);

		Mockito.when(documentRepository.findBypreregId("98076543218976")).thenReturn(documentEntities);
		InputStream sourceFile = new FileInputStream(file);
		Mockito.doReturn(sourceFile).when(ceph).getFile(Mockito.anyString(), Mockito.anyString());
		ResponseDTO<DocumentGetAllDTO> serviceResponseDto = documentUploadService
				.getAllDocumentForPreId("98076543218976");

		assertEquals(serviceResponseDto.getResponse().get(0).getDoc_id(), responseDto.getResponse().get(0).getDoc_id());
	}

	@Test
	public void getAllDocumentForPreIdTest() throws Exception{
		List<DocumentGetAllDTO> docCopyList = new ArrayList<>();
		DocumentGetAllDTO getAllDto = new DocumentGetAllDTO();
		getAllDto.setPrereg_id("48690172097498");
		docCopyList.add(getAllDto);
		responseGetAllPreid.setResponse(docCopyList);
		Mockito.when(documentRepository.findBypreregId(preId)).thenReturn(docEntity);
		InputStream sourceFile = new FileInputStream(file);
		Mockito.doReturn(sourceFile).when(ceph).getFile(Mockito.anyString(), Mockito.anyString());
		ResponseDTO<DocumentGetAllDTO> responseDto = documentUploadService.getAllDocumentForPreId(preId);
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
		Mockito.doReturn(true).when(ceph).deleteFile(Mockito.anyString(), Mockito.anyString());
		Mockito.when(documentRepository.findBydocumentId(Integer.parseInt(documentId))).thenReturn(entity);
		Mockito.when(documentRepository.deleteAllBydocumentId(Integer.parseInt(documentId))).thenReturn(1);
		ResponseDTO<DocumentDeleteDTO> responseDto = documentUploadService.deleteDocument(documentId);
		assertEquals(responseDto.getResponse().get(0).getResMsg(), responsedelete.getResponse().get(0).getResMsg());
	}

	@Test(expected=DocumentNotFoundException.class)
	public void deleteDocumentFailureTest() {
		Mockito.when(documentRepository.findBydocumentId(Mockito.anyInt())).thenReturn(null);
        documentUploadService.deleteDocument(documentId);

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
		Mockito.doReturn(true).when(ceph).deleteFile(Mockito.anyString(), Mockito.anyString());
		Mockito.when(documentRepository.findBypreregId(preId)).thenReturn(docEntity);
		Mockito.when(documentRepository.deleteAllBypreregId(preId)).thenReturn(1);
		ResponseDTO<DocumentDeleteDTO> responseDto = documentUploadService.deleteAllByPreId(preId);
		assertEquals(responseDto.getResponse().get(0).getDocumnet_Id(),
				delResponseDto.getResponse().get(0).getDocumnet_Id());
	}
	
	@Test(expected = DocumentFailedToDeleteException.class)
	public void deleteFailureTest() {
		Mockito.when(documentRepository.findBydocumentId(Mockito.anyInt())).thenThrow(DataAccessLayerException.class);
		documentUploadService.deleteDocument("1");
	}

	@Test(expected=DocumentNotFoundException.class)
	public void deleteAllByPreIdFailureTest() {
		Mockito.when(documentRepository.findBydocumentId(Mockito.anyInt())).thenReturn(null);
	    documentUploadService.deleteAllByPreId(preId);

	}

	@Test(expected = DocumentFailedToDeleteException.class)
	public void deleteByPreIdFailureTest() {
		Mockito.when(documentRepository.findBypreregId(Mockito.anyString())).thenThrow(DataAccessLayerException.class);
		documentUploadService.deleteAllByPreId("113245675675");
	}
}