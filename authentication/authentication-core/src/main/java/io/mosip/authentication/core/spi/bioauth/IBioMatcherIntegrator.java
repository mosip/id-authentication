package io.mosip.authentication.core.spi.bioauth;

import java.util.Map;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;

public interface IBioMatcherIntegrator {

	/**
	 * Match the request and entity values and return calculated score.
	 *
	 * @param reqInfo
	 *            the req info
	 * @param entityInfo
	 *            the entity info
	 * @param properties 
	 * @return the double
	 * @throws IdAuthenticationBusinessException
	 *             the id authentication business exception
	 */
	double matchValue(Map<String, String> reqInfo, Map<String, String> entityInfo, Map<String, Object> properties)
			throws IdAuthenticationBusinessException;

	/**
	 * Match Multiple values and return calculated score.
	 *
	 * @param reqInfo
	 *            the req info
	 * @param entityInfo
	 *            the entity info
	 * @param properties 
	 * @return the double
	 * @throws IdAuthenticationBusinessException
	 *             the id authentication business exception
	 */
	double matchMultiValue(Map<String, String> reqInfo, Map<String, String> entityInfo, Map<String, Object> properties)
			throws IdAuthenticationBusinessException;

}