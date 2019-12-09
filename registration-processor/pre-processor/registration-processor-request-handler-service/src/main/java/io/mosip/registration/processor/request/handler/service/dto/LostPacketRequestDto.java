/**
 * 
 */
package io.mosip.registration.processor.request.handler.service.dto;

import java.io.Serializable;

import io.mosip.registration.processor.core.common.rest.dto.BaseRestRequestDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Sowmya
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LostPacketRequestDto extends BaseRestRequestDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2312281864823472930L;

	private LostRequestDto request;

}
