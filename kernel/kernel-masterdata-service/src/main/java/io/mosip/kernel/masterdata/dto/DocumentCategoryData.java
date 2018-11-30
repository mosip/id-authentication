package io.mosip.kernel.masterdata.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response dto for Document Category Detail
 * 
 * @author Neha
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentCategoryData {

	/**
	 * Document category code.
	 */
	private String code;

	/**
	 * Document category name.
	 */
	private String name;

	/**
	 * Document category description
	 */
	private String description;

	/**
	 * The Language Code.
	 */
	private String langCode;

	/**
	 * Is active or not.
	 */
	private Boolean isActive;

}
