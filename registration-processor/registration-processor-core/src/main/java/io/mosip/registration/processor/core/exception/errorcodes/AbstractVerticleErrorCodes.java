package io.mosip.registration.processor.core.exception.errorcodes;

/**
 * The Class AbstractVerticleErrorCodes.
 */
public class AbstractVerticleErrorCodes {

	/**
	 * Instantiates a new abstract verticle error codes.
	 */
	private AbstractVerticleErrorCodes() {
	}

	/** The Constant IIS_EPU_ATU_PREFIX. */
	private static final String IIS_EPU_ATU_PREFIX = "IIS_";

	/** The Constant IIS_EPU_ATU_GEN_MODULE. */
	private static final String IIS_EPU_ATU_GEN_MODULE = IIS_EPU_ATU_PREFIX + "GEN_";

	/** The Constant IIS_EPU_ATU_DEPLOYMENT_FAILURE. */
	public static final String IIS_EPU_ATU_DEPLOYMENT_FAILURE = IIS_EPU_ATU_GEN_MODULE + "DEPLOYMENT_FAILURE";

	/** The Constant IIS_EPU_ATU_UNSUPPORTED_ENCODING. */
	public static final String IIS_EPU_ATU_UNSUPPORTED_ENCODING = IIS_EPU_ATU_GEN_MODULE + "UNSUPPORTED_ENCODING";


	public static final String IIS_EPU_ATU_CONFIGURATION_SERVER_FAILURE_EXCEPTION = IIS_EPU_ATU_GEN_MODULE
			+ "CONFIGURATION_SERVER_FAILURE_EXCEPTION";
}
