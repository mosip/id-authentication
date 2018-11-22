package io.mosip.authentication.core.spi.id.service;

import java.util.List;
import java.util.Map;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.exception.IdAuthenticationDaoException;

/**
 * 
 * @author Dinesh Karuppiah.T
 */
public interface IdInfoService {
	Map<String, List<IdentityInfoDTO>> getIdInfo(String uinRefId) throws IdAuthenticationDaoException;
}
