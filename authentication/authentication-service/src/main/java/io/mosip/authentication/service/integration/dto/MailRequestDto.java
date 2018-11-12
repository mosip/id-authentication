package io.mosip.authentication.service.integration.dto;

import org.springframework.web.multipart.MultipartFile;
import lombok.Data;

@Data
public class MailRequestDto {

	String[] mailTo;
	String[] mailCc;
	String mailSubject;
	String mailContent;
	MultipartFile[] attachments;

}
