package io.mosip.registration.processor.packet.service.dto;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import io.mosip.registration.processor.packet.service.dto.demographic.DemographicDTO;
import lombok.Data;

/**
 * This class contains the Registration details.
 * 
 * @author Dinesh Asokan
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
@Data
public class RegistrationDTO extends BaseDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5931095944645820246L;
	private DemographicDTO demographicDTO;
	private String registrationId;
	private String registrationIdHash;

	private RegistrationMetaDataDTO registrationMetaDataDTO;
	private List<AuditDTO> auditDTOs;
	private Timestamp auditLogStartTime;
	private Timestamp auditLogEndTime;

}
