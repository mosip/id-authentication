package io.mosip.kernel.masterdata.dto.getresponse;

import java.util.List;

import io.mosip.kernel.masterdata.dto.MachineRegistrationCenterDto;
import lombok.Data;
/**
 * 
 * @author M1047717
 *
 */
@Data
public class RegistrationCenterMachineResponseDto {
	List<MachineRegistrationCenterDto> machines;

}
