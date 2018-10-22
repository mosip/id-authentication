package io.mosip.registration.dao.impl;

import static io.mosip.registration.constants.RegConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegConstants.APPLICATION_NAME;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.factory.MosipLogfactory;
import io.mosip.registration.audit.AuditFactory;
import io.mosip.registration.constants.AppModuleEnum;
import io.mosip.registration.constants.AuditEventEnum;
import io.mosip.registration.constants.RegClientStatusCode;
import io.mosip.registration.constants.RegProcessorExceptionCode;
import io.mosip.registration.dao.SyncJobDAO;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.entity.SyncControl;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.repositories.RegistrationRepository;
import io.mosip.registration.repositories.SyncJobRepository;

/**
 * The implementation class for {@link SyncJobDAO}.
 *
 * @author Sreekar Chukka
 * @since 1.0.0
 */
@Repository
public class SyncJobDAOImpl implements SyncJobDAO {

	/** Object for Sync Status Repository. */
	@Autowired
	private SyncJobRepository syncStatusRepository;

	/** Object for Registration Repository. */
	@Autowired
	private RegistrationRepository registrationRepository;

	/**
	 * Object for Logger
	 */
	private static MosipLogger LOGGER;

	/**
	 * Initializing logger
	 * 
	 * @param mosipRollingFileAppender
	 */
	@Autowired
	private void initializeLogger(MosipRollingFileAppender mosipRollingFileAppender) {
		LOGGER = MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, this.getClass());
	}

	@Autowired
	private AuditFactory auditFactory;
	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.dao.SyncJobDAO#validateSyncStatus()
	 */
	public SyncJobInfo getSyncStatus() {
		LOGGER.debug("REGISTRATION - SYNC - VALIDATION", APPLICATION_NAME,
				APPLICATION_ID, "Fetching the last sync details from databse started");

		double yetToExportCount = 0;
		List<SyncControl> syncControlList = new ArrayList<>();
		try {
			List<String> statusCodes = new ArrayList<>();
			statusCodes.add(RegClientStatusCode.CREATED.getCode());
			statusCodes.add(RegClientStatusCode.REJECTED.getCode());
			statusCodes.add(RegClientStatusCode.APPROVED.getCode());
			statusCodes.add(RegClientStatusCode.CORRECTION.getCode());
			statusCodes.add(RegClientStatusCode.UIN_UPDATE.getCode());
			statusCodes.add(RegClientStatusCode.UIN_LOST.getCode());
			statusCodes.add(RegClientStatusCode.META_INFO_SYN_SERVER.getCode());
			statusCodes.add(RegClientStatusCode.ON_HOLD.getCode());
			statusCodes.add(RegClientStatusCode.PACKET_ERROR.getCode());

			syncControlList = syncStatusRepository.findAll();
			List<Registration> registrationsList = registrationRepository.findByClientStatusCodeIn(statusCodes);
			yetToExportCount = registrationsList.size();
			LOGGER.debug("REGISTRATION - SYNC - VALIDATION", APPLICATION_NAME,
					APPLICATION_ID, "Fetching the last sync details from databse ended");
			auditFactory.audit(AuditEventEnum.SYNCJOB_INFO_FETCH, AppModuleEnum.SYNC_VALIDATE,
					"SyncJobInfo containing the synccontrol list and yet to exportpacket count fetched successfully",
					"refId", "refIdType");
			
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegProcessorExceptionCode.SYNC_STATUS_VALIDATE,
					runtimeException.toString());
		}
		return new SyncJobInfo(syncControlList, yetToExportCount);

	}

}
