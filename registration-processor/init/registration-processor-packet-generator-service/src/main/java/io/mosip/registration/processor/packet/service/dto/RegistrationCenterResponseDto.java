package io.mosip.registration.processor.packet.service.dto;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/* (non-Javadoc)
 * @see java.lang.Object#toString()
 */
@Data

/**
 * Instantiates a new registration center response dto.
 *
 * @param registrationCenters
 *            the registration centers
 */
@AllArgsConstructor

/**
 * Instantiates a new registration center response dto.
 */
@NoArgsConstructor
public class RegistrationCenterResponseDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** The registration centers. */
	private List<RegistrationCenterDto> registrationCenters;

	private List<ErrorDTO> errors;

}
