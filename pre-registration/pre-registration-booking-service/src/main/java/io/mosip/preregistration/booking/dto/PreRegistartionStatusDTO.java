package io.mosip.preregistration.booking.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class PreRegistartionStatusDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4158912869008406732L;

	private String PreRegistartionId;

	private String StatusCode;

}
