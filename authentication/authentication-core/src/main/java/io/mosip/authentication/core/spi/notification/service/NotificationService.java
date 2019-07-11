package io.mosip.authentication.core.spi.notification.service;

import java.util.List;
import java.util.Map;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.AuthResponseDTO;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;

/***
 * 
 * @author Dinesh Karuppiah.T
 *
 *  To send Notitcation for Authentication service
 *
 */
public interface NotificationService {
	public void sendAuthNotification(AuthRequestDTO authRequestDTO, String uin, AuthResponseDTO authResponseDTO,
			Map<String, List<IdentityInfoDTO>> idInfo, boolean isAuth) throws IdAuthenticationBusinessException;

}
