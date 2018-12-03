package io.mosip.kernel.masterdata.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.mosip.kernel.masterdata.entity.RegistrationCenterType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO class for {@link RegistrationCenterType}.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationCenterTypeDto {
	/**
	 * code of the registration center type.
	 */
	@NotNull
	@Size(min = 1, max = 36)
	private String code;
	/**
	 * language code of the registration center type.
	 */
	@NotNull
	@Size(min = 1, max = 3)
	private String langCode;
	/**
	 * name of the registration center type.
	 */
	@NotNull
	@Size(min = 1, max = 64)
	private String name;
	/**
	 * description of the registration center type.
	 */
	@NotNull
	@Size(min = 1, max = 128)
	private String descr;
	/**
	 * activeness of the registration center type.
	 */
	@NotNull
	private Boolean isActive;
}
