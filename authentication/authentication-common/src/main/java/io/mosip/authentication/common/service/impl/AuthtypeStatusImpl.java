package io.mosip.authentication.common.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.entity.AuthtypeLock;
import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.repository.AuthLockRepository;
import io.mosip.authentication.core.authtype.dto.AuthtypeRequestDto;
import io.mosip.authentication.core.authtype.dto.AuthtypeStatus;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.authtype.status.service.AuthtypeStatusService;
import io.mosip.authentication.core.spi.id.service.IdService;
import io.mosip.kernel.core.util.HMACUtils;

/**
 * 
 * @author Dinesh Karuppiah.T
 *
 */

@Component
public class AuthtypeStatusImpl implements AuthtypeStatusService {

	private static final String HYPHEN = "-";

	@Autowired
	AuthLockRepository authLockRepository;

	@Autowired
	private IdService<AutnTxn> idService;

	private static final String UIN_KEY = "uin";

	@Override
	public List<AuthtypeStatus> fetchAuthtypeStatus(AuthtypeRequestDto authtypeRequestDto)
			throws IdAuthenticationBusinessException {
		String individualId = authtypeRequestDto.getIndividualId();
		String individualIdType = authtypeRequestDto.getIndividualIdType();
		List<AuthtypeLock> authTypeLockList = new ArrayList<>();
		Map<String, Object> idResDTO = idService.processIdType(individualIdType, individualId, false);
		if (idResDTO != null && !idResDTO.isEmpty() && idResDTO.containsKey(UIN_KEY)) {
			String uin = String.valueOf(idResDTO.get(UIN_KEY));
			authTypeLockList = authLockRepository.findByUin(uin);
		}
		return processAuthtypeList(authTypeLockList);
	}

	public List<AuthtypeStatus> fetchAuthtypeStatus(String individualId, String individualIdType)
			throws IdAuthenticationBusinessException {
		List<AuthtypeLock> authTypeLockList = new ArrayList<>();
		Map<String, Object> idResDTO = idService.processIdType(individualIdType, individualId, false);
		if (idResDTO != null && !idResDTO.isEmpty() && idResDTO.containsKey(UIN_KEY)) {
			String uin = String.valueOf(idResDTO.get(UIN_KEY));
			authTypeLockList = authLockRepository.findByUin(uin);
		}
		return processAuthtypeList(authTypeLockList);
	}

	private List<AuthtypeStatus> processAuthtypeList(List<AuthtypeLock> authtypelockList) {
		return authtypelockList.stream().map(this::getAuthTypeStatus).collect(Collectors.toList());
	}

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
		authtypeStatus.setLocked(isLocked);
		return authtypeStatus;
	}

}
