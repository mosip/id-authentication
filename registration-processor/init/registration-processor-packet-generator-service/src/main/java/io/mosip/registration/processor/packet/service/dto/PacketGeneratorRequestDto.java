package io.mosip.registration.processor.packet.service.dto;

import java.io.Serializable;

import io.mosip.registration.processor.core.common.rest.dto.BaseRestRequestDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/*
 * (non-Javadoc)
 * 
 * @see io.mosip.registration.processor.core.common.rest.dto.BaseRestRequestDTO#
 * hashCode()
 * @author Sowmya
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PacketGeneratorRequestDto extends BaseRestRequestDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7684570757540652649L;

	private PacketGeneratorDto request;

}
