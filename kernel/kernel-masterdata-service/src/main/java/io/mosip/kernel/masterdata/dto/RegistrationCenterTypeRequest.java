package io.mosip.kernel.synchandler.dto;

import java.util.List;

import lombok.Data;

/**
 * The request object to be sent in the
 * {@link RegistrationCenterTypeRequestDto}.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@Data
public class RegistrationCenterTypeRequest {
	/**
	 * The list of registration center types to be added.
	 */
	private List<RegistrationCenterType> regcentertypes;
}
