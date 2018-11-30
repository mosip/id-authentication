package io.mosip.kernel.masterdata.dto.getresponse;

import java.util.List;

import io.mosip.kernel.masterdata.dto.RegistrationCenterHierarchyLevelDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationCenterHierarchyLevelResponseDto {
	private List<RegistrationCenterHierarchyLevelDto> registrationCenters;

}
