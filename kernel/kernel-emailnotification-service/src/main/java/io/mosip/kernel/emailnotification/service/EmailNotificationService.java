package io.mosip.kernel.emailnotification.service;

import java.util.concurrent.CompletableFuture;

import org.springframework.web.multipart.MultipartFile;

import io.mosip.kernel.emailnotification.dto.ResponseDto;

/**
 * Service implementation class for {@link MailNotifier}.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 */

public interface EmailNotificationService {

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
			String mailContent, MultipartFile[] attachments);
}
