package io.mosip.registration.processor.packet.service.dto;

import java.io.Serializable;

import lombok.Data;

/**
 * The Class PacketGeneratorResDto.
 * 
 * @author Sowmya
 */
@Data
public class PacketGeneratorResDto implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6491468160192269529L;

	/** The registration id. */
	private String registrationId;

	/** The status. */
	private String status;

	/** The message. */
	private String message;

	/**
	 * Instantiates a new packet generator res dto.
	 */
	public PacketGeneratorResDto() {
		super();
	}

	/**
	 * Gets the registration id.
	 *
	 * @return the registration id
	 */
	public String getRegistrationId() {
		return registrationId;
	}

	/**
	 * Sets the registration id.
	 *
	 * @param registrationId
	 *            the new registration id
	 */
	public void setRegistrationId(String registrationId) {
		this.registrationId = registrationId;
	}

	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Sets the status.
	 *
	 * @param status
	 *            the new status
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * Gets the message.
	 *
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets the message.
	 *
	 * @param message
	 *            the new message
	 */
	public void setMessage(String message) {
		this.message = message;
	}

}
