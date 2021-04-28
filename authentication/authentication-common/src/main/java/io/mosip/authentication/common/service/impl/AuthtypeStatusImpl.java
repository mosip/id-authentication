package io.mosip.authentication.common.service.impl;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.entity.AuthtypeLock;
import io.mosip.authentication.common.service.repository.AuthLockRepository;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.authtype.status.service.AuthtypeStatusService;
import io.mosip.idrepository.core.dto.AuthtypeStatus;
import io.mosip.kernel.core.util.DateUtils;

/**
 * The Class AuthtypeStatusImpl - implementation of
 * {@link AuthtypeStatusService}.
 *
 * @author Dinesh Karuppiah.T
 */

@Component
public class AuthtypeStatusImpl implements AuthtypeStatusService {

	/** The Constant HYPHEN. */
	private static final String HYPHEN = "-";

	/** The auth lock repository. */
	@Autowired
	AuthLockRepository authLockRepository;

	@Override
	public List<AuthtypeStatus> fetchAuthtypeStatus(String token) throws IdAuthenticationBusinessException {
		List<AuthtypeLock> authTypeLockList = getAuthTypeList(token);
		return processAuthtypeList(authTypeLockList);
	}

	public List<AuthtypeLock> getAuthTypeList(String token) throws IdAuthenticationBusinessException {
		List<AuthtypeLock> authTypeLockList;
		List<Object[]> authTypeLockObjectsList = authLockRepository.findByToken(token);
		authTypeLockList = authTypeLockObjectsList.stream()
				.map(obj -> new AuthtypeLock((String) obj[0], (String) obj[1], Objects.nonNull(obj[2]) ? ((Timestamp) obj[2]).toLocalDateTime() : null))
				.collect(Collectors.toList());
		return authTypeLockList;
	}

	/**
	 * Process authtype list.
	 *
	 * @param authtypelockList
	 *            the authtypelock list
	 * @return the list
	 */
	private List<AuthtypeStatus> processAuthtypeList(List<AuthtypeLock> authtypelockList) {
		return authtypelockList.stream().map(this::getAuthTypeStatus).collect(Collectors.toList());
	}

	/**
	 * Gets the auth type status.
	 *
	 * @param authtypeLock
	 *            the authtype lock
	 * @return the auth type status
	 */
	private AuthtypeStatus getAuthTypeStatus(AuthtypeLock authtypeLock) {
		AuthtypeStatus authtypeStatus = new AuthtypeStatus();
		String authtypecode = authtypeLock.getAuthtypecode();
		if (authtypecode.contains(HYPHEN)) {
			String[] authcode = authtypecode.split(HYPHEN);
			authtypeStatus.setAuthType(authcode[0]);
			authtypeStatus.setAuthSubType(authcode[1]);
		} else {
			authtypeStatus.setAuthType(authtypecode);
			authtypeStatus.setAuthSubType(null);
		}
		boolean isLocked = authtypeLock.getStatuscode().equalsIgnoreCase(Boolean.TRUE.toString());
		boolean isAuthTypeUnlockedTemporarily = isLocked && Objects.nonNull(authtypeLock.getUnlockExpiryDTtimes())
				&& authtypeLock.getUnlockExpiryDTtimes().isAfter(DateUtils.getUTCCurrentDateTime());
		authtypeStatus.setLocked(isAuthTypeUnlockedTemporarily ? false : isLocked);
		return authtypeStatus;
	}

}
