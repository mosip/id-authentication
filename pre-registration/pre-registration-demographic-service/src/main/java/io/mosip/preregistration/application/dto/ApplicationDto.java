package io.mosip.preregistration.application.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Application DTO
 * 
 * @author M1037717
 *
 */
@Getter
@Setter
@NoArgsConstructor
public class ApplicationDto implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6705845720255847210L;

	/** The List of Applications. */
	private List<RegistrationDto> Applications;

}
