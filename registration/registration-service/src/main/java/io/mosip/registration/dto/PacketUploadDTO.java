package io.mosip.registration.dto;

import lombok.Data;

@Data
public class PacketUploadDTO {
	private String userid;
	private String password;
	private String filepath;

}
