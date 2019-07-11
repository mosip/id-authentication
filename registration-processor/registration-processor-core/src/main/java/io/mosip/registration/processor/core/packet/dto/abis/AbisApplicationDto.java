package io.mosip.registration.processor.core.packet.dto.abis;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AbisApplicationDto  implements Serializable {

	private String code;
	
	private String langCode;

	private String descr;

	private String name;

	private String statusCode;

	private Boolean isDeleted;
	
	private String updBy;

	private String crBy;
	
	private LocalDateTime statusUpdateDtimes;
	
	private LocalDateTime crDtimes;

	private LocalDateTime delDtimes;
	
	private LocalDateTime updDtimes;
	
	



}