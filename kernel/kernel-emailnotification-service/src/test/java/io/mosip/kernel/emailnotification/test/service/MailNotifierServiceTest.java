package io.mosip.kernel.emailnotification.test.service;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import javax.mail.internet.MimeMessage;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import io.mosip.kernel.emailnotification.NotificationEmailBootApplication;
import io.mosip.kernel.emailnotification.service.impl.EmailNotificationServiceImpl;
import io.mosip.kernel.emailnotification.util.EmailNotificationUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = NotificationEmailBootApplication.class)
public class MailNotifierServiceTest {

	@Autowired
	private JavaMailSender emailSender;

	@MockBean
	EmailNotificationUtils utils;

	@Autowired
	EmailNotificationServiceImpl service;

	@Test
	public void verifyAddAttachmentFunctionality() throws Exception {
		String[] mailTo = { "test@gmail.com" };
		String[] mailCc = { "testTwo@gmail.com" };
		String mailSubject = "Test Subject";
		String mailContent = "Test Content";
		MultipartFile attachment = new MockMultipartFile("test.txt", "test.txt", "", new byte[10]);
		MultipartFile[] attachments = { attachment };
		MimeMessage message = emailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);
		helper.setTo(mailTo);
		helper.setCc(mailCc);
		helper.setSubject(mailSubject);
		helper.setText(mailContent);
		doNothing().when(utils).addAttachments(Mockito.any(), Mockito.any());
		service.sendEmail(mailTo, mailCc, mailSubject, mailContent, attachments);
		verify(utils, times(1)).addAttachments(Mockito.any(), Mockito.any());
	}

	@Test
	public void verifySendMessageFunctionality() throws Exception {
		String[] mailTo = { "test@gmail.com" };
		String[] mailCc = { "testTwo@gmail.com" };
		String mailSubject = "Test Subject";
		String mailContent = "Test Content";
		MultipartFile attachment = new MockMultipartFile("test.txt", "test.txt", "", new byte[10]);
		MultipartFile[] attachments = { attachment };
		MimeMessage message = emailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);
		helper.setTo(mailTo);
		helper.setCc(mailCc);
		helper.setSubject(mailSubject);
		helper.setText(mailContent);
		doNothing().when(utils).sendMessage(Mockito.any(), Mockito.any());
		service.sendEmail(mailTo, mailCc, mailSubject, mailContent, attachments);
		verify(utils, times(1)).sendMessage(Mockito.any(), Mockito.any());
	}
}
