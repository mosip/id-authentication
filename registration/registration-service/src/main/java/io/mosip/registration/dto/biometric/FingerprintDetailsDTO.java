package io.mosip.registration.dto.biometric;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.mosip.registration.dto.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * This class contains the information on captured Finger prints
 * 
 * @author Dinesh Asokan
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FingerprintDetailsDTO extends BaseDTO {
	
	@JsonIgnore
	private byte[] fingerPrint;
	protected String fingerprintImageName;
	protected double qualityScore;
	protected boolean isForceCaptured;
	protected String fingerType;
	protected int numRetry;
}
