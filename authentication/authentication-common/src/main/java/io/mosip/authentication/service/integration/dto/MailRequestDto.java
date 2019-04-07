package io.mosip.authentication.service.integration.dto;

import org.springframework.web.multipart.MultipartFile;
import lombok.Data;

/**
 * 
 * @author Dinesh Karuppiah.T
 */
@Data
public class MailRequestDto {

	/**
	 * Variable to hold mail to
	 */
	String[] mailTo;
	/**
	 * Variable to hold mail cc
	 */
	String[] mailCc;
	/**
	 * Variable to hold mail subject
	 */
	String mailSubject;
	/**
	 * Variable to hold mail content
	 */
	String mailContent;
	/**
	 * Variable to hold attachments
	 */
	MultipartFile[] attachments;

}
