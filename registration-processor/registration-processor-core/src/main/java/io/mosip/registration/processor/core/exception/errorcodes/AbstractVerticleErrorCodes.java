package io.mosip.registration.processor.core.exception.errorcodes;

public class AbstractVerticleErrorCodes {

	private AbstractVerticleErrorCodes() {
	}

	private static final String IIS_EPU_ATU_PREFIX = "IIS_";
	private static final String IIS_EPU_ATU_GEN_MODULE = IIS_EPU_ATU_PREFIX + "GEN_";
	public static final String IIS_EPU_ATU_DEPLOYMENT_FAILURE = IIS_EPU_ATU_GEN_MODULE + "DEPLOYMENT_FAILURE";
	public static final String IIS_EPU_ATU_UNSUPPORTED_ENCODING = IIS_EPU_ATU_GEN_MODULE + "UNSUPPORTED_ENCODING";
	public static final String IIS_EPU_ATU_CONFIGURATION_SERVER_FAILURE_EXCEPTION = IIS_EPU_ATU_GEN_MODULE
			+ "CONFIGURATION_SERVER_FAILURE_EXCEPTION";
}
