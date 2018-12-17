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

import io.mosip.preregistration.documents.dto.DocumentDto;
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

	@Test
	public void documentSizeExceedTest() throws FileNotFoundException, IOException, URISyntaxException {

		DocumentSizeExceedException exceedException = new DocumentSizeExceedException(
				DOCUMENT_EXCEEDING_PERMITTED_SIZE);

		DocumentDto documentDto = new DocumentDto("48690172097498", "address", "POA", "PDF", "Draft",
				new Timestamp(System.currentTimeMillis()), "Jagadishwari");
		ClassLoader classLoader = getClass().getClassLoader();

		URI uri = new URI(classLoader.getResource("Doc.pdf").getFile().trim().replaceAll("\\u0020", "%20"));
		File file = new File(uri.getPath());

		this.multiPartFile = new MockMultipartFile("file", "Doc.pdf", "mixed/multipart", new FileInputStream(file));

		Mockito.when(documentUploadService.uploadDoucment(multiPartFile, documentDto)).thenThrow(exceedException);
		try {

			documentUploadService.uploadDoucment(multiPartFile, documentDto);
			fail();

		} catch (DocumentSizeExceedException e) {
			assertThat("Should throw DocumentSizeExceed exception with correct error codes",
					e.getErrorCode().equalsIgnoreCase(ErrorCodes.PRG_PAM_DOC_007.toString()));
			assertThat("Should throw DocumentSizeExceed exception with correct messages",
					e.getErrorText().equalsIgnoreCase(DOCUMENT_EXCEEDING_PERMITTED_SIZE));

		}
	}
}
