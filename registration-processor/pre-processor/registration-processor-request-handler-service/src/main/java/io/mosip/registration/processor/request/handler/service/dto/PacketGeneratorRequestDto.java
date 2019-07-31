package io.mosip.registration.processor.request.handler.service.dto;

import java.io.Serializable;

import io.mosip.registration.processor.core.common.rest.dto.BaseRestRequestDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/*
 * (non-Javadoc)
 * 
 * @see io.mosip.registration.processor.core.common.rest.dto.BaseRestRequestDTO#
 * hashCode()
 * @author Sowmya
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class PacketGeneratorRequestDto extends BaseRestRequestDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7684570757540652649L;

	private PacketGeneratorDto request;

}
