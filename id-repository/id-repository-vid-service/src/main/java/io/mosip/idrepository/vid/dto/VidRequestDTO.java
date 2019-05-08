package io.mosip.idrepository.vid.dto;
/**
 * 
 * @author Prem Kumar.
 *
 */

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class VidRequestDTO {

	private String id;
	
	private String version;
	
	private LocalDateTime requestTime;
	
	private RequestDto request;
}
