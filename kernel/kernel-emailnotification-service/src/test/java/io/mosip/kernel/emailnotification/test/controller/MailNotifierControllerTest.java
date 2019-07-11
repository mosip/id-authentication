package io.mosip.kernel.emailnotification.test.controller;

import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.emailnotification.controller.EmailNotificationController;
import io.mosip.kernel.emailnotification.dto.ResponseDto;
import io.mosip.kernel.emailnotification.service.impl.EmailNotificationServiceImpl;
import io.mosip.kernel.emailnotification.test.NotificationEmailTestBootApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = NotificationEmailTestBootApplication.class)
public class MailNotifierControllerTest {
	@Mock
	EmailNotificationServiceImpl service;

	@InjectMocks
	EmailNotificationController controller;

	@Test
	public void testToCheckMailSendController() throws Exception {
		MultipartFile file = new MockMultipartFile("test.txt", "test.txt", null, new byte[1100]);
		MultipartFile fileTwo = new MockMultipartFile("test.txt", "test.txt", null, new byte[1100]);
		MultipartFile[] arr = { file, fileTwo };
		String[] mailTo = { "mosip.emailnotifier@gmail.com" };
		String[] mailCc = { "mosip.emailcc@gmail.com" };
		String mailSubject = "Test Subject";
		String mailContent = "Test Content";
		ResponseDto response = new ResponseDto();
		response.setStatus("Email Request submitted");
		ResponseDto dto = new ResponseDto();
		dto.setMessage("");
		dto.setStatus("");
		when(service.sendEmail(mailTo, mailCc, mailSubject, mailContent, arr)).thenReturn(dto);
		assertThat(controller.sendEMail(mailTo, mailCc, mailSubject, mailContent, arr), isA(ResponseWrapper.class));
	}
}