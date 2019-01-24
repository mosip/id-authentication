package io.mosip.authentication.service.impl.spin.facade;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.AuthError;
import io.mosip.authentication.core.dto.indauth.IdType;
import io.mosip.authentication.core.dto.spinstore.StaticPinRequestDTO;
import io.mosip.authentication.core.dto.spinstore.StaticPinResponseDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.id.service.IdAuthService;
import io.mosip.authentication.core.spi.spin.facade.StaticPinFacade;
import io.mosip.authentication.core.spi.spin.service.StaticPinService;
import io.mosip.kernel.core.util.DateUtils;

/**
 * 
 * @author Prem Kumar
 *
 */
@Service
public class StaticPinFacadeImpl implements StaticPinFacade {

	private static final String FAILED = "N";

	private static final String SUCCESS = "Y";

	/** The Constant DATETIME_PATTERN. */
	private static final String DATETIME_PATTERN = "datetime.pattern";

	/** The Environment */
	@Autowired
	private Environment env;

	@Autowired
	private StaticPinService staticPinService;

	/** The id auth service. */
	@Autowired
	private IdAuthService idAuthService;

	@Override
	public StaticPinResponseDTO storeSpin(StaticPinRequestDTO staticPinRequestDTO)
			throws IdAuthenticationBusinessException {
		StaticPinResponseDTO staticPinResponseDTO = new StaticPinResponseDTO();
		staticPinResponseDTO.setStatus(SUCCESS);
		String uin = staticPinRequestDTO.getRequest().getIdentity().getUin();
		String vid = staticPinRequestDTO.getRequest().getIdentity().getVid();
		Map<String, Object> idResDTO = null;
		boolean status = false;
		String resTime = null;
		if (uin != null) {
			idResDTO = idAuthService.processIdType(IdType.UIN.getType(), uin, false);
		} else if (vid != null) {
			idResDTO = idAuthService.processIdType(IdType.VID.getType(), vid, false);
		}

		String uinValue = String.valueOf(idResDTO.get("uin"));
		if (uinValue != null && uin.equals(uinValue)) {

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
		} 

		return staticPinResponseDTO;
	}

}
