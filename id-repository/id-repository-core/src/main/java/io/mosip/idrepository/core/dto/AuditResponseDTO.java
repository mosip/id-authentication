package io.mosip.idrepository.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class for Audit Response having status of audit
 * 
 * @author Manoj SP
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditResponseDTO {

	/**
	 * The boolean audit status
	 */
	private boolean status;

}
