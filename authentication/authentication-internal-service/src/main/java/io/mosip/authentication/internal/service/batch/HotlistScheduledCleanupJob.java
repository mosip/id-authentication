package io.mosip.authentication.internal.service.batch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.repository.HotlistCacheRepository;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.hotlist.constant.HotlistStatus;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;

/**
 * The Class HotlistScheduledCleanupJob.
 *
 * @author Manoj SP
 */
@Component
public class HotlistScheduledCleanupJob {
	
	/** The mosip logger. */
	private static Logger mosipLogger = IdaLogger.getLogger(HotlistScheduledCleanupJob.class);

	/** The hotlist repo. */
	@Autowired
	private HotlistCacheRepository hotlistRepo;
	
	@Autowired
	private IdAuthSecurityManager securityManager;

	/**
	 * Cleanup unblocked ids.
	 */
	@Scheduled(fixedDelayString = "#{60 * 60 * 1000 * ${mosip.hotlist.cleanup-schedule.fixed-delay-in-hours}}")
	public void cleanupUnblockedIds() {
		try {
			mosipLogger.info(securityManager.getUser(), "HotlistScheduledCleanupJob", "cleanupUnblockedIds",
					"INITIATED CLEANUP OF UNBLOCKED IDs");
			hotlistRepo.findByStatusAndExpiryDTimes(HotlistStatus.UNBLOCKED, null).forEach(hotlistRepo::delete);
		} catch (Exception e) {
			mosipLogger.warn(securityManager.getUser(), "HotlistScheduledCleanupJob", "cleanupUnblockedIds",
					"HOTLIST STATUS CLEANUP FAILED WITH EXCEPTION - " + ExceptionUtils.getStackTrace(e));
		}

	}

	/**
	 * Cleanup expired ids.
	 */
	@Scheduled(fixedDelayString = "#{60 * 60 * 1000 * ${mosip.hotlist.cleanup-schedule.fixed-delay-in-hours}}")
	public void cleanupExpiredIds() {
		try {
			mosipLogger.info(securityManager.getUser(), "HotlistScheduledCleanupJob", "cleanupExpiredIds",
					"INITIATED CLEANUP OF EXPIRED IDs");
			hotlistRepo.findByExpiryDTimesLessThanAndStatus(DateUtils.getUTCCurrentDateTime(), HotlistStatus.UNBLOCKED).forEach(hotlistRepo::delete);
		} catch (Exception e) {
			mosipLogger.warn(securityManager.getUser(), "HotlistScheduledCleanupJob", "cleanupUnblockedIds",
					"HOTLIST STATUS CLEANUP FAILED WITH EXCEPTION - " + ExceptionUtils.getStackTrace(e));
		}
	}
}
