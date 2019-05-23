package io.mosip.registration.processor.packet.service.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * Gets the registration id.
 *
 * @return the registration id
 */
@Getter

/**
 * Sets the registration id.
 *
 * @param registrationId
 *            the new registration id
 */
@Setter
public class RegistrationIdDTO implements Serializable {

	/** The registration id. */
	private String registrationId;

}
