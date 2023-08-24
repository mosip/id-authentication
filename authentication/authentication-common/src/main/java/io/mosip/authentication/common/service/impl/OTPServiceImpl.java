package io.mosip.authentication.common.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.mosip.authentication.authfilter.exception.IdAuthenticationFilterException;
import io.mosip.authentication.common.service.entity.AuthtypeLock;
import io.mosip.authentication.common.service.repository.AuthLockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import io.mosip.authentication.common.manager.IdAuthFraudAnalysisEventManager;
import io.mosip.authentication.common.service.builder.AuthTransactionBuilder;
import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.helper.IdInfoHelper;
import io.mosip.authentication.common.service.impl.match.DemoMatchType;
import io.mosip.authentication.common.service.integration.OTPManager;
import io.mosip.authentication.common.service.integration.TokenIdManager;
import io.mosip.authentication.common.service.repository.AutnTxnRepository;
import io.mosip.authentication.common.service.repository.IdaUinHashSaltRepo;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.common.service.util.IdaRequestResponsConsumerUtil;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RequestType;
import io.mosip.authentication.core.dto.ObjectWithMetadata;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.NotificationType;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.otp.dto.MaskedResponseDTO;
import io.mosip.authentication.core.otp.dto.OtpRequestDTO;
import io.mosip.authentication.core.otp.dto.OtpResponseDTO;
import io.mosip.authentication.core.partner.dto.PartnerDTO;
import io.mosip.authentication.core.spi.id.service.IdService;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.authentication.core.spi.otp.service.OTPService;
import io.mosip.authentication.core.spi.partner.service.PartnerService;
import io.mosip.authentication.core.util.LanguageComparator;
import io.mosip.authentication.core.util.MaskUtil;
import io.mosip.kernel.core.exception.ParseException;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.StringUtils;

/**
 * Service implementation of OtpTriggerService.
 * 
 * @author Rakesh Roshan
 * @author Dinesh Karuppiah.T
 */
@Service
public class OTPServiceImpl implements OTPService {
	
	/** The Constant NAME. */
	private static final String NAME = "name";
	private static final String OTP = "otp";
	private static final String PHONE = "PHONE";
	private static final String EMAIL = "EMAIL";
	private static final String OTP_SMS = "otp-sms";
	private static final String OTP_EMAIL = "otp-email";


	/** The id auth service. */
	@Autowired
	private IdService<AutnTxn> idAuthService;

	/** The autntxnrepository. */
	@Autowired
	private AutnTxnRepository autntxnrepository;

	/** The auth lock repository. */
	@Autowired
	AuthLockRepository authLockRepository;

	/** The env. */
	@Autowired
	private EnvUtil env;

	@Autowired
	private IdInfoHelper idInfoHelper;

	@Autowired
	private IdInfoFetcher idInfoFetcher;

	/** The otp manager. */
	@Autowired
	private OTPManager otpManager;

	/** The TokenId manager */
	@Autowired
	private TokenIdManager tokenIdManager;
	
	@Autowired
	private IdaUinHashSaltRepo uinHashSaltRepo;
	
	@Autowired
	private IdAuthSecurityManager securityManager;
	
	@Autowired
	private PartnerService partnerService;
	
	@Autowired
	private IdAuthFraudAnalysisEventManager fraudEventManager;
	
	@Autowired
	@Qualifier("NotificationLangComparator")
	private LanguageComparator languageComparator;
	
	/** The mosip logger. */
	private static Logger mosipLogger = IdaLogger.getLogger(OTPServiceImpl.class);

	/**
	 * Generate OTP, store the OTP request details for success/failure. And send OTP
	 * notification by sms(on mobile)/mail(on email-id).
	 *
	 * @param otpRequestDto the otp request dto
	 * @return otpResponseDTO
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	@Override
	public OtpResponseDTO generateOtp(OtpRequestDTO otpRequestDto, String partnerId, ObjectWithMetadata requestWithMetadata)
			throws IdAuthenticationBusinessException {
		boolean isInternal = partnerId != null && partnerId.equalsIgnoreCase(IdAuthCommonConstants.INTERNAL);
		boolean status;
		String token = null;
		try {
			String individualIdType = IdType.getIDTypeStrOrDefault(otpRequestDto.getIndividualIdType());
			String individualId = otpRequestDto.getIndividualId();

			Map<String, Object> idResDTO = idAuthService.processIdType(individualIdType, individualId, false, false,
					idInfoHelper.getDefaultFilterAttributes());
			
			token = idAuthService.getToken(idResDTO);

			validateAllowedOtpChannles(token, otpRequestDto.getOtpChannel());

			OtpResponseDTO otpResponseDTO = doGenerateOTP(otpRequestDto, partnerId, isInternal, token, individualIdType, idResDTO);
			IdaRequestResponsConsumerUtil.setIdVersionToResponse(requestWithMetadata, otpResponseDTO);

			status = otpResponseDTO.getErrors() == null || otpResponseDTO.getErrors().isEmpty();
			saveToTxnTable(otpRequestDto, isInternal, status, partnerId, token, otpResponseDTO, requestWithMetadata);
			
			return otpResponseDTO;

		} catch(IdAuthenticationBusinessException e) {
			status = false;
			//FIXME check if for this condition auth transaction is stored, then remove below code
			//saveToTxnTable(otpRequestDto, isInternal, status, partnerId, token, null, null);
			throw e;
		}


	}

	private void validateAllowedOtpChannles(String token, List<String> otpChannel) throws IdAuthenticationFilterException {

		if(containsChannel(otpChannel, OTP)) {
			checkAuthLock(token, OTP);
		}
		else if(containsChannel(otpChannel, PHONE)) {
			checkAuthLock(token, OTP_SMS);
		}
		else if(containsChannel(otpChannel, EMAIL)) {
			checkAuthLock(token, OTP_EMAIL);
		}
	}

	private static boolean containsChannel(List<String> otpChannel, String channel) {
		return otpChannel.stream().anyMatch(channelItem -> channel.equalsIgnoreCase(channelItem));
	}

	private void checkAuthLock(String token, String authTypeCode) throws IdAuthenticationFilterException {
		List<AuthtypeLock> authTypeLocks = authLockRepository.findByTokenAndAuthtypecode(token, authTypeCode);
		for(AuthtypeLock authtypeLock : authTypeLocks) {
			if(authtypeLock.getStatuscode().equalsIgnoreCase("true")){
				throw new IdAuthenticationFilterException(
						IdAuthenticationErrorConstants.AUTH_TYPE_LOCKED.getErrorCode(),
						String.format(IdAuthenticationErrorConstants.AUTH_TYPE_LOCKED.getErrorMessage(),
								authTypeCode));
			}
		}
	}

	private void saveToTxnTable(OtpRequestDTO otpRequestDto, boolean isInternal, boolean status, String partnerId, String token, OtpResponseDTO otpResponseDTO, ObjectWithMetadata requestWithMetadata)
			throws IdAuthenticationBusinessException {
		if (token != null) {
			boolean authTokenRequired = !isInternal
					&& EnvUtil.getAuthTokenRequired();
			String authTokenId = authTokenRequired ? tokenIdManager.generateTokenId(token, partnerId) : null;
			saveTxn(otpRequestDto, token, authTokenId, status, partnerId, isInternal, otpResponseDTO, requestWithMetadata);
		}
	}

	private OtpResponseDTO doGenerateOTP(OtpRequestDTO otpRequestDto, String partnerId, boolean isInternal, String token, String individualIdType, Map<String, Object> idResDTO)
			throws IdAuthenticationBusinessException, IDDataValidationException {
		String individualId = otpRequestDto.getIndividualId();
		String requestTime = otpRequestDto.getRequestTime();
		OtpResponseDTO otpResponseDTO = new OtpResponseDTO();
		
		if (isOtpFlooded(token, requestTime)) {
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.OTP_REQUEST_FLOODED);
		} else {
			String transactionId = otpRequestDto.getTransactionID();
			otpResponseDTO.setId(otpRequestDto.getId());
			otpResponseDTO.setTransactionID(transactionId);
			
			Map<String, List<IdentityInfoDTO>> idInfo = IdInfoFetcher.getIdInfo(idResDTO);			
			Map<String, String> valueMap = new HashMap<>();
			
			List<String> templateLanguages = getTemplateLanguages(idInfo);			
			for (String lang : templateLanguages) {
				valueMap.put(NAME + "_" + lang, getName(lang, idInfo));
			}

			String email = getEmail(idInfo);
			String phoneNumber = getPhoneNumber(idInfo);			
			valueMap.put(IdAuthCommonConstants.PHONE_NUMBER, phoneNumber);
			valueMap.put(IdAuthCommonConstants.EMAIL, email);
			
			List<String> otpChannel = otpRequestDto.getOtpChannel();
			if (StringUtils.isBlank(phoneNumber) && containsChannel(otpChannel, PHONE) && !containsChannel(otpChannel, EMAIL)) {
				throw new IdAuthenticationBusinessException(
						IdAuthenticationErrorConstants.OTP_GENERATION_FAILED.getErrorCode(),
						IdAuthenticationErrorConstants.OTP_GENERATION_FAILED.getErrorMessage()
								+ ". Phone Number is not found in identity data.");
			}
			
			if (StringUtils.isBlank(email) && containsChannel(otpChannel, EMAIL) && !containsChannel(otpChannel, PHONE)) {
				throw new IdAuthenticationBusinessException(
						IdAuthenticationErrorConstants.OTP_GENERATION_FAILED.getErrorCode(),
						IdAuthenticationErrorConstants.OTP_GENERATION_FAILED.getErrorMessage()
								+ ". Email ID is not found in identity data.");
			}
			
			if(StringUtils.isBlank(phoneNumber) && StringUtils.isBlank(email) && (containsChannel(otpChannel, PHONE) && containsChannel(otpChannel, EMAIL))) {
				throw new IdAuthenticationBusinessException(
						IdAuthenticationErrorConstants.OTP_GENERATION_FAILED.getErrorCode(),
						IdAuthenticationErrorConstants.OTP_GENERATION_FAILED.getErrorMessage()
								+ ". Both Phone Number and Email ID are not found in identity data.");
			}
			
			boolean isOtpGenerated = otpManager.sendOtp(otpRequestDto, individualId, individualIdType, valueMap,
					templateLanguages);

			if (isOtpGenerated) {
				otpResponseDTO.setErrors(null);
				String responseTime = IdaRequestResponsConsumerUtil.getResponseTime(otpRequestDto.getRequestTime(),
						EnvUtil.getDateTimePattern());
				otpResponseDTO.setResponseTime(responseTime);
				MaskedResponseDTO maskedResponseDTO = new MaskedResponseDTO();
				List<String> otpChannels = otpRequestDto.getOtpChannel();
				for (String channel : otpChannels) {
					processChannel(channel, phoneNumber, email, maskedResponseDTO);
				}
				otpResponseDTO.setResponse(maskedResponseDTO);
				
				mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getName(), this.getClass().getName(),
						" is OTP generated: " + isOtpGenerated);
			} else {
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getName(),
						this.getClass().getName(), "OTP Generation failed");
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED);
			}
			
		}
		return otpResponseDTO;
	}
	
	/**
	 * Audit txn.
	 *
	 * @param otpRequestDto the otp request dto
	 * @param token           the uin
	 * @param authTokenId the auth token id
	 * @param status        the status
	 * @param otpResponseDTO 
	 * @param requestWithMetadata 
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	private void saveTxn(OtpRequestDTO otpRequestDto, String token, String authTokenId, boolean status, String partnerId, boolean isInternal, OtpResponseDTO otpResponseDTO, ObjectWithMetadata requestWithMetadata)
			throws IdAuthenticationBusinessException {
		Optional<PartnerDTO> partner = isInternal ? Optional.empty() : partnerService.getPartner(partnerId, otpRequestDto.getMetadata());
		AutnTxn authTxn = AuthTransactionBuilder.newInstance()
				.withRequest(otpRequestDto)
				.addRequestType(RequestType.OTP_REQUEST)
				.withAuthToken(authTokenId)
				.withStatus(status)
				.withToken(token)
				.withPartner(partner)
				.withInternal(isInternal)
				.build(env,uinHashSaltRepo,securityManager);
		fraudEventManager.analyseEvent(authTxn);
		if(requestWithMetadata != null) {
			requestWithMetadata.setMetadata(Map.of(AutnTxn.class.getSimpleName(), authTxn));	
		} else {
			idAuthService.saveAutnTxn(authTxn);
		}
	}

	private String getName(String language, Map<String, List<IdentityInfoDTO>> idInfo)
			throws IdAuthenticationBusinessException {
		return idInfoHelper.getEntityInfoAsString(DemoMatchType.NAME, language, idInfo);

	}	

	/**
	 * Validate the number of request for OTP generation. Limit for the number of
	 * request for OTP is should not exceed 3 in 60sec.
	 *
	 * @return true, if is otp flooded
	 * @throws IdAuthenticationBusinessException
	 */
	private boolean isOtpFlooded(String token, String requestTime) throws IdAuthenticationBusinessException {
		boolean isOtpFlooded = false;
		LocalDateTime reqTime;
		try {
			String strUTCDate = DateUtils.getUTCTimeFromDate(
					DateUtils.parseToDate(requestTime, EnvUtil.getDateTimePattern()));
			reqTime = LocalDateTime.parse(strUTCDate,
					DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern()));

		} catch (ParseException e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getName(), e.getClass().getName(),
					e.getMessage());
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}
		int addMinutes = EnvUtil.getOtpRequestFloodingDuration();
		LocalDateTime addMinutesInOtpRequestDTimes = reqTime.minus(addMinutes, ChronoUnit.MINUTES);
		int maxCount = EnvUtil.getOtpRequestFloodingMaxCount();
		if (autntxnrepository.countRequestDTime(reqTime, addMinutesInOtpRequestDTimes, token) > maxCount) {
			isOtpFlooded = true;
		}
		return isOtpFlooded;
	}

	private void processChannel(String value, String phone, String email, MaskedResponseDTO maskedResponseDTO) throws IdAuthenticationBusinessException {
		if (value.equalsIgnoreCase(NotificationType.SMS.getChannel())) {
			if(phone != null && !phone.isEmpty()) {
				maskedResponseDTO.setMaskedMobile(MaskUtil.maskMobile(phone));
			} else {
				mosipLogger.warn("Phone Number is not available in identity data. But PHONE channel is requested for OTP.");
			}
		} else if (value.equalsIgnoreCase(NotificationType.EMAIL.getChannel())) {
			if(email != null && !email.isEmpty()) {
				maskedResponseDTO.setMaskedEmail(MaskUtil.maskEmail(email));
			} else {
				mosipLogger.warn("Email ID is not available in identity data. But email channel is requested for OTP.");
			}
		}

	}

	/**
	 * Get Mail.
	 * 
	 * @param idInfo List of IdentityInfoDTO
	 * @return mail
	 * @throws IdAuthenticationBusinessException
	 */
	private String getEmail(Map<String, List<IdentityInfoDTO>> idInfo) throws IdAuthenticationBusinessException {
		return idInfoHelper.getEntityInfoAsString(DemoMatchType.EMAIL, idInfo);
	}

	/**
	 * Get Mobile number.
	 * 
	 * @param idInfo List of IdentityInfoDTO
	 * @return Mobile number
	 * @throws IdAuthenticationBusinessException
	 */
	private String getPhoneNumber(Map<String, List<IdentityInfoDTO>> idInfo) throws IdAuthenticationBusinessException {
		return idInfoHelper.getEntityInfoAsString(DemoMatchType.PHONE, idInfo);
	}

	/**
	 * This method gets the template languages in following order.
	 * 1. Gets user preferred languages if not
	 * 2. Gets default template languages from configuration if not
	 * 3. Gets the data capture languages
	 * @param idInfo
	 * @return
	 * @throws IdAuthenticationBusinessException
	 */
	private List<String> getTemplateLanguages(Map<String, List<IdentityInfoDTO>> idInfo)
			throws IdAuthenticationBusinessException {		
		List<String> userPreferredLangs = idInfoFetcher.getUserPreferredLanguages(idInfo);
		List<String> defaultTemplateLanguges = userPreferredLangs.isEmpty()
				? idInfoFetcher.getTemplatesDefaultLanguageCodes()
				: userPreferredLangs;
		if (defaultTemplateLanguges.isEmpty()) {
			List<String> dataCaptureLanguages = idInfoHelper.getDataCapturedLanguages(DemoMatchType.NAME, idInfo);
			Collections.sort(dataCaptureLanguages, languageComparator);
			return dataCaptureLanguages;
		}

		return defaultTemplateLanguges;

	}
}