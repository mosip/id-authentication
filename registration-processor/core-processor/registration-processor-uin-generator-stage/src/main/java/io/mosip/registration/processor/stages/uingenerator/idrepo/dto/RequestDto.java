/**
 * 
 */
package io.mosip.registration.processor.stages.uingenerator.idrepo.dto;

import java.util.List;

import io.mosip.registration.processor.core.idrepo.dto.Documents;
import lombok.Data;

/**
 * The Class RequestDto.
 *
 * @author M1047487
 */

/**
 * Instantiates a new request dto.
 */
@Data
public class RequestDto {
	
	/** The identity. */
	private Object identity;
	
	/** The documents. */
	private List<Documents> documents;
	
	/** The registration id. */
	private String registrationId;
	
	private String status;

	/** The UIN */
	private String biometricReferenceId;
}
