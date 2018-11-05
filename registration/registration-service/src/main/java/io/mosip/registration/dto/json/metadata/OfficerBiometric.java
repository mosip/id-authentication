package io.mosip.registration.dto.json.metadata;

/**
 * This class contains the attributes for Officer Biometric in PacketMetaInfo
 * JSON
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
public class OfficerBiometric extends FieldValue {

	private String biometricType;

	/**
	 * @return the biometricType
	 */
	public String getBiometricType() {
		return biometricType;
	}

	/**
	 * @param biometricType
	 *            the biometricType to set
	 */
	public void setBiometricType(String biometricType) {
		this.biometricType = biometricType;
	}

}
