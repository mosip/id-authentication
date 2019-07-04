package io.mosip.kernel.otpmanager.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import io.mosip.kernel.otpmanager.dto.GenerationDTOValidationLevels.InvalidLengthValidationLevel;
import io.mosip.kernel.otpmanager.dto.GenerationDTOValidationLevels.NullEmptyValidationLevel;
import lombok.Data;

/**
 * The DTO class for OTP Generation request.
 * 
 * @author Ritesh Sinha
 * @author Sagar Mahapatra
 * @since 1.0.0
 * 
 */
@Data
public class OtpGeneratorRequestDto {
	/**
	 * The key against which OTP needs to be generated.
	 */
	@NotBlank(message = "Key can't be empty or null", groups = NullEmptyValidationLevel.class)
	@Size(min = 3, max = 64, message = "length should be in the range of 3-64", groups = InvalidLengthValidationLevel.class)
	private String key;

	/**
	 * Getter for key.
	 * 
	 * @return the key.
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Setter for key.
	 * 
	 * @param key the key.
	 */
	public void setKey(String key) {
		if (key != null) {
			this.key = key.trim();
		} else {
			this.key = key;
		}
	}
}
