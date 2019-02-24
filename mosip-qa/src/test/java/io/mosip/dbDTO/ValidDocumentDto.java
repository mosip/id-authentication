package io.mosip.dbDTO;


import java.io.Serializable;

import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import io.mosip.dbDTO.BaseEntity;

import lombok.Data;

/**
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@Data
@Table(name="master.valid_document")
public class ValidDocumentDto extends BaseEntity implements Serializable {

	@NotBlank
	@Size(min = 1, max = 36)
	private String docTypeCode;

	@NotBlank
	@Size(min = 1, max = 36)
	private String docCategoryCode;

	@NotBlank
	@Size(min = 1, max = 3)
	private String langCode;

	@NotNull
	private Boolean isActive;
}
