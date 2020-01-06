package io.mosip.idrepository.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The Class DocumentsDTO - contains fields for documents data in request or
 * response.
 *
 * @author Manoj SP
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentsDTO {

	/** The doc type. */
	private String category;

	/** The doc value. */
	private String value;
}
