package io.mosip.kernel.emailnotification.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.emailnotification.constant.MailNotifierArgumentErrorConstants;
import io.mosip.kernel.emailnotification.constant.MailNotifierConstants;
import io.mosip.kernel.emailnotification.exception.InvalidArgumentsException;
import io.mosip.kernel.emailnotification.exception.NotificationException;

/**
 * This class provides with the utility methods for email-notifier service.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 */
@Component
public class EmailNotificationUtils {
	/**
	 * This method sends the message.
	 * 
	 * @param message
	 *            the message to be sent.
	 * @param emailSender
	 *            the EmailSender object.
	 */
	@Async
	public void sendMessage(MimeMessage message, JavaMailSender emailSender) {
		emailSender.send(message);
	}

	/**
	 * This method adds the attachments to the mail.
	 * 
	 * @param attachments
	 *            the attachments.
	 * @param helper
	 *            the helper object.
	 */
	public void addAttachments(MultipartFile[] attachments, MimeMessageHelper helper) {
		Arrays.asList(attachments).forEach(attachment -> {
			try {
				helper.addAttachment(attachment.getOriginalFilename(), new ByteArrayResource(attachment.getBytes()));
			} catch (MessagingException | IOException exception) {
				throw new NotificationException(exception);
			}
		});
	}

	/**
	 * This method handles argument validations.
	 * 
	 * @param mailTo
	 *            the to address to be validated.
	 * @param mailSubject
	 *            the subject to be validated.
	 * @param mailContent
	 *            the content to be validated.
	 */
	public static void validateMailArguments(String[] mailTo, String mailSubject, String mailContent) {
		Set<ServiceError> validationErrorsList = new HashSet<>();
		if (mailTo == null || mailTo.length == Integer.parseInt(MailNotifierConstants.DIGIT_ZERO.getValue())) {
			validationErrorsList
					.add(new ServiceError(MailNotifierArgumentErrorConstants.RECEIVER_ADDRESS_NOT_FOUND.getErrorCode(),
							MailNotifierArgumentErrorConstants.RECEIVER_ADDRESS_NOT_FOUND.getErrorMessage()));
		} else {
			List<String> tos = Arrays.asList(mailTo);
			tos.forEach(to -> {
				if (to.trim().isEmpty()) {
					validationErrorsList.add(new ServiceError(
							MailNotifierArgumentErrorConstants.RECEIVER_ADDRESS_NOT_FOUND.getErrorCode(),
							MailNotifierArgumentErrorConstants.RECEIVER_ADDRESS_NOT_FOUND.getErrorMessage()));
				}
			});
		}
		if (mailSubject == null || mailSubject.trim().isEmpty()) {
			validationErrorsList
					.add(new ServiceError(MailNotifierArgumentErrorConstants.SUBJECT_NOT_FOUND.getErrorCode(),
							MailNotifierArgumentErrorConstants.SUBJECT_NOT_FOUND.getErrorMessage()));
		}
		if (mailContent == null || mailContent.trim().isEmpty()) {
			validationErrorsList
					.add(new ServiceError(MailNotifierArgumentErrorConstants.CONTENT_NOT_FOUND.getErrorCode(),
							MailNotifierArgumentErrorConstants.CONTENT_NOT_FOUND.getErrorMessage()));
		}
		if (!validationErrorsList.isEmpty()) {
			throw new InvalidArgumentsException(new ArrayList<ServiceError>(validationErrorsList));
		}
	}
}