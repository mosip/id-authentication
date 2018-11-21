package io.mosip.kernel.masterdata.dto;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PacketRejectionReasonRequestDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6392015742386472560L;
	
	public List<ReasonListDto> reasonList;
	
	public List<ReasonCategoryDto> reasonCategories;

}
