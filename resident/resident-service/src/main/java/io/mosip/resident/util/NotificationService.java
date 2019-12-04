package io.mosip.resident.util;

import io.mosip.resident.constant.IdType;
import io.mosip.resident.dto.NotificationRequestDto;

public class NotificationService {

	private static final String regType = "RES_UPDATE";
	private static final String emailTemplateCode = "";
	private static final String smsTemplateCode = "";
	// idType
	// subject

	// ---------dto---
	// ---need to find email and phone number
	// regId
	// uin
	// vid

	private String email;
	private long phone;

	public String getEmailSubject() {
		return "Request for re-print";
	}

	public void sendNotification(NotificationRequestDto dto) {

		if (dto.getIdType().equals(IdType.RID)) {

		}

	}

}
