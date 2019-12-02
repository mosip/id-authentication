package io.mosip.kernel.core.packetstatusupdater.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Srinivasan
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PacketStatusUpdateDto {

	private String registrationId;
	
	private String statusCode;
}
