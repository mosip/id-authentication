package io.mosip.registration.dao.impl;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.registration.audit.AuditFactory;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.AppModule;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.RegistrationClientStatusCode;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dao.SyncJobDAO;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.repositories.RegistrationRepository;
import io.mosip.registration.repositories.SyncJobRepository;

/**
 * The implementation class for {@link SyncJobDAO}.
 *
 * @author Sreekar Chukka
 * @author Mahesh Kumar
 * @since 1.0.0
 */
@Repository
public class SyncJobDAOImpl implements SyncJobDAO {

	private static final List<String> REG_STATUS_CODES = Arrays.asList(RegistrationClientStatusCode.CREATED.getCode(),
			RegistrationClientStatusCode.REJECTED.getCode(), RegistrationClientStatusCode.APPROVED.getCode(),
			RegistrationClientStatusCode.CORRECTION.getCode(), RegistrationClientStatusCode.UIN_UPDATE.getCode(),
			RegistrationClientStatusCode.UIN_LOST.getCode(),
			RegistrationClientStatusCode.META_INFO_SYN_SERVER.getCode(), RegistrationClientStatusCode.ON_HOLD.getCode(),
			RegistrationClientStatusCode.PACKET_ERROR.getCode());

	/** Object for Sync Status Repository. */
	@Autowired
	private SyncJobRepository syncStatusRepository;

	/** Object for Registration Repository. */
	@Autowired
	private RegistrationRepository registrationRepository;

	/**
	 * Object for Logger
	 */
	private static final MosipLogger LOGGER = AppConfig.getLogger(SyncJobDAOImpl.class);

	@Autowired
	private AuditFactory auditFactory;
	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.dao.SyncJobDAO#validateSyncStatus()
	 */
	public SyncJobInfo getSyncStatus() {

		LOGGER.debug("REGISTRATION - SYNC - VALIDATION", APPLICATION_NAME, APPLICATION_ID,
				"Fetching the last sync details from databse started");

		try {
			List<Registration> registrationsList = registrationRepository.findByClientStatusCodeIn(REG_STATUS_CODES);

			LOGGER.debug("REGISTRATION - SYNC - VALIDATION", APPLICATION_NAME, APPLICATION_ID,
					"Fetching the last sync details from databse ended");

			auditFactory.audit(AuditEvent.SYNCJOB_INFO_FETCH, AppModule.SYNC_VALIDATE,
					"SyncJobInfo containing the synccontrol list and yet to exportpacket count fetched successfully",
					"refId", "refIdType");

			return new SyncJobInfo(syncStatusRepository.findAll(), registrationsList.size());

		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationConstants.SYNC_STATUS_VALIDATE,
					runtimeException.toString());
		}
	}

}
