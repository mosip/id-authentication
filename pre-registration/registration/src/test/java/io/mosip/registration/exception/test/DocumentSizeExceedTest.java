package io.mosip.registration.exception.test;

import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.registration.dto.DocumentDto;
import io.mosip.registration.errorcodes.ErrorCodes;
import io.mosip.registration.exception.DocumentSizeExceedException;
import io.mosip.registration.service.DocumentUploadService;

@RunWith(SpringRunner.class)
public class DocumentSizeExceedTest {

	private static final String DOCUMENT_EXCEEDING_PERMITTED_SIZE = "This is document size exceed exception";

	@Mock
	private DocumentUploadService documentUploadService;

	@MockBean
	private MockMultipartFile multiPartFile;

	@Test
	public void documentSizeExceedTest() throws FileNotFoundException, IOException {

		DocumentDto documentDto = new DocumentDto("99887654323321", "88779876543212", "address", "POA", ".pdf", "Save",
				"eng", "kishan", "kishan", true);

		ClassLoader classLoader = getClass().getClassLoader();

		File file = new File(classLoader.getResource("SampleSizeTest.pdf").getFile());

		this.multiPartFile = new MockMultipartFile("file", "SampleSizeTest.pdf", "mixed/multipart",
				new FileInputStream(file));

		try {

			documentUploadService.uploadDoucment(multiPartFile, documentDto);

		} catch (DocumentSizeExceedException e) {
			assertThat("Should throw DocumentSizeExceed exception with correct error codes",
					e.getErrorCode().equalsIgnoreCase(ErrorCodes.PRG_PAM‌_001.toString()));
			assertThat("Should throw DocumentSizeExceed exception with correct messages",
					e.getErrorText().equalsIgnoreCase(DOCUMENT_EXCEEDING_PERMITTED_SIZE));

		}
	}
}
