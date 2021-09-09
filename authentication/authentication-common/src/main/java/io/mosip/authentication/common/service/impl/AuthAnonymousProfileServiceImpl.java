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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.entity.AnonymousProfileEntity;
import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.helper.IdInfoHelper;
import io.mosip.authentication.common.service.impl.idevent.AnonymousAuthenticationProfile;
import io.mosip.authentication.common.service.repository.AuthAnonymousProfileRepository;
import io.mosip.authentication.common.service.websub.impl.AuthAnonymousEventPublisher;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.partner.service.PartnerService;
import io.mosip.authentication.core.spi.profile.AuthAnonymousProfileService;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;

@Service
@Lazy
public class AuthAnonymousProfileServiceImpl implements AuthAnonymousProfileService {
	
	private static final String AUTH_STATUS = "authStatus";


	private static final String SUCCESS = "success";


	private static final String FAILURE = "failure";


	private static final String RESPONSE = "response";


	private Logger logger = IdaLogger.getLogger(AuthAnonymousProfileServiceImpl.class);

	
	private static final String IDA = "IDA";

	@Autowired
	private IdInfoHelper idInfoHelper;
	
	@Autowired
	private AuthAnonymousEventPublisher authAnonymousEventPublisher;
	
	@Autowired
	private AuthAnonymousProfileRepository authAnonymousProfileRepository;
	
	@Value("${" + IdAuthConfigKeyConstants.DATE_TIME_PATTERN + "}")
	private String dateTimePattern;
	
	@Autowired
	private PartnerService partnerService;
	
	@Autowired
	private ObjectMapper mapper;
	

	@Override
	public void storeAnonymousProfile(Map<String, Object> requestBody, Map<String, Object> responseBody, Map<String, Object> requestMetadata, Map<String, Object> responseMetadata) {
		AnonymousAuthenticationProfile ananymousProfile = createAnanymousProfile(requestBody, responseBody, requestMetadata, responseMetadata);
		storeAnanymousProfile(ananymousProfile);
		authAnonymousEventPublisher.publishEvent(ananymousProfile);
	}

	private void storeAnanymousProfile(AnonymousAuthenticationProfile ananymousProfile) {
		AnonymousProfileEntity authAnonymousProfileEntity = new AnonymousProfileEntity();
		String id = UUID.randomUUID().toString();
		authAnonymousProfileEntity.setId(id);
		authAnonymousProfileEntity.setCrBy(IDA);
		LocalDateTime crDTimes = DateUtils.getUTCCurrentDateTime();
		authAnonymousProfileEntity.setCrDTimes(crDTimes);
		
		try {
			authAnonymousProfileEntity.setProfile(mapper.writeValueAsString(ananymousProfile));
		} catch (JsonProcessingException e) {
			logger.error("Error saving anonymous profile. %s", ExceptionUtils.getStackTrace(e));
		}
		
		authAnonymousProfileRepository.save(authAnonymousProfileEntity);
		authAnonymousProfileRepository.flush();
	}

	private AnonymousAuthenticationProfile createAnanymousProfile(Map<String, Object> requestBody,
			Map<String, Object> responseBody, Map<String, Object> requestMetadata, Map<String, Object> responseMetadata) {
		AnonymousAuthenticationProfile ananymousProfile = new AnonymousAuthenticationProfile();
		
		Map<String, List<IdentityInfoDTO>> idInfo = (Map<String, List<IdentityInfoDTO>>) responseMetadata.get(IdAuthCommonConstants.IDENTITY_INFO);

		if(idInfo != null) {
			// Year of Birth is Mandatory, rest are optional and will be set if fetched as
			// part of authentication / kyc request processing
			//String yearOfBirth = idInfoHelper.getIdEntityInfo(null, null)
			//ananymousProfile.setYearOfBirth(yearOfBirth);

//			ananymousProfile.setBiometricInfo(biometricInfo);
			
//			ananymousProfile.setGender(gender);
//			ananymousProfile.setLocation(location);
			
//			ananymousProfile.setPreferredLanguages(preferredLanguages);
		}
		
		setAuthFactors(responseMetadata, ananymousProfile);
		
		setDate(ananymousProfile);
		
		setErrorCodes(responseBody, ananymousProfile);
		
		setPartnerName(requestMetadata, ananymousProfile);

		setStatus(responseBody, ananymousProfile);
		
		return ananymousProfile;
	}

	private void setStatus(Map<String, Object> responseBody, AnonymousAuthenticationProfile ananymousProfile) {
		String status;
		if(responseBody != null && responseBody.get(RESPONSE) instanceof Map) {
			status = String.valueOf(((Map<String, Object>)responseBody.get(RESPONSE)).get(AUTH_STATUS));
		} else {
			status = String.valueOf(false);
		}
		ananymousProfile.setStatus(Boolean.valueOf(status) ? SUCCESS : FAILURE);
	}

	private void setDate(AnonymousAuthenticationProfile ananymousProfile) {
		LocalDateTime requestDateTime = DateUtils.getUTCCurrentDateTime();
		ananymousProfile.setDate(requestDateTime.toLocalDate().toString());
	}

	private void setAuthFactors(Map<String, Object> responseMetadata, AnonymousAuthenticationProfile ananymousProfile) {
		if(responseMetadata != null) {
			Object authTxnObj = responseMetadata.get(AutnTxn.class.getSimpleName());
			if(authTxnObj != null) {
				AutnTxn autnTxn = mapper.convertValue(authTxnObj, AutnTxn.class);
				List<String> authFactors = Arrays.stream(autnTxn.getAuthTypeCode().split(","))
											.distinct()
											.collect(Collectors.toList());
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

	private void setPartnerName(Map<String, Object> requestMetadata, AnonymousAuthenticationProfile ananymousProfile) {
		Object partnerIdObj = requestMetadata.get("partnerId");
		if(partnerIdObj instanceof String) {
			String partnerId = (String) partnerIdObj;
			Object partnerObj = requestMetadata.get(partnerId);
			if(partnerObj instanceof Map) {
				Object partnerNameObj = ((Map<String, Object>)partnerObj).get("partnerName");
				if(partnerNameObj instanceof String) {
					ananymousProfile.setPartnerName((String) partnerNameObj);
				}
			}
		}
	}

}
