package io.mosip.authentication.service.impl.spin.service;

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
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.mosip.authentication.core.constant.AuditEvents;
import io.mosip.authentication.core.constant.AuditModules;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.IdType;
import io.mosip.authentication.core.dto.spinstore.StaticPinIdentityDTO;
import io.mosip.authentication.core.dto.spinstore.StaticPinRequestDTO;
import io.mosip.authentication.core.dto.spinstore.StaticPinResponseDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.id.service.IdAuthService;
import io.mosip.authentication.core.spi.spin.service.StaticPinService;
import io.mosip.authentication.service.entity.AutnTxn;
import io.mosip.authentication.service.entity.StaticPin;
import io.mosip.authentication.service.entity.StaticPinHistory;
import io.mosip.authentication.service.helper.AuditHelper;
import io.mosip.authentication.service.repository.StaticPinHistoryRepository;
import io.mosip.authentication.service.repository.StaticPinRepository;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.HMACUtils;

/**
 * This Class will provide service for storing the Static Pin.
 * 
 * @author Prem Kumar
 *
 */
@Service
public class StaticPinServiceImpl implements StaticPinService {

	/** The Constant for IDA */
	private static final String IDA = "IDA";

	/** The Constant UIN_Key */
	private static final String UIN_KEY = "uin";

	/** The Constant SUCCESS. */
	private static final String SUCCESS = "Y";

	/** The Constant DATETIME_PATTERN. */
	private static final String DATETIME_PATTERN = "datetime.pattern";

	/** The Constant SESSION_ID. */
	private static final String SESSION_ID = "sessionId";

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
	private IdAuthService<AutnTxn> idAuthService;

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
	 * @param staticPinRequestDTO
	 *            the static pin request DTO
	 * @return the static pin response DTO
	 * @throws IdAuthenticationBusinessException
	 *             the id authentication business exception
	 */
	@Override
	public StaticPinResponseDTO storeSpin(StaticPinRequestDTO staticPinRequestDTO)
			throws IdAuthenticationBusinessException {
		try {
			StaticPinIdentityDTO requestdto = staticPinRequestDTO.getRequest().getIdentity();
			IdType idtype = IdType.UIN;
			String uin = requestdto.getUin();
			String vid = requestdto.getVid();
			String idvId = "";
			if (uin != null) {
				idvId = uin;
				idtype =  IdType.UIN;
			} else if (vid != null) {
				idvId = vid;
				idtype =  IdType.VID;
			}
			Map<String, Object> idResDTO = idAuthService.processIdType(idtype.getType(), idvId, false);
			
			String uinValue = null;
			if (idResDTO != null && idResDTO.containsKey(UIN_KEY)) {
				uinValue = (String) idResDTO.get(UIN_KEY);
			}
			if (uinValue != null && !uinValue.isEmpty()) {
				storeSpin(staticPinRequestDTO, uinValue);
			}
			String dateTimePattern = env.getProperty(DATETIME_PATTERN);
			DateTimeFormatter isoPattern = DateTimeFormatter.ofPattern(dateTimePattern);
			String reqTime = staticPinRequestDTO.getReqTime();
			ZonedDateTime zonedDateTime2 = ZonedDateTime.parse(reqTime, isoPattern);
			ZoneId zone = zonedDateTime2.getZone();
			String resTime = DateUtils.formatDate(new Date(), dateTimePattern, TimeZone.getTimeZone(zone));
			StaticPinResponseDTO staticPinResponseDTO = new StaticPinResponseDTO();
			auditHelper.audit(AuditModules.STATIC_PIN_STORAGE, AuditEvents.STATIC_PIN_STORAGE_REQUEST_RESPONSE, idvId,
					idtype, AuditModules.STATIC_PIN_STORAGE.getDesc());
			staticPinResponseDTO.setStatus(SUCCESS);
			staticPinResponseDTO.setErr(Collections.emptyList());
			staticPinResponseDTO.setId(staticPinRequestDTO.getId());
			staticPinResponseDTO.setVer(staticPinRequestDTO.getVer());
			staticPinResponseDTO.setResTime(resTime);
			return staticPinResponseDTO;
		} catch (DataAccessException e) {
			logger.error(SESSION_ID, this.getClass().getName(), e.getClass().getName(), e.getMessage());
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.STATICPIN_NOT_STORED_PINVAUE, e);
		}
	}

	/**
	 * This method is to store the StaticPin in StaticPin and StaticPinHistory
	 * Table.
	 *
	 * @param staticPinRequestDTO
	 *            the static pin request DTO
	 * @param uinValue
	 *            the uin value
	 * @return true, if successful
	 * @throws IdAuthenticationBusinessException
	 *             the id authentication business exception
	 */
	@Transactional
	private boolean storeSpin(StaticPinRequestDTO staticPinRequestDTO, String uinValue)
			throws IdAuthenticationBusinessException {
		boolean status = false;

		String pinValue = staticPinRequestDTO.getRequest().getStaticPin();
		String hashedPin = hashStaticPin(pinValue.getBytes());
		Optional<StaticPin> entityValues = staticPinRepo.findById(uinValue);
		if (!entityValues.isPresent()) {
			StaticPin staticPin = new StaticPin();
			staticPin.setUin(uinValue);
			staticPin.setPin(hashedPin);
			staticPin.setCreatedBy(IDA);
			staticPin.setCreatedOn(now());
			staticPin.setUpdatedBy(IDA);
			staticPin.setUpdatedOn(now());
			staticPin.setActive(true);
			staticPin.setDeleted(false);
			staticPinRepo.save(staticPin);
		} else {
			StaticPin staticPinEntity = entityValues.get();
			staticPinEntity.setPin(hashedPin);
			staticPinEntity.setUpdatedOn(now());
			staticPinEntity.setUpdatedBy(IDA);
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
	 * @param pinValue
	 *            the Static Pin
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
		StaticPinHistory staticPinHistory = new StaticPinHistory();
		staticPinHistory.setUin(uinValue);
		staticPinHistory.setPin(hashedPin);
		staticPinHistory.setCreatedBy(IDA);
		staticPinHistory.setCreatedOn(now());
		staticPinHistory.setEffectiveDate(now());
		staticPinHistory.setActive(true);
		staticPinHistory.setDeleted(false);
		staticPinHistory.setUpdatedBy(IDA);
		staticPinHistory.setUpdatedOn(now());
		return staticPinHistory;
	}

}
