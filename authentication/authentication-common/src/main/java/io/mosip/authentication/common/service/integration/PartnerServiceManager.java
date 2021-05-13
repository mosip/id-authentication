package io.mosip.authentication.common.service.integration;

import java.io.IOException;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.entity.ApiKeyData;
import io.mosip.authentication.common.service.entity.MispLicenseData;
import io.mosip.authentication.common.service.entity.PartnerData;
import io.mosip.authentication.common.service.entity.PartnerMapping;
import io.mosip.authentication.common.service.entity.PolicyData;
import io.mosip.authentication.common.service.repository.ApiKeyDataRepository;
import io.mosip.authentication.common.service.repository.MispLicenseDataRepository;
import io.mosip.authentication.common.service.repository.PartnerDataRepository;
import io.mosip.authentication.common.service.repository.PartnerMappingRepository;
import io.mosip.authentication.common.service.repository.PolicyDataRepository;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.partner.dto.PartnerPolicyResponseDTO;
import io.mosip.authentication.core.partner.dto.PolicyDTO;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.websub.model.EventModel;

/**
 * This class Partner Service Manager connects to partner service to validate
 * and get the policy file for a given partnerID, partner api key and misp
 * license key.
 * 
 * @author Nagarjuna
 *
 */
@Component
@Transactional
public class PartnerServiceManager {

	private static final String API_KEY_DATA = "apiKeyData";

	private static final String PARTNER_DATA = "partnerData";

	private static final String POLICY_DATA = "policyData";

	private static final String MISP_LICENSE_DATA = "mispLicenseData";

	/** The logger. */
	private static final Logger logger = IdaLogger.getLogger(PartnerServiceManager.class);

	@Autowired
	private PartnerMappingRepository partnerMappingRepo;

	@Autowired
	private PartnerDataRepository partnerDataRepo;

	@Autowired
	private PolicyDataRepository policyDataRepo;

	@Autowired
	private ApiKeyDataRepository apiKeyRepo;

	@Autowired
	private MispLicenseDataRepository mispLicDataRepo;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private IdAuthSecurityManager securityManager;

	public PartnerPolicyResponseDTO validateAndGetPolicy(String partnerId, String partner_api_key, String misp_license_key,
			boolean certificateNeeded) throws IdAuthenticationBusinessException {
		Optional<PartnerMapping> partnerMappingDataOptional = partnerMappingRepo.findByPartnerId(partnerId);
		Optional<MispLicenseData> mispLicOptional = mispLicDataRepo.findByLicenseKey(misp_license_key);
		validatePartnerMappingDetails(partnerMappingDataOptional, mispLicOptional);
		PartnerPolicyResponseDTO response = new PartnerPolicyResponseDTO();
		PartnerMapping partnerMapping = partnerMappingDataOptional.get();
		PartnerData partnerData = partnerMapping.getPartnerData();
		PolicyData policyData = partnerMapping.getPolicyData();
		ApiKeyData apiKeyData = partnerMapping.getApiKeyData();
		MispLicenseData mispLicenseData = mispLicOptional.get();
		response.setPolicyId(policyData.getPolicyId());
		response.setPolicyName(policyData.getPolicyName());
		response.setPolicy(mapper.convertValue(policyData.getPolicy(), PolicyDTO.class));
		response.setPolicyDescription(policyData.getPolicyDescription());
		response.setPolicyStatus(policyData.getPolicyStatus().contentEquals("ACTIVE"));
		response.setPartnerId(partnerData.getPartnerId());
		response.setPartnerName(partnerData.getPartnerName());
		if (certificateNeeded) {
			response.setCertificateData(partnerData.getCertificateData());
		}
		response.setPolicyExpiresOn(policyData.getPolicyExpiresOn());
		response.setApiKeyExpiresOn(apiKeyData.getApiKeyExpiresOn());
		response.setMispExpiresOn(mispLicenseData.getMispExpiresOn());
		return response;
	}

	private void validatePartnerMappingDetails(Optional<PartnerMapping> partnerMappingDataOptional,
			Optional<MispLicenseData> mispLicOptional) throws IdAuthenticationBusinessException {
		if (partnerMappingDataOptional.isPresent() && !partnerMappingDataOptional.get().isDeleted()) {
			PartnerMapping partnerMapping = partnerMappingDataOptional.get();
			if (partnerMapping.getPartnerData().isDeleted()) {
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.PARTNER_NOT_REGISTERED.getErrorCode(),
						IdAuthenticationErrorConstants.PARTNER_NOT_REGISTERED.getErrorMessage());
			}
			if (!partnerMapping.getPartnerData().getPartnerStatus().contentEquals("ACTIVE")) {
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.PARTNER_DEACTIVATED.getErrorCode(),
						IdAuthenticationErrorConstants.PARTNER_DEACTIVATED.getErrorMessage());
			}
			if (partnerMapping.getPolicyData().isDeleted()) {
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.INVALID_POLICY_ID.getErrorCode(),
						IdAuthenticationErrorConstants.INVALID_POLICY_ID.getErrorMessage());
			}
			if (!partnerMapping.getPolicyData().getPolicyStatus().contentEquals("ACTIVE")) {
				throw new IdAuthenticationBusinessException(
						IdAuthenticationErrorConstants.PARTNER_POLICY_NOT_ACTIVE.getErrorCode(),
						IdAuthenticationErrorConstants.PARTNER_POLICY_NOT_ACTIVE.getErrorMessage());
			}
			if (!(partnerMapping.getPolicyData().getPolicyCommenceOn().isBefore(DateUtils.getUTCCurrentDateTime())
					&& partnerMapping.getPolicyData().getPolicyExpiresOn().isAfter(DateUtils.getUTCCurrentDateTime()))) {
				throw new IdAuthenticationBusinessException(
						IdAuthenticationErrorConstants.PARTNER_POLICY_NOT_ACTIVE.getErrorCode(),
						IdAuthenticationErrorConstants.PARTNER_POLICY_NOT_ACTIVE.getErrorMessage());
			}
			if (partnerMapping.getApiKeyData().isDeleted()) {
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.PARTNER_NOT_REGISTERED.getErrorCode(),
						IdAuthenticationErrorConstants.PARTNER_NOT_REGISTERED.getErrorMessage());
			}
			if (!partnerMapping.getApiKeyData().getApiKeyStatus().contentEquals("ACTIVE")) {
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.PARTNER_DEACTIVATED.getErrorCode(),
						IdAuthenticationErrorConstants.PARTNER_DEACTIVATED.getErrorMessage());
			}
			if (!(partnerMapping.getApiKeyData().getApiKeyCommenceOn().isBefore(DateUtils.getUTCCurrentDateTime())
					&& partnerMapping.getApiKeyData().getApiKeyExpiresOn().isAfter(DateUtils.getUTCCurrentDateTime()))) {
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.PARTNER_NOT_REGISTERED.getErrorCode(),
						IdAuthenticationErrorConstants.PARTNER_NOT_REGISTERED.getErrorMessage());
			}
			if (mispLicOptional.isPresent()) {
				MispLicenseData mispLicenseData = mispLicOptional.get();
				if (mispLicenseData.isDeleted()) {
					throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.INVALID_LICENSEKEY.getErrorCode(),
							IdAuthenticationErrorConstants.INVALID_LICENSEKEY.getErrorMessage());
				}
				if (!mispLicenseData.getMispStatus().contentEquals("ACTIVE")) {
					throw new IdAuthenticationBusinessException(
							IdAuthenticationErrorConstants.LICENSEKEY_SUSPENDED.getErrorCode(),
							IdAuthenticationErrorConstants.LICENSEKEY_SUSPENDED.getErrorMessage());
				}
				if (mispLicenseData.getMispCommenceOn().isAfter(DateUtils.getUTCCurrentDateTime())) {
					// TODO need to throw different exception for misp not active
					throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.INVALID_LICENSEKEY.getErrorCode(),
							IdAuthenticationErrorConstants.INVALID_LICENSEKEY.getErrorMessage());
				}
				if (mispLicenseData.getMispExpiresOn().isBefore(DateUtils.getUTCCurrentDateTime())) {
					throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.LICENSEKEY_EXPIRED.getErrorCode(),
							IdAuthenticationErrorConstants.LICENSEKEY_EXPIRED.getErrorMessage());
				}
			} else {
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.INVALID_LICENSEKEY.getErrorCode(),
						IdAuthenticationErrorConstants.INVALID_LICENSEKEY.getErrorMessage());
			}
		} else {
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.PARTNER_NOT_REGISTERED.getErrorCode(),
					IdAuthenticationErrorConstants.PARTNER_NOT_REGISTERED.getErrorMessage());
		}
	}

	public void updateApiKeyData(EventModel eventModel) throws JsonParseException, JsonMappingException, IOException {
		if (eventModel.getTopic().contentEquals(IdAuthCommonConstants.APIKEY_APPROVED)) {
			PartnerMapping mapping = new PartnerMapping();
			PartnerData partnerEventData = mapper.convertValue(eventModel.getEvent().getData().get(PARTNER_DATA), PartnerData.class);
			mapping.setPartnerId(partnerEventData.getPartnerId());
			partnerEventData.setCreatedBy(securityManager.getUser());
			partnerEventData.setCrDTimes(DateUtils.getUTCCurrentDateTime());
			ApiKeyData apiKeyEventData = mapper.convertValue(eventModel.getEvent().getData().get(API_KEY_DATA), ApiKeyData.class);
			mapping.setApiKeyId(apiKeyEventData.getApiKeyId());
			apiKeyEventData.setCreatedBy(securityManager.getUser());
			apiKeyEventData.setCrDTimes(DateUtils.getUTCCurrentDateTime());
			PolicyData policyEventData = mapper.convertValue(eventModel.getEvent().getData().get(POLICY_DATA), PolicyData.class);
			mapping.setPolicyId(policyEventData.getPolicyId());
			policyEventData.setCreatedBy(securityManager.getUser());
			policyEventData.setCrDTimes(DateUtils.getUTCCurrentDateTime());
			mapping.setCreatedBy(securityManager.getUser());
			mapping.setCrDTimes(DateUtils.getUTCCurrentDateTime());
			partnerDataRepo.save(partnerEventData);
			apiKeyRepo.save(apiKeyEventData);
			policyDataRepo.save(policyEventData);
			partnerMappingRepo.save(mapping);
		} else {
			ApiKeyData apiKeyEventData = mapper.convertValue(eventModel.getEvent().getData().get(API_KEY_DATA), ApiKeyData.class);
			Optional<ApiKeyData> apiKeyDataOptional = apiKeyRepo.findById(apiKeyEventData.getApiKeyId());
			if (apiKeyDataOptional.isPresent()) {
				ApiKeyData apiKeyData = apiKeyDataOptional.get();
				apiKeyData.setApiKeyCommenceOn(apiKeyEventData.getApiKeyCommenceOn());
				apiKeyData.setApiKeyExpiresOn(apiKeyEventData.getApiKeyExpiresOn());
				apiKeyData.setApiKeyStatus(apiKeyEventData.getApiKeyStatus());
				apiKeyData.setUpdatedBy(securityManager.getUser());
				apiKeyData.setUpdDTimes(DateUtils.getUTCCurrentDateTime());
				apiKeyRepo.save(apiKeyData);
			} else {
				apiKeyEventData.setCreatedBy(securityManager.getUser());
				apiKeyEventData.setCrDTimes(DateUtils.getUTCCurrentDateTime());
				apiKeyRepo.save(apiKeyEventData);
			}
		}
	}

	public void updatePartnerData(EventModel eventModel) {
		PartnerData partnerEventData = mapper.convertValue(eventModel.getEvent().getData().get(PARTNER_DATA), PartnerData.class);
		Optional<PartnerData> partnerDataOptional = partnerDataRepo.findById(partnerEventData.getPartnerId());
		if (partnerDataOptional.isPresent()) {
			PartnerData partnerData = partnerDataOptional.get();
			partnerData.setPartnerId(partnerEventData.getPartnerId());
			partnerData.setPartnerName(partnerEventData.getPartnerName());
			partnerData.setCertificateData(partnerEventData.getCertificateData());
			partnerData.setPartnerStatus(partnerEventData.getPartnerStatus());
			partnerData.setUpdatedBy(securityManager.getUser());
			partnerData.setUpdDTimes(DateUtils.getUTCCurrentDateTime());
			partnerDataRepo.save(partnerData);
		} else {
			partnerEventData.setCreatedBy(securityManager.getUser());
			partnerEventData.setCrDTimes(DateUtils.getUTCCurrentDateTime());
			partnerDataRepo.save(partnerEventData);
		}
	}

	public void updatePolicyData(EventModel eventModel) {
		PolicyData policyEventData = mapper.convertValue(eventModel.getEvent().getData().get(POLICY_DATA), PolicyData.class);
		Optional<PolicyData> policyDataOptional = policyDataRepo.findById(policyEventData.getPolicyId());
		if (policyDataOptional.isPresent()) {
			PolicyData policyData = policyDataOptional.get();
			policyData.setUpdatedBy(securityManager.getUser());
			policyData.setUpdDTimes(DateUtils.getUTCCurrentDateTime());
			policyData.setPolicyId(policyEventData.getPolicyId());
			policyData.setPolicy(policyEventData.getPolicy());
			policyData.setPolicyName(policyEventData.getPolicyName());
			policyData.setPolicyStatus(policyEventData.getPolicyStatus());
			policyData.setPolicyDescription(policyEventData.getPolicyDescription());
			policyData.setPolicyCommenceOn(policyEventData.getPolicyCommenceOn());
			policyData.setPolicyExpiresOn(policyEventData.getPolicyExpiresOn());
			policyDataRepo.save(policyData);
		} else {
			policyEventData.setCreatedBy(securityManager.getUser());
			policyEventData.setCrDTimes(DateUtils.getUTCCurrentDateTime());
			policyDataRepo.save(policyEventData);
		}
	}

	public void updateMispLicenseData(EventModel eventModel) {
		MispLicenseData mispLicenseEventData = mapper.convertValue(eventModel.getEvent().getData().get(MISP_LICENSE_DATA), MispLicenseData.class);
		Optional<MispLicenseData> mispLicenseDataOptional = mispLicDataRepo.findById(mispLicenseEventData.getMispId());
		if (mispLicenseDataOptional.isPresent()) {
			MispLicenseData mispLicenseData = mispLicenseDataOptional.get();
			mispLicenseData.setUpdatedBy(securityManager.getUser());
			mispLicenseData.setUpdDTimes(DateUtils.getUTCCurrentDateTime());
			mispLicenseData.setMispId(mispLicenseEventData.getMispId());
			mispLicenseData.setLicenseKey(mispLicenseEventData.getLicenseKey());
			mispLicenseData.setMispCommenceOn(mispLicenseEventData.getMispCommenceOn());
			mispLicenseData.setMispExpiresOn(mispLicenseEventData.getMispExpiresOn());
			mispLicenseData.setMispStatus(mispLicenseEventData.getMispStatus());
			mispLicDataRepo.save(mispLicenseData);
		} else {
			mispLicenseEventData.setCreatedBy(securityManager.getUser());
			mispLicenseEventData.setCrDTimes(DateUtils.getUTCCurrentDateTime());
			mispLicDataRepo.save(mispLicenseEventData);
		}
	}
}