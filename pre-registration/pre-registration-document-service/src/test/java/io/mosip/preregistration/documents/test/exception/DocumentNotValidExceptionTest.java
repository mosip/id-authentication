package io.mosip.preregistration.documents.test.exception;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
import io.mosip.preregistration.documents.exception.DocumentNotValidException;
import io.mosip.preregistration.documents.service.DocumentUploadService;

@RunWith(SpringRunner.class)
public class DocumentNotValidExceptionTest {

	private static final String DOCUMENT_INVALID_FORMAT = "This is document format is invalid exception";

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
	public void notValidException() throws FileNotFoundException, IOException {

		DocumentNotValidException documentNotValidException = new DocumentNotValidException(DOCUMENT_INVALID_FORMAT);

		DocumentDTO documentDto = new DocumentDTO("48690172097498", "address", "POA", "pdf", "Pending-Appoinment",
				new Timestamp(System.currentTimeMillis()), "9217148168");

		ClassLoader classLoader = getClass().getClassLoader();

		File file = new File(classLoader.getResource("Doc.pdf").getFile());

		this.multiPartFile = new MockMultipartFile("file", "Doc.pdf", "mixed/multipart", new FileInputStream(file));

		Mockito.when(documentUploadService.uploadDoucment(multiPartFile, json))
				.thenThrow(documentNotValidException);
		try {

			documentUploadService.uploadDoucment(multiPartFile, json);
			fail();

		} catch (DocumentNotValidException e) {
			assertThat("Should throw dopcument invalid exception with correct error codes",
					e.getErrorCode().equalsIgnoreCase(ErrorCodes.PRG_PAM_DOC_004.toString()));
			assertThat("Should throw dopcument invalid exception with correct messages",
					e.getErrorText().equalsIgnoreCase(DOCUMENT_INVALID_FORMAT));
		}
	}
}
