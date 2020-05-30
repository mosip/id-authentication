package io.mosip.authentication.common.service.impl.bioauth;

import java.util.Map;

import org.hibernate.cfg.NotYetImplementedException;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.bioauth.IBioMatcherIntegrator;

/**
 * 
 *  The BioMatcher Integrator that integrates with v2 version of BioSDK
 */
public class BioMatcherIntegratorV2 implements IBioMatcherIntegrator {

	@Override
	public double matchValue(Map<String, String> reqInfo, Map<String, String> entityInfo,
			Map<String, Object> properties) throws IdAuthenticationBusinessException {
		throw new NotYetImplementedException();
	}

	@Override
	public double matchMultiValue(Map<String, String> reqInfo, Map<String, String> entityInfo,
			Map<String, Object> properties) throws IdAuthenticationBusinessException {
		throw new NotYetImplementedException();
	}
}
