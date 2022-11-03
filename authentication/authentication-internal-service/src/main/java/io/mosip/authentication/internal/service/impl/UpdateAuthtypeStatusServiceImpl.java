package io.mosip.authentication.internal.service.impl;

import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.AUTH_TYPE_STATUS_ACK_TOPIC;

import java.time.LocalDateTime;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.entity.AuthtypeLock;
import io.mosip.authentication.common.service.repository.AuthLockRepository;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.common.service.websub.impl.AuthTypeStatusEventPublisher;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.authtype.status.service.UpdateAuthtypeStatusService;
import io.mosip.authentication.core.spi.indauth.match.MatchType.Category;
import io.mosip.idrepository.core.dto.AuthtypeStatus;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;

/**
 * The Class UpdateAuthtypeStatusServiceImpl.
 *
 * @author Dinesh Karuppaiah T
 */
@Component
public class UpdateAuthtypeStatusServiceImpl implements UpdateAuthtypeStatusService {

	private static Logger mosipLogger = IdaLogger.getLogger(UpdateAuthtypeStatusServiceImpl.class);

	private static final String STAUTS_LOCKED = "LOCKED";

	private static final String STATUS_UNLOCKED = "UNLOCKED";

	/** The Constant UNLOCK_EXP_TIMESTAMP. */
	private static final String UNLOCK_EXP_TIMESTAMP = "unlockExpiryTimestamp";

	/** The auth lock repository. */
	@Autowired
	private AuthLockRepository authLockRepository;

	@Autowired
	private AuthTypeStatusEventPublisher authTypeStatusEventPublisherManager;

	/**
	 * Update auth type status.
	 *
	 * @param tokenId            the token id
	 * @param authTypeStatusList the auth type status list
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	@Override
	public void updateAuthTypeStatus(String tokenId, List<AuthtypeStatus> authTypeStatusList)
			throws IdAuthenticationBusinessException {
		List<Entry<String, AuthtypeLock>> entitiesForRequestId = authTypeStatusList.stream()
				.map(authTypeStatus -> new SimpleEntry<>(authTypeStatus.getRequestId(),
						this.putAuthTypeStatus(authTypeStatus, tokenId)))
				.collect(Collectors.toList());
		List<AuthtypeLock> entities = entitiesForRequestId.stream().map(Entry::getValue).collect(Collectors.toList());
		entities.forEach(entity -> authLockRepository.findByTokenAndAuthtypecode(tokenId, entity.getAuthtypecode())
				.forEach(authLockRepository::delete));
		authLockRepository.saveAll(entities);
		mosipLogger.debug("List of Auth Type Status- "+ authTypeStatusList);
		authTypeStatusEventPublisherManager.publishEvent(authTypeStatusList);
	}

	/**
	 * Put auth type status.
	 *
	 * @param authtypeStatus the authtype status
	 * @param token          the token
	 * @return the authtype lock
	 */
	private AuthtypeLock putAuthTypeStatus(AuthtypeStatus authtypeStatus, String token) {
		AuthtypeLock authtypeLock = new AuthtypeLock();
		authtypeLock.setToken(token);
		String authType = authtypeStatus.getAuthType();
		if (authType.equalsIgnoreCase(Category.BIO.getType()) || authType.equalsIgnoreCase(Category.OTP.getType())) {
			authType = authType + "-" + authtypeStatus.getAuthSubType();
		}
		authtypeLock.setAuthtypecode(authType);
		LocalDateTime currentDtime = DateUtils.getUTCCurrentDateTime();
		authtypeLock.setLockrequestDTtimes(currentDtime);
		authtypeLock.setLockstartDTtimes(currentDtime);
		if (Objects.nonNull(authtypeStatus.getMetadata())
				&& authtypeStatus.getMetadata().containsKey(UNLOCK_EXP_TIMESTAMP)) {
			authtypeLock.setUnlockExpiryDTtimes(
					DateUtils.parseToLocalDateTime((String) authtypeStatus.getMetadata().get(UNLOCK_EXP_TIMESTAMP)));
		}
		authtypeLock.setStatuscode(Boolean.toString(authtypeStatus.getLocked()));
		authtypeLock.setCreatedBy(EnvUtil.getAppId());
		authtypeLock.setCrDTimes(currentDtime);
		authtypeLock.setLangCode(IdAuthCommonConstants.NA);
		return authtypeLock;
	}

}
