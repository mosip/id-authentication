package io.mosip.preregistration.batchjobservices.dto;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import io.mosip.preregistration.core.exceptions.dto.ExceptionJSONInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Response DTO
 * 
 * @author M1043008
 *
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class ResponseDto<T> implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6705845720255847210L;
	
	/** The error details. */
	private List<ExceptionJSONInfo> err;
	
	private Boolean status;
	
	private Timestamp resTime;
	
	private T response;
	

}
