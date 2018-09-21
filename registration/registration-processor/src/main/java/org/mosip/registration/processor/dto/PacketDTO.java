package org.mosip.registration.processor.dto;

import java.util.List;

import org.mosip.registration.processor.dto.biometric.BiometricDTO;
import org.mosip.registration.processor.dto.demographic.DemographicDTO;

import lombok.Data;

/**
 * 
 * @author M1047595
 *
 */
@Data
public class PacketDTO extends BaseDTO{
	private BiometricDTO biometricDTO;
	private DemographicDTO demographicDTO;
	private String enrollmentID;
	private String preEnrollmentId;
	private PacketMetaDataDTO packetMetaDataDTO;
	private OSIDataDTO osiDataDTO;
	private List<AuditDTO> auditDTOs;
}
