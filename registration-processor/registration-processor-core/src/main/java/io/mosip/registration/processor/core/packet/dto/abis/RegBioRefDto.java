package io.mosip.registration.processor.core.packet.dto.abis;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class RegBioRefDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String regId;
	
	private String bioRefId;

	private String crBy ;

	private LocalDateTime crDtimes;

	private LocalDateTime delDtimes;

	private Boolean isActive;

	private Boolean isDeleted;

	private String updBy;

	private LocalDateTime updDtimes;
}
