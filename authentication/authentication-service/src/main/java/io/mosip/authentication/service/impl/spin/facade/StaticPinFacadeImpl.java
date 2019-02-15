package io.mosip.authentication.service.impl.spin.facade;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import io.mosip.authentication.core.constant.AuditEvents;
import io.mosip.authentication.core.constant.AuditModules;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.IdType;
import io.mosip.authentication.core.dto.spinstore.StaticPinIdentityDTO;
import io.mosip.authentication.core.dto.spinstore.StaticPinRequestDTO;
import io.mosip.authentication.core.dto.spinstore.StaticPinResponseDTO;
import io.mosip.authentication.core.dto.vid.VIDResponseDTO;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.id.service.IdAuthService;
import io.mosip.authentication.core.spi.spin.facade.StaticPinFacade;
import io.mosip.authentication.core.spi.spin.service.StaticPinService;
import io.mosip.authentication.service.entity.AutnTxn;
import io.mosip.authentication.service.entity.VIDEntity;
import io.mosip.authentication.service.helper.AuditHelper;
import io.mosip.authentication.service.repository.VIDRepository;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.idgenerator.vid.impl.VidGeneratorImpl;

/**
 * This Class Provide facade implementation for calling the StaticPinServiceImpl
 * Class
 * 
 * @author Prem Kumar
 *
 */
@Service
public class StaticPinFacadeImpl implements StaticPinFacade {

	/** The Constant UIN_Key */
	private static final String UIN_KEY = "uin";

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
	private IdAuthService<AutnTxn> idAuthService;

	/** The AuditHelper */
	@Autowired
	private AuditHelper auditHelper;
	
    @Autowired
	private VIDRepository vidRepository;

	@Autowired
	private VidGeneratorImpl vidGenerator;

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
			StaticPinIdentityDTO requestdto = staticPinRequestDTO.getRequest().getIdentity();
			IdType uinidtype = IdType.UIN;
			IdType vididtype = IdType.VID;
			IdType idtype = null;
			String uin = requestdto.getUin();
			String vid = requestdto.getVid();
			Map<String, Object> idResDTO = new HashMap<>();
			String idvId = null;
			if (uin != null) {
				idResDTO = idAuthService.processIdType(uinidtype.getType(), uin, false);
				idvId = uin;
				idtype = uinidtype;
			} else if (vid != null) {
				idResDTO = idAuthService.processIdType(vididtype.getType(), vid, false);
				idvId = vid;
				idtype = vididtype;
			}
			String uinValue = null;
			if (idResDTO != null && idResDTO.containsKey(UIN_KEY)) {
				uinValue = (String) idResDTO.get(UIN_KEY);
			}
			if (uinValue != null && !uinValue.isEmpty()) {
				staticPinService.storeSpin(staticPinRequestDTO, uinValue);
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
	
	// FIXME this method has to be in refactored facade as seperate facade should
	// not be created.
	// @Override
	public VIDResponseDTO generateVID(String uin) throws IdAuthenticationBusinessException {
		Map<String, Object> uinMap = idAuthService.processIdType(IdType.UIN.getType(), uin, false);
		VIDEntity vidEntityObj = null;
		VIDResponseDTO vidResponseDTO = new VIDResponseDTO();
		vidResponseDTO.setId("mosip.identity.vid");
		vidResponseDTO.setVersion("1.0");
		if (!uinMap.isEmpty())  {            //FIXME
			List<VIDEntity> vidEntityList = vidRepository.findByUIN(uin,PageRequest.of(0,1));
			if (vidEntityList.isEmpty()) {
				vidEntityObj = new VIDEntity();
				vidEntityObj.setId((String) vidGenerator.generateId());
				vidEntityObj.setUin(uin);
				vidEntityObj.setActive(true);
				vidEntityObj.setCreatedBy("IDA");
				vidEntityObj.setCreatedDTimes(DateUtils.getUTCCurrentDateTime());
				vidEntityObj.setExpiryDate(
						DateUtils.getUTCCurrentDateTime().plusHours(env.getProperty("mosip.vid.validity.hours", Long.class)));
				vidEntityObj.setGeneratedOn(DateUtils.getUTCCurrentDateTime());
				vidEntityObj.setDeleted(false);
				try {
					vidRepository.save(vidEntityObj);
				} catch (DataAccessException ex) {
					logger.error(SESSION_ID, this.getClass().getName(), ex.getClass().getName(), ex.getMessage());
					throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.VID_GENERATION_FAILED,
							ex);
				}
				vidResponseDTO.setVid(vidEntityObj.getId());
			}

			else {
				vidEntityObj = vidEntityList.get(0);
				if (vidEntityObj.isActive() && vidEntityObj.getExpiryDate().isAfter(DateUtils.getUTCCurrentDateTime())) {
					throw new IDDataValidationException(IdAuthenticationErrorConstants.VID_REGENERATION_FAILED, vidEntityObj.getId());
				}

				else if (!vidEntityObj.isActive() || vidEntityObj.getExpiryDate().isBefore(DateUtils.getUTCCurrentDateTime())) {
					vidEntityObj = new VIDEntity();
					vidEntityObj.setId((String) vidGenerator.generateId());
					vidEntityObj.setUin(uin);
					vidEntityObj.setActive(true);
					vidEntityObj.setCreatedBy("IDA");
					vidEntityObj.setCreatedDTimes(DateUtils.getUTCCurrentDateTime());
					vidEntityObj.setExpiryDate(
					DateUtils.getUTCCurrentDateTime().plusHours(env.getProperty("mosip.vid.validity.hours", Long.class)));
					vidEntityObj.setGeneratedOn(DateUtils.getUTCCurrentDateTime());
					vidEntityObj.setDeleted(false);
					try {
						vidRepository.save(vidEntityObj);
					} catch (DataAccessException ex) {
						logger.error(SESSION_ID, this.getClass().getName(), ex.getClass().getName(), ex.getMessage());
						throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.VID_GENERATION_FAILED,
								ex);
					}
					vidResponseDTO.setVid(vidEntityObj.getId());
				}
			}
		}
		vidResponseDTO.setResponseTime(DateUtils.getUTCCurrentDateTimeString(env.getProperty("datetime.pattern")));
		vidResponseDTO.setError(Collections.EMPTY_LIST);
		String desc = "VID generation request";
		auditHelper.audit(AuditModules.VID_GENERATION_REQUEST, AuditEvents.VID_GENERATE_REQUEST_RESPONSE,
				IdType.UIN.getType(),IdType.UIN, desc);
		return vidResponseDTO;
	}
	
	
	public void generateVIDTest() {
		
	}

}
