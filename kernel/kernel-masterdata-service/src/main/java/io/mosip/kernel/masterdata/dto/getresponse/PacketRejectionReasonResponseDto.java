package io.mosip.kernel.masterdata.dto.getresponse;

import java.util.List;

import io.mosip.kernel.masterdata.dto.ReasonCategoryDto;
import lombok.Data;

@Data


public class PacketRejectionReasonResponseDto {
	
	private List<ReasonCategoryDto> reasonCategories;
	

}
