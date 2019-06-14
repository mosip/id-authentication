package io.mosip.registration.test.integrationtest;

import static org.junit.Assert.assertEquals;

import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.Arguments;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.service.template.NotificationService;
import junit.framework.Assert;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes=AppConfig.class)
public class NotificationServiceIntegrationTest extends BaseIntegrationTest{

	/**
	 * @author Leona Mary S
	 *
	 *Validating whether Notification service is working as expected for invalid and valid inputs
	 */
	@Autowired
	NotificationService notifiserviceTest;
	
	
	@Before
	public void setvalue()
	{
	SessionContext.getInstance().getUserContext().setUserId(IntegrationTestConstants.userId_val);
	}
	
	@Test
public void Validate_sendSMSTest()
{
	String expectedmsg="Success";
	ResponseDTO resSMSDTO=notifiserviceTest.sendSMS("Testing SMS service", "9551085729", "12345678901234567890123456789");
	String actualmsg=resSMSDTO.getSuccessResponseDTO().getMessage();
	assertEquals(expectedmsg,actualmsg);
	
}

@Test
public void Validate_sendEmailTest()
{
	String expectedmsg="Success";
	ResponseDTO resEmailDTO=notifiserviceTest.sendEmail("Testing Email service", "maryroseline2@gmail.com", "12345678901234567890123456789");
	String actualmsg= resEmailDTO.getSuccessResponseDTO().getMessage();
	assertEquals(expectedmsg,actualmsg);
}

@Test
public void Validate_sendEmailTest_fail()
{
	String expectedmsg="Unable to send EMAIL Notification";
	ResponseDTO resEmailDTO=notifiserviceTest.sendEmail("Testing Email service", "maryroseline2@", "12345678901234567890123456789");
	String actualmsg=resEmailDTO.getErrorResponseDTOs().get(0).getMessage();
	assertEquals(expectedmsg,actualmsg);
}

@Test
public void Validate_sendSMSTestFail()
{
	String expectedmsg="Contact number cannot contains alphabet,special character or less than or more than 10 digits";
	ResponseDTO resSMSDTO=notifiserviceTest.sendSMS("Testing SMS service", "955108", "12345678901234567890123456789");
	String actualmsg=resSMSDTO.getErrorResponseDTOs().get(0).getMessage();
	assertEquals(expectedmsg,actualmsg);
}

@Test
public void Validate_sendSMSTest_alpha_Fail()
{
	String expectedmsg="Contact number cannot contains alphabet,special character or less than or more than 10 digits";
	ResponseDTO resSMSDTO=notifiserviceTest.sendSMS("Testing SMS service", "955108qwee", "12345678901234567890123456789");
	String actualmsg=resSMSDTO.getErrorResponseDTOs().get(0).getMessage();
	assertEquals(expectedmsg,actualmsg);
}
	
}
