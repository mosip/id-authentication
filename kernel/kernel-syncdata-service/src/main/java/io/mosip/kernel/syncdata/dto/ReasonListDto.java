package io.mosip.kernel.syncdata.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ReasonListDto extends BaseDto{

	private String code;

	private String name;

	private String description;

	private String rsnCatCode;

	private String langCode;

	private Boolean isActive;

	private boolean isDeleted;

}
