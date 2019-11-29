package io.mosip.admin.packetstatusupdater.dto;

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

	private String id;

	private String registrationId;

	private String transactionTypeCode;
	
	private String parentTransactionCode;
	
	private String statusCode;
	
	private String statusCommentCode;
	
	private String statusComment;
	
	private String createdDateTimes;
}
