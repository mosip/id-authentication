package io.mosip.kernel.emailnotification.service.impl;

import java.util.concurrent.CompletableFuture;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.mosip.kernel.core.notification.spi.EmailNotification;
import io.mosip.kernel.emailnotification.constant.MailNotifierConstants;
import io.mosip.kernel.emailnotification.dto.ResponseDto;
import io.mosip.kernel.emailnotification.exception.NotificationException;
import io.mosip.kernel.emailnotification.util.EmailNotificationUtils;

/**
 * Service implementation class for {@link EmailNotification}.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 */
@Service
public class EmailNotificationServiceImpl
		implements EmailNotification<MultipartFile[], CompletableFuture<ResponseDto>> {
	/**
	 * Autowired reference for {@link JavaMailSender}
	 */
	@Autowired
	private JavaMailSender emailSender;

	/**
	 * Autowired reference for {@link EmailNotificationUtils}
	 */
	@Autowired
	EmailNotificationUtils emailNotificationUtils;

	/**
	 * SendEmail
	 * 
	 * @param mailTo
	 *            email address to which mail will be sent.
	 * @param mailCc
	 *            email addresses to be cc'ed.
	 * @param mailSubject
	 *            the subject.
	 * @param mailContent
	 *            the content.
	 * @param attachments
	 *            the attachments.
	 * @return the response dto.
	 */
	@Override
	public CompletableFuture<ResponseDto> sendEmail(String[] mailTo, String[] mailCc, String mailSubject,
			String mailContent, MultipartFile[] attachments) {
		/**
		 * Calling utility method to validate mail arguments.
		 */
		EmailNotificationUtils.validateMailArguments(mailTo, mailSubject, mailContent);
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
			throw new NotificationException(exception);
		}
		if (attachments != null) {
			/**
			 * Adds attachments.
			 */
			emailNotificationUtils.addAttachments(attachments, helper);
		}
		/**
		 * Sends the mail.
		 */
		emailNotificationUtils.sendMessage(message, emailSender);
		dto.setStatus(MailNotifierConstants.MESSAGE_REQUEST_SENT.getValue());
		return CompletableFuture.completedFuture(dto);
	}
}
