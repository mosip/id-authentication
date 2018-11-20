package io.mosip.preregistration.application.test.exception;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.preregistration.application.errorcodes.ErrorCodes;
import io.mosip.preregistration.application.exception.OperationNotAllowedException;
import io.mosip.preregistration.application.repository.PreRegistrationRepository;
import io.mosip.preregistration.application.service.PreRegistrationService;

@RunWith(SpringRunner.class)
public class OperationNotAllowedTest {

	private static final String OPERATION_NOT_ALLOWED="DELETE_OPERATION_NOT_ALLOWED_FOR_OTHERTHEN_DRAFT";

	
	@Mock
	private PreRegistrationService 	preRegistrationService;
	
	@MockBean
	private PreRegistrationRepository preRegistrationRepository;

	
	@Test
	public void notAllowedTest() {
		
		OperationNotAllowedException operationnotallowedexception = new OperationNotAllowedException(OPERATION_NOT_ALLOWED);

		
//		ClassLoader classLoader = getClass().getClassLoader();
//
//		File file = new File(classLoader.getResource("pre-registration.json").getFile());
//		
//		jsonObject = (JSONObject) parser.parse(new FileReader(file));
		
	   Mockito.when(preRegistrationService.deleteIndividual("1234567"))
				.thenThrow(operationnotallowedexception);
	   Mockito.when(preRegistrationRepository.findBypreRegistrationId("1234567"))
		.thenThrow(operationnotallowedexception);
	   
		try {

			preRegistrationService.deleteIndividual("1234567");
			fail();

		} catch (OperationNotAllowedException e) {
			assertThat("DELETE_OPERATION_NOT_ALLOWED_FOR_OTHERTHEN_DRAFT",
					e.getErrorCode().equalsIgnoreCase(ErrorCodes.PRG_PAM_010.toString()));
//			assertThat("DELETE_OPERATION_NOT_ALLOWED_FOR_OTHERTHEN_DRAFT",
//					e.getErrorText().equalsIgnoreCase(OPERATION_NOT_ALLOWED));
		}

	}
	
}
