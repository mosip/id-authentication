package io.mosip.kernel.syncdata.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import io.mosip.kernel.syncdata.dto.RoleDto;
import lombok.Data;

@Data
public class RolesResponseDto {

	private List<RoleDto> roles;
	
	private LocalDateTime lastSyncTime;

}
