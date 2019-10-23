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
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DigitalIdDto {

	/** The serial no. */
	@NotBlank
	private String serialNo;

	/** The make. */
	@NotBlank
	private String make;

	/** The model. */
	@NotBlank
	private String model;

	/** The type. */

	private String type;

	/** The dp. */
	@NotBlank
	private String dp;

	/** The dp id. */
	@NotBlank
	private String dpId;

	/** The date time. */

	private String dateTime;
}
