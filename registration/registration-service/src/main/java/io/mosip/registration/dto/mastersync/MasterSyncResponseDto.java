package io.mosip.registration.dto.mastersync;

import java.util.List;
import java.util.Map;

import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;

/**
 * @author Sreekar Chukka
 *
 * @since 1.0.0
 */
public class MasterSyncResponseDto {

	private String code;
	private String message;
	private Map<String, Object> otherAttributes;
	private String infoType;

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the otherAttributes
	 */
	public Map<String, Object> getOtherAttributes() {
		return otherAttributes;
	}

	/**
	 * @param otherAttributes the otherAttributes to set
	 */
	public void setOtherAttributes(Map<String, Object> otherAttributes) {
		this.otherAttributes = otherAttributes;
	}

	/**
	 * @return the infoType
	 */
	public String getInfoType() {
		return infoType;
	}

	/**
	 * @param infoType the infoType to set
	 */
	public void setInfoType(String infoType) {
		this.infoType = infoType;
	}

}
