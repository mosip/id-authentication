package io.mosip.kernel.keygenerator.bouncycastle.config;

/**
 * This is contains configuration for keygenerator
 * 
 * @author Urvil Joshi
 *
 * @since 1.0.0
 */
public enum KeyGeneratorConfig {
	/**
	 * Asymmetric key algorithm configuration {@link #algorithmName} and
	 * {@link #size} in bits
	 */
	ASYMMETRIC_ALGORITHM("RSA", "2048"),
	/**
	 * Symmetric key algorithm configuration {@link #algorithmName} and
	 * {@link #size} in bytes
	 */
	SYMMETRIC_ALGORITHM("AES", "256");

	/**
	 * Constructor for this class
	 */
	private KeyGeneratorConfig() {
	}

	/**
	 * Constructor for this class
	 * 
	 * @param algorithmName name of algorithm
	 * @param size          size of algorithm key in respective units
	 */
	private KeyGeneratorConfig(String algorithmName, String size) {
		this.setAlgorithmName(algorithmName);
		this.setSize(size);
	}

	/**
	 * Name of the algorithm
	 */
	String algorithmName;

	/**
	 * Size of the algorithm keys in respective units
	 */
	String size;

	/**
	 * Getter for {@link #algorithmName}
	 * 
	 * @return {@link #algorithmName}
	 */
	public String getAlgorithmName() {
		return algorithmName;
	}

	/**
	 * Setter for {@link #algorithmName}
	 * 
	 * @param algorithm {@link #algorithmName}
	 */
	private void setAlgorithmName(String algorithmName) {
		this.algorithmName = algorithmName;
	}

	/**
	 * Getter for {@link #size}
	 * 
	 * @return {@link #size}
	 */
	public String getSize() {
		return size;
	}

	/**
	 * Setter for {@link #size}
	 * 
	 * @param size {@link #size}
	 */
	private void setSize(String size) {
		this.size = size;
	}
}
