package io.mosip.authentication.common.service.impl.hotlist;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.authentication.common.service.entity.HotlistCache;
import io.mosip.authentication.common.service.repository.HotlistCacheRepository;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.hotlist.service.HotlistService;
import io.mosip.kernel.core.logger.spi.Logger;

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
		Optional<HotlistCache> hotlistData = hotlistCacheRepo.findByIdHashAndIdType(id, idType);
		if (hotlistData.isPresent()) {
			HotlistCache hotlistCache = hotlistData.get();
			hotlistCache.setStatus(status);
			hotlistCache.setExpiryDTimes(expiryTimestamp);
			hotlistCacheRepo.save(hotlistCache);
		} else {
			HotlistCache hotlistCache = new HotlistCache();
			hotlistCache.setIdHash(id);
			hotlistCache.setIdType(idType);
			hotlistCache.setStatus(status);
			hotlistCache.setExpiryDTimes(expiryTimestamp);
			hotlistCacheRepo.save(hotlistCache);
		}
	}

	@Override
	public void unblock(String id, String idType) throws IdAuthenticationBusinessException {
		Optional<HotlistCache> hotlistData = hotlistCacheRepo.findByIdHashAndIdType(id, idType);
		if (hotlistData.isPresent()) {
			HotlistCache hotlistCache = hotlistData.get();
			hotlistCacheRepo.delete(hotlistCache);
		}
	}

}
