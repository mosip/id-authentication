package io.mosip.registration.processor.stages.uingenerator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response dto for uin generator
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UinResponseDto {

	/**
	 * The uin
	 */
	private String uin;

}
