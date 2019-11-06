package io.mosip.kernel.masterdata.dto;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Srinivasan
 * @since 1.0.0
 *
 */
//@AllArgsConstructor
//@NoArgsConstructor
@Data
public class DigitalIdDto {

	/** The serial no. */
	@NotBlank
	private String serialNumber;
	
	
	/** The Device Provider Name. */
	@NotBlank
	private String providerName;
	

	/** The Device Provider id. */
	@NotBlank
	private String providerId;
	
	
	/** The make. */
	@NotBlank
	private String make;

	/** The model. */
	@NotBlank
	private String model;

	/** The type.
	 * (can only be “Fingerprint”, “Slab Fingerprint”, “Iris Monocular”, “Iris Binocular”, “Face”)
	 */
	private String type;

	

	/** The date time. */

	private String dateTime;
}
