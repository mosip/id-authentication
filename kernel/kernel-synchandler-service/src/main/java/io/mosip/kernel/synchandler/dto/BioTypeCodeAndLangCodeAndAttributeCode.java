package io.mosip.kernel.synchandler.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response class for Biometric attribute save
 * 
 * @author Uday Kumar
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BioTypeCodeAndLangCodeAndAttributeCode {
	private String code;
	private String biometricTypeCode;
	private String langCode;
}
