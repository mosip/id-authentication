package io.mosip.registration.processor.core.packet.dto.abis;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AbisResponseDetDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String abiRespId;
	
	private String matchedBioRefId;

	private String crBy;

	private LocalDateTime crDtimes;

	private LocalDateTime delDtimes;

	private Boolean isDeleted;

	private Integer score;

	private String updBy;

	private LocalDateTime updDtimes;

	

}
