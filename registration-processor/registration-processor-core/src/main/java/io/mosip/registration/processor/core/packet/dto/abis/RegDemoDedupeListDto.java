package io.mosip.registration.processor.core.packet.dto.abis;

import java.io.Serializable;
import java.time.LocalDateTime;

import io.mosip.registration.processor.core.packet.dto.TransactionDto;
import lombok.Data;

@Data
public class RegDemoDedupeListDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String matchedRegId;

	private String regtrnId;
	
	private String crBy;

	private LocalDateTime crDtimes;

	private LocalDateTime delDtimes;

	private Boolean isDeleted;

	private String regId;

	private String updBy;

	private LocalDateTime updDtimes;
	
	
}
