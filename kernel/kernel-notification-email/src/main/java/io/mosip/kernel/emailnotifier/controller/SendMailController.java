package io.mosip.kernel.emailnotifier.controller;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.mosip.kernel.core.spi.notifier.email.MailNotifierService;
import io.mosip.kernel.emailnotifier.dto.ResponseDto;

/**
 * Controller class for sending mail.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@RestController
public class SendMailController {
	/**
	 * Autowired reference for MailNotifierService.
	 */
	@Autowired
	MailNotifierService<MultipartFile, ResponseDto> service;

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
	 * @param attachment
	 *            the attachments.
	 * @return the response.
	 */
	@PostMapping(value = "/notifieremail/sends", consumes = "multipart/form-data")
	public @ResponseBody CompletableFuture<ResponseEntity<ResponseDto>> sendMail(String[] mailTo,
			String[] mailCc, String mailSubject, String mailContent, MultipartFile[] attachments) {
		return service.sendTextMailWithCcWithAttachment(mailTo, mailCc, mailSubject, mailContent, attachments)
				.thenApplyAsync(responseDto -> new ResponseEntity<>(responseDto, HttpStatus.ACCEPTED));
	}
}
