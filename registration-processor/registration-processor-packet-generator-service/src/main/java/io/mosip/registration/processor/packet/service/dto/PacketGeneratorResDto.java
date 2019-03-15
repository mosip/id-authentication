package io.mosip.registration.processor.packet.service.dto;

import java.io.Serializable;

public class PacketGeneratorResDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6491468160192269529L;

	/** The registration id. */
	private String registrationId;

	/** The status. */
	private String status;

	/** The message. */
	private String message;

	public PacketGeneratorResDto() {
		super();
	}

	public String getRegistrationId() {
		return registrationId;
	}

	public void setRegistrationId(String registrationId) {
		this.registrationId = registrationId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
