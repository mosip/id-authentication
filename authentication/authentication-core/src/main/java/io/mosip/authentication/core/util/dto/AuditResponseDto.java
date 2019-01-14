/**
 * 
 */
package io.mosip.authentication.core.util.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class for Audit Response having status of audit
 * 
 * @author Manoj SP
 * @since 1.0.0
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditResponseDto {

	/**
	 * The boolean audit status
	 */
	private boolean status;

}
