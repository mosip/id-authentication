package io.mosip.kernel.emailnotification.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.mosip.kernel.core.http.ResponseFilter;
import io.mosip.kernel.core.http.ResponseWrapper;
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
    @PreAuthorize("hasAnyRole('INDIVIDUAL','REGISTRATION_PROCESSOR','REGISTRATION_ADMIN','REGISTRATION_SUPERVISOR','REGISTRATION_OFFICER','ID_AUTHENTICATION','AUTH','ZONAL_ADMIN','PRE_REGISTRATION_ADMIN','PRE_REGISTRATION_ADMIN')")
	@ResponseFilter
	@PostMapping(value = "/email/send", consumes = "multipart/form-data")
	public @ResponseBody ResponseWrapper<ResponseDto> sendEMail(String[] mailTo, String[] mailCc, String mailSubject,
			String mailContent, MultipartFile[] attachments) {
		ResponseWrapper<ResponseDto> responseWrapper = new ResponseWrapper<>();
		System.out.println("Test adding");
		responseWrapper
				.setResponse(emailNotificationService.sendEmail(mailTo, mailCc, mailSubject, mailContent, attachments));
		return responseWrapper;
	}
}
