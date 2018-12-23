package io.mosip.registration.processor.message.sender.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class MessageNotificationRequest {
	private String templateCode;
	private String rid;
	private String uid;
	private String idType;
	private String langCode;
	private List<String> otherAttribute; 
	private String mailSubject;
	private String[] mailCc;
	private MultipartFile[] attatchments;
	
}
