package io.mosip.authentication.internal.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.entity.AuthtypeLock;
import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.core.authtype.dto.AuthtypeStatus;
import io.mosip.authentication.core.authtype.dto.UpdateAuthtypeStatusResponseDto;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.authtype.status.service.AuthTypeStatusDto;
import io.mosip.authentication.core.spi.authtype.status.service.UpdateAuthtypeStatusService;
import io.mosip.authentication.core.spi.id.service.IdService;
import io.mosip.authentication.core.spi.indauth.match.MatchType.Category;

@Component
public class UpdateAuthtypeStatusServiceImpl implements UpdateAuthtypeStatusService {

	private static final Object UIN_KEY = "uin";
	@Autowired
	private IdService<AutnTxn> idService;

	public UpdateAuthtypeStatusResponseDto updateAuthtypeStatus(AuthTypeStatusDto authTypeStatusDto)
			throws IdAuthenticationBusinessException {
		Map<String, Object> idResDTO = idService.processIdType(authTypeStatusDto.getIndividualIdType(),
				authTypeStatusDto.getIndividualId(), false);
		if (idResDTO != null && !idResDTO.isEmpty() && idResDTO.containsKey(UIN_KEY)) {
			String uin = String.valueOf(idResDTO.get(UIN_KEY));

//			authTypeStatusDto.getRequest().stream().map(this::putAuthTypeStatus).collect(Collectors.toList());
		}

		return null;
	}

	private AuthtypeLock putAuthStatus(AuthtypeStatus authtypeStatus) {
		AuthtypeLock authtypeLock = new AuthtypeLock();
		String authType = authtypeStatus.getAuthType();
		if (authType.equalsIgnoreCase(Category.BIO.getType())) {
			authtypeLock.setAuthtypecode(authtypeStatus.getAuthType() + "-" + authtypeStatus.getAuthSubType());
		} else {
			authtypeLock.setAuthtypecode(authtypeStatus.getAuthType());
		}
		if (authtypeStatus.isLocked()) {
			authtypeLock.setStatuscode("true");
		} else {
			authtypeLock.setStatuscode("false");
		}
		return authtypeLock;
	}

}
