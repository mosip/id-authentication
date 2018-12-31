package io.mosip.preregistration.documents.test.service;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
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
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.virusscanner.spi.VirusScanner;
import io.mosip.preregistration.documents.code.StatusCodes;
import io.mosip.preregistration.documents.dto.DocumentCopyResponseDTO;
import io.mosip.preregistration.documents.dto.DocumentDeleteResponseDTO;
import io.mosip.preregistration.documents.dto.DocumentMultipartResponseDTO;
import io.mosip.preregistration.documents.dto.DocumentRequestDTO;
import io.mosip.preregistration.documents.dto.DocumentResponseDTO;
import io.mosip.preregistration.documents.dto.MainListResponseDTO;
import io.mosip.preregistration.documents.entity.DocumentEntity;
import io.mosip.preregistration.documents.exception.DocumentFailedToCopyException;
import io.mosip.preregistration.documents.exception.DocumentFailedToDeleteException;
import io.mosip.preregistration.documents.exception.DocumentFailedToUploadException;
import io.mosip.preregistration.documents.exception.DocumentNotFoundException;
import io.mosip.preregistration.documents.exception.DocumentNotValidException;
import io.mosip.preregistration.documents.exception.DocumentSizeExceedException;
import io.mosip.preregistration.documents.exception.MandatoryFieldNotFoundException;
import io.mosip.preregistration.documents.repository.DocumentRepository;
import io.mosip.preregistration.documents.service.DocumentService;
import io.mosip.preregistration.documents.service.util.DocumentServiceUtil;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.FilesystemCephAdapterImpl;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DocumentUploadServiceTest {

	@Autowired
	private DocumentService documentUploadService;

	@Autowired
	private DocumentServiceUtil serviceUtil;
	
	@MockBean
	RestTemplateBuilder restTemplateBuilder; 

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
	DocumentRequestDTO documentDto = new DocumentRequestDTO("48690172097498", "address", "POA", "PDF",
			"Pending_Appointment", new Date(), "ENG", "Jagadishwari");
	DocumentRequestDTO dummyDto = new DocumentRequestDTO("48690172097499", "address", "POI", "PDF",
			"Pending_Appointment", new Date(), "ENG", "Jagadishwari");
	private DocumentEntity entity;
	private DocumentEntity copyEntity;
	String documentId;
	String preId;
	private Map<String, String> map = new HashMap<>();
	boolean flag;
	MainListResponseDTO<DocumentDeleteResponseDTO> responsedelete = new MainListResponseDTO<>();
	DocumentResponseDTO docResp = new DocumentResponseDTO();
	DocumentDeleteResponseDTO documentDeleteDTO = null;
	MainListResponseDTO<DocumentMultipartResponseDTO> responseGetAllPreid = new MainListResponseDTO<>();
	MainListResponseDTO<DocumentResponseDTO> responseUpload = new MainListResponseDTO<>();
	MainListResponseDTO<DocumentCopyResponseDTO> responseCopy = new MainListResponseDTO<>();

	String docJson;
	String errJson;
	File file;

	@Before
	public void setUp() throws URISyntaxException, FileNotFoundException, IOException {

		/*docJson = "{\r\n" + "	\"id\": \"mosip.pre-registration.document.upload\",\r\n" + "	\"ver\": \"1.0\",\r\n"
				+ "	\"reqTime\": \"2018-12-22T08:28:23.057Z\",\r\n" + "	\"request\": {\r\n"
				+ "		\"prereg_id\": \"48690172097498\",\r\n" + "		\"doc_cat_code\": \"POA\",\r\n"
				+ "		\"doc_typ_code\": \"address\",\r\n" + "		\"doc_file_format\": \"pdf\",\r\n"
				+ "		\"status_code\": \"Pending-Appoinment\",\r\n" + "		\"upd_by\": \"9217148168\",\r\n"
				+ "		\"upload_DateTime\": \"2018-12-22T08:28:23.057Z\"\r\n" + "	}\r\n" + "}";
*/
		
		docJson = "{\"id\": \"mosip.pre-registration.document.upload\",\"ver\" : \"1.0\","
				+ "\"reqTime\" : \"2018-12-28T05:23:08.019Z\",\"request\" :"
				+ "{\"pre_registartion_id\" : \"86710482195706\",\"doc_cat_code\" "
				+ ": \"POA\",\"doc_typ_code\" : \"address\",\"lang_code\":\"ENG\","
				+ "\"doc_file_format\" : \"pdf\",\"status_code\" : \"Pending-Appoinment\","
				+ "\"upload_by\" : \"9900806086\",\"upload_date_time\" : \"2018-12-28T05:23:08.019Z\"}}" ;
 

		errJson = "{\"id\": \"mosip.pre-registration.document.upload\",\"ver\" : \"1.0\","
				+ "\"reqTime\" : \"2018-12-28T05:23:08.019Z\",\"request\" :"
				+ "{\"pre_registartion_id\" : \"86710482195706\",\"doc_cat_code\" "
				+ ": \"\",\"doc_typ_code\" : \"address\",\"lang_code\":\"ENG\","
				+ "\"doc_file_format\" : \"pdf\",\"status_code\" : \"Pending-Appoinment\","
				+ "\"upload_by\" : \"9900806086\",\"upload_date_time\" : \"2018-12-28T05:23:08.019Z\"}}" ;

		ClassLoader classLoader = getClass().getClassLoader();

		URI uri = new URI(classLoader.getResource("Doc.pdf").getFile().trim().replaceAll("\\u0020", "%20"));
		file = new File(uri.getPath());
		// byte[] bFile = Files.readAllBytes(file.toPath());

		URI uriExtCheck = new URI(classLoader.getResource("sample2.img").getFile().trim().replaceAll("\\u0020", "%20"));
		File fileExtCheck = new File(uriExtCheck.getPath());

		URI uriSaveCheck = new URI(classLoader.getResource("sample.pdf").getFile().trim().replaceAll("\\u0020", "%20"));
		File fileSaveCheck = new File(uriSaveCheck.getPath());

		URI uriFileSize = new URI(
				classLoader.getResource("SampleSizeTest.pdf").getFile().trim().replaceAll("\\u0020", "%20"));
		File SampleSizeTestFile = new File(uriFileSize.getPath());

		mockMultipartFileSizeCheck = new MockMultipartFile("file", "SampleSizeTest.pdf", "mixed/multipart",
				new FileInputStream(SampleSizeTestFile));

		mockMultipartFileExtnCheck = new MockMultipartFile("file", "sample2.img", "mixed/multipart",
				new FileInputStream(fileExtCheck));

		mockMultipartSaveCheck = new MockMultipartFile("file", "sample.pdf", "mixed/multipart",
				new FileInputStream(fileSaveCheck));

		mockMultipartFile = new MockMultipartFile("file", "Doc.pdf", "mixed/multipart", new FileInputStream(file));

		entity = new DocumentEntity("1", "48690172097498", "Doc.pdf", "address", "POA", "PDF", "Pending_Appointment",
				"ENG", "Jagadishwari", DateUtils.parseDateToLocalDateTime(new Date()), "Jagadishwari",
				DateUtils.parseDateToLocalDateTime(new Date()));

		copyEntity = new DocumentEntity("2", "48690172097499", "Doc.pdf", "address", "POA", "PDF", "Pending_Appointment",
				"ENG", "Jagadishwari", DateUtils.parseDateToLocalDateTime(new Date()), "Jagadishwari",
				DateUtils.parseDateToLocalDateTime(new Date()));

		map.put("DocumentId", "1");
		map.put("Status", "Pending_Appointment");
		documentId = map.get("DocumentId");
		flag = true;

		docEntity.add(entity);
		preId = "98076543218976";
	}

	@Test
	public void uploadDocumentSuccessTest() throws IOException {
		List<DocumentResponseDTO> responseUploadList = new ArrayList<>();
		MainListResponseDTO restRes= new MainListResponseDTO<>();
		docResp.setResMsg(StatusCodes.DOCUMENT_UPLOAD_SUCCESSFUL.toString());
		responseUploadList.add(docResp);
		responseUpload.setResponse(responseUploadList);
		RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
		Mockito.when(restTemplateBuilder.build()).thenReturn(restTemplate);
		restRes.setStatus(true);

		ResponseEntity<MainListResponseDTO> rescenter = new ResponseEntity<>(restRes, HttpStatus.OK);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
				Mockito.eq(MainListResponseDTO.class))).thenReturn(rescenter);
		Mockito.when(virusScan.scanDocument(mockMultipartFile.getBytes())).thenReturn(true);
		Mockito.doReturn(true).when(ceph).storeFile(Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.when(documentRepository.save(Mockito.any())).thenReturn(entity);
		MainListResponseDTO<DocumentResponseDTO> responseDto = documentUploadService.uploadDoucment(mockMultipartFile,
				docJson);
		assertEquals(responseDto.getResponse().get(0).getResMsg(), responseUpload.getResponse().get(0).getResMsg());
	}

	@Test(expected = MandatoryFieldNotFoundException.class)
	public void mandatoryFeildNotPresentTesst() throws IOException {
		Mockito.when(virusScan.scanDocument(mockMultipartFile.getBytes())).thenReturn(true);
		documentUploadService.uploadDoucment(mockMultipartFile, errJson);
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
		documentUploadService.uploadDoucment(mockMultipartFileSizeCheck, docJson);
	}

	@Test(expected = DocumentNotValidException.class)
	public void uploadDocumentExtnFailurTest() throws IOException {
		Mockito.when(virusScan.scanDocument(mockMultipartFileExtnCheck.getBytes())).thenReturn(true);
		documentUploadService.uploadDoucment(mockMultipartFileExtnCheck, docJson);
	}

	@Test(expected = DocumentFailedToUploadException.class)
	public void uploadDocumentRepoFailurTest() throws IOException {
		MainListResponseDTO restRes= new MainListResponseDTO<>();
		RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
		Mockito.when(restTemplateBuilder.build()).thenReturn(restTemplate);
		restRes.setStatus(true);
		ResponseEntity<MainListResponseDTO> rescenter = new ResponseEntity<>(restRes, HttpStatus.OK);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
				Mockito.eq(MainListResponseDTO.class))).thenReturn(rescenter);
		Mockito.when(virusScan.scanDocument(mockMultipartSaveCheck.getBytes())).thenReturn(true);
		Mockito.when(documentRepository.save(entity)).thenReturn(null);
		documentUploadService.uploadDoucment(mockMultipartSaveCheck, docJson);
	}

	@Test
	public void documentCopySuccessTest() throws Exception {
		List<DocumentCopyResponseDTO> docCopyList = new ArrayList<>();
		DocumentCopyResponseDTO copyDcoResDto = new DocumentCopyResponseDTO();
		copyDcoResDto.setSourcePreRegId("48690172097498");
		copyDcoResDto.setSourceDocumnetId("1");
		copyDcoResDto.setDestPreRegId("48690172097499");
		copyDcoResDto.setDestDocumnetId("2");
		System.out.println("DocumentCopyDTO " + copyDcoResDto);
		docCopyList.add(copyDcoResDto);

		responseCopy.setStatus(true);
		responseCopy.setResponse(docCopyList);
		responseCopy.setResTime(serviceUtil.getCurrentResponseTime());

		Mockito.when(documentRepository.findSingleDocument(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(entity);
		Mockito.when(documentRepository.save(Mockito.any())).thenReturn(copyEntity);
		InputStream sourceFile;
		sourceFile = new FileInputStream(file);
		Mockito.doReturn(sourceFile).when(ceph).getFile(Mockito.anyString(), Mockito.anyString());
		Mockito.doReturn(true).when(ceph).storeFile(Mockito.any(), Mockito.any(), Mockito.any());
		MainListResponseDTO<DocumentCopyResponseDTO> responseDto = documentUploadService.copyDoucment("POA",
				"48690172097498", "48690172097499");
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
		List<DocumentMultipartResponseDTO> documentGetAllDtos = new ArrayList<>();
		List<DocumentEntity> documentEntities = new ArrayList<>();
		documentEntities.add(entity);
		DocumentMultipartResponseDTO allDocDto = new DocumentMultipartResponseDTO();
		allDocDto.setDoc_cat_code(entity.getDocCatCode());
		allDocDto.setDoc_file_format(entity.getDocFileFormat());
		allDocDto.setDoc_name(entity.getDocName());
		allDocDto.setDoc_id(entity.getDocumentId());
		allDocDto.setDoc_typ_code(entity.getDocTypeCode());
		allDocDto.setPrereg_id(entity.getPreregId());
		System.out.println("DocumentGetAllDTO " + allDocDto);
		documentGetAllDtos.add(allDocDto);

		MainListResponseDTO<DocumentMultipartResponseDTO> responseDto = new MainListResponseDTO<>();
		responseDto.setResponse(documentGetAllDtos);

		Mockito.when(documentRepository.findBypreregId("98076543218976")).thenReturn(documentEntities);
		InputStream sourceFile = new FileInputStream(file);
		Mockito.doReturn(sourceFile).when(ceph).getFile(Mockito.anyString(), Mockito.anyString());
		MainListResponseDTO<DocumentMultipartResponseDTO> serviceResponseDto = documentUploadService
				.getAllDocumentForPreId("98076543218976");

		assertEquals(serviceResponseDto.getResponse().get(0).getDoc_id(), responseDto.getResponse().get(0).getDoc_id());
	}

	@Test
	public void getAllDocumentForPreIdTest() throws Exception {
		List<DocumentMultipartResponseDTO> docCopyList = new ArrayList<>();
		DocumentMultipartResponseDTO getAllDto = new DocumentMultipartResponseDTO();
		getAllDto.setPrereg_id("48690172097498");
		docCopyList.add(getAllDto);
		responseGetAllPreid.setResponse(docCopyList);
		Mockito.when(documentRepository.findBypreregId(preId)).thenReturn(docEntity);
		InputStream sourceFile = new FileInputStream(file);
		Mockito.doReturn(sourceFile).when(ceph).getFile(Mockito.anyString(), Mockito.anyString());
		MainListResponseDTO<DocumentMultipartResponseDTO> responseDto = documentUploadService
				.getAllDocumentForPreId(preId);
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
		List<DocumentDeleteResponseDTO> deleteresponseList = new ArrayList<>();
		documentDeleteDTO = new DocumentDeleteResponseDTO();
		documentDeleteDTO.setDocumnet_Id("1");
		documentDeleteDTO.setResMsg(StatusCodes.DOCUMENT_DELETE_SUCCESSFUL.toString());
		deleteresponseList.add(documentDeleteDTO);

		responsedelete.setResponse(deleteresponseList);
		Mockito.doReturn(true).when(ceph).deleteFile(Mockito.anyString(), Mockito.anyString());
		Mockito.when(documentRepository.findBydocumentId(Integer.parseInt(documentId))).thenReturn(entity);
		Mockito.when(documentRepository.deleteAllBydocumentId(Integer.parseInt(documentId))).thenReturn(1);
		MainListResponseDTO<DocumentDeleteResponseDTO> responseDto = documentUploadService.deleteDocument(documentId);
		assertEquals(responseDto.getResponse().get(0).getResMsg(), responsedelete.getResponse().get(0).getResMsg());
	}

	@Test(expected = DocumentNotFoundException.class)
	public void deleteDocumentFailureTest() {
		Mockito.when(documentRepository.findBydocumentId(Mockito.anyInt())).thenReturn(null);
		documentUploadService.deleteDocument(documentId);

	}

	@Test
	public void deleteAllByPreIdSuccessTest() {
		List<DocumentDeleteResponseDTO> deleteresponseList = new ArrayList<>();
		documentDeleteDTO = new DocumentDeleteResponseDTO();
		documentDeleteDTO.setDocumnet_Id("1");
		documentDeleteDTO.setResMsg(StatusCodes.DOCUMENT_DELETE_SUCCESSFUL.toString());
		deleteresponseList.add(documentDeleteDTO);

		MainListResponseDTO<DocumentDeleteResponseDTO> delResponseDto = new MainListResponseDTO<>();
		delResponseDto.setResponse(deleteresponseList);
		Mockito.doReturn(true).when(ceph).deleteFile(Mockito.anyString(), Mockito.anyString());
		Mockito.when(documentRepository.findBypreregId(preId)).thenReturn(docEntity);
		Mockito.when(documentRepository.deleteAllBypreregId(preId)).thenReturn(1);
		MainListResponseDTO<DocumentDeleteResponseDTO> responseDto = documentUploadService.deleteAllByPreId(preId);
		assertEquals(responseDto.getResponse().get(0).getDocumnet_Id(),
				delResponseDto.getResponse().get(0).getDocumnet_Id());
	}

	@Test(expected = DocumentFailedToDeleteException.class)
	public void deleteFailureTest() {
		Mockito.when(documentRepository.findBydocumentId(Mockito.anyInt())).thenThrow(DataAccessLayerException.class);
		documentUploadService.deleteDocument("1");
	}

	@Test(expected = DocumentNotFoundException.class)
	public void deleteAllByPreIdFailureTest() {
		Mockito.when(documentRepository.findBydocumentId(Mockito.anyInt())).thenReturn(null);
		documentUploadService.deleteAllByPreId(preId);

	}

	@Test(expected = DocumentFailedToDeleteException.class)
	public void deleteByPreIdFailureTest() {
		Mockito.when(documentRepository.findBypreregId(Mockito.anyString())).thenThrow(DataAccessLayerException.class);
		documentUploadService.deleteAllByPreId("91324567567565");
	}
}
