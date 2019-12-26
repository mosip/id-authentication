/**
 * 
 */
package io.mosip.registration.processor.status.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The Class RegistrationStatusErrorDto.
 *
 * @author M1022006
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonPropertyOrder({ "registrationId", "errorCode", "errorMessage" })
public class RegistrationStatusErrorDto extends ErrorDTO {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -5261464773892046294L;

	/**
	 * Instantiates a new registration status error dto.
	 *
	 * @param errorcode
	 *            the errorcode
	 * @param message
	 *            the message
	 */
	public RegistrationStatusErrorDto(String errorcode, String message) {
		super(errorcode, message);
	}

	/** The registration id. */
	private String registrationId;

}
