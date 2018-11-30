package io.mosip.kernel.masterdata.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Uday Kumar
 * @version 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BiometricAttributeRequestDto {
	private String id;
	private String ver;
	private String timestamp;
	private BiometricAttributeDto biometricAttribute;

}
