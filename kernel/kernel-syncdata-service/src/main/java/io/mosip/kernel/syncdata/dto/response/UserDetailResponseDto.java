package io.mosip.kernel.syncdata.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import io.mosip.kernel.syncdata.dto.UserDetailDto;
import lombok.Data;

@Data
public class UserDetailResponseDto {

	private List<UserDetailDto> mosipUserDtoList;
	
	private LocalDateTime lastSyncTime;
}