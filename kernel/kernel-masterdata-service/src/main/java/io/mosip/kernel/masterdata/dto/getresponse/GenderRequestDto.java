package io.mosip.kernel.masterdata.dto.getresponse;

import java.util.List;

import io.mosip.kernel.masterdata.dto.GenderDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenderRequestDto {
	
	private List<GenderDto> genderList;

}
