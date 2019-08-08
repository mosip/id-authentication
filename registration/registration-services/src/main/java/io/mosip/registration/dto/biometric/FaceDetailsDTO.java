package io.mosip.registration.dto.biometric;

import io.mosip.registration.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * This class contains the information on captured Face.
 *
 * @author Sravya Surampalli
 * @since 1.0.0
 */

@Getter
@Setter
public class FaceDetailsDTO extends BaseDTO {

	/** The face. */
	private byte[] face;

	/** The quality score. */
	private double qualityScore;

	/** The is force captured. */
	private boolean isForceCaptured;

	/** The num of retry. */
	private int numOfRetries;
	
	/** The photograph name. */
	private String photographName;

	/** The compressed photo for QR Code. */
	private byte[] compressedFacePhoto;
	
	/** The face. */
	private byte[] faceISO;

}
