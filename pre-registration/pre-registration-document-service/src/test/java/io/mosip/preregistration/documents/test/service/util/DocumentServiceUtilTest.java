package io.mosip.preregistration.documents.test.service.util;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.core.exception.IOException;
import io.mosip.kernel.core.virusscanner.exception.VirusScannerException;
import io.mosip.kernel.core.virusscanner.spi.VirusScanner;
import io.mosip.preregistration.core.exception.InvalidRequestParameterException;
import io.mosip.preregistration.documents.dto.DocumentRequestDTO;
import io.mosip.preregistration.documents.entity.DocumentEntity;
import io.mosip.preregistration.documents.exception.DemographicGetDetailsException;
import io.mosip.preregistration.documents.exception.InvalidDocumnetIdExcepion;
import io.mosip.preregistration.documents.service.util.DocumentServiceUtil;
import software.amazon.ion.IonException;


@RunWith(SpringRunner.class)
@SpringBootTest
public class DocumentServiceUtilTest {
	
	@MockBean
	private VirusScanner<Boolean, String> virusScan;
	
	List<DocumentEntity> docEntity = new ArrayList<>();
	
	@Autowired
	private DocumentServiceUtil serviceUtil;
	
	private MockMultipartFile mockMultipartFile;
	
	DocumentRequestDTO documentDto = new DocumentRequestDTO("48690172097498", "address", "POA", "PDF",
			"Pending_Appointment", new Date(), "ENG", "Jagadishwari");
	File file;
	
	@Before
	public void setUp() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		URI uri = new URI(classLoader.getResource("Doc.pdf").getFile().trim().replaceAll("\\u0020", "%20"));
		file = new File(uri.getPath());
		mockMultipartFile = new MockMultipartFile("file", "Doc.pdf", "mixed/multipart", new FileInputStream(file));
	}

	@Test
	public void getDateStringTest() {
		serviceUtil.getDateString(new Date());
	}
	
	@Test
	public void parseDocumentIdTest() {
		serviceUtil.parseDocumentId("1234");
	}

	@Test(expected=InvalidDocumnetIdExcepion.class)
	public void parseDocumentIdFailureTest() throws Exception {
		serviceUtil.parseDocumentId("1234!@#$&^$$~~~~~~#@!$^%");
	}
	
	@Test(expected=InvalidRequestParameterException.class)
	public void isValidCatCodeTest() throws Exception{
		serviceUtil.isValidCatCode("13fww");
	}
	
	@Test(expected=InvalidRequestParameterException.class)
	public void inValidPreIDTest() throws Exception {
		documentDto.setPreregId(null);
		serviceUtil.isValidRequest(documentDto);
	}
	
	@Test(expected=InvalidRequestParameterException.class)
	public void inValidFileFormatTest() throws Exception {
		documentDto.setDocFileFormat(null);
		serviceUtil.isValidRequest(documentDto);
	}
	
	@Test(expected=InvalidRequestParameterException.class)
	public void inValidDocTypeTest() throws Exception {
		documentDto.setDocTypeCode(null);
		serviceUtil.isValidRequest(documentDto);
	}
	
	@Test(expected=InvalidRequestParameterException.class)
	public void inValidLangCodeTest() throws Exception {
		documentDto.setLangCode(null);
		serviceUtil.isValidRequest(documentDto);
	}
	
	@Test(expected=InvalidRequestParameterException.class)
	public void inValidStatusCodeTest() throws Exception {
		documentDto.setStatusCode(null);
		serviceUtil.isValidRequest(documentDto);
	}
	
	@Test(expected=InvalidRequestParameterException.class)
	public void inValidUploadByTest() throws Exception {
		documentDto.setUploadBy(null);
		serviceUtil.isValidRequest(documentDto);
	}
	
	@Test(expected=InvalidRequestParameterException.class)
	public void inValidUploadDateTest() throws Exception {
		documentDto.setUploadDateTime(null);
		serviceUtil.isValidRequest(documentDto);
	}
	
//	@Test(expected=DemographicGetDetailsException.class)
//	public void callGetPreRegInfoRestServiceFailureTest() {
//		
//	}
	

}
