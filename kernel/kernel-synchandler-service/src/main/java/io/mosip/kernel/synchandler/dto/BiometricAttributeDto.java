package io.mosip.kernel.synchandler.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Uday Kumar
 * @since 1.0.0
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BiometricAttributeDto {
	private String code;
	private String name;
	private String description;
	private Boolean isActive;

}
