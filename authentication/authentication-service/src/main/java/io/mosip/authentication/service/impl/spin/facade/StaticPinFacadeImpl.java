package io.mosip.authentication.service.impl.spin.facade;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.authentication.core.constant.AuditEvents;
import io.mosip.authentication.core.constant.AuditModules;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RequestType;
import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.IdType;
import io.mosip.authentication.core.dto.spinstore.StaticPinIdentityDTO;
import io.mosip.authentication.core.dto.spinstore.StaticPinRequestDTO;
import io.mosip.authentication.core.dto.spinstore.StaticPinResponseDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.id.service.IdAuthService;
import io.mosip.authentication.core.spi.spin.facade.StaticPinFacade;
import io.mosip.authentication.core.spi.spin.service.StaticPinService;
import io.mosip.authentication.service.entity.AutnTxn;
import io.mosip.authentication.service.helper.AuditHelper;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.UUIDUtils;

/**
 * This Class Provide facade implementation for calling the StaticPinServiceImpl
 * Class
 * 
 * @author Prem Kumar
 *
 */
@Service
public class StaticPinFacadeImpl implements StaticPinFacade {

	private static final String UTC = "UTC";

	/** The Constant UIN_Key */
	private static final String UIN_KEY = "uin";

	/** The Constant FAILED. */
	private static final String FAILED = "N";

	/** The Constant SUCCESS. */
	private static final String SUCCESS = "Y";

	/** The Constant DATETIME_PATTERN. */
	private static final String DATETIME_PATTERN = "datetime.pattern";

	/** The Environment */
	@Autowired
	private Environment env;

	/** The Static Pin Service */
	@Autowired
	private StaticPinService staticPinService;

	/** The id auth service. */
	@Autowired
	private IdAuthService idAuthService;

	/** The AuditHelper */
	@Autowired
	private AuditHelper auditHelper;

	/** The logger. */
	private static Logger logger = IdaLogger.getLogger(StaticPinFacadeImpl.class);

	/** The Constant SESSION_ID. */
	private static final String SESSION_ID = "sessionId";

	/**
	 * 
	 * This method is to call the StaticPinServiceImpl and constructs the Response
	 * based on the status got from StaticPinServiceImpl
	 * 
	 * @param staticPinRequestDTO
	 * @throws IdAuthenticationBusinessException
	 */
	@Override
	public StaticPinResponseDTO storeSpin(StaticPinRequestDTO staticPinRequestDTO)
			throws IdAuthenticationBusinessException {
		try {
			boolean status = false;
			StaticPinIdentityDTO requestdto = staticPinRequestDTO.getRequest().getIdentity();
			IdType uinidtype = IdType.UIN;
			IdType vididtype = IdType.VID;
			IdType idtype = null;
			String uin = requestdto.getUin();
			String vid = requestdto.getVid();
			String idvIdType = null;
			Map<String, Object> idResDTO = new HashMap<>();
			String idvId = null;
			if (uin != null) {
				idResDTO = idAuthService.processIdType(uinidtype.getType(), uin, false);
				idvId = uin;
				idtype = uinidtype;
				idvIdType = uinidtype.getType();
			} else if (vid != null) {
				idResDTO = idAuthService.processIdType(vididtype.getType(), vid, false);
				idvId = vid;
				idtype = vididtype;
				idvIdType = vididtype.getType();
			}
			String uinValue = null;
			if (idResDTO != null && idResDTO.containsKey(UIN_KEY)) {
				uinValue = (String) idResDTO.get(UIN_KEY);
			}
			if (uinValue != null && !uinValue.isEmpty()) {
				status = staticPinService.storeSpin(staticPinRequestDTO, uinValue);
			} else {
				status = false;
			}
			String dateTimePattern = env.getProperty(DATETIME_PATTERN);
			DateTimeFormatter isoPattern = DateTimeFormatter.ofPattern(dateTimePattern);
			String reqTime = staticPinRequestDTO.getReqTime();
			ZonedDateTime zonedDateTime2 = ZonedDateTime.parse(reqTime, isoPattern);
			ZoneId zone = zonedDateTime2.getZone();
			String resTime = DateUtils.formatDate(new Date(), dateTimePattern, TimeZone.getTimeZone(zone));
			StaticPinResponseDTO staticPinResponseDTO = new StaticPinResponseDTO();
			if (status) {
				String tspIdValue = staticPinRequestDTO.getTspID();
				String statusValue = status ? SUCCESS : FAILED;
				String comment = status ? "Static pin stored successfully" : "Faild to store Static pin";
//				idAuthService.saveAutnTxn(idvId, idvIdType, uin, reqTime, tspIdValue, statusValue, comment,
//						RequestType.STATICPIN_STORE_REQUEST);
				
				AutnTxn auth_txn=createAuthTxn(idvId, idvIdType, uin, reqTime, tspIdValue, statusValue, comment,
						RequestType.STATICPIN_STORE_REQUEST);
				idAuthService.saveAutnTxn(auth_txn);
				String desc = "Static Pin Storage requested";
				auditHelper.audit(AuditModules.STATIC_PIN_STORAGE, AuditEvents.STATIC_PIN_STORAGE_REQUEST_RESPONSE,
						idvId, idtype, desc);
				staticPinResponseDTO.setStatus(SUCCESS);
				staticPinResponseDTO.setErr(Collections.emptyList());

			} else {
				staticPinResponseDTO.setStatus(FAILED);
			}
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
	 * sets AuthTxn entity values
	 * 
	 * @param idvId
	 * @param idvIdType
	 * @param uin
	 * @param reqTime
	 * @param tspIdValue
	 * @param statusValue
	 * @param comment
	 * @param requestType
	 * @return
	 * @throws IdAuthenticationBusinessException
	 */
	private AutnTxn createAuthTxn(String idvId, String idvIdType, String uin, String reqTime, String tspIdValue,
			String statusValue, String comment, RequestType requestType) throws IdAuthenticationBusinessException {
		try {
		AutnTxn autnTxn = new AutnTxn();
		autnTxn.setRefId(idvId);
		autnTxn.setRefIdType(idvIdType);
		String id = createId(uin);
		autnTxn.setId(id); // FIXME
		// TODO check
		autnTxn.setCrBy("IDA");
		autnTxn.setCrDTimes(now());
		Date reqDate = null;
		reqDate = DateUtils.parseToDate(reqTime, env.getProperty(DATETIME_PATTERN));
		String dateTimePattern = env.getProperty(DATETIME_PATTERN);
		DateTimeFormatter isoPattern = DateTimeFormatter.ofPattern(dateTimePattern);
		LocalDateTime utcLocalDateTime = DateUtils.parseDateToLocalDateTime(reqDate);
		ZonedDateTime zonedDateTime2 = ZonedDateTime.parse(reqTime, isoPattern);
		ZoneId zone = zonedDateTime2.getZone();
		ZonedDateTime ldtZoned = utcLocalDateTime.atZone(zone);
		ZonedDateTime utcDateTime = ldtZoned.withZoneSameInstant(ZoneId.of(UTC));
		LocalDateTime localDateTime = utcDateTime.toLocalDateTime();
		autnTxn.setRequestDTtimes(localDateTime);
		autnTxn.setResponseDTimes(now()); // TODO check this
		autnTxn.setAuthTypeCode(requestType.getRequestType());
		autnTxn.setRequestTrnId(tspIdValue);
		autnTxn.setStatusCode(statusValue);
		autnTxn.setStatusComment(comment);
		// FIXME
		autnTxn.setLangCode(env.getProperty("mosip.primary.lang-code"));
		return autnTxn;
		} catch (ParseException e) {
			logger.error(SESSION_ID, this.getClass().getName(), e.getClass().getName(), e.getMessage());
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST_TIMESTAMP,
					e);
		}
	}
	/**
	 * Creates UUID
	 * 
	 * @param uin
	 * @return
	 */
	private String createId(String uin) {
		String currentDate = DateUtils.formatDate(new Date(), env.getProperty("datetime.pattern"));
		String uinAndDate = uin + "-" + currentDate;
		return UUIDUtils.getUUID(UUIDUtils.NAMESPACE_OID, uinAndDate).toString();
	}

	/**
	 * Method to get UTC Date time from kernal
	 * 
	 * @return
	 * @throws IdAuthenticationBusinessException
	 */
	private LocalDateTime now() throws IdAuthenticationBusinessException {
		return DateUtils.getUTCCurrentDateTime();
	}
}
