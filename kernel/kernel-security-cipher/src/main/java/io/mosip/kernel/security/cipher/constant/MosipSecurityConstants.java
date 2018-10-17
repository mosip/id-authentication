package io.mosip.kernel.security.cipher.constant;

/**
 * This is contains configuration for keygenerator
 * 
 * @author Urvil Joshi
 *
 * @since 1.0.0
 */
public enum MosipSecurityConstants {

	/**
	 * Symmetric key algorithm configuration {@link #algorithmName} and
	 * {@link #size} in bytes
	 */
	SYMMETRIC_ALGORITHM("AES");

	/**
	 * Constructor for this class
	 */
	private MosipSecurityConstants() {
	}

	/**
	 * Constructor for this class
	 * 
	 * @param algorithmName
	 *            name of algorithm
	 * @param size
	 *            size of algorithm key in respective units
	 */
	private MosipSecurityConstants(String algorithmName) {
		this.setAlgorithmName(algorithmName);
	}

	/**
	 * Name of the algorithm
	 */
	String algorithmName;

	/**
	 * Setter for {@link #algorithmName}
	 * 
	 * @param algorithm
	 *            {@link #algorithmName}
	 */
	private void setAlgorithmName(String algorithmName) {
		this.algorithmName = algorithmName;
	}

	/**
	 * Getter for {@link #algorithmName}
	 * 
	 * @return {@link #algorithmName}
	 */
	public String getAlgorithmName() {
		return algorithmName;
	}
}
