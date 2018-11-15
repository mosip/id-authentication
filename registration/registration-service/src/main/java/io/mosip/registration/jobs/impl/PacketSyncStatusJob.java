package io.mosip.registration.jobs.impl;

import java.util.LinkedList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.jobs.BaseJob;
import io.mosip.registration.manager.BaseTransactionManager;
import io.mosip.registration.manager.impl.SyncTransactionManagerImpl;
import io.mosip.registration.service.RegPacketStatusService;
import io.mosip.registration.service.impl.JobConfigurationServiceImpl;

/**
 * This is a job to sync the packet status
 * 
 * @author YASWANTH S
 * @since 1.0.0
 *
 */
@Component(value = "packetSyncStatusJob")
public class PacketSyncStatusJob extends BaseJob {

	/**
	 * The SncTransactionManagerImpl, which Have the functionalities to get the job
	 * and to create sync transaction
	 */
	@Autowired
	private BaseTransactionManager baseTransactionManager;

	/**
	 * The RegPacketStatusServiceImpl
	 */
	@Autowired
	private RegPacketStatusService packetStatusService;

	/**
	 * LOGGER for logging
	 */
	private static final Logger LOGGER = AppConfig.getLogger(SyncTransactionManagerImpl.class);

	

	@Override
	public ResponseDTO executeJob(String triggerPoint) {

		System.out.println("Started Job Execution");
		LOGGER.debug(RegistrationConstants.PACKET_SYNC_STATUS_JOB_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "execute Job started");

		if (this.applicationContext != null) {
			/*
			 * If it was system triggered
			 * Get the Beans of baseTransactionManager and packetStatusService through
			 * application context
			 */
			this.baseTransactionManager = this.applicationContext.getBean(BaseTransactionManager.class);
			this.packetStatusService = this.applicationContext.getBean(RegPacketStatusService.class);
		}
		ResponseDTO responseDTO = packetStatusService.packetSyncStatus();
		if (responseDTO.getErrorResponseDTOs() != null) {
			try {
				// Insert Sync Transaction
				baseTransactionManager.createSyncTransaction(RegistrationConstants.JOB_EXECUTION_FAILED,
						RegistrationConstants.JOB_EXECUTION_FAILED, triggerPoint,
						JobConfigurationServiceImpl.SYNC_JOB_MAP.get("1"));

			} catch (RegBaseUncheckedException regBaseUncheckedException) {
				LinkedList<ErrorResponseDTO> errorResponseDTOs = new LinkedList<>();

				ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();
				errorResponseDTO.setInfoType(RegistrationConstants.ERROR);
				errorResponseDTO.setCode(RegistrationConstants.BATCH_JOB_CODE);
				errorResponseDTO.setMessage(regBaseUncheckedException.getMessage());

				errorResponseDTOs.add(errorResponseDTO);

				responseDTO.setErrorResponseDTOs(errorResponseDTOs);
			}
		}
		LOGGER.debug(RegistrationConstants.PACKET_SYNC_STATUS_JOB_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "execute job ended");

		System.out.println("Endedm Job Execution");

		return responseDTO;
	}
}
