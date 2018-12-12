package io.mosip.kernel.masterdata.dto;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * DTO class for idtypes request.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@Data
public class IdTypeRequestDto {
	/**
	 * The idtype request id.
	 */
	private String id;
	
	/**
	 * The idtype request version.
	 */
	private String ver;
	
	/**
	 * The idtype request timestamp.
	 */
	private LocalDateTime timestamp;
	
	/**
	 * The idtype request.
	 */
	private IdTypeListDto request;
}
