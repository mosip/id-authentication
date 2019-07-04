package io.mosip.registration.processor.core.idrepo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The Class Documents.
 *
 * @author M1048358 Alok
 */

/**
 * Instantiates a new documents.
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
