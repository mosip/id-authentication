package io.mosip.preregistration.booking.dto;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Response DTO
 * 
 * @author M1037717
 *
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class PreRegResponseDto<T> implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6705845720255847210L;
	
	/** The error details. */
	private List<ExceptionJSONInfo> err;
	
	private Boolean status;
	
	private Timestamp resTime;
	
	private List<T> response;
	

}
