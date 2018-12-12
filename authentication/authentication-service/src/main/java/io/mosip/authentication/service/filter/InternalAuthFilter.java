package io.mosip.authentication.service.filter;

import java.util.Map;

import io.mosip.authentication.core.exception.IdAuthenticationAppException;

/**
 * The Class InternalAuthFilter.
 * 
 * @author Sanjay Murali
 */
public class InternalAuthFilter extends BaseAuthFilter {

	/** The Constant STATUS. */
	private static final String STATUS = "status";

	/** The Constant Y. */
	private static final String Y = "Y";

	/** The Constant TXN_ID. */
	private static final String TXN_ID = "txnID";
	
	/** The Constant INFO. */
	private static final String INFO = "info";
	
	/** The Constant MATCH_INFOS. */
	private static final String MATCH_INFOS = "matchInfos";
	
	/** The Constant AUTH_TYPE. */
	private static final String AUTH_TYPE = "authType";
	
	/** The Constant FULL_ADDRESS. */
	private static final String FULL_ADDRESS = "fullAddress";
	
	/** The Constant PERSONAL_IDENTITY. */
	private static final String PERSONAL_IDENTITY = "personalIdentity";

	/* (non-Javadoc)
	 * @see io.mosip.authentication.service.filter.BaseAuthFilter#decodedRequest(java.util.Map)
	 */
	@Override
	protected Map<String, Object> decodedRequest(Map<String, Object> requestBody) throws IdAuthenticationAppException {
		return requestBody;
	}

	/* (non-Javadoc)
	 * @see io.mosip.authentication.service.filter.BaseAuthFilter#encodedResponse(java.util.Map)
	 */
	@Override
	protected Map<String, Object> encodedResponse(Map<String, Object> responseBody)	throws IdAuthenticationAppException {
		return responseBody;
	}

	/* (non-Javadoc)
	 * @see io.mosip.authentication.service.filter.BaseAuthFilter#setTxnId(java.util.Map, java.util.Map)
	 */
	@Override
	protected Map<String, Object> setTxnId(Map<String, Object> requestBody, Map<String, Object> responseBody) {
		if(Y.equals(responseBody.get(STATUS))) {
			Map<String, Object> authType = (Map<String, Object>) requestBody.get(AUTH_TYPE);
			if ((authType.get(PERSONAL_IDENTITY) instanceof Boolean) && (authType.get(FULL_ADDRESS) instanceof Boolean) 
					&& !(boolean) authType.get(PERSONAL_IDENTITY) && !(boolean) authType.get(FULL_ADDRESS)) {
				Map<String, Object> info = (Map<String, Object>) responseBody.get(INFO);
				info.remove(MATCH_INFOS);
				responseBody.replace(INFO, info);				
			}			
		}
		responseBody.replace(TXN_ID, requestBody.get(TXN_ID));
		return responseBody;
	}

	/* (non-Javadoc)
	 * @see io.mosip.authentication.service.filter.BaseAuthFilter#validateSignature(java.util.Map, java.lang.String)
	 */
	@Override
	protected boolean validateSignature(Map<String, Object> requestBody, String signature) {
		return true;
	}

}
