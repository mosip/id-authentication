package io.mosip.authentication.common.service.impl.patrner;

import static io.mosip.authentication.core.constant.IdAuthCommonConstants.UTF_8;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.integration.PartnerServiceManager;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.partner.dto.License;
import io.mosip.authentication.core.partner.dto.PartnerDTO;
import io.mosip.authentication.core.partner.dto.PartnerPolicyResponseDTO;
import io.mosip.authentication.core.partner.dto.PolicyDTO;
import io.mosip.authentication.core.spi.partner.service.PartnerService;

/**
 * The Implementation of PartnerService to fetch the Partner, Policy, MISP and
 * License information. Any caching of these data to be handled here.
 * 
 * @author Loganathan Sekar
 *
 */
@Service
public class PartnerServiceImpl implements PartnerService {
	
	/** The env. */
	@Autowired
	protected Environment env;

	/** The mapper. */
	@Autowired
	protected ObjectMapper mapper;
	
	@Autowired
	private PartnerServiceManager partnerServiceManager;
	
	ConcurrentHashMap<String,PartnerPolicyResponseDTO> partnerServiceResponseMap = new ConcurrentHashMap<String, PartnerPolicyResponseDTO>();	
	

	/* (non-Javadoc)
	 * @see io.mosip.authentication.core.spi.partner.service.PartnerService#getPolicy(java.lang.String)
	 */
	@Override
	public Optional<PolicyDTO> getPolicy(String policyId) throws IdAuthenticationBusinessException  {
		try {
			String policyJson =  env.getProperty(IdAuthConfigKeyConstants.POLICY + policyId);
			if (policyJson != null) {
				PolicyDTO policyDTO = mapper.readValue(policyJson.getBytes(UTF_8), PolicyDTO.class);
				policyDTO.setPolicyId(policyId);
				return Optional.of(policyDTO);
			}
		} catch (IOException e) {
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}
		
		return Optional.empty();
		
	}
	
	/* (non-Javadoc)
	 * @see io.mosip.authentication.core.spi.partner.service.PartnerService#getPartner(java.lang.String)
	 */
	public Optional<PartnerDTO> getPartner(String partnerId) throws IdAuthenticationBusinessException {
		
		if(partnerServiceResponseMap.containsKey(partnerId)) {
			PartnerPolicyResponseDTO partnerPolicyDTO = new PartnerPolicyResponseDTO();//partnerServiceResponseMap.get(partnerId);
			PartnerDTO partnerDTO = new PartnerDTO();
			partnerDTO.setPartnerId(partnerPolicyDTO.getPartnerId());
			partnerDTO.setPartnerName(partnerPolicyDTO.getPartnerName());
			partnerDTO.setPolicyId(partnerPolicyDTO.getPolicyId());
			partnerDTO.setStatus("Active");
			return Optional.of(partnerDTO);
		}
		
		return Optional.empty();
	}
	
	/* (non-Javadoc)
	 * @see io.mosip.authentication.core.spi.partner.service.PartnerService#getMispPartnerMapping(java.lang.String, java.lang.String)
	 */
	public boolean getMispPartnerMapping(String partnerId, String mispId) throws IdAuthenticationBusinessException {
		return env
				.getProperty(IdAuthConfigKeyConstants.MISP_PARTNER_MAPPING + mispId + "." + partnerId, boolean.class, false);
	}

	/* (non-Javadoc)
	 * @see io.mosip.authentication.core.spi.partner.service.PartnerService#getLicense(java.lang.String)
	 */
	@Override
	public Optional<License> getLicense(String licenseKey) throws IdAuthenticationBusinessException {
		try {
		String licenseJson = env.getProperty(IdAuthConfigKeyConstants.LICENSE_KEY + licenseKey);
		if(licenseJson != null) {
			License license = mapper.readValue(licenseJson.getBytes(UTF_8), License.class);
			license.setLicenseKey(licenseKey);
			return Optional.of(license);
		}
		
		} catch (IOException e) {
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}
		
		return Optional.empty();
	}

	@Override
	public PartnerPolicyResponseDTO validateAndGetPolicy(String partnerId, String partner_api_key, String misp_license_key) throws IdAuthenticationBusinessException {
		PartnerPolicyResponseDTO response;
			response = partnerServiceManager.validateAndGetPolicy(partnerId, partner_api_key, misp_license_key);			
		partnerServiceResponseMap.putIfAbsent(response.getPartnerId(), response);		
		return response;
	}

}
