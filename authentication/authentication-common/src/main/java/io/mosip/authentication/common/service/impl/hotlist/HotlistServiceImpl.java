package io.mosip.authentication.common.service.impl.hotlist;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.authentication.common.service.entity.HotlistCache;
import io.mosip.authentication.common.service.repository.HotlistCacheRepository;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.hotlist.service.HotlistService;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.HMACUtils2;

/**
 * @author Manoj SP
 *
 */
@Service
public class HotlistServiceImpl implements HotlistService {

	private static Logger logger = IdaLogger.getLogger(HotlistServiceImpl.class);

	@Autowired
	private HotlistCacheRepository hotlistCacheRepo;

	@Override
	public void block(String id, String idType, String status, LocalDateTime expiryTimestamp)
			throws IdAuthenticationBusinessException {
		try {
			Optional<HotlistCache> hotlistData = hotlistCacheRepo
					.findByIdHashAndIdType(HMACUtils2.digestAsPlainText(id.getBytes()), idType);
			if (hotlistData.isPresent()) {
				HotlistCache hotlistCache = hotlistData.get();
				hotlistCache.setStatus(status);
				hotlistCache.setExpiryDTimes(expiryTimestamp);
				hotlistCacheRepo.save(hotlistCache);
			} else {
				HotlistCache hotlistCache = new HotlistCache();
				hotlistCache.setIdHash(HMACUtils2.digestAsPlainText(id.getBytes()));
				hotlistCache.setIdType(idType);
				hotlistCache.setStatus(status);
				hotlistCache.setExpiryDTimes(expiryTimestamp);
				hotlistCacheRepo.save(hotlistCache);
			}
		} catch (NoSuchAlgorithmException e) {
			logger.error(IdAuthCommonConstants.SESSION_ID, "HotlistServiceImpl", "block",
					ExceptionUtils.getStackTrace(e));
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS);
		}
	}

	@Override
	public void unblock(String id, String idType) throws IdAuthenticationBusinessException {
		try {
			Optional<HotlistCache> hotlistData = hotlistCacheRepo
					.findByIdHashAndIdType(HMACUtils2.digestAsPlainText(id.getBytes()), idType);
			if (hotlistData.isPresent()) {
				HotlistCache hotlistCache = hotlistData.get();
				hotlistCacheRepo.delete(hotlistCache);
			}
		} catch (NoSuchAlgorithmException e) {
			logger.error(IdAuthCommonConstants.SESSION_ID, "HotlistServiceImpl", "block",
					ExceptionUtils.getStackTrace(e));
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS);
		}
	}

}
