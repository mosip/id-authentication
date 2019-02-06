package io.mosip.kernel.syncdata.dto.response;

import java.util.List;

import io.mosip.kernel.syncdata.dto.UserDetailDto;
import lombok.Data;

@Data
public class UserDetailResponseDto {

	List<UserDetailDto> mosipUserDtoList;
}