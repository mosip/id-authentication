package io.mosip.authentication.common.service.impl.patrner;

import static io.mosip.authentication.core.constant.IdAuthCommonConstants.UTF_8;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.partner.dto.PartnerDTO;
import io.mosip.authentication.core.partner.dto.PolicyDTO;
import io.mosip.authentication.core.spi.partner.service.PartnerService;

@Service
public class PartnerServiceImpl implements PartnerService {
	
	/** The env. */
	@Autowired
	protected Environment env;

	/** The mapper. */
	@Autowired
	protected ObjectMapper mapper;

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
	
	public Optional<PartnerDTO> getPartner(String partnerId) throws IdAuthenticationBusinessException {
		try {
			String partnerJson = env.getProperty(IdAuthConfigKeyConstants.PARTNER_KEY + partnerId);
			if(partnerJson != null) {
				PartnerDTO partnerDTO = mapper.readValue(partnerJson.getBytes(UTF_8), PartnerDTO.class);
				partnerDTO.setPartnerId(partnerId);
				return Optional.of(partnerDTO);
			}
		} catch (IOException e) {
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}
		
		return Optional.empty();
		
	}
	
	public boolean hasMispPartnerMapping(String partnerId, String mispId) throws IdAuthenticationBusinessException {
		return env
				.getProperty(IdAuthConfigKeyConstants.MISP_PARTNER_MAPPING + mispId + "." + partnerId, boolean.class, false);
	}

}
