package io.mosip.registration.dto;

import java.sql.Timestamp;
import java.util.List;

import io.mosip.registration.dto.biometric.BiometricDTO;
import io.mosip.registration.dto.demographic.DemographicDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * This DTO class contains the Registration details.
 * 
 * @author Dinesh Asokan
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class RegistrationDTO extends BaseDTO {

	private BiometricDTO biometricDTO;
	private DemographicDTO demographicDTO;
	private String registrationId;
	private String preRegistrationId;
	private RegistrationMetaDataDTO registrationMetaDataDTO;
	private OSIDataDTO osiDataDTO;
	private List<AuditDTO> auditDTOs;
	private SelectionListDTO selectionListDTO;
	private Timestamp auditLogStartTime;
	private Timestamp auditLogEndTime;
	private boolean isUpdateUINNonBiometric;	
	private boolean isNameNotUpdated;	
	private boolean isUpdateUINChild;
	private boolean isAgeCalculatedByDOB;
	
}
