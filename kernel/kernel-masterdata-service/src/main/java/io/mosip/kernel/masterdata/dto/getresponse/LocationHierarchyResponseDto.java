package io.mosip.kernel.masterdata.dto.getresponse;

import java.io.Serializable;
import java.util.List;

import io.mosip.kernel.masterdata.dto.LocationHierarchyDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationHierarchyResponseDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8111451245460042722L;
	
	private List<LocationHierarchyDto> locations;

}
