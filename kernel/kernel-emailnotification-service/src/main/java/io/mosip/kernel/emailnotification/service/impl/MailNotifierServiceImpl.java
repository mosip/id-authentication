package io.mosip.kernel.emailnotification.service.impl;

import java.util.concurrent.CompletableFuture;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.mosip.kernel.emailnotification.constant.MailNotifierConstants;
import io.mosip.kernel.emailnotification.dto.ResponseDto;
import io.mosip.kernel.emailnotification.exception.MosipAsyncCaughtExceptionHandler;
import io.mosip.kernel.emailnotification.util.MailNotifierUtils;

/**
 * Service implementation class for {@link MailNotifier}.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 */
@Service
public class MailNotifierServiceImpl {
	/**
	 * Autowired reference for {@link JavaMailSender}
	 */
	@Autowired
	private JavaMailSender emailSender;

	/**
	 * Autowired reference for {@link MailNotifierUtils}
	 */
	@Autowired
	MailNotifierUtils mailNotifierUtils;

	/**
	 * SendEmail
	 * 
	 * @param mailTo
	 * @param mailCc
	 * @param mailSubject
	 * @param mailContent
	 * @param attachments
	 * @return
	 */
	public CompletableFuture<ResponseDto> sendEmail(String[] mailTo, String[] mailCc, String mailSubject,
			String mailContent, MultipartFile[] attachments) {
		/**
		 * Calling utility method to validate mail arguments.
		 */
		MailNotifierUtils.validateMailArguments(mailTo, mailSubject, mailContent);
		ResponseDto dto = new ResponseDto();
		/**
		 * Creates the message.
		 */
		MimeMessage message = emailSender.createMimeMessage();
		MimeMessageHelper helper;
		try {
			helper = new MimeMessageHelper(message, true);
			/**
			 * Sets to, subject, content.
			 */
			helper.setTo(mailTo);
			if (mailCc != null) {
				helper.setCc(mailCc);
			}
			if (mailSubject != null) {
				helper.setSubject(mailSubject);
			}
			helper.setText(mailContent);
		} catch (MessagingException exception) {
			throw new MosipAsyncCaughtExceptionHandler(exception);
		}
		if (attachments != null) {
			/**
			 * Adds attachments.
			 */
			mailNotifierUtils.addAttachments(attachments, helper);
		}
		/**
		 * Sends the mail.
		 */
		mailNotifierUtils.sendMessage(message, emailSender);
		dto.setStatus(MailNotifierConstants.MESSAGE_REQUEST_SENT.getValue());
		return CompletableFuture.completedFuture(dto);
	}
}
