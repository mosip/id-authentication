package io.mosip.kernel.masterdata.dto;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReasonListRequestDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7511843521806760077L;
	
	private List<ReasonListDto> reasonList;
}
