package io.mosip.registration.service.impl;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.sql.Connection;
import java.sql.SQLException;
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
import io.mosip.registration.entity.id.GlobalParamId;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.packet.PacketUploadService;
import io.mosip.registration.service.packet.RegPacketStatusService;
import io.mosip.registration.service.sync.PacketSynchService;
import io.mosip.registration.util.healthcheck.RegistrationAppHealthCheckUtil;

/**
 * Class to handles all the operations when the machine is rempaped
 * 
 * @author balamurugan.ramamoorthy
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
	private JdbcTemplate jdbcTemplate;

	private static final Logger LOGGER = AppConfig.getLogger(CenterMachineReMapServiceImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.impl.CenterMachineReMapService#
	 * handleReMapProcess()
	 */
	@Override
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
						packetStatusService.packetSyncStatus(RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM);

						/* 3.sync and upload the reg packets to server */
						packetSynchService.packetSync(RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM);

						packetUploadService.uploadAllSyncedPackets();

					} catch (RegBaseCheckedException exception) {
						LOGGER.error("REGISTRATION CENTER MACHINE REMAP : ", APPLICATION_NAME, APPLICATION_ID,
								exception.getMessage() + ExceptionUtils.getStackTrace(exception));
					}
				}
			}

			if (!isPacketsPendingForProcessing()) {
				/* TODO-all packets/pre reg and master data can be deleted- */
				deletePreRegPackets();
			}

			/* 4.deletions of packets */
			packetStatusService.deletePacketsWhenMachineRemapped();

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.impl.CenterMachineReMapService#
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
	 * @see io.mosip.registration.service.impl.CenterMachineReMapService#
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
	private void disableAllSyncJobs() {
		List<SyncJobDef> jobDefs = jobConfigDAO.getActiveJobs();
		if (isNotNullNotEmpty(jobDefs)) {
			jobDefs.forEach(job -> {
				job.setIsActive(false);
			});
			jobConfigDAO.updateAll(jobDefs);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.impl.CenterMachineReMapService#
	 * isMachineRemapped()
	 */
	@Override
	public Boolean isMachineRemapped() {
		GlobalParamId globalParamId = new GlobalParamId();
		globalParamId.setCode(RegistrationConstants.MACHINE_CENTER_REMAP_FLAG);
		globalParamId.setLangCode("eng");
		GlobalParam globalParam = globalParamDAO.get(globalParamId);
		return globalParam != null ? Boolean.valueOf(globalParam.getVal()) : false;
	}

	private boolean isNotNullNotEmpty(Collection<?> collection) {
		return collection != null && !collection.isEmpty();
	}

	private void deletePreRegPackets() {
		LOGGER.info("REGISTRATION CENTER MACHINE REMAP : ", APPLICATION_NAME, APPLICATION_ID,
				"delete preRegPackets() method is called");
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
