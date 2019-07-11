package io.mosip.kernel.syncdata.dto.response;

import java.util.List;

import io.mosip.kernel.syncdata.dto.RoleDto;
import lombok.Data;

/**
 * 
 * @author Srinivasan
 * @since 1.0.0
 *
 */
@Data
public class RolesResponseDto {

	private String lastSyncTime;

	private List<RoleDto> roles;

}
