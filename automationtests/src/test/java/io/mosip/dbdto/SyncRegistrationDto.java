/**
 * 
 */
package io.mosip.dbdto;
	
import java.math.BigInteger;

import lombok.Data;

/**
 * The Class SyncRegistrationDto.
 *
 * @author M1047487
 * @author Girish Yarru
 */
@Data
public class SyncRegistrationDto  {

	public String getLangCode() {
		return langCode;
	}
	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}
	public String getRegistrationId() {
		return registrationId;
	}
	public void setRegistrationId(String registrationId) {
		this.registrationId = registrationId;
	}
	public String getRegistrationType() {
		return registrationType;
	}
	public void setRegistrationType(String registrationType) {
		this.registrationType = registrationType;
	}
	public String getPacketHashValue() {
		return packetHashValue;
	}
	public void setPacketHashValue(String packetHashValue) {
		this.packetHashValue = packetHashValue;
	}
	public BigInteger getPacketSize() {
		return packetSize;
	}
	public void setPacketSize(BigInteger packetSize) {
		this.packetSize = packetSize;
	}
	public String getSupervisorStatus() {
		return supervisorStatus;
	}
	public void setSupervisorStatus(String supervisorStatus) {
		this.supervisorStatus = supervisorStatus;
	}
	public String getSupervisorComment() {
		return supervisorComment;
	}
	public void setSupervisorComment(String supervisorComment) {
		this.supervisorComment = supervisorComment;
	}
	private String langCode;
	private String registrationId;
	private String registrationType;
	private String packetHashValue;
	private BigInteger packetSize;
	private String supervisorStatus;
	private String supervisorComment;
}