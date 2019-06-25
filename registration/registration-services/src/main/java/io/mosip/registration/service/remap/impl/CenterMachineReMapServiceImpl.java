package io.mosip.registration.service.remap.impl;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptException;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.IOException;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.FileUtils;
import io.mosip.registration.audit.AuditManagerService;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.AuditReferenceIdTypes;
import io.mosip.registration.constants.Components;
import io.mosip.registration.constants.RegistrationClientStatusCode;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.dao.GlobalParamDAO;
import io.mosip.registration.dao.PreRegistrationDataSyncDAO;
import io.mosip.registration.dao.RegistrationDAO;
import io.mosip.registration.dao.SyncJobConfigDAO;
import io.mosip.registration.entity.GlobalParam;
import io.mosip.registration.entity.PreRegistrationList;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.entity.SyncJobDef;
import io.mosip.registration.entity.id.GlobalParamId;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.config.GlobalParamService;
import io.mosip.registration.service.packet.PacketUploadService;
import io.mosip.registration.service.packet.RegPacketStatusService;
import io.mosip.registration.service.remap.CenterMachineReMapService;
import io.mosip.registration.service.sync.PacketSynchService;
import io.mosip.registration.util.healthcheck.RegistrationAppHealthCheckUtil;

/**
 * Class {@code CenterMachineReMapServiceImpl} to handles all the operations
 * when the machine is rempaped
 * 
 * @author balamurugan.ramamoorthy
 * @author Brahmananda Reddy
 *
 */
@Service
public class CenterMachineReMapServiceImpl implements CenterMachineReMapService {

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

	@Autowired
	private PreRegistrationDataSyncDAO preRegistrationDataSyncDAO;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private AuditManagerService auditFactory;

	@Autowired
	private GlobalParamService globalParamService;

	private static final Logger LOGGER = AppConfig.getLogger(CenterMachineReMapServiceImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.remap.CenterMachineReMapService#
	 * handleReMapProcess()
	 */
	@Override
	public void handleReMapProcess(int step) {

		Boolean isMachineReMapped = isMachineRemapped();
		if (isMachineReMapped) {
			LOGGER.info("REGISTRATION CENTER MACHINE REMAP : ", APPLICATION_NAME, APPLICATION_ID,
					"handleReMapProcess called and machine has been remaped");

			auditFactory.audit(AuditEvent.MACHINE_REMAPPED, Components.CENTER_MACHINE_REMAP, "REGISTRATION",
					AuditReferenceIdTypes.APPLICATION_ID.getReferenceTypeId());

			switch (step) {
			case 1:
				LOGGER.info("REGISTRATION CENTER MACHINE REMAP : ", APPLICATION_NAME, APPLICATION_ID,
						"Step 1  disableAllSyncJobs started");
				disableAllSyncJobs();
				break;
			case 2:
				LOGGER.info("REGISTRATION CENTER MACHINE REMAP : ", APPLICATION_NAME, APPLICATION_ID,
						"Step 2  syncAndUploadAllPendingPackets started");
				syncAndUploadAllPendingPackets();
				break;
			case 3:
				LOGGER.info("REGISTRATION CENTER MACHINE REMAP : ", APPLICATION_NAME, APPLICATION_ID,
						"Step 3  deleteRegAndPreRegPackets started");
				deleteRegAndPreRegPackets();
				break;
			case 4:
				LOGGER.info("REGISTRATION CENTER MACHINE REMAP : ", APPLICATION_NAME, APPLICATION_ID,
						"Step 4  cleanUpCenterSpecificData started");
				cleanUpCenterSpecificData();
				break;
			default:
				break;
			}

		}

	}

	@Override
	public void startRemapProcess() {
		for (int i = 1; i <= 4; i++) {
			/* starts the remap process */
			handleReMapProcess(i);
		}
	}

	/**
	 * disable all sync jobs
	 */
	private void disableAllSyncJobs() {

		updateAllSyncJobs(false);
	}

	/**
	 * sync and upload process for Reg packets
	 */
	private void syncAndUploadAllPendingPackets() {
		if (isPacketsPendingForProcessing()) {

			if (RegistrationAppHealthCheckUtil.isNetworkAvailable()) {
				try {

					/* sync packet status from server to Reg client */
					packetStatusService.packetSyncStatus(RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM);
					LOGGER.info("REGISTRATION CENTER MACHINE REMAP : ", APPLICATION_NAME, APPLICATION_ID,
							"packetSyncStatus completed");
					auditFactory.audit(AuditEvent.MACHINE_REMAPPED, Components.PACKET_STATUS_SYNCHED, "REGISTRATION",
							AuditReferenceIdTypes.APPLICATION_ID.getReferenceTypeId());

					/* sync and upload the reg packets to server */
					packetSynchService.syncAllPackets();
					LOGGER.info("REGISTRATION CENTER MACHINE REMAP : ", APPLICATION_NAME, APPLICATION_ID,
							"syncAllPackets completed");
					auditFactory.audit(AuditEvent.MACHINE_REMAPPED, Components.PACKET_SYNCHED, "REGISTRATION",
							AuditReferenceIdTypes.USER_ID.getReferenceTypeId());

					packetUploadService.uploadAllSyncedPackets();
					LOGGER.info("REGISTRATION CENTER MACHINE REMAP : ", APPLICATION_NAME, APPLICATION_ID,
							"uploadAllSyncedPackets completed");
					
					/*sync packet status after packet upload*/
					packetStatusService.packetSyncStatus(RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM);
					LOGGER.info("REGISTRATION CENTER MACHINE REMAP : ", APPLICATION_NAME, APPLICATION_ID,
							"packetSyncStatus completed");
					
					auditFactory.audit(AuditEvent.MACHINE_REMAPPED, Components.PACKETS_UPLOADED, "REGISTRATION",
							AuditReferenceIdTypes.APPLICATION_ID.getReferenceTypeId());

				} catch (RegBaseCheckedException exception) {
					LOGGER.error("REGISTRATION CENTER MACHINE REMAP : ", APPLICATION_NAME, APPLICATION_ID,
							exception.getMessage() + ExceptionUtils.getStackTrace(exception));
				}
			}
		}
	}

	/**
	 * Reg and pre reg packet deletion
	 */
	private void deleteRegAndPreRegPackets() {
		/* deletions of packets */
		packetStatusService.deleteAllProcessedRegPackets();
		deleteAllPreRegPackets();
	}

	/**
	 * clean up of all center specific data
	 */
	private void cleanUpCenterSpecificData() {
		/*
		 * final clean up if no packets are pending to be sent and processed by reg
		 * processor
		 */
		if (!isPacketsPendingForProcessing()) {
			/* clean up all the pre reg data and previous center data */
			cleanUpRemappedMachineData();
			LOGGER.info("REGISTRATION CENTER MACHINE REMAP : ", APPLICATION_NAME, APPLICATION_ID,
					"Step 1  Remap table clean up Completed");
			auditFactory.audit(AuditEvent.MACHINE_REMAPPED, Components.CLEAN_UP, "REGISTRATION",
					AuditReferenceIdTypes.APPLICATION_ID.getReferenceTypeId());
			/*
			 * enabling all the jobs after all the clean up activities for the previous
			 * center
			 */
			if (!isPacketsPendingForProcessing()) {
				/* enable intial set up flag */
				globalParamService.update(RegistrationConstants.INITIAL_SETUP, RegistrationConstants.ENABLE);
				/* disable the remap flag after completing the remap process */
				GlobalParam globalParam = getRemapFlagValue();
				if (null != globalParam) {
					globalParam.setVal(RegistrationConstants.FALSE);
					globalParamDAO.saveAll(Arrays.asList(globalParam));
				}

				updateAllSyncJobs(true);
				LOGGER.info("REGISTRATION CENTER MACHINE REMAP : ", APPLICATION_NAME, APPLICATION_ID,
						"Step 1  Enabled all sync jobs after remap success");
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.remap.CenterMachineReMapService#
	 * isPacketsPendingForProcessing()
	 */
	@Override
	public boolean isPacketsPendingForProcessing() {
		List<Registration> registrations = registrationDAO
				.findByServerStatusCodeNotIn(RegistrationConstants.PACKET_STATUS_CODES_FOR_REMAPDELETE);
		return isNotNullNotEmpty(registrations);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.remap.CenterMachineReMapService#
	 * isPacketsPendingForEOD()
	 */
	@Override
	public boolean isPacketsPendingForEOD() {
		List<Registration> newRegistrations = registrationDAO
				.getEnrollmentByStatus(RegistrationClientStatusCode.CREATED.getCode());

		return isNotNullNotEmpty(newRegistrations);
	}

	/**
	 * disables all the sync jobs
	 */
	private void updateAllSyncJobs(boolean isJobActive) {
		List<SyncJobDef> jobDefs = jobConfigDAO.getAll();
		if (isNotNullNotEmpty(jobDefs)) {
			jobDefs.forEach(job -> {
				job.setIsActive(isJobActive);
			});
			jobConfigDAO.updateAll(jobDefs);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.remap.CenterMachineReMapService#
	 * isMachineRemapped()
	 */
	@Override
	public Boolean isMachineRemapped() {
		GlobalParam globalParam = getRemapFlagValue();
		return globalParam != null ? Boolean.valueOf(globalParam.getVal()) : false;
	}

	protected GlobalParam getRemapFlagValue() {
		GlobalParamId globalParamId = new GlobalParamId();
		globalParamId.setCode(RegistrationConstants.MACHINE_CENTER_REMAP_FLAG);
		globalParamId.setLangCode("eng");
		return globalParamDAO.get(globalParamId);
	}

	private boolean isNotNullNotEmpty(Collection<?> collection) {
		return collection != null && !collection.isEmpty();
	}

	/**
	 * delete all the pre reg packets and table records
	 */
	private void deleteAllPreRegPackets() {
		try {
			List<PreRegistrationList> preRegistrationLists = preRegistrationDataSyncDAO.getAllPreRegPackets();
			if (isNotNullNotEmpty(preRegistrationLists)) {
				preRegistrationDataSyncDAO.deleteAll(preRegistrationLists);
			}
			FileUtils.deleteDirectory(FileUtils
					.getFile((String) ApplicationContext.map().get(RegistrationConstants.PRE_REG_PACKET_LOCATION)));
			LOGGER.info("REGISTRATION CENTER MACHINE REMAP : ", APPLICATION_NAME, APPLICATION_ID,
					"Step 1  Deleted all the pre reg packets due to remap");
		} catch (IOException exception) {

			LOGGER.error("REGISTRATION CENTER MACHINE REMAP : ", APPLICATION_NAME, APPLICATION_ID,
					exception.getMessage() + ExceptionUtils.getStackTrace(exception));
		}

	}

	/**
	 * clean up all the data which is specific to the previous center
	 */
	private void cleanUpRemappedMachineData() {
		LOGGER.info("REGISTRATION CENTER MACHINE REMAP : ", APPLICATION_NAME, APPLICATION_ID,
				"delete cleanUpRemappedMachineData() method is called");
		try {
			Resource resource = new ClassPathResource("script.sql");
			Connection connection = jdbcTemplate.getDataSource().getConnection();
			ScriptUtils.executeSqlScript(connection, resource);

		} catch (ScriptException | SQLException exception) {
			LOGGER.error("REGISTRATION CENTER MACHINE REMAP : ", APPLICATION_NAME, APPLICATION_ID,
					exception.getMessage() + ExceptionUtils.getStackTrace(exception));

		}

	}

}
