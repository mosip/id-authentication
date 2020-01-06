package io.mosip.kernel.uingenerator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response dto for update the uin status
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UinStatusUpdateReponseDto {

	/**
	 * The uin
	 */
	private String uin;
	/**
	 * The uin status
	 */
	private String status;

}
