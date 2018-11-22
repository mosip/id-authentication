package io.mosip.kernel.masterdata.dto;

import java.io.Serializable;
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
public class ReasonCategoryRequestDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7207202901342908222L;
	
	private List<PostReasonCategoryDto> reasonCategories;
}
