package io.mosip.authentication.service.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequestWrapper;

import org.springframework.stereotype.Component;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthTypeDTO;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.spi.indauth.match.MatchType;
import io.mosip.authentication.service.policy.AuthPolicy;
import io.mosip.authentication.service.policy.Policy;
import io.mosip.kernel.core.util.DateUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class IdAuthFilter.
 *
 * @author Manoj SP
 */
@Component
public class IdAuthFilter extends BaseAuthFilter {
	
	private static final String MISP_ID = "mispId";

	private static final String MISP_LK = "mispLk";

	private static final String PATRNER_ID = "partnerId";

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
			/* String partnerId=(String)requestBody.get(PATRNER_ID);
			 String licenseKey=(String)requestBody.get(MISP_LK);
			 String mispId= licenseKeyMISPMapping(licenseKey);
			 validPartnerId(partnerId);
			 String policyId=validMISPPartnerMapping(partnerId, mispId);*/
			 //checkAllowedAuthTypebasedOnPolicy(policyId, authRequestDTO);
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
	private String licenseKeyMISPMapping(String licenseKey) throws IdAuthenticationAppException {
		String mispId=null;
		Map<String, String> licenseKeyMap = null;
		String licensekeyMappingJson = env.getProperty("licensekey." + licenseKey);
		if (null != licensekeyMappingJson) {
			try {
				licenseKeyMap = mapper.readValue(mapper.writeValueAsBytes(licensekeyMappingJson), Map.class);
				mispId=licenseKeyMap.get(MISP_ID);
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
      return mispId;
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
	
	public void checkAllowedAuthTypebasedOnPolicy(String policyId ,AuthRequestDTO authRequestDTO) throws IdAuthenticationAppException {
		String policyJson = env.getProperty("policy." +policyId);
		Policy policy=null;
		try {
			policy = mapper.readValue(mapper.writeValueAsBytes(policyJson), Policy.class);
			List<AuthPolicy> authPolicy=policy.getListAuthPolicy();
			List<String> allowedauthType=authPolicy.stream().filter(s->s.isMandatory()).map(s->s.getAuthType()).collect(Collectors.toList());
			List<String> allowedsubAuthType=authPolicy.stream().filter(s->s.isMandatory()).map(s->s.getAuthType()).collect(Collectors.toList());
			AuthTypeDTO authType=authRequestDTO.getRequestedAuth();
			if(authType.isDemo()&& !allowedauthType.contains(MatchType.Category.DEMO.name())) {
				throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.AUTHTYPE_NOT_ALLOWED);
				}
			
			if(authType.isBio()&& !allowedauthType.contains(MatchType.Category.BIO.name())) {
				throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.AUTHTYPE_NOT_ALLOWED);
			}
			if(authType.isBio()&& allowedauthType.contains(MatchType.Category.BIO.name())) {
				List<String> bioInfoList=authRequestDTO.getBioMetadata().stream().map(s->s.getBioType()).collect(Collectors.toList());
	              for(String bioInfo :bioInfoList) {
	            	  if(!bioInfoList.contains(bioInfo)) {
	            		  throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.AUTHTYPE_NOT_ALLOWED);
	            	  }
	              }
				}
			if(authType.isPin()&& !allowedauthType.contains(MatchType.Category.SPIN.name())) {
				throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.AUTHTYPE_NOT_ALLOWED);
			}
			if(authType.isOtp()&& !allowedauthType.contains(MatchType.Category.OTP.name())) {
				throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.OTPREQUEST_NOT_ALLOWED);
			}
			
		} catch (IOException e) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS);
		}
	}
	
	/*protected String[] getAuthPart(ResettableStreamHttpServletRequest requestWrapper) {
		String[] ver = null;
		if (requestWrapper instanceof HttpServletRequestWrapper) {
			String url = requestWrapper.getRequestURL().toString();
			String contextPath = requestWrapper.getContextPath();

			if ((Objects.nonNull(url) && !url.isEmpty()) && (Objects.nonNull(contextPath) && !contextPath.isEmpty())) {
				String[] splitedUrlByContext = url.split(contextPath);
				ver = splitedUrlByContext[1].split("/");
			}
		}
		return ver;
	}*/
	
}