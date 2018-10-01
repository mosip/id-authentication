package org.mosip.registration.dto;

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
