package io.mosip.authentication.internal.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.entity.AuthtypeLock;
import io.mosip.authentication.common.service.repository.AuthLockRepository;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.authtype.status.service.UpdateAuthtypeStatusService;
import io.mosip.authentication.core.spi.indauth.match.MatchType.Category;
import io.mosip.idrepository.core.dto.AuthtypeStatus;
import io.mosip.kernel.core.util.DateUtils;

/**
 * The Class UpdateAuthtypeStatusServiceImpl.
 *
 * @author Dinesh Karuppaiah T
 */
@Component
@Transactional
public class UpdateAuthtypeStatusServiceImpl implements UpdateAuthtypeStatusService {

	/** The auth lock repository. */
	@Autowired
	private AuthLockRepository authLockRepository;

	/** The environment. */
	@Autowired
	private Environment environment;

	@Override
	public void updateAuthTypeStatus(String tokenId, List<AuthtypeStatus> authTypeStatusList)
			throws IdAuthenticationBusinessException {
		List<AuthtypeLock> entities = authTypeStatusList.stream()
				.map(authtypeStatus -> this.putAuthTypeStatus(authtypeStatus, tokenId)).collect(Collectors.toList());
		authLockRepository.saveAll(entities);
	}

	/**
	 * Put auth type status.
	 *
	 * @param authtypeStatus
	 *            the authtype status
	 * @param uin
	 *            the uin
	 * @param reqTime
	 *            the req time
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
		authtypeLock.setStatuscode(Boolean.toString(authtypeStatus.getLocked()));
		authtypeLock.setCreatedBy(environment.getProperty(IdAuthConfigKeyConstants.APPLICATION_ID));
		authtypeLock.setCrDTimes(currentDtime);
		authtypeLock.setLangCode(environment.getProperty(IdAuthConfigKeyConstants.MOSIP_PRIMARY_LANGUAGE));
		return authtypeLock;
	}
}
