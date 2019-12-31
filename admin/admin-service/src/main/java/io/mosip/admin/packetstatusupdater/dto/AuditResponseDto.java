package io.mosip.admin.packetstatusupdater.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Audit response DTO
 * @author Srinivasan
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditResponseDto {

	/** The status. */
	private boolean status;
}
