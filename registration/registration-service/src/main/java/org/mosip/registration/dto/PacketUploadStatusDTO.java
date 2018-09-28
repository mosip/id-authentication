package org.mosip.registration.dto;

import lombok.Data;

@Data
public class PacketUploadStatusDTO {

	private String sourcePath;
	private String fileName;
	private String uploadStatus;
	private String uploadTime;
	private String remarks;
}
