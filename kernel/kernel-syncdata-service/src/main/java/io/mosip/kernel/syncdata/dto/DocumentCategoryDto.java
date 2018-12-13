package io.mosip.kernel.syncdata.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class DocumentCategoryDto extends BaseDto{

	/**
	 * Document category code.
	 */
	@NotNull
	@Size(min = 1, max = 36)
	private String code;

	/**
	 * Document category name.
	 */
	@NotNull
	@Size(min = 1, max = 64)
	private String name;

	/**
	 * Document category description
	 */
	@Size(min = 1, max = 128)
	private String description;

	/**
	 * The Language Code.
	 */
	@NotNull
	@Size(min = 1, max = 3)
	private String langCode;

	/**
	 * Is active or not.
	 */
	@NotNull
	private Boolean isActive;

}
