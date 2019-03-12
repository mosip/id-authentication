package io.mosip.registration.service.impl;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationClientStatusCode;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dao.GlobalParamDAO;
import io.mosip.registration.dao.RegistrationDAO;
import io.mosip.registration.dao.SyncJobConfigDAO;
import io.mosip.registration.entity.GlobalParam;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.entity.SyncJobDef;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.packet.PacketUploadService;
import io.mosip.registration.service.packet.RegPacketStatusService;
import io.mosip.registration.service.sync.PacketSynchService;
import io.mosip.registration.util.healthcheck.RegistrationAppHealthCheckUtil;

/**
 * 
 * @author balamurugan.ramamoorthy
 *
 */
@Service
public class CenterMachineReMapServiceImpl {

	@Autowired
	private GlobalParamDAO globalParamDAO;

	@Autowired
	private PacketSynchService packetSynchService;

	@Autowired
	private PacketUploadService packetUploadService;

	@Autowired
	private RegPacketStatusService packetStatusService;

	@Autowired
	private RegistrationDAO registrationDAO;

	@Autowired
	private SyncJobConfigDAO jobConfigDAO;

	private static final Logger LOGGER = AppConfig.getLogger(CenterMachineReMapServiceImpl.class);

	/**
	 * Checks and handles all the operations to be done when the machine is re
	 * mapped to another center
	 * 
	 */
	public void handleReMapProcess() {

		Boolean isMachineReMapped = isMachineRemapped();
		if (isMachineReMapped) {
			LOGGER.info("REGISTRATION CENTER MACHINE REMAP : ", APPLICATION_NAME, APPLICATION_ID, 
					"handleReMapProcess called and machine has been remaped");

			/* (TODO-has to check whether to delete or disable) 1.disable all sync jobs */
			disableAllSyncJobs();
			if (isPacketsPendingForProcessing()) {
				if (RegistrationAppHealthCheckUtil.isNetworkAvailable()) {
					try {

						/* 2. sync packet status from server to Reg client */
						packetStatusService.packetSyncStatus();

						/* 3.sync and upload the reg packets to server */
						packetSynchService.syncAllPackets();

						packetUploadService.uploadAllSyncedPackets();

					} catch (RegBaseCheckedException exception) {
						LOGGER.error("REGISTRATION CENTER MACHINE REMAP : ", APPLICATION_NAME, APPLICATION_ID,
								exception.getMessage() + ExceptionUtils.getStackTrace(exception));
					}
				}
			}

			if (!isPacketsPendingForProcessing()) {
				/* TODO-all packets/pre reg and master data can be deleted- */
			}

			/* 4.deletions of packets */
			packetStatusService.deletePacketsWhenMachineRemapped();

			/* TODO - call this directly from ui */
			isPacketsPendingForEOD();

		}

	}

	/**
	 * checks if there is any Registration packets are not yet been processed by the
	 * Reg processor
	 * 
	 * @return boolean
	 */
	public boolean isPacketsPendingForProcessing() {
		List<Registration> registrations = registrationDAO
				.findByServerStatusCodeNotIn(RegistrationConstants.PACKET_STATUS_CODES_FOR_REMAPDELETE);
		return isNotNullNotEmpty(registrations);
	}

	/**
	 * Checks if there is any Reg packets are pending for EOD Approval
	 * 
	 * @return boolean
	 */
	public boolean isPacketsPendingForEOD() {
		List<Registration> newRegistrations = registrationDAO
				.getEnrollmentByStatus(RegistrationClientStatusCode.CREATED.getCode());

		return isNotNullNotEmpty(newRegistrations);
	}

	/**
	 * disables all the sync jobs
	 */
	private void disableAllSyncJobs() {
		List<SyncJobDef> jobDefs = jobConfigDAO.getActiveJobs();
		if (isNotNullNotEmpty(jobDefs)) {
			jobDefs.forEach(job -> {
				job.setIsActive(false);
			});
			jobConfigDAO.updateAll(jobDefs);
		}
	}

	/**
	 * checks if the Machine has been re mapped to some other center
	 * 
	 * @return
	 */
	public Boolean isMachineRemapped() {
		GlobalParam globalParam = globalParamDAO.get(RegistrationConstants.MACHINE_CENTER_REMAP_FLAG);
		return globalParam != null ? Boolean.valueOf(globalParam.getVal()) : false;
	}

	private boolean isNotNullNotEmpty(Collection<?> collection) {
		return collection != null && !collection.isEmpty();
	}

}
