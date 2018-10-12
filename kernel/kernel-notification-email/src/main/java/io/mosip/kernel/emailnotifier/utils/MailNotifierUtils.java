package io.mosip.kernel.emailnotifier.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import io.mosip.kernel.emailnotifier.constants.MailNotifierArgumentErrorConstants;
import io.mosip.kernel.emailnotifier.exceptionhandler.MosipAsyncCaughtExceptionHandler;
import io.mosip.kernel.emailnotifier.exceptionhandler.MosipErrors;
import io.mosip.kernel.emailnotifier.exceptionhandler.MosipMailNotifierInvalidArgumentsException;

/**
 * This class provides with the utility methods for email-notifier service.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 */
@Component
public class MailNotifierUtils {
	/**
	 * This method sends the message.
	 * 
	 * @param message
	 *            the message to be sent.
	 * @param emailSender
	 *            the emailsender object.
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
			} catch (MessagingException | IOException e) {
				throw new MosipAsyncCaughtExceptionHandler();
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
		List<MosipErrors> validationErrorsList = new ArrayList<>();
		if (mailTo == null || ArrayUtils.isEmpty(mailTo)) {
			validationErrorsList
					.add(new MosipErrors(MailNotifierArgumentErrorConstants.RECEIVER_ADDRESS_NOT_FOUND.getErrorCode(),
							MailNotifierArgumentErrorConstants.RECEIVER_ADDRESS_NOT_FOUND.getErrorMessage()));
		}
		if (mailSubject == null || mailSubject.isEmpty()) {
			validationErrorsList
					.add(new MosipErrors(MailNotifierArgumentErrorConstants.SUBJECT_NOT_FOUND.getErrorCode(),
							MailNotifierArgumentErrorConstants.SUBJECT_NOT_FOUND.getErrorMessage()));
		}
		if (mailContent == null || mailSubject == null || mailSubject.isEmpty()) {
			validationErrorsList
					.add(new MosipErrors(MailNotifierArgumentErrorConstants.CONTENT_NOT_FOUND.getErrorCode(),
							MailNotifierArgumentErrorConstants.CONTENT_NOT_FOUND.getErrorMessage()));
		}
		if (!validationErrorsList.isEmpty()) {
			throw new MosipMailNotifierInvalidArgumentsException(validationErrorsList);
		}
	}
}