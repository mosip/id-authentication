package io.mosip.kernel.masterdata.dto.getresponse;

import java.util.ArrayList;
import java.util.List;

import io.mosip.kernel.masterdata.dto.IndividualTypeDto;
import lombok.Data;

@Data
public class IndividualTypeResponseDto {
	private List<IndividualTypeDto> individualTypes = new ArrayList<>();
}
