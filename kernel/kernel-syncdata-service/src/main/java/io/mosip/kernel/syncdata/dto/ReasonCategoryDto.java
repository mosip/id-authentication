package io.mosip.kernel.syncdata.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ReasonCategoryDto extends BaseDto{
	private String code;

	private String name;

	private String description;

	private String langCode;
	
	private Boolean isActive;
	
	private Boolean isDeleted;

	private List<ReasonListDto> reasonList = new ArrayList<>();
}
