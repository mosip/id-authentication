package io.mosip.kernel.emailnotification.smtp.test.controller;

import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.concurrent.CompletableFuture;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import io.mosip.kernel.emailnotification.smtp.NotificationEmailBootApplication;
import io.mosip.kernel.emailnotification.smtp.controller.SendMailController;
import io.mosip.kernel.emailnotification.smtp.dto.ResponseDto;
import io.mosip.kernel.emailnotification.smtp.service.impl.MailNotifierServiceImpl;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=NotificationEmailBootApplication.class)
public class MailNotifierControllerTest {
	@Mock
	MailNotifierServiceImpl service;
	
	@InjectMocks
	SendMailController controller;
	
	@Test
	public void testToCheckMailSendController() throws Exception {
		MultipartFile file = new MockMultipartFile("test.txt", "test.txt", null, new byte[1100]);
		MultipartFile fileTwo = new MockMultipartFile("test.txt", "test.txt", null, new byte[1100]);
		MultipartFile[] arr = { file, fileTwo };
		String[] mailTo = {"mosip.emailnotifier@gmail.com"};
		String[] mailCc = {"mosip.emailcc@gmail.com"};
		String mailSubject = "Test Subject";
		String mailContent = "Test Content";
		ResponseDto response = new ResponseDto();
		response.setStatus("Email Request submitted");
		CompletableFuture<ResponseDto> dto = new CompletableFuture<>();
		when(service.sendEmail(mailTo, mailCc, mailSubject, mailContent, arr)).thenReturn(dto);
		assertThat(controller.sendMail(mailTo, mailCc, mailSubject, mailContent, arr),
				isA(CompletableFuture.class));
	}
}
