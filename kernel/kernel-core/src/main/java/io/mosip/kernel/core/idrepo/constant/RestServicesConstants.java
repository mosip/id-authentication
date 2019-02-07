package io.mosip.kernel.core.idrepo.constant;

/**
 * The Enum RestServiceContants.
 *
 * @author Manoj SP
 */
public enum RestServicesConstants {

	/** The audit manager service. */
	AUDIT_MANAGER_SERVICE("audit"), 
	
	CRYPTO_MANAGER_ENCRYPT("encryptor"),
	
	CRYPTO_MANAGER_DECRYPT("decryptor");

	/** The service name. */
	private final String serviceName;

	/**
	 * Instantiates a new rest service contants.
	 *
	 * @param serviceName the service name
	 */
	private RestServicesConstants(String serviceName) {
		this.serviceName = serviceName;
	}

	/**
	 * Gets the service name.
	 *
	 * @return the service name
	 */
	public String getServiceName() {
		return serviceName;
	}
}
