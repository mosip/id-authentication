package io.mosip.kernel.masterdata.dto;

import lombok.Data;

/**
 * Response class for Biometric attribute save
 * 
 * @author Uday Kumar
 * @version 1.0.0
 */
@Data


public class BioTypeCodeAndLangCodeAndAttributeCode {
	private String code;
	private String biometricTypeCode;
	private String langCode;
}
