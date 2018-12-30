package io.mosip.kernel.idrepo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * The Class Documents.
 *
 * @author Manoj SP
 */

/**
 * Instantiates a new documents.
 */
@Data
@AllArgsConstructor
public class Documents {

	/** The doc type. */
	private String docType;
	
	/** The doc value. */
	private String docValue; 
}
