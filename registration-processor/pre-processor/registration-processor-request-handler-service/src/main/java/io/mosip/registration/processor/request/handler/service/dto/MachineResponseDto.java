package io.mosip.registration.processor.request.handler.service.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * 
 * @author Sowmya
 * 
 */
@Data
public class MachineResponseDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4266319575132486164L;
	private List<MachineDto> machines;
}
