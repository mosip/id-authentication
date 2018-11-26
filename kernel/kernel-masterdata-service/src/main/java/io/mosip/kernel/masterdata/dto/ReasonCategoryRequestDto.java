package io.mosip.kernel.masterdata.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * 
 * @author Srinivasan
 *
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReasonCategoryRequestDto {
	
	
	private List<PostReasonCategoryDto> reasonCategories;
}
