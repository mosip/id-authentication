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

import io.mosip.preregistration.documents.dto.DocumentDto;
import io.mosip.preregistration.documents.errorcodes.ErrorCodes;
import io.mosip.preregistration.documents.exception.DocumentNotFoundException;
import io.mosip.preregistration.documents.service.DocumentUploadService;

@RunWith(SpringRunner.class)
public class DocumentNotFoundTest {

	private static final String DOCUMENT_Not_FOUND = "This is document not found exception";

	@Mock
	private DocumentUploadService documentUploadService;

	@MockBean
	private MockMultipartFile multiPartFile;

	@Test
	public void notFoundException() throws FileNotFoundException, IOException {

		DocumentNotFoundException documentNotFoundException = new DocumentNotFoundException(DOCUMENT_Not_FOUND);

		DocumentDto documentDto = new DocumentDto("48690172097498", "address", "POA", "PDF", "Draft",
				new Timestamp(System.currentTimeMillis()), "Jagadishwari");

		ClassLoader classLoader = getClass().getClassLoader();

		File file = new File(classLoader.getResource("Doc.pdf").getFile());

		this.multiPartFile = new MockMultipartFile("file", "Doc.pdf", "mixed/multipart", new FileInputStream(file));

		Mockito.when(documentUploadService.uploadDoucment(multiPartFile, documentDto))
				.thenThrow(documentNotFoundException);
		try {

			documentUploadService.uploadDoucment(multiPartFile, documentDto);
			fail();

		} catch (DocumentNotFoundException e) {
			assertThat("Should throw dopcument not found exception with correct error codes",
					e.getErrorCode().equalsIgnoreCase(ErrorCodes.PRG_PAM_DOC_005.toString()));
			assertThat("Should throw dopcument not found exception with correct messages",
					e.getErrorText().equalsIgnoreCase(DOCUMENT_Not_FOUND));
		}

	}
}
