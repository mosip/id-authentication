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

	private Biometric leftEye;
	private Biometric rightEye;
	private Biometric leftSlap;
	private Biometric rightSlap;
	private Biometric thumbs;
	private Biometric parentFingerprint;
	private Biometric parentIris;
	private List<BiometricException> exceptionBiometrics;
	private Photograph applicantPhotograph;
	private Photograph exceptionPhotograph;
	private List<FieldValue> metaData;
	private List<FieldValue> osiData;
	private List<FieldValueArray> hashSequence;
	private List<FieldValue> checkSum;

	/**
	 * @return the leftEye
	 */
	public Biometric getLeftEye() {
		return leftEye;
	}

	/**
	 * @param leftEye
	 *            the leftEye to set
	 */
	public void setLeftEye(Biometric leftEye) {
		this.leftEye = leftEye;
	}

	/**
	 * @return the rightEye
	 */
	public Biometric getRightEye() {
		return rightEye;
	}

	/**
	 * @param rightEye
	 *            the rightEye to set
	 */
	public void setRightEye(Biometric rightEye) {
		this.rightEye = rightEye;
	}

	/**
	 * @return the leftSlap
	 */
	public Biometric getLeftSlap() {
		return leftSlap;
	}

	/**
	 * @param leftSlap
	 *            the leftSlap to set
	 */
	public void setLeftSlap(Biometric leftSlap) {
		this.leftSlap = leftSlap;
	}

	/**
	 * @return the rightSlap
	 */
	public Biometric getRightSlap() {
		return rightSlap;
	}

	/**
	 * @param rightSlap
	 *            the rightSlap to set
	 */
	public void setRightSlap(Biometric rightSlap) {
		this.rightSlap = rightSlap;
	}

	/**
	 * @return the thumbs
	 */
	public Biometric getThumbs() {
		return thumbs;
	}

	/**
	 * @param thumbs
	 *            the thumbs to set
	 */
	public void setThumbs(Biometric thumbs) {
		this.thumbs = thumbs;
	}

	/**
	 * @return the parentFingerprint
	 */
	public Biometric getParentFingerprint() {
		return parentFingerprint;
	}

	/**
	 * @param parentFingerprint
	 *            the parentFingerprint to set
	 */
	public void setParentFingerprint(Biometric parentFingerprint) {
		this.parentFingerprint = parentFingerprint;
	}

	/**
	 * @return the parentIris
	 */
	public Biometric getParentIris() {
		return parentIris;
	}

	/**
	 * @param parentIris
	 *            the parentIris to set
	 */
	public void setParentIris(Biometric parentIris) {
		this.parentIris = parentIris;
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
