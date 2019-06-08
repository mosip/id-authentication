package io.mosip.registration.test.integrationtest;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.service.template.NotificationService;

public class NotificationServiceIntegrationTest extends BaseIntegrationTest {

	/**
	 * @author Leona Mary S
	 *
	 *         Validating whether Notification service is working as expected for
	 *         invalid and valid inputs
	 */
	@Autowired
	NotificationService notificationServiceTest;

	@Before
	public void setValue() {
		SessionContext.getInstance().getUserContext().setUserId(IntegrationTestConstants.USERIDVAL);
	}

	@Test
	public void validateSendSMSTest() {
		ResponseDTO resSMSDTO = notificationServiceTest.sendSMS(IntegrationTestConstants.SMSMESSAGENS,
				IntegrationTestConstants.NSNUMBER, IntegrationTestConstants.NSREGID);
		assertEquals(IntegrationTestConstants.NSSUCCESSMSG, resSMSDTO.getSuccessResponseDTO().getMessage());

	}

	@Test
	public void validateSendEmailTest() {

		ResponseDTO resEmailDTO = notificationServiceTest.sendEmail(IntegrationTestConstants.EMAILMESSAGENS,
				IntegrationTestConstants.NSEMAIL, IntegrationTestConstants.NSREGID);
		assertEquals(IntegrationTestConstants.NSSUCCESSMSG, resEmailDTO.getSuccessResponseDTO().getMessage());
	}

	@Test
	public void validateSendEmailTestfail() {
		ResponseDTO resEmailDTO = notificationServiceTest.sendEmail(IntegrationTestConstants.EMAILMESSAGENS,
				IntegrationTestConstants.INVALIDEMAILNS, IntegrationTestConstants.NSREGID);
		assertEquals(IntegrationTestConstants.NSERRORMSG, resEmailDTO.getErrorResponseDTOs().get(0).getMessage());
	}

	@Test
	public void validateSendSMSTestFail() {
		ResponseDTO resSMSDTO = notificationServiceTest.sendSMS(IntegrationTestConstants.SMSMESSAGENS,
				IntegrationTestConstants.NSNUMBERINCORRECT, IntegrationTestConstants.NSREGID);
		assertEquals(IntegrationTestConstants.NSERRORSMS, resSMSDTO.getErrorResponseDTOs().get(0).getMessage());
	}

	@Test
	public void validateSendSMSTestalphaFail() {
		ResponseDTO resSMSDTO = notificationServiceTest.sendSMS(IntegrationTestConstants.SMSMESSAGENS,
				IntegrationTestConstants.INVALIDNUM, IntegrationTestConstants.NSREGID);
		assertEquals(IntegrationTestConstants.NSERRORSMS, resSMSDTO.getErrorResponseDTOs().get(0).getMessage());
	}

}
