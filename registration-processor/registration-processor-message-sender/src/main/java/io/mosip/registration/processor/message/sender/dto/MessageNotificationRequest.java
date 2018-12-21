package io.mosip.registration.processor.message.sender.dto;

import java.util.List;

import lombok.Data;

@Data
public class MessageNotificationRequest {
	private String templateCode;
	private String rid;
	private String uid;
	private String idType;
	private List<String> otherAttribute; 
}
