package io.mosip.kernel.emailnotification.service.impl;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
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
public class EmailNotificationServiceImpl implements EmailNotification<MultipartFile[], ResponseDto> {
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
	 * @param mailTo      email address to which mail will be sent.
	 * @param mailCc      email addresses to be cc'ed.
	 * @param mailSubject the subject.
	 * @param mailContent the content.
	 * @param attachments the attachments.
	 * @return the response dto.
	 */
	@Override
	public ResponseDto sendEmail(String[] mailTo, String[] mailCc, String mailSubject, String mailContent,
			MultipartFile[] attachments) {
		ResponseDto dto = new ResponseDto();
		send(mailTo, mailCc, mailSubject, mailContent, attachments);
		dto.setStatus(MailNotifierConstants.MESSAGE_SUCCESS_STATUS.getValue());
		dto.setMessage(MailNotifierConstants.MESSAGE_REQUEST_SENT.getValue());
		return dto;
	}

	@Async
	private void send(String[] mailTo, String[] mailCc, String mailSubject, String mailContent,
			MultipartFile[] attachments) {
		EmailNotificationUtils.validateMailArguments(mailTo, mailSubject, mailContent);
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
	}
}
