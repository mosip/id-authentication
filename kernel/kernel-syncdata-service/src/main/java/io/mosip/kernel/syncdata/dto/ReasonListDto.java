package io.mosip.kernel.syncdata.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReasonListDto {

	private String code;

	private String name;

	private String description;

	private String rsnCatCode;

	private String langCode;

	private Boolean isActive;

	private boolean isDeleted;

}
