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
	
	public Timestamp getAuditLogStartTime() {
		if(this.auditLogStartTime!=null) {
			return new Timestamp(this.auditLogStartTime.getTime());
		}
		else
			return null;
		
	}
	
	public void setAuditLogStartTime(Timestamp auditLogStartTime) {
		if(auditLogStartTime!=null) {
			this.auditLogStartTime=new Timestamp(this.auditLogStartTime.getTime());
		}
		else
			this.auditLogStartTime=null;
		
	}
	
	public Timestamp getAuditLogEndTime() {
		if(this.auditLogEndTime!=null) {
			return new Timestamp(this.auditLogEndTime.getTime());
		}
		else
			return null;
	}
	
	public void setAuditLogEndTime(Timestamp auditLogEndTime) {
		if(auditLogEndTime!=null) {
			this.auditLogEndTime=new Timestamp(this.auditLogEndTime.getTime());
		}
		else
			this.auditLogEndTime=null;
	
	
	}

}
