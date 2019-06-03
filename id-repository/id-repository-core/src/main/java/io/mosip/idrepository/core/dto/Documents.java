package io.mosip.idrepository.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The Class Documents.
 *
 * @author Manoj SP
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
