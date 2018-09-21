package org.mosip.registration.processor.dto;

import lombok.Data;
/**
 * Registration and metainfo 
 * @author M1047595
 *
 */
@Data
public class EnrollmentDTO extends BaseDTO{
	private PacketDTO packetDTO;
	private EnrollmentMetaDataDTO enrollmentMetaDataDTO;
}
