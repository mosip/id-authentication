package io.mosip.registration.processor.core.packet.dto;

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
	private Document document;
	private List<FieldValue> metaData;
	private List<FieldValue> osiData;
	private List<FieldValueArray> hashSequence;
	private List<FieldValue> checkSum;

	public Biometric getBiometric() {
		return biometric;
	}

	public void setBiometric(Biometric biometric) {
		this.biometric = biometric;
	}

	public List<BiometricException> getExceptionBiometrics() {
		return exceptionBiometrics;
	}

	public void setExceptionBiometrics(List<BiometricException> exceptionBiometrics) {
		this.exceptionBiometrics = exceptionBiometrics;
	}

	public Photograph getApplicantPhotograph() {
		return applicantPhotograph;
	}

	public void setApplicantPhotograph(Photograph applicantPhotograph) {
		this.applicantPhotograph = applicantPhotograph;
	}

	public Photograph getExceptionPhotograph() {
		return exceptionPhotograph;
	}

	public void setExceptionPhotograph(Photograph exceptionPhotograph) {
		this.exceptionPhotograph = exceptionPhotograph;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public List<FieldValue> getMetaData() {
		return metaData;
	}

	public void setMetaData(List<FieldValue> metaData) {
		this.metaData = metaData;
	}

	public List<FieldValue> getOsiData() {
		return osiData;
	}

	public void setOsiData(List<FieldValue> osiData) {
		this.osiData = osiData;
	}

	public List<FieldValueArray> getHashSequence() {
		return hashSequence;
	}

	public void setHashSequence(List<FieldValueArray> hashSequence) {
		this.hashSequence = hashSequence;
	}

	public List<FieldValue> getCheckSum() {
		return checkSum;
	}

	public void setCheckSum(List<FieldValue> checkSum) {
		this.checkSum = checkSum;
	}

}
