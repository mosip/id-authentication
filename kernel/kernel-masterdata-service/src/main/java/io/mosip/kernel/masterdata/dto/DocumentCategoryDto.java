package io.mosip.kernel.masterdata.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Response dto for Document Category Detail
 * 
 * @author Neha
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@Data
@ApiModel(value = "DocumentCategory", description = "DocumentCategory resource representation")
public class DocumentCategoryDto {

	/**
	 * Document category code.
	 */
	@NotBlank
	@Size(min = 1, max = 36)
	@ApiModelProperty(value = "code", required = true, dataType = "java.lang.String")
	private String code;

	/**
	 * Document category name.
	 */
	@NotBlank
	@Size(min = 1, max = 64)
	@ApiModelProperty(value = "name", required = true, dataType = "java.lang.String")
	private String name;

	/**
	 * Document category description
	 */
	@Size(min = 0, max = 128)
	@ApiModelProperty(value = "Application description", required = false, dataType = "java.lang.String")
	private String description;

	/**
	 * The Language Code.
	 */
	@NotBlank
	@Size(min = 1, max = 3)
	@ApiModelProperty(value = "Language Code", required = true, dataType = "java.lang.String")
	private String langCode;

	/**
	 * Is active or not.
	 */
	@NotNull
	@ApiModelProperty(value = "Application isActive Status", required =  true, dataType = "java.lang.Boolean")
	private Boolean isActive;

}
