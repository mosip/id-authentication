package io.mosip.preregistration.documents.test.exception;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.preregistration.documents.dto.DocumentRequestDTO;
import io.mosip.preregistration.documents.errorcodes.ErrorCodes;
import io.mosip.preregistration.documents.exception.DocumentNotFoundException;
import io.mosip.preregistration.documents.service.DocumentService;

/**
 * Test class to test the DocumentNotFound Exception
 * 
 * @author Rajath KR
 * @author Tapaswini Bahera
 * @author Jagadishwari S
 * @author Kishan Rathore
 * @since 1.0.0
 * 
 */
@RunWith(SpringRunner.class)
public class DocumentNotFoundTest {

	private static final String DOCUMENT_Not_FOUND = "This is document not found exception";

	@Mock
	private DocumentService documentUploadService;

	@MockBean
	private MockMultipartFile multiPartFile;
	
	String  json="{\r\n" + 
			"	\"id\": \"mosip.pre-registration.document.upload\",\r\n" + 
			"	\"ver\": \"1.0\",\r\n" + 
			"	\"reqTime\": \"2018-10-17T07:22:57.086+0000\",\r\n" + 
			"	\"request\": {\r\n" + 
			"		\"prereg_id\": \"48690172097498\",\r\n" + 
			"		\"doc_cat_code\": \"POA\",\r\n" + 
			"		\"doc_typ_code\": \"address\",\r\n" + 
			"		\"doc_file_format\": \"pdf\",\r\n" + 
			"		\"status_code\": \"Pending-Appoinment\",\r\n" + 
			"		\"upload_by\": \"9217148168\",\r\n" + 
			"		\"upload_DateTime\": \"2018-10-17T07:22:57.086+0000\"\r\n" + 
			"	}\r\n" + 
			"}";


	@Test
	public void notFoundException() throws FileNotFoundException, IOException {

		DocumentNotFoundException documentNotFoundException = new DocumentNotFoundException(DOCUMENT_Not_FOUND);

		DocumentRequestDTO documentDto = new DocumentRequestDTO("48690172097498", "address", "POA", "pdf", "Pending-Appoinment",
				new Date(),"ENG" ,"9217148168");

		ClassLoader classLoader = getClass().getClassLoader();

		File file = new File(classLoader.getResource("Doc.pdf").getFile());

		this.multiPartFile = new MockMultipartFile("file", "Doc.pdf", "mixed/multipart", new FileInputStream(file));

		Mockito.when(documentUploadService.uploadDocument(multiPartFile, json))
				.thenThrow(documentNotFoundException);
		try {

			documentUploadService.uploadDocument(multiPartFile, json);
			fail();

		} catch (DocumentNotFoundException e) {
			assertThat("Should throw dopcument not found exception with correct error codes",
					e.getErrorCode().equalsIgnoreCase(ErrorCodes.PRG_PAM_DOC_005.toString()));
			assertThat("Should throw dopcument not found exception with correct messages",
					e.getErrorText().equalsIgnoreCase(DOCUMENT_Not_FOUND));
		}

	}
}
