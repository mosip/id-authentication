package io.mosip.kernel.auth.entities.otp;

import lombok.Data;

@Data
public class OtpTemplateDto {
	private String id;
	private String name;
	private String description;
	private String fileFormatCode;
	private String model;
	private String fileText;
	private String moduleId;
	private String moduleName;
	private String templateTypeCode;
	private String langCode;
	private Boolean isActive;
}
