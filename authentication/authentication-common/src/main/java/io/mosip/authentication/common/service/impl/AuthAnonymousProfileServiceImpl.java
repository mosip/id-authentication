package io.mosip.authentication.common.service.impl;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.entity.AnonymousProfileEntity;
import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.impl.idevent.AnonymousAuthenticationProfile;
import io.mosip.authentication.common.service.repository.AuthAnonymousProfileRepository;
import io.mosip.authentication.common.service.websub.impl.AuthAnonymouseEventPublisher;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.authentication.core.spi.partner.service.PartnerService;
import io.mosip.authentication.core.spi.profile.AuthAnonymousProfileService;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;

@Service
@Lazy
public class AuthAnonymousProfileServiceImpl implements AuthAnonymousProfileService {
	
	private Logger logger = IdaLogger.getLogger(AuthAnonymousProfileServiceImpl.class);

	
	private static final String IDA = "IDA";

	@Autowired
	private IdInfoFetcher idInfoFetcher;
	
	@Autowired
	private AuthAnonymouseEventPublisher authAnonymouseEventPublisher;
	
	@Autowired
	private AuthAnonymousProfileRepository authAnonymousProfileRepository;
	
	@Value("${" + IdAuthConfigKeyConstants.DATE_TIME_PATTERN + "}")
	private String dateTimePattern;
	
	@Autowired
	private PartnerService partnerService;
	
	@Autowired
	private ObjectMapper mapper;
	

	@Override
	public void storeAnonymousProfile(Map<String, Object> requestBody, Map<String, Object> responseBody, Map<String, Object> metadata) {
		AnonymousAuthenticationProfile ananymousProfile = createAnanymousProfile(requestBody, responseBody, metadata);
		storeAnanymousProfile(ananymousProfile);
		authAnonymouseEventPublisher.publishEvent(ananymousProfile);
	}

	private void storeAnanymousProfile(AnonymousAuthenticationProfile ananymousProfile) {
		AnonymousProfileEntity auuthAnanymousProfileEntity = new AnonymousProfileEntity();
		String id = UUID.randomUUID().toString();
		auuthAnanymousProfileEntity.setId(id);
		auuthAnanymousProfileEntity.setCrBy(IDA);
		LocalDateTime crDTimes = DateUtils.getUTCCurrentDateTime();
		auuthAnanymousProfileEntity.setCrDTimes(crDTimes);
		authAnonymousProfileRepository.save(auuthAnanymousProfileEntity);
		authAnonymousProfileRepository.flush();
	}

	private AnonymousAuthenticationProfile createAnanymousProfile(Map<String, Object> requestBody, Map<String, Object> responseBody, Map<String, Object> metadata) {
		AnonymousAuthenticationProfile ananymousProfile = new AnonymousAuthenticationProfile();
		
		setAuthFactors(metadata, ananymousProfile);
//		ananymousProfile.setBiometricInfo(biometricInfo);
		
		setDate(ananymousProfile);
		
		setErrorCodes(responseBody, ananymousProfile);
		
		setPartnerName(metadata, ananymousProfile);

		
//		ananymousProfile.setGender(gender);
//		ananymousProfile.setLocation(location);
		
//		ananymousProfile.setPreferredLanguages(preferredLanguages);
		
		setStatus(responseBody, ananymousProfile);
		
//		ananymousProfile.setYearOfBirth(yearOfBirth);
		return ananymousProfile;
	}

	private void setStatus(Map<String, Object> responseBody, AnonymousAuthenticationProfile ananymousProfile) {
		String status;
		if(responseBody != null && responseBody.get("response") instanceof Map) {
			status = String.valueOf(((Map<String, Object>)responseBody.get("response")).get("authStatus"));
		} else {
			status = String.valueOf(false);
		}
		ananymousProfile.setStatus(status);
	}

	private void setDate(AnonymousAuthenticationProfile ananymousProfile) {
		LocalDateTime requestDateTime = DateUtils.getUTCCurrentDateTime();
		ananymousProfile.setDate(requestDateTime.toLocalDate().toString());
	}

	private void setAuthFactors(Map<String, Object> metadata, AnonymousAuthenticationProfile ananymousProfile) {
		if(metadata != null) {
			Object authTxnObj = metadata.get(AutnTxn.class.getSimpleName());
			if(authTxnObj != null) {
				AutnTxn autnTxn = mapper.convertValue(authTxnObj, AutnTxn.class);
				List<String> authFactors = Arrays.asList(autnTxn.getAuthTypeCode().split(","));
				ananymousProfile.setAuthFactors(authFactors);
			}
		}
	}

	private void setErrorCodes(Map<String, Object> responseBody, AnonymousAuthenticationProfile ananymousProfile) {
		List<String> errorCodes = Optional.ofNullable((List<Map<String, Object>>)responseBody.get("errors"))
										.map(list -> 
												list.stream()
													.map(map -> (String)map.get("errorCode"))
													.collect(Collectors.toList()))
										.orElseGet(List::of);
		ananymousProfile.setErrorCode(errorCodes);
	}

	private void setPartnerName(Map<String, Object> metadata, AnonymousAuthenticationProfile ananymousProfile) {
		if(metadata.get(AutnTxn.class.getSimpleName()) instanceof Map) {
			Map<String, Object> txn = (Map<String, Object>) metadata.get(AutnTxn.class.getSimpleName());
			String partnerName = (String) txn.get("entityName");
			ananymousProfile.setPartnerName(partnerName);
		}

	}

}
