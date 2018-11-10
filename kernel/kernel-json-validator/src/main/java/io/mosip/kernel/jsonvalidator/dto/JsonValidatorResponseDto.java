package io.mosip.kernel.jsonvalidator.dto;

import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class for Json Validator Response having status as String and warnings of Validation as ArrayList
 * 
 * @author Swati Raj
 * @since 1.0.0
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JsonValidatorResponseDto {
	
	/**
	 * Status of the JSON Validation
	 */
	private boolean valid;
	
	/**
	 * Warnings if any, present in report of JSON validation
	 */
	private ArrayList<String> warnings;

}
