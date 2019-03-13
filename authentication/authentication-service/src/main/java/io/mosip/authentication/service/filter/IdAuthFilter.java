package io.mosip.authentication.service.filter;

import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Component;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.kernel.core.util.DateUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class IdAuthFilter.
 *
 * @author Manoj SP
 */
@Component
public class IdAuthFilter extends BaseAuthFilter {
	
	/** The Constant POLICY_ID. */
	private static final String POLICY_ID = "policyId";

	/** The Constant ACTIVE_STATUS. */
	private static final String ACTIVE_STATUS = "active";
	
	/** The Constant EXPIRY_DT. */
	private static final String EXPIRY_DT = "expiryDt";
	
	/** The Constant STATUS. */
	private static final String STATUS = "status";
	
	/** The Constant REQUEST. */
	private static final String REQUEST = "request";
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.authentication.service.filter.BaseAuthFilter#setTxnId(java.util.Map,
	 * java.util.Map)
	 */
	@Override
	protected Map<String, Object> setResponseParams(Map<String, Object> requestBody, Map<String, Object> responseBody)
			throws IdAuthenticationAppException {
		Map<String, Object> responseParams = super.setResponseParams(requestBody, responseBody);
		return setAuthResponseParam(requestBody, responseParams);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.authentication.service.filter.BaseAuthFilter#decodedRequest(java.
	 * util.Map)
	 */
	@Override
	protected Map<String, Object> decipherRequest(Map<String, Object> requestBody) throws IdAuthenticationAppException {
		try {
			requestBody.replace(REQUEST, decode((String) requestBody.get(REQUEST)));
			if (null != requestBody.get(REQUEST)) {
				Map<String, Object> request = keyManager.requestData(requestBody, mapper);
				requestBody.replace(REQUEST, request);
			}
			return requestBody;
		} catch (ClassCastException e) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(),
					IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.authentication.service.filter.BaseAuthFilter#validateSignature(java.
	 * lang.String, byte[])
	 */
	@Override
	protected boolean validateSignature(String signature, byte[] requestAsByte) throws IdAuthenticationAppException {
		return true;
	}
	
	/**
	 * License key MISP mapping is associated with this method.It checks for the license key expiry and staus.
	 *
	 * @param licenseKey the license key
	 * @param mispId the misp id
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	private void licenseKeyMISPMapping(String licenseKey, String mispId) throws IdAuthenticationAppException {
		String licensekeyMappingJson = env.getProperty("licensekey.mispmapping." + licenseKey + "." + mispId);
		Map<String, String> licenseKeyMap = null;
		if (null != licensekeyMappingJson) {
			try {
				licenseKeyMap = mapper.readValue(mapper.writeValueAsBytes(licensekeyMappingJson), Map.class);
			} catch (IOException e) {
				throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS);
			}
			String lkExpiryDt = licenseKeyMap.get(EXPIRY_DT);
			if (DateUtils.convertUTCToLocalDateTime(lkExpiryDt).isBefore(DateUtils.getUTCCurrentDateTime())) {
				throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.LICENSEKEY_EXPIRED);
			}
			String lkStatus = licenseKeyMap.get(STATUS);
			if (lkStatus != ACTIVE_STATUS) {
				throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.LICENSEKEY_SUSPENDED);
			}
		} else {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.INVALID_LICENSEKEY);
		}

	}

	/**
	 *this method checks whether  partner id is valid.
	 *
	 * @param partnerId the partner id
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	public void validPartnerId(String partnerId) throws IdAuthenticationAppException {
		String partnerIdJson = env.getProperty("partner." + partnerId);
		Map<String, String> partnerIdMap = null;
		if (null == partnerIdJson) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.PARTNER_NOT_REGISTERED);
		} else {
			try {
				partnerIdMap = mapper.readValue(mapper.writeValueAsBytes(partnerIdJson), Map.class);
			} catch (IOException e) {
				throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS);
			}
			 String policyId = partnerIdMap.get(POLICY_ID);
			 if(null==policyId || policyId.equalsIgnoreCase("")) {
			  throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.PARTNER_NOT_REGISTERED);//FIXME
			 } 
			 String partnerStatus = partnerIdMap.get(STATUS);
			 if(partnerStatus!=ACTIVE_STATUS) {
			 throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.PARTNER_DEACTIVATED);
			 }
		}
	}

	/**
	 * Validates MISP partner mapping,if its valid it returns the policyId.
	 *
	 * @param partnerId the partner id
	 * @param mispId the misp id
	 * @return the string
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	public String validMISPPartnerMapping(String partnerId, String mispId) throws IdAuthenticationAppException {
		boolean partnerPolicyMappingJson = env.getProperty("partner.policy." + partnerId + "." + mispId,boolean.class);
		Map<String, String> partnerIdMap=null;
		String  policyId=null;
		if (partnerPolicyMappingJson != true) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.PARTNER_NOT_MAPPED);
		}
		String partnerIdJson = env.getProperty("misp.partner.mapping." + partnerId);
		 try {
			partnerIdMap = mapper.readValue(mapper.writeValueAsBytes(partnerIdJson), Map.class);
			policyId=partnerIdMap.get(POLICY_ID);
		} catch (IOException e) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS);
		}
		 return policyId;
	}
	
	
}