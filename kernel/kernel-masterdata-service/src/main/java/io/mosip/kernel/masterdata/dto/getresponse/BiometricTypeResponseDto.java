package io.mosip.kernel.masterdata.dto.getresponse;

import java.util.List;

import io.mosip.kernel.masterdata.dto.BiometricTypeDto;
import lombok.Data;

@Data


public class BiometricTypeResponseDto {

	private List<BiometricTypeDto> biometrictypes;
	
}
