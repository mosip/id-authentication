package io.mosip.authentication.internal.service.impl;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.AUTH_TYPE_STATUS_ACK_TOPIC;

import java.time.LocalDateTime;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.entity.AuthtypeLock;
import io.mosip.authentication.common.service.repository.AuthLockRepository;
import io.mosip.authentication.common.service.validator.AuthRequestValidator;
import io.mosip.authentication.common.service.websub.impl.AuthTypeStatusEventPublisherManager;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
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
@Transactional
public class UpdateAuthtypeStatusServiceImpl implements UpdateAuthtypeStatusService {
	
	private static Logger mosipLogger = IdaLogger.getLogger(UpdateAuthtypeStatusServiceImpl.class);

	private static final String STAUTS_LOCKED = "LOCKED";

	private static final String STATUS_UNLOCKED = "UNLOCKED";

	/** The Constant UNLOCK_EXP_TIMESTAMP. */
	private static final String UNLOCK_EXP_TIMESTAMP = "unlockExpiryTimestamp";

	/** The auth lock repository. */
	@Autowired
	private AuthLockRepository authLockRepository;

	/** The environment. */
	@Autowired
	private Environment environment;
	
	@Autowired
	private AuthTypeStatusEventPublisherManager authTypeStatusEventPublisherManager;

	/**
	 * Update auth type status.
	 *
	 * @param tokenId the token id
	 * @param authTypeStatusList the auth type status list
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	@Override
	public void updateAuthTypeStatus(String tokenId, List<AuthtypeStatus> authTypeStatusList)
			throws IdAuthenticationBusinessException {
		List<Entry<String, AuthtypeLock>> entitiesForRequestId = authTypeStatusList.stream()
				.map(authTypeStatus -> new SimpleEntry<>(authTypeStatus.getRequestId(), 
						this.putAuthTypeStatus(authTypeStatus, tokenId) ))
				.collect(Collectors.toList());
		List<AuthtypeLock> entities = entitiesForRequestId.stream().map(Entry::getValue).collect(Collectors.toList());
		authLockRepository.saveAll(entities);
		
		entitiesForRequestId.stream().forEach(entry -> {
			String requestId = entry.getKey();
			if(requestId != null) {
				AuthtypeLock authtypeLock = entry.getValue();
				String status = Boolean.valueOf(authtypeLock.getStatuscode()) ? STAUTS_LOCKED : STATUS_UNLOCKED;
				authTypeStatusEventPublisherManager.publishCredentialUpdateStatusEvent(status, requestId, authtypeLock.getCrDTimes());
			} else {
				mosipLogger.error("requestId is null; Websub Notification for {} topic is not sent." , AUTH_TYPE_STATUS_ACK_TOPIC);
			}
		});
	}

	/**
	 * Put auth type status.
	 *
	 * @param authtypeStatus            the authtype status
	 * @param token the token
	 * @return the authtype lock
	 */
	private AuthtypeLock putAuthTypeStatus(AuthtypeStatus authtypeStatus, String token) {
		AuthtypeLock authtypeLock = new AuthtypeLock();
		authtypeLock.setToken(token);
		String authType = authtypeStatus.getAuthType();
		if (authType.equalsIgnoreCase(Category.BIO.getType())) {
			authType = authType + "-" + authtypeStatus.getAuthSubType();
		}
		authtypeLock.setAuthtypecode(authType);
		LocalDateTime currentDtime = DateUtils.getUTCCurrentDateTime();
		authtypeLock.setLockrequestDTtimes(currentDtime);
		authtypeLock.setLockstartDTtimes(currentDtime);
		if (Objects.nonNull(authtypeStatus.getMetadata())
				&& authtypeStatus.getMetadata().containsKey(UNLOCK_EXP_TIMESTAMP)
				&& authtypeStatus.getMetadata().get(UNLOCK_EXP_TIMESTAMP) instanceof LocalDateTime) {
			authtypeLock.setUnlockExpiryDTtimes((LocalDateTime) authtypeStatus.getMetadata().get(UNLOCK_EXP_TIMESTAMP));
		}
		authtypeLock.setStatuscode(Boolean.toString(authtypeStatus.getLocked()));
		authtypeLock.setCreatedBy(environment.getProperty(IdAuthConfigKeyConstants.APPLICATION_ID));
		authtypeLock.setCrDTimes(currentDtime);
		authtypeLock.setLangCode(environment.getProperty(IdAuthConfigKeyConstants.MOSIP_PRIMARY_LANGUAGE));
		return authtypeLock;
	}
	
	
}
