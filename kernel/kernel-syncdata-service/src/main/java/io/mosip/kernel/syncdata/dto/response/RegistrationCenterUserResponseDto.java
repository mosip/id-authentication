package io.mosip.kernel.syncdata.dto.response;

import java.util.List;

import io.mosip.kernel.syncdata.dto.RegistrationCenterUserDto;
import lombok.Data;

@Data
public class RegistrationCenterUserResponseDto {

	List<RegistrationCenterUserDto> registrationCenterUsers;
}
