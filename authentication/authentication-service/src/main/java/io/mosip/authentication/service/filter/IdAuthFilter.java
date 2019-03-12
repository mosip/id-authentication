package io.mosip.authentication.service.filter;

import java.util.Map;

import org.springframework.stereotype.Component;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;

/**
 * The Class IdAuthFilter.
 *
 * @author Manoj SP
 */
@Component
public class IdAuthFilter extends BaseAuthFilter {
	
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
	
	private void licenseKeyMISPMapping(String licenseKey,String mispId) throws IdAuthenticationAppException {
		String licensekeyMappingJson=env.getProperty("licensekey.mispmapping."+licenseKey+"."+mispId);
		
	  if(null!=licensekeyMappingJson) {
//		  String lkExpiryDt = JsonPath.read(licensekeyMappingJson, "expiryDt"); 
//		if(DateUtils.convertUTCToLocalDateTime(lkExpiryDt).isBefore(DateUtils.getUTCCurrentDateTime())){
//			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.MISP_LICENSEKEYEXP);
//		}
//		String lkStatus = JsonPath.read(licensekeyMappingJson, "status");
//		if(lkStatus!="active"){
//			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.MISP_LKINACTIVE);
//		}
		} else {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.LICENSEKEY_EXPIRED);
		}

	}
    
	public void validPartnerId(String partnerId) throws IdAuthenticationAppException {
		String partnerIdJson = env.getProperty("partner." + partnerId);
		if (null == partnerIdJson) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.LICENSEKEY_EXPIRED);
		} else {
//			 String policyId = JsonPath.read(partnerIdJson, "policyId");
//		   if(null==policyId || policyId.equalsIgnoreCase("") )	
//			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.POLICY_NOTREGISTERED);
//		   String partnerStatus = JsonPath.read(partnerIdJson, "status"); 
//		   if(partnerStatus!="active") {
//			   throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.PARTNER_NOTACTIVE);   
//		   }
		}
	}

	public void validMISPPartnerMapping(String patnerId, String mispId) throws IdAuthenticationAppException {
		String partnerPolicyMappingJson = env.getProperty("partner.policy." + patnerId + "." + mispId);
		if (partnerPolicyMappingJson != "true") {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.PARTNER_NOT_MAPPED);
		}
	 }
	
}