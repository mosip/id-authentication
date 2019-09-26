package io.mosip.authentication.internal.service.impl;

import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.entity.AuthtypeLock;
import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.repository.AuthLockRepository;
import io.mosip.authentication.core.authtype.dto.AuthtypeStatus;
import io.mosip.authentication.core.authtype.dto.UpdateAuthtypeStatusResponseDto;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.spi.authtype.status.service.AuthTypeStatusDto;
import io.mosip.authentication.core.spi.authtype.status.service.UpdateAuthtypeStatusService;
import io.mosip.authentication.core.spi.id.service.IdService;
import io.mosip.authentication.core.spi.indauth.match.MatchType.Category;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.HMACUtils;

/**
 * The Class UpdateAuthtypeStatusServiceImpl.
 *
 * @author Dinesh Karuppaiah T
 */
@Component
@Transactional
public class UpdateAuthtypeStatusServiceImpl implements UpdateAuthtypeStatusService {

	/** The Constant UIN_KEY. */
	private static final Object UIN_KEY = "uin";

	/** The id service. */
	@Autowired
	private IdService<AutnTxn> idService;

	/** The auth lock repository. */
	@Autowired
	private AuthLockRepository authLockRepository;

	/** The environment. */
	@Autowired
	private Environment environment;

	/* (non-Javadoc)
	 * @see io.mosip.authentication.core.spi.authtype.status.service.UpdateAuthtypeStatusService#updateAuthtypeStatus(io.mosip.authentication.core.spi.authtype.status.service.AuthTypeStatusDto)
	 */
	public UpdateAuthtypeStatusResponseDto updateAuthtypeStatus(AuthTypeStatusDto authTypeStatusDto) throws IdAuthenticationBusinessException {
		Map<String, Object> idResDTO = idService.processIdType(IdType.getIDTypeStrOrDefault(authTypeStatusDto.getIndividualIdType()),
				authTypeStatusDto.getIndividualId(), false);
		if (idResDTO != null && !idResDTO.isEmpty() && idResDTO.containsKey(UIN_KEY)) {
			String uin = String.valueOf(idResDTO.get(UIN_KEY));
			List<AuthtypeLock> entities = authTypeStatusDto.getRequest().stream().map(
					authtypeStatus -> this.putAuthTypeStatus(authtypeStatus, uin, authTypeStatusDto.getRequestTime()))
					.collect(Collectors.toList());
			authLockRepository.saveAll(entities);
		}
		
		return buildResponse();
	}

	/**
	 * Put auth type status.
	 *
	 * @param authtypeStatus the authtype status
	 * @param uin the uin
	 * @param reqTime the req time
	 * @return the authtype lock
	 */
	private AuthtypeLock putAuthTypeStatus(AuthtypeStatus authtypeStatus, String uin, String reqTime) {
		AuthtypeLock authtypeLock = new AuthtypeLock();
		authtypeLock.setUin(uin);
		authtypeLock.setHashedUin(HMACUtils.digestAsPlainText(HMACUtils.generateHash(uin.getBytes())));
		String authType = authtypeStatus.getAuthType();
		if (authType.equalsIgnoreCase(Category.BIO.getType())) {
			authType = authType + "-" + authtypeStatus.getAuthSubType();
		}
		authtypeLock.setAuthtypecode(authType);
		authtypeLock.setCrDTimes(DateUtils.getUTCCurrentDateTime());
		String strUTCDate = DateUtils.getUTCTimeFromDate(
				DateUtils.parseToDate(reqTime, environment.getProperty(IdAuthConfigKeyConstants.DATE_TIME_PATTERN)));
		authtypeLock.setLockrequestDTtimes(DateUtils.parseToLocalDateTime(strUTCDate));
		authtypeLock.setLockstartDTtimes(DateUtils.parseToLocalDateTime(strUTCDate));
		authtypeLock.setStatuscode(Boolean.toString(authtypeStatus.getLocked()));
		authtypeLock.setCreatedBy(environment.getProperty(IdAuthConfigKeyConstants.APPLICATION_ID));
		authtypeLock.setCrDTimes(DateUtils.getUTCCurrentDateTime());
		authtypeLock.setLangCode(environment.getProperty(IdAuthConfigKeyConstants.MOSIP_PRIMARY_LANGUAGE));
		return authtypeLock;
	}
	
	/**
	 * Builds the response.
	 *
	 * @return the update authtype status response dto
	 */
	private UpdateAuthtypeStatusResponseDto buildResponse() {
		UpdateAuthtypeStatusResponseDto authtypeStatusResponseDto = new UpdateAuthtypeStatusResponseDto();
		authtypeStatusResponseDto.setResponseTime(getResponseTime());
		return authtypeStatusResponseDto;
	}
	
	/**
	 * To get Response Time.
	 *
	 * @return the response time
	 */
	private String getResponseTime() {
		return DateUtils.formatDate(
				DateUtils.parseToDate(DateUtils.getUTCCurrentDateTimeString(),
						environment.getProperty(IdAuthConfigKeyConstants.DATE_TIME_PATTERN),
						TimeZone.getTimeZone(ZoneOffset.UTC)),
				environment.getProperty(IdAuthConfigKeyConstants.DATE_TIME_PATTERN),
				TimeZone.getTimeZone(ZoneOffset.UTC));
	}

}
