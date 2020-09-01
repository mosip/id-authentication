package io.mosip.authentication.common.service.impl;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import io.mosip.authentication.common.service.builder.AuthTransactionBuilder;
import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.helper.IdInfoHelper;
import io.mosip.authentication.common.service.impl.match.DemoMatchType;
import io.mosip.authentication.common.service.integration.OTPManager;
import io.mosip.authentication.common.service.integration.TokenIdManager;
import io.mosip.authentication.common.service.repository.AutnTxnRepository;
import io.mosip.authentication.common.service.repository.UinEncryptSaltRepo;
import io.mosip.authentication.common.service.repository.UinHashSaltRepo;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RequestType;
import io.mosip.authentication.core.dto.MaskUtil;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.LanguageType;
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
import io.mosip.kernel.core.exception.ParseException;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.HMACUtils;

/**
 * Service implementation of OtpTriggerService.
 * 
 * @author Rakesh Roshan
 * @author Dinesh Karuppiah.T
 */
@Service
public class OTPServiceImpl implements OTPService {

	/** The id auth service. */
	@Autowired
	private IdService<AutnTxn> idAuthService;

	/** The autntxnrepository. */
	@Autowired
	private AutnTxnRepository autntxnrepository;

	/** The env. */
	@Autowired
	private Environment env;

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
	private UinEncryptSaltRepo uinEncryptSaltRepo;

	@Autowired
	private UinHashSaltRepo uinHashSaltRepo;
	
	@Autowired
	private IdAuthSecurityManager securityManager;
	
	@Autowired
	private PartnerService partnerService;

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
	public OtpResponseDTO generateOtp(OtpRequestDTO otpRequestDto, String partnerId)
			throws IdAuthenticationBusinessException {
		boolean isInternal = partnerId != null && partnerId.equalsIgnoreCase(IdAuthCommonConstants.INTERNAL);
		boolean status;
		String token = null;
		try {
			String individualIdType = IdType.getIDTypeStrOrDefault(otpRequestDto.getIndividualIdType());
			String individualId = otpRequestDto.getIndividualId();

			Map<String, Object> idResDTO = idAuthService.processIdType(individualIdType, individualId, false);
			token = idAuthService.getToken(idResDTO);

			OtpResponseDTO otpResponseDTO = doGenerateOTP(otpRequestDto, partnerId, isInternal, token, individualIdType, idResDTO);
			
			status = otpResponseDTO.getErrors() == null || otpResponseDTO.getErrors().isEmpty();
			saveToTxnTable(otpRequestDto, isInternal, status, partnerId, token);
			
			return otpResponseDTO;

		} catch(IdAuthenticationBusinessException e) {
			status = false;
			saveToTxnTable(otpRequestDto, isInternal, status, partnerId, token);
			throw e;
		}


	}

	private void saveToTxnTable(OtpRequestDTO otpRequestDto, boolean isInternal, boolean status, String partnerId, String token)
			throws IdAuthenticationBusinessException {
		if (token != null) {
			boolean staticTokenRequired = !isInternal
					&& env.getProperty(IdAuthConfigKeyConstants.STATIC_TOKEN_ENABLE, boolean.class, false);
			String staticTokenId = staticTokenRequired ? tokenIdManager.generateTokenId(token, partnerId) : null;
			saveTxn(otpRequestDto, token, staticTokenId, status, partnerId, isInternal);
		}
	}

	private OtpResponseDTO doGenerateOTP(OtpRequestDTO otpRequestDto, String partnerId, boolean isInternal, String uin, Object individualIdType, Map<String, Object> idResDTO)
			throws IdAuthenticationBusinessException, IDDataValidationException {
		String individualId = otpRequestDto.getIndividualId();
		String hashedIndividualId = HMACUtils.digestAsPlainText(HMACUtils.generateHash(individualId.getBytes()));
		String requestTime = otpRequestDto.getRequestTime();
		OtpResponseDTO otpResponseDTO = new OtpResponseDTO();
		
		if (isOtpFlooded(hashedIndividualId, requestTime)) {
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.OTP_REQUEST_FLOODED);
		} else {
			String userIdForSendOtp = uin;
			String userIdTypeForSendOtp = IdType.UIN.getType();
			if(userIdForSendOtp.isEmpty()) {
				if (individualIdType.equals(IdType.USER_ID.getType())) {
					userIdForSendOtp = individualId;
					userIdTypeForSendOtp = IdType.USER_ID.getType();
				} else {
					//This condition will not happen mostly, due to prior request validation.
					mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getName(),
							this.getClass().getName(), "OTP Generation failed - idvid missing");
					throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED);
				}
			}
			
			String transactionId = otpRequestDto.getTransactionID();
			Map<String, List<IdentityInfoDTO>> idInfo = idAuthService.getIdInfo(idResDTO);
			String priLang = getLanguagecode(LanguageType.PRIMARY_LANG);
			String secLang = getLanguagecode(LanguageType.SECONDARY_LANG);
			String namePri = getName(priLang, idInfo);
			String nameSec = getName(secLang, idInfo);
			Map<String, String> valueMap = new HashMap<>();
			valueMap.put(IdAuthCommonConstants.PRIMARY_LANG, priLang);
			valueMap.put(IdAuthCommonConstants.SECONDAY_LANG, secLang);
			valueMap.put(IdAuthCommonConstants.NAME_PRI, namePri);
			valueMap.put(IdAuthCommonConstants.NAME_SEC, nameSec);
			boolean isOtpGenerated = otpManager.sendOtp(otpRequestDto, userIdForSendOtp, userIdTypeForSendOtp, valueMap);

			if (isOtpGenerated) {
				otpResponseDTO.setId(otpRequestDto.getId());
				otpResponseDTO.setErrors(Collections.emptyList());
				otpResponseDTO.setTransactionID(transactionId);
				String responseTime = formatDate(new Date(),
						env.getProperty(IdAuthConfigKeyConstants.DATE_TIME_PATTERN));
				otpResponseDTO.setResponseTime(responseTime);
				String email = getEmail(idInfo);
				String phoneNumber = getPhoneNumber(idInfo);
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
	 * @param staticTokenId the static token id
	 * @param status        the status
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	private void saveTxn(OtpRequestDTO otpRequestDto, String token, String staticTokenId, boolean status, String partnerId, boolean isInternal)
			throws IdAuthenticationBusinessException {
		Optional<PartnerDTO> partner = isInternal ? Optional.empty() : partnerService.getPartner(partnerId);
		AutnTxn authTxn = AuthTransactionBuilder.newInstance()
				.withOtpRequest(otpRequestDto)
				.withRequestType(RequestType.OTP_REQUEST)
				.withStaticToken(staticTokenId)
				.withStatus(status)
				.withToken(token)
				.withPartner(partner)
				.withInternal(isInternal)
				.build(env,uinEncryptSaltRepo,uinHashSaltRepo,securityManager);
		idAuthService.saveAutnTxn(authTxn);
	}

	private String getName(String language, Map<String, List<IdentityInfoDTO>> idInfo)
			throws IdAuthenticationBusinessException {
		return idInfoHelper.getEntityInfoAsString(DemoMatchType.NAME, language, idInfo);

	}

	private String getLanguagecode(LanguageType languageType) {
		return idInfoFetcher.getLanguageCode(languageType);
	}

	/**
	 * Validate the number of request for OTP generation. Limit for the number of
	 * request for OTP is should not exceed 3 in 60sec.
	 *
	 * @param otpRequestDto the otp request dto
	 * @return true, if is otp flooded
	 * @throws IdAuthenticationBusinessException
	 */
	private boolean isOtpFlooded(String individualId, String requestTime) throws IdAuthenticationBusinessException {
		boolean isOtpFlooded = false;
		LocalDateTime reqTime;
		try {
			String strUTCDate = DateUtils.getUTCTimeFromDate(
					DateUtils.parseToDate(requestTime, env.getProperty(IdAuthConfigKeyConstants.DATE_TIME_PATTERN)));
			reqTime = LocalDateTime.parse(strUTCDate,
					DateTimeFormatter.ofPattern(env.getProperty(IdAuthConfigKeyConstants.DATE_TIME_PATTERN)));

		} catch (ParseException e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getName(), e.getClass().getName(),
					e.getMessage());
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}
		int addMinutes = Integer.parseInt(env.getProperty(IdAuthConfigKeyConstants.OTP_REQUEST_FLOODING_DURATION));
		LocalDateTime addMinutesInOtpRequestDTimes = reqTime.minus(addMinutes, ChronoUnit.MINUTES);
		int maxCount = Integer.parseInt(env.getProperty(IdAuthConfigKeyConstants.OTP_REQUEST_FLOODING_MAX_COUNT));
		if (autntxnrepository.countRequestDTime(reqTime, addMinutesInOtpRequestDTimes, individualId) > maxCount) {
			isOtpFlooded = true;
		}
		return isOtpFlooded;
	}

	private void processChannel(String value, String phone, String email, MaskedResponseDTO maskedResponseDTO) {
		if (value.equalsIgnoreCase(NotificationType.SMS.getChannel())) {
			maskedResponseDTO.setMaskedMobile(MaskUtil.maskMobile(phone));
		} else if (value.equalsIgnoreCase(NotificationType.EMAIL.getChannel())) {
			maskedResponseDTO.setMaskedEmail(MaskUtil.maskEmail(email));
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
	 * Formate date.
	 *
	 * @param date   the date
	 * @param format the formate
	 * @return the date
	 */
	private String formatDate(Date date, String format) {
		return new SimpleDateFormat(format).format(date);
	}

}
