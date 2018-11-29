package io.mosip.registration.dto.json.metadata;

import java.util.List;

/**
 * This contains the attributes which have to be displayed in PacketMetaInfo
 * JSON
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
public class Identity {

	private Biometric biometric;
	private List<BiometricException> exceptionBiometrics;
	private Photograph applicantPhotograph;
	private Photograph exceptionPhotograph;
	private List<Document> documents;
	private List<FieldValue> metaData;
	private List<FieldValue> osiData;
	private List<FieldValueArray> hashSequence;
	private List<FieldValue> checkSum;

	/**
	 * @return the biometric
	 */
	public Biometric getBiometric() {
		return biometric;
	}

	/**
	 * @param biometric
	 *            the biometric to set
	 */
	public void setBiometric(Biometric biometric) {
		this.biometric = biometric;
	}

	/**
	 * @return the exceptionBiometrics
	 */
	public List<BiometricException> getExceptionBiometrics() {
		return exceptionBiometrics;
	}

	/**
	 * @param exceptionBiometrics
	 *            the exceptionBiometrics to set
	 */
	public void setExceptionBiometrics(List<BiometricException> exceptionBiometrics) {
		this.exceptionBiometrics = exceptionBiometrics;
	}

	/**
	 * @return the applicantPhotograph
	 */
	public Photograph getApplicantPhotograph() {
		return applicantPhotograph;
	}

	/**
	 * @param applicantPhotograph
	 *            the applicantPhotograph to set
	 */
	public void setApplicantPhotograph(Photograph applicantPhotograph) {
		this.applicantPhotograph = applicantPhotograph;
	}

	/**
	 * @return the exceptionPhotograph
	 */
	public Photograph getExceptionPhotograph() {
		return exceptionPhotograph;
	}

	/**
	 * @param exceptionPhotograph
	 *            the exceptionPhotograph to set
	 */
	public void setExceptionPhotograph(Photograph exceptionPhotograph) {
		this.exceptionPhotograph = exceptionPhotograph;
	}

	/**
	 * @return the documents
	 */
	public List<Document> getDocuments() {
		return documents;
	}

	/**
	 * @param documents
	 *            the documents to set
	 */
	public void setDocuments(List<Document> documents) {
		this.documents = documents;
	}

	/**
	 * @return the metaData
	 */
	public List<FieldValue> getMetaData() {
		return metaData;
	}

	/**
	 * @param metaData
	 *            the metaData to set
	 */
	public void setMetaData(List<FieldValue> metaData) {
		this.metaData = metaData;
	}

	/**
	 * @return the osiData
	 */
	public List<FieldValue> getOsiData() {
		return osiData;
	}

	/**
	 * @param osiData
	 *            the osiData to set
	 */
	public void setOsiData(List<FieldValue> osiData) {
		this.osiData = osiData;
	}

	/**
	 * @return the hashSequence
	 */
	public List<FieldValueArray> getHashSequence() {
		return hashSequence;
	}

	/**
	 * @param hashSequence
	 *            the hashSequence to set
	 */
	public void setHashSequence(List<FieldValueArray> hashSequence) {
		this.hashSequence = hashSequence;
	}

	/**
	 * @return the checkSum
	 */
	public List<FieldValue> getCheckSum() {
		return checkSum;
	}

	/**
	 * @param checkSum
	 *            the checkSum to set
	 */
	public void setCheckSum(List<FieldValue> checkSum) {
		this.checkSum = checkSum;
	}

}
