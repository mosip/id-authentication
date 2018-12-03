package io.mosip.kernel.masterdata.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationHierarchyDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4552110961570300174L;
	
	@NotNull
	private Short locationHierarchylevel;
	
	@NotNull
	@Size(min=1,max=64)
	private String locationHierarchyName;
	
	@NotNull
	private Boolean isActive;

}
