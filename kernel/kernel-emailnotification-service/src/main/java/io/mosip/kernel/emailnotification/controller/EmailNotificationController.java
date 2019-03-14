package io.mosip.kernel.emailnotification.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.mosip.kernel.core.http.ResponseFilter;
import io.mosip.kernel.core.notification.spi.EmailNotification;
import io.mosip.kernel.emailnotification.dto.ResponseDto;

/**
 * Controller class for sending mail.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@RestController
public class EmailNotificationController {
	/**
	 * Autowired reference for MailNotifierService.
	 */
	@Autowired
	EmailNotification<MultipartFile[], ResponseDto> emailNotificationService;

	/**
	 * @param mailTo
	 *            array of email id's, to which mail should be sent.
	 * @param mailCc
	 *            array of email id's, to which the email should be sent as carbon
	 *            copy.
	 * @param mailSubject
	 *            the subject.
	 * @param mailContent
	 *            the content.
	 * @param attachments
	 *            the attachments.
	 * @return the dto response.
	 */
	@ResponseFilter
	@PostMapping(value = "/email/send", consumes = "multipart/form-data")
	public @ResponseBody ResponseEntity<ResponseDto> sendMail(String[] mailTo, String[] mailCc, String mailSubject,
			String mailContent, MultipartFile[] attachments) {
		return new ResponseEntity<>(
				emailNotificationService.sendEmail(mailTo, mailCc, mailSubject, mailContent, attachments),
				HttpStatus.OK);
	}
}
