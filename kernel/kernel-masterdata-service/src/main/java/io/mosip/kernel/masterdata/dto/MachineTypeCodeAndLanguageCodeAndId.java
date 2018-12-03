package io.mosip.kernel.masterdata.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response class for Machine specification save 
 * 
 * @author Megha Tanga
 * @version 1.0.0
 */
@Data


public class MachineTypeCodeAndLanguageCodeAndId {
	
	private String id;
	private String machineTypeCode;
	private String langCode;

}
