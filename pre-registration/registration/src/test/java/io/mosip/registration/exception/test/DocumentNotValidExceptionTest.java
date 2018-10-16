package io.mosip.registration.exception.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.registration.dto.DocumentDto;
import io.mosip.registration.errorcodes.ErrorCodes;
import io.mosip.registration.exception.DocumentNotValidException;
import io.mosip.registration.service.DocumentUploadService;

@RunWith(SpringRunner.class)
public class DocumentNotValidExceptionTest {

	private static final String DOCUMENT_INVALID_FORMAT = "This is document format is invalid exception";

	@Mock
	private DocumentUploadService documentUploadService;

	@MockBean
	private MockMultipartFile multiPartFile;

	@Test
	public void notValidException() throws FileNotFoundException, IOException {

		DocumentNotValidException documentNotValidException = new DocumentNotValidException(DOCUMENT_INVALID_FORMAT);

		DocumentDto documentDto = new DocumentDto("99887654323321", "88779876543212", "address", "POA", ".pdf", "Save",
				"ENG", "kishan", "kishan", true);

		ClassLoader classLoader = getClass().getClassLoader();

		File file = new File(classLoader.getResource("SampleZip.zip").getFile());

		this.multiPartFile = new MockMultipartFile("file", "SampleZip.zip", "mixed/multipart",
				new FileInputStream(file));

		Mockito.when(documentUploadService.uploadDoucment(multiPartFile, documentDto))
				.thenThrow(documentNotValidException);
		try {

			documentUploadService.uploadDoucment(multiPartFile, documentDto);
			fail();

		} catch (DocumentNotValidException e) {
			assertThat("Should throw dopcument invalid exception with correct error codes",
					e.getErrorCode().equalsIgnoreCase(ErrorCodes.PRG_PAM‌_004.toString()));
			assertThat("Should throw dopcument invalid exception with correct messages",
					e.getErrorText().equalsIgnoreCase(DOCUMENT_INVALID_FORMAT));
		}
	}
}
