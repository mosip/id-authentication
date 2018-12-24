package io.mosip.preregistration.documents.test.exception;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.preregistration.documents.dto.DocumentDTO;
import io.mosip.preregistration.documents.errorcodes.ErrorCodes;
import io.mosip.preregistration.documents.exception.DocumentSizeExceedException;
import io.mosip.preregistration.documents.service.DocumentUploadService;

@RunWith(SpringRunner.class)
public class DocumentSizeExceedTest {

	private static final String DOCUMENT_EXCEEDING_PERMITTED_SIZE = "This is document size exceed exception";

	@Mock
	private DocumentUploadService documentUploadService;

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
	public void documentSizeExceedTest() throws FileNotFoundException, IOException, URISyntaxException {

		DocumentSizeExceedException exceedException = new DocumentSizeExceedException(
				DOCUMENT_EXCEEDING_PERMITTED_SIZE);

		DocumentDTO documentDto = new DocumentDTO("48690172097498", "address", "POA", "pdf", "Pending-Appoinment",
				new Timestamp(System.currentTimeMillis()), "9217148168");
		ClassLoader classLoader = getClass().getClassLoader();

		URI uri = new URI(classLoader.getResource("Doc.pdf").getFile().trim().replaceAll("\\u0020", "%20"));
		File file = new File(uri.getPath());

		this.multiPartFile = new MockMultipartFile("file", "Doc.pdf", "mixed/multipart", new FileInputStream(file));

		Mockito.when(documentUploadService.uploadDoucment(multiPartFile, json)).thenThrow(exceedException);
		try {

			documentUploadService.uploadDoucment(multiPartFile, json);
			fail();

		} catch (DocumentSizeExceedException e) {
			assertThat("Should throw DocumentSizeExceed exception with correct error codes",
					e.getErrorCode().equalsIgnoreCase(ErrorCodes.PRG_PAM_DOC_007.toString()));
			assertThat("Should throw DocumentSizeExceed exception with correct messages",
					e.getErrorText().equalsIgnoreCase(DOCUMENT_EXCEEDING_PERMITTED_SIZE));

		}
	}
}
