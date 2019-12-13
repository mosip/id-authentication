package io.mosip.admin.packetstatusupdater.dto;

import lombok.Data;

@Data
public class AuditManagerResponseDto {
	

	/**
	 * The audit status
	 */
	private String status;
	private String message;

}
