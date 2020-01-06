/**
 * 
 */
package io.mosip.registration.processor.stages.uingenerator.idrepo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author M1047487
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor	
public class Documents {

	/** The doc type. */
	private String category;
	
	/** The doc value. */
	private String value; 
}
