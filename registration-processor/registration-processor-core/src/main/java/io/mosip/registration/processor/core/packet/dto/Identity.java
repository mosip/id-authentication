package io.mosip.registration.processor.core.packet.dto;

import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * This contains the attributes which have to be displayed in PacketMetaInfo
 * JSON.
 *
 * @author Balaji Sridharan
 * @since 1.0.0
 */
public class Identity {

	/** The biometric. */
	private Biometric biometric;

	/** The exception biometrics. */
	private List<BiometricExceptionDto> exceptionBiometrics;

	/** The applicant photograph. */
	private Photograph applicantPhotograph;

	/** The exception photograph. */
	private Photograph exceptionPhotograph;

	/** The documents. */
	private List<Document> documents;

	/** The meta data. */
	private List<FieldValue> metaData;

	/** The osi data. */
	private List<FieldValue> osiData;

	/** The hash sequence. */
	private List<FieldValueArray> hashSequence;

	/** The captured registered devices. */
	private List<FieldValue> capturedRegisteredDevices;

	/** The captured non registered devices. */
	private List<FieldValue> capturedNonRegisteredDevices;

	/** The check sum. */
	private List<FieldValue> checkSum;

	/**
	 * Gets the biometric.
	 *
	 * @return the biometric
	 */
	public Biometric getBiometric() {
		return biometric;
	}

	/**
	 * Sets the biometric.
	 *
	 * @param biometric
	 *            the biometric to set
	 */
	public void setBiometric(Biometric biometric) {
		this.biometric = biometric;
	}

	/**
	 * Gets the exception biometrics.
	 *
	 * @return the exceptionBiometrics
	 */
	public List<BiometricExceptionDto> getExceptionBiometrics() {
		return exceptionBiometrics;
	}

	/**
	 * Sets the exception biometrics.
	 *
	 * @param exceptionBiometrics
	 *            the exceptionBiometrics to set
	 */
	public void setExceptionBiometrics(List<BiometricExceptionDto> exceptionBiometrics) {
		this.exceptionBiometrics = exceptionBiometrics;
	}

	/**
	 * Gets the applicant photograph.
	 *
	 * @return the applicantPhotograph
	 */
	public Photograph getApplicantPhotograph() {
		return applicantPhotograph;
	}

	/**
	 * Sets the applicant photograph.
	 *
	 * @param applicantPhotograph
	 *            the applicantPhotograph to set
	 */
	public void setApplicantPhotograph(Photograph applicantPhotograph) {
		this.applicantPhotograph = applicantPhotograph;
	}

	/**
	 * Gets the exception photograph.
	 *
	 * @return the exceptionPhotograph
	 */
	public Photograph getExceptionPhotograph() {
		return exceptionPhotograph;
	}

	/**
	 * Sets the exception photograph.
	 *
	 * @param exceptionPhotograph
	 *            the exceptionPhotograph to set
	 */
	public void setExceptionPhotograph(Photograph exceptionPhotograph) {
		this.exceptionPhotograph = exceptionPhotograph;
	}

	/**
	 * Gets the documents.
	 *
	 * @return the documents
	 */
	public List<Document> getDocuments() {
		return documents;
	}

	/**
	 * Sets the documents.
	 *
	 * @param documents
	 *            the documents to set
	 */
	public void setDocuments(List<Document> documents) {
		this.documents = documents;
	}

	/**
	 * Gets the meta data.
	 *
	 * @return the metaData
	 */
	public List<FieldValue> getMetaData() {
		return metaData;
	}

	/**
	 * Sets the meta data.
	 *
	 * @param metaData
	 *            the metaData to set
	 */
	public void setMetaData(List<FieldValue> metaData) {
		this.metaData = metaData;
	}

	/**
	 * Gets the osi data.
	 *
	 * @return the osiData
	 */
	public List<FieldValue> getOsiData() {
		return osiData;
	}

	/**
	 * Sets the osi data.
	 *
	 * @param osiData
	 *            the osiData to set
	 */
	public void setOsiData(List<FieldValue> osiData) {
		this.osiData = osiData;
	}

	/**
	 * Gets the hash sequence.
	 *
	 * @return the hashSequence
	 */
	public List<FieldValueArray> getHashSequence() {
		return hashSequence;
	}

	/**
	 * Sets the hash sequence.
	 *
	 * @param hashSequence
	 *            the hashSequence to set
	 */
	public void setHashSequence(List<FieldValueArray> hashSequence) {
		this.hashSequence = hashSequence;
	}

	/**
	 * Gets the check sum.
	 *
	 * @return the checkSum
	 */
	public List<FieldValue> getCheckSum() {
		return checkSum;
	}

	/**
	 * Sets the check sum.
	 *
	 * @param checkSum
	 *            the checkSum to set
	 */
	public void setCheckSum(List<FieldValue> checkSum) {
		this.checkSum = checkSum;
	}

	/**
	 * Gets the captured registered devices.
	 *
	 * @return the captured registered devices
	 */
	public List<FieldValue> getCapturedRegisteredDevices() {
		return capturedRegisteredDevices;
	}

	/**
	 * Sets the captured registered devices.
	 *
	 * @param capturedRegisteredDevices
	 *            the new captured registered devices
	 */
	public void setCapturedRegisteredDevices(List<FieldValue> capturedRegisteredDevices) {
		this.capturedRegisteredDevices = capturedRegisteredDevices;
	}

	/**
	 * Gets the captured non registered devices.
	 *
	 * @return the captured non registered devices
	 */
	public List<FieldValue> getCapturedNonRegisteredDevices() {
		return capturedNonRegisteredDevices;
	}

	/**
	 * Sets the captured non registered devices.
	 *
	 * @param capturedNonRegisteredDevices
	 *            the new captured non registered devices
	 */
	public void setCapturedNonRegisteredDevices(List<FieldValue> capturedNonRegisteredDevices) {
		this.capturedNonRegisteredDevices = capturedNonRegisteredDevices;
	}

}
