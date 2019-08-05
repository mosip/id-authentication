package io.mosip.registration.processor.request.handler.service.dto;

import java.io.Serializable;
import java.util.Map;

import lombok.Data;

/**
 * Instantiates a new error response DTO.
 */
@Data
public class ErrorResponseDTO implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 7377182973303657440L;

	/** The code. */
	private String code;

	/** The message. */
	private String message;

	/** The other attributes. */
	private Map<String, Object> otherAttributes;

	/** The info type. */
	private String infoType;

	/**
	 * Gets the code.
	 *
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * Sets the code.
	 *
	 * @param code
	 *            the new code
	 */
	public void setCode(String code) {
		this.code = code;
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

	/**
	 * Gets the other attributes.
	 *
	 * @return the other attributes
	 */
	public Map<String, Object> getOtherAttributes() {
		return otherAttributes;
	}

	/**
	 * Sets the other attributes.
	 *
	 * @param otherAttributes
	 *            the other attributes
	 */
	public void setOtherAttributes(Map<String, Object> otherAttributes) {
		this.otherAttributes = otherAttributes;
	}

	/**
	 * Gets the info type.
	 *
	 * @return the info type
	 */
	public String getInfoType() {
		return infoType;
	}

	/**
	 * Sets the info type.
	 *
	 * @param infoType
	 *            the new info type
	 */
	public void setInfoType(String infoType) {
		this.infoType = infoType;
	}

}
