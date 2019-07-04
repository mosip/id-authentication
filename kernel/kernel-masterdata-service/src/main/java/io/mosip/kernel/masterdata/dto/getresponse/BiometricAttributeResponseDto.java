package io.mosip.kernel.masterdata.dto.getresponse;

import java.util.List;

import io.mosip.kernel.masterdata.dto.BiometricAttributeDto;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 
 * @author Uday Kumar
 * @version 1.0.0
 * @since 23-10-2016
 */
@Data
@AllArgsConstructor
public class BiometricAttributeResponseDto {
	List<BiometricAttributeDto> biometricattributes;
}
