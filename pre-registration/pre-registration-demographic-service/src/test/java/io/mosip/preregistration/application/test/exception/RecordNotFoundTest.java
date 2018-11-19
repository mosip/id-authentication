package io.mosip.preregistration.application.test.exception;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.jsonvalidator.validator.JsonValidator;
import io.mosip.preregistration.application.exception.RecordNotFoundException;
import io.mosip.preregistration.application.service.PreRegistrationService;

@RunWith(SpringRunner.class)
public class RecordNotFoundTest {
	
	
private static final String RECORD_NOT_FOUND="This is record format is invalid exception";

@Mock
private PreRegistrationService 	preRegistrationService;

@MockBean
private JsonValidator jsonValidator;

@Test
public void notFoundException() throws FileNotFoundException, IOException {

	RecordNotFoundException recordNotFoundException = new RecordNotFoundException(RECORD_NOT_FOUND);

	// documentDto = new DocumentDto("48690172097498", "address", "POA", "PDF", "Draft", "ENG",
		//	"Jagadishwari", "Jagadishwari");

	ClassLoader classLoader = getClass().getClassLoader();

	File file = new File(classLoader.getResource("Doc.pdf").getFile());

	//this.multiPartFile = new MockMultipartFile("file", "Doc.pdf", "mixed/multipart", new FileInputStream(file));
//
//	Mockito.when(preRegistrationService.addRegistration(jsonObject))
//			.thenThrow(documentNotFoundException);
//	try {
//
//		documentUploadService.uploadDoucment(multiPartFile, documentDto);
//		fail();
//
//	} catch (DocumentNotFoundException e) {
//		assertThat("Should throw dopcument not found exception with correct error codes",
//				e.getErrorCode().equalsIgnoreCase(ErrorCodes.PRG_PAM‌_006.toString()));
//		assertThat("Should throw dopcument not found exception with correct messages",
//				e.getErrorText().equalsIgnoreCase(DOCUMENT_Not_FOUND));
//	}

}
}
