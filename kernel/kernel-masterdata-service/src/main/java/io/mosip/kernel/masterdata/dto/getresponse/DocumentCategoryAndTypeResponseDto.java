package io.mosip.kernel.masterdata.dto.getresponse;

import java.util.Collection;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.mosip.kernel.masterdata.dto.DocumentTypeDto;
import io.mosip.kernel.masterdata.validator.ValidLangCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Response dto for Document Category having list of document type supported.
 * 
 * @author Bal Vikash Sharma
 * @since 1.0.0
 *
 */
@Data
@ApiModel(value = "DocumentCategoryAndTypeResponse", description = "DocumentCategory having list of suppoted document types")
public class DocumentCategoryAndTypeResponseDto {

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
	@ValidLangCode
	@NotBlank
	@Size(min = 1, max = 3)
	@ApiModelProperty(value = "Language Code", required = true, dataType = "java.lang.String")
	private String langCode;

	/**
	 * Is active or not.
	 */
	@NotNull
	@ApiModelProperty(value = "Application isActive Status", required = true, dataType = "java.lang.Boolean")
	private Boolean isActive;

	@NotNull
	private Collection<DocumentTypeDto> documentTypes;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DocumentCategoryAndTypeResponseDto other = (DocumentCategoryAndTypeResponseDto) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code)) {
			return false;
		}
		if (langCode == null) {
			if (other.langCode != null)
				return false;
		} else if (!langCode.equals(other.langCode)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((langCode == null) ? 0 : langCode.hashCode());
		return result;
	}

}
