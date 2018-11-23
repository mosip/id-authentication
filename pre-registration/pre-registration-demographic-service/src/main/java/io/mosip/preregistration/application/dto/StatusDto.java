package io.mosip.preregistration.application.dto;

import java.io.Serializable;
import java.sql.Timestamp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class StatusDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1088172470058130997L;
	
	private String PreRegistartionId;
	
	private String StatusCode;

}
