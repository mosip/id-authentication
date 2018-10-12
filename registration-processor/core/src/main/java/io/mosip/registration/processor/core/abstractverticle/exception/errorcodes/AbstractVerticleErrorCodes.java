package io.mosip.registration.processor.core.abstractverticle.exception.errorcodes;

public class AbstractVerticleErrorCodes {
	
	private AbstractVerticleErrorCodes() {
	}
	
	private static final String IIS_EPU_ATU_PREFIX = "IIS_";
	private static final String IIS_EPU_ATU_GEN_MODULE = IIS_EPU_ATU_PREFIX + "GEN_";
	public static final String IIS_EPU_ATU_DEPLOYMENT_FAILURE = IIS_EPU_ATU_GEN_MODULE + "DEPLOYMENT_FAILURE";

}
