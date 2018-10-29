package io.mosip.registration.dto.json.metadata;
/**
 * This class is to capture the json parsing osi data
 * 
 * @author Dinesh Asokan
 * @since 1.0.0
 *
 */
public class OSIData {
	private String operatorId;
	private String operatorFingerprintImage;
	private String operatorIrisName;
	private String supervisorId;
	private String supervisorName;
	private String supervisorFingerprintImage;
	private String supervisorIrisName;
	// Below fields are used for Introducer or HOF
	private String introducerType;
	private String introducerName;
	private String introducerUIN;
	private String introducerUINHash;
	private String introducerRID;
	private String introducerRIDHash;
	private String introducerFingerprintImage;
	private String introducerIrisImage;
	public String getOperatorId() {
		return operatorId;
	}
	public void setOperatorId(String operatorId) {
		this.operatorId = operatorId;
	}
	public String getOperatorFingerprintImage() {
		return operatorFingerprintImage;
	}
	public void setOperatorFingerprintImage(String operatorFingerprintImage) {
		this.operatorFingerprintImage = operatorFingerprintImage;
	}
	public String getOperatorIrisName() {
		return operatorIrisName;
	}
	public void setOperatorIrisName(String operatorIrisName) {
		this.operatorIrisName = operatorIrisName;
	}
	public String getSupervisorId() {
		return supervisorId;
	}
	public void setSupervisorId(String supervisorId) {
		this.supervisorId = supervisorId;
	}
	public String getSupervisorName() {
		return supervisorName;
	}
	public void setSupervisorName(String supervisorName) {
		this.supervisorName = supervisorName;
	}
	public String getSupervisorFingerprintImage() {
		return supervisorFingerprintImage;
	}
	public void setSupervisorFingerprintImage(String supervisorFingerprintImage) {
		this.supervisorFingerprintImage = supervisorFingerprintImage;
	}
	public String getSupervisorIrisName() {
		return supervisorIrisName;
	}
	public void setSupervisorIrisName(String supervisorIrisName) {
		this.supervisorIrisName = supervisorIrisName;
	}
	public String getIntroducerType() {
		return introducerType;
	}
	public void setIntroducerType(String introducerType) {
		this.introducerType = introducerType;
	}
	public String getIntroducerName() {
		return introducerName;
	}
	public void setIntroducerName(String introducerName) {
		this.introducerName = introducerName;
	}
	public String getIntroducerUIN() {
		return introducerUIN;
	}
	public void setIntroducerUIN(String introducerUIN) {
		this.introducerUIN = introducerUIN;
	}
	public String getIntroducerUINHash() {
		return introducerUINHash;
	}
	public void setIntroducerUINHash(String introducerUINHash) {
		this.introducerUINHash = introducerUINHash;
	}
	public String getIntroducerRID() {
		return introducerRID;
	}
	public void setIntroducerRID(String introducerRID) {
		this.introducerRID = introducerRID;
	}
	public String getIntroducerRIDHash() {
		return introducerRIDHash;
	}
	public void setIntroducerRIDHash(String introducerRIDHash) {
		this.introducerRIDHash = introducerRIDHash;
	}
	public String getIntroducerFingerprintImage() {
		return introducerFingerprintImage;
	}
	public void setIntroducerFingerprintImage(String introducerFingerprintImage) {
		this.introducerFingerprintImage = introducerFingerprintImage;
	}
	public String getIntroducerIrisImage() {
		return introducerIrisImage;
	}
	public void setIntroducerIrisImage(String introducerIrisImage) {
		this.introducerIrisImage = introducerIrisImage;
	}

}
