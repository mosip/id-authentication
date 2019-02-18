package io.mosip.authentication.service.impl.id.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;

import io.mosip.authentication.core.constant.AuditEvents;
import io.mosip.authentication.core.constant.AuditModules;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.IdType;
import io.mosip.authentication.core.dto.vid.VIDResponseDTO;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.id.service.IdAuthService;
import io.mosip.authentication.core.spi.id.service.VIDService;
import io.mosip.authentication.service.entity.AutnTxn;
import io.mosip.authentication.service.entity.VIDEntity;
import io.mosip.authentication.service.helper.AuditHelper;
import io.mosip.authentication.service.impl.spin.service.StaticPinServiceImpl;
import io.mosip.authentication.service.repository.VIDRepository;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.idgenerator.vid.impl.VidGeneratorImpl;

/**
 * The Class VIDServiceImpl.
 * 
 * @author Arun Bose
 */
public class VIDServiceImpl implements VIDService {	

	/** The Constant DATETIME_PATTERN. */
	private static final String DATETIME_PATTERN = "datetime.pattern";

	/** The Constant SESSION_ID. */
	private static final String SESSION_ID = "sessionId";

	/** The logger. */
	private static Logger logger = IdaLogger.getLogger(StaticPinServiceImpl.class);

	/** The id auth service. */
	@Autowired
	private IdAuthService<AutnTxn> idAuthService;

	/** The AuditHelper */
	@Autowired
	private AuditHelper auditHelper;

	/** The vid repository. */
	@Autowired
	private VIDRepository vidRepository;

	/** The vid generator. */
	@Autowired
	private VidGeneratorImpl vidGenerator;

	/** The env. */
	@Autowired
	Environment env;

	// FIXME this method has to be in refactored facade as seperate facade should
		// not be created.
		/* (non-Javadoc)
		 * @see io.mosip.authentication.core.spi.spin.service.StaticPinService#generateVID(java.lang.String)
		 */
		// @Override
		public VIDResponseDTO generateVID(String uin) throws IdAuthenticationBusinessException {
			Map<String, Object> uinMap = idAuthService.processIdType(IdType.UIN.getType(), uin, false);
			VIDEntity vidEntityObj = null;
			VIDResponseDTO vidResponseDTO = new VIDResponseDTO();
			vidResponseDTO.setId("mosip.identity.vid");
			vidResponseDTO.setVersion("1.0");
			if (!uinMap.isEmpty()) { // FIXME
				List<VIDEntity> vidEntityList = vidRepository.findByUIN(uin, PageRequest.of(0, 1));
				if (vidEntityList.isEmpty()) {
					vidEntityObj = new VIDEntity();
					vidEntityObj.setId((String) vidGenerator.generateId());
					vidEntityObj.setUin(uin);
					vidEntityObj.setActive(true);
					vidEntityObj.setCreatedBy("IDA");
					vidEntityObj.setCreatedDTimes(DateUtils.getUTCCurrentDateTime());
					vidEntityObj.setExpiryDate(DateUtils.getUTCCurrentDateTime()
							.plusHours(env.getProperty("mosip.vid.validity.hours", Long.class)));
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
					if (vidEntityObj.isActive()
							&& vidEntityObj.getExpiryDate().isAfter(DateUtils.getUTCCurrentDateTime())) {
						throw new IDDataValidationException(IdAuthenticationErrorConstants.VID_REGENERATION_FAILED,
								vidEntityObj.getId());
					}

					else if (!vidEntityObj.isActive()
							|| vidEntityObj.getExpiryDate().isBefore(DateUtils.getUTCCurrentDateTime())) {
						vidEntityObj = new VIDEntity();
						vidEntityObj.setId((String) vidGenerator.generateId());
						vidEntityObj.setUin(uin);
						vidEntityObj.setActive(true);
						vidEntityObj.setCreatedBy("IDA");
						vidEntityObj.setCreatedDTimes(DateUtils.getUTCCurrentDateTime());
						vidEntityObj.setExpiryDate(DateUtils.getUTCCurrentDateTime()
								.plusHours(env.getProperty("mosip.vid.validity.hours", Long.class)));
						vidEntityObj.setGeneratedOn(DateUtils.getUTCCurrentDateTime());
						vidEntityObj.setDeleted(false);
						try {
							vidRepository.save(vidEntityObj);
						} catch (DataAccessException ex) {
							logger.error(SESSION_ID, this.getClass().getName(), ex.getClass().getName(), ex.getMessage());
							throw new IdAuthenticationBusinessException(
									IdAuthenticationErrorConstants.VID_GENERATION_FAILED, ex);
						}
						vidResponseDTO.setVid(vidEntityObj.getId());
					}
				}
			}
			vidResponseDTO.setResponseTime(DateUtils.getUTCCurrentDateTimeString(env.getProperty(DATETIME_PATTERN)));
			vidResponseDTO.setError(Collections.emptyList());
			String desc = "VID generation request";
			auditHelper.audit(AuditModules.VID_GENERATION_REQUEST, AuditEvents.VID_GENERATE_REQUEST_RESPONSE,
					IdType.UIN.getType(), IdType.UIN, desc);
			return vidResponseDTO;
		}
}
