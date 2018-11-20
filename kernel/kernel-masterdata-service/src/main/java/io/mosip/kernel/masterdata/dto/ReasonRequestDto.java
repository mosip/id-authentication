package io.mosip.kernel.masterdata.dto;

import java.io.Serializable;
import java.util.List;

public class ReasonRequestDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6392015742386472560L;
	
	public List<ReasonListDto> reasonListRequestDto;
	
	public List<ReasonCategoryDto> reasonCategoryRequestDtos;

}
