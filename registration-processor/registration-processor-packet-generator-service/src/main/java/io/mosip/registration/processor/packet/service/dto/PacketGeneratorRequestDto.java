package io.mosip.registration.processor.packet.service.dto;

import io.mosip.registration.processor.core.common.rest.dto.BaseRestRequestDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/*
 * (non-Javadoc)
 * 
 * @see io.mosip.registration.processor.core.common.rest.dto.BaseRestRequestDTO#
 * hashCode()
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PacketGeneratorRequestDto extends BaseRestRequestDTO {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7684570757540652649L;

	private PacketGeneratorDto request;

}
