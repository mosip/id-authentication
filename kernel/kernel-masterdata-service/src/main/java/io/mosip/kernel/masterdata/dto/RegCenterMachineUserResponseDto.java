package io.mosip.kernel.masterdata.dto;

import java.util.ArrayList;
import java.util.List;

import io.mosip.kernel.masterdata.entity.id.RegistrationCenterMachineUserID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Uday Kumar
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegCenterMachineUserResponseDto {
	private List<RegistrationCenterMachineUserID> mapped = new ArrayList<>();
	private List<RegistrationCenterMachineUserID> notmapped=new ArrayList<>();
	
}
