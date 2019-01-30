package io.mosip.authentication.service.impl.spin.facade;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import io.mosip.authentication.core.constant.RequestType;
import io.mosip.authentication.core.dto.indauth.IdType;
import io.mosip.authentication.core.dto.spinstore.StaticPinRequestDTO;
import io.mosip.authentication.core.dto.spinstore.StaticPinResponseDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.id.service.IdAuthService;
import io.mosip.authentication.core.spi.spin.facade.StaticPinFacade;
import io.mosip.authentication.core.spi.spin.service.StaticPinService;
import io.mosip.authentication.service.helper.AuditHelper;
import io.mosip.kernel.core.util.DateUtils;

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
		StaticPinResponseDTO staticPinResponseDTO = new StaticPinResponseDTO();
		staticPinResponseDTO.setStatus(SUCCESS);
		String uin = staticPinRequestDTO.getRequest().getIdentity().getUin();
		String vid = staticPinRequestDTO.getRequest().getIdentity().getVid();
		String reqTime = staticPinRequestDTO.getReqTime();
		String tspIdValue = staticPinRequestDTO.getTspID();
		Map<String, Object> idResDTO = null;
		String uinValue = null;
		boolean status = false;
		String resTime = null;
		String idvId = null;
		String idvIdType = null;
		if (uin != null) {
			idResDTO = idAuthService.processIdType(IdType.UIN.getType(), uin, false);
			idvIdType = IdType.UIN.getType();
			idvId = uin;
		} else if (vid != null) {
			idResDTO = idAuthService.processIdType(IdType.VID.getType(), vid, false);
			idvIdType = IdType.VID.getType();
			idvId = vid;
		}

		if (idResDTO != null && idResDTO.containsKey(UIN_KEY)) {
			uinValue = (String) idResDTO.get(UIN_KEY);
		}

		if (uinValue != null && !uinValue.isEmpty()) {
			status = staticPinService.storeSpin(staticPinRequestDTO, uinValue);
		}
		staticPinResponseDTO.setId(staticPinRequestDTO.getId());
		staticPinResponseDTO.setVer(staticPinRequestDTO.getVer());
		String dateTimePattern = env.getProperty(DATETIME_PATTERN);

		DateTimeFormatter isoPattern = DateTimeFormatter.ofPattern(dateTimePattern);

		ZonedDateTime zonedDateTime2 = ZonedDateTime.parse(staticPinRequestDTO.getReqTime(), isoPattern);
		ZoneId zone = zonedDateTime2.getZone();
		resTime = DateUtils.formatDate(new Date(), dateTimePattern, TimeZone.getTimeZone(zone));
		staticPinResponseDTO.setResTime(resTime);
		if (status) {
			staticPinResponseDTO.setStatus(SUCCESS);
			staticPinResponseDTO.setErr(Collections.emptyList());
			String statusValue = status ? SUCCESS : FAILED;
			String comment = status ? "Static pin stored successfully" : "Faild to store Static pin";
			idAuthService.saveAutnTxn(idvId, idvIdType, reqTime, tspIdValue, statusValue, comment,
					RequestType.STATICPIN_STORE_REQUEST);
			// auditHelper.audit(AuditModules., AuditEvents., idvId, idvIdType, desc);
		}

		return staticPinResponseDTO;
	}

}
