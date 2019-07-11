package io.mosip.authentication.staticpin.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.entity.StaticPin;
import io.mosip.authentication.common.service.entity.StaticPinHistory;
import io.mosip.authentication.common.service.helper.AuditHelper;
import io.mosip.authentication.common.service.repository.StaticPinHistoryRepository;
import io.mosip.authentication.common.service.repository.StaticPinRepository;
import io.mosip.authentication.core.constant.AuditEvents;
import io.mosip.authentication.core.constant.AuditModules;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.id.service.IdService;
import io.mosip.authentication.core.spi.staticpin.service.StaticPinService;
import io.mosip.authentication.core.staticpin.dto.StaticPinRequestDTO;
import io.mosip.authentication.core.staticpin.dto.StaticPinResponseDTO;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.HMACUtils;

/**
 * This Class will provide service for storing the Static Pin
 * {@link StaticPinService}}.
 * 
 * @author Prem Kumar
 *
 */
@Service
public class StaticPinServiceImpl implements StaticPinService {

	

	/** The Constant UIN_Key */
	private static final String UIN_KEY = "uin";

	/** The logger. */
	private static Logger logger = IdaLogger.getLogger(StaticPinServiceImpl.class);

	/** The StaticPinRepository */
	@Autowired
	private StaticPinRepository staticPinRepo;

	/** The StaticPinHistoryRepository */
	@Autowired
	private StaticPinHistoryRepository staticPinHistoryRepo;

	/** The id auth service. */
	@Autowired
	private IdService<AutnTxn> idAuthService;

	/** The AuditHelper */
	@Autowired
	private AuditHelper auditHelper;

	/** The env. */
	@Autowired
	Environment env;

	/**
	 * This method is to call the StaticPinServiceImpl and constructs the Response
	 * based on the status got from StaticPinServiceImpl.
	 *
	 * @param staticPinRequestDTO the static pin request DTO
	 * @return the static pin response DTO
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	@Override
	public StaticPinResponseDTO storeSpin(StaticPinRequestDTO staticPinRequestDTO)
			throws IdAuthenticationBusinessException {
		String idvId = staticPinRequestDTO.getIndividualId();
		String idTypedto = staticPinRequestDTO.getIndividualIdType();
		String idTypeStr = null;
		IdType idType = IdType.UIN;
		if (idTypedto.equals(IdType.UIN.getType())) {
			idType = IdType.UIN;
			idTypeStr = idType.getType();
		} else if (idTypedto.equals(IdType.VID.getType())) {
			idType = IdType.VID;
			idTypeStr = idType.getType();
		}
		Map<String, Object> idResDTO = idAuthService.processIdType(idTypeStr, idvId, false);
		Optional<String> uinValue = getUINValue(idResDTO);
		if (uinValue.isPresent()) {
			storeSpin(staticPinRequestDTO, uinValue.get());
		}
		String dateTimePattern = env.getProperty(IdAuthConfigKeyConstants.DATE_TIME_PATTERN);
		DateTimeFormatter isoPattern = DateTimeFormatter.ofPattern(dateTimePattern);
		String reqTime = staticPinRequestDTO.getRequestTime();
		ZonedDateTime zonedDateTime2 = ZonedDateTime.parse(reqTime, isoPattern);
		ZoneId zone = zonedDateTime2.getZone();
		String resTime = DateUtils.formatDate(new Date(), dateTimePattern, TimeZone.getTimeZone(zone));
		StaticPinResponseDTO staticPinResponseDTO = new StaticPinResponseDTO();
		logger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "STATICPIN STORE--", "AUDIT REQUESTED");
		auditHelper.audit(AuditModules.STATIC_PIN_STORAGE, AuditEvents.STATIC_PIN_STORAGE_REQUEST_RESPONSE, idvId,
				idType, AuditModules.STATIC_PIN_STORAGE.getDesc());
		staticPinResponseDTO.setStatus(true);
		staticPinResponseDTO.setErrors(Collections.emptyList());
		staticPinResponseDTO.setId(staticPinRequestDTO.getId());
		staticPinResponseDTO.setVersion(staticPinRequestDTO.getVersion());
		staticPinResponseDTO.setResponseTime(resTime);
		return staticPinResponseDTO;

	}

	private Optional<String> getUINValue(Map<String, Object> idResDTO) {

		if (idResDTO != null && !idResDTO.isEmpty() && idResDTO.containsKey(UIN_KEY)) {
			return Optional.ofNullable((String) idResDTO.get(UIN_KEY));
		}
		return Optional.empty();
	}

	/**
	 * This method is to store the StaticPin in StaticPin and StaticPinHistory
	 * Table.
	 *
	 * @param staticPinRequestDTO the static pin request DTO
	 * @param uinValue            the uin value
	 * @return true, if successful
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	@Transactional
	private boolean storeSpin(StaticPinRequestDTO staticPinRequestDTO, String uinValue)
			throws IdAuthenticationBusinessException {
		boolean status = false;

		String pinValue = staticPinRequestDTO.getRequest().getStaticPin();
		String hashedPin = hashStaticPin(pinValue.getBytes());
		Optional<StaticPin> entityValues = staticPinRepo.findById(uinValue);
		if (!entityValues.isPresent()) {
			StaticPin staticPin = new StaticPin(hashedPin, uinValue, true, env.getProperty(IdAuthConfigKeyConstants.APPLICATION_ID), now(), env.getProperty(IdAuthConfigKeyConstants.APPLICATION_ID), now(), false, now());
			staticPinRepo.save(staticPin);
		} else {
			StaticPin staticPinEntity = entityValues.get();
			staticPinEntity.setPin(hashedPin);
			staticPinEntity.setUpdatedOn(now());
			staticPinEntity.setUpdatedBy(env.getProperty(IdAuthConfigKeyConstants.APPLICATION_ID));
			staticPinRepo.update(staticPinEntity);
		}
		status = true;
		StaticPinHistory staticPinHistory = getPinHistory(uinValue, hashedPin);
		staticPinHistoryRepo.save(staticPinHistory);
		return status;
	}

	/**
	 * Method to get UTC Date time from kernal
	 * 
	 * @return
	 * @throws IdAuthenticationBusinessException
	 */
	private LocalDateTime now() {
		return DateUtils.getUTCCurrentDateTime();
	}

	/**
	 * Hash the Static Pin.
	 *
	 * @param pinValue the Static Pin
	 * @return the string
	 */
	private String hashStaticPin(byte[] pinValue) {
		return HMACUtils.digestAsPlainText(HMACUtils.generateHash(pinValue));
	}

	/**
	 * To generate Static Pin History
	 * 
	 * @param uinValue
	 * @param hashedPin
	 * @return
	 * @throws IdAuthenticationBusinessException
	 */
	private StaticPinHistory getPinHistory(String uinValue, String hashedPin) throws IdAuthenticationBusinessException {
		StaticPinHistory staticPinHistory = new StaticPinHistory(hashedPin,uinValue,true,env.getProperty(IdAuthConfigKeyConstants.APPLICATION_ID),now(),env.getProperty(IdAuthConfigKeyConstants.APPLICATION_ID),now(),false,now(),now());
		return staticPinHistory;
	}

}
