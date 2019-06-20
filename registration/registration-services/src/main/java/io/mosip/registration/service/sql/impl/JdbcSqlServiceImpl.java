package io.mosip.registration.service.sql.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.util.FileUtils;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.service.BaseService;
import io.mosip.registration.service.config.GlobalParamService;
import io.mosip.registration.service.sql.JdbcSqlService;

/**
 * Execute Sql files
 * 
 * @author YASWANTH S
 * @since 1.0.0
 *
 */
@Service
public class JdbcSqlServiceImpl extends BaseService implements JdbcSqlService {

	@Autowired
	private ApplicationContext applicationContext;

	private Connection derbyRegConnection;

	@Autowired
	private GlobalParamService globalParamService;

//	@Autowired
//	private JobConfigurationService jobConfigurationService;

	// TODO move to application.properties
	private String backUpPath = "D://mosip/AutoBackUp";

	private static String libFolder = "lib/";
	private String binFolder = "bin/";

	private String manifestFile = "MANIFEST.MF";

	@Override
	public ResponseDTO executeSqlFile(String latestVersion, String previousVersion) {
		ResponseDTO responseDTO = new ResponseDTO();

		// clearScheduler();

		try (Connection connection = getConnection()) {
			// Get JDBC Connection
			this.derbyRegConnection = connection;

			if (derbyRegConnection != null) {
				executeSqlFile(responseDTO, latestVersion, previousVersion);
			} else {
				// Prepare Error Response as unable to establish connection
				setErrorResponse(responseDTO, "unable to esablish connection", null);
			}
		} catch (SQLException | RuntimeException exception) {
			// Prepare Error Response as unable to establish connection
			setErrorResponse(responseDTO, "unable to esablish connection", null);
		}
		return responseDTO;
	}

	/*
	 * private void clearScheduler() { if (jobConfigurationService != null &&
	 * jobConfigurationService.isSchedulerRunning()) {
	 * jobConfigurationService.stopScheduler();
	 * 
	 * boolean isCompleted = false;
	 * 
	 * while (!isCompleted) { isCompleted = isRunningJobsCompleted(); } } }
	 */

	/*
	 * private boolean isRunningJobsCompleted() {
	 * 
	 * boolean isCompleted = false; isCompleted =
	 * BaseJob.getCompletedJobMap().keySet()
	 * .containsAll(jobConfigurationService.getActiveSyncJobMap().keySet());
	 * 
	 * return isCompleted; }
	 */

	private Connection getConnection() {

		return DataSourceUtils.getConnection((DataSource) applicationContext.getBean("dataSource"));

	}

	private File getSqlFile(String path) {

		// Get File
		return new File(path);
	}

	private void runSqlFile(File sqlFile) throws IOException, SQLException {

		for (File file : sqlFile.listFiles()) {
			try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {

				String str;
				StringBuilder sb = new StringBuilder();
				while ((str = bufferedReader.readLine()) != null) {
					sb.append(str + "\n ");
				}

				List<String> statments = java.util.Arrays.asList(sb.toString().split(";"));

				for (String stat : statments) {
					if (!stat.trim().equals("")) {
						try (PreparedStatement prepStmt = derbyRegConnection.prepareStatement(stat)) {
							prepStmt.executeUpdate();
						}
					}
				}

			}

		}

	}

	private void executeSqlFile(ResponseDTO responseDTO, String latestVersion, String previousVersion) {
		URL resource = this.getClass().getResource("/sql/" + latestVersion + "/");
		if (resource != null) {

			File sqlFile = getSqlFile(resource.getPath());

			// execute sql file
			try {

				runSqlFile(sqlFile);

			} catch (RuntimeException | IOException | SQLException runtimeException) {

				replaceWithBackUpApplication(responseDTO, previousVersion);

				try {
					File rollBackFile = getSqlFile(
							this.getClass().getResource("/sql/" + latestVersion + "_rollback/").getPath());

					if (rollBackFile.exists()) {
						runSqlFile(rollBackFile);
					}
				} catch (RuntimeException | IOException | SQLException exception) {
					// Prepare Error Response
					setErrorResponse(responseDTO, "unable to execute sql file", null);

				}
				// Prepare Error Response
				setErrorResponse(responseDTO, "unable to execute sql file", null);
			}
		}

		else {
			// Update global param with current version
			globalParamService.update(RegistrationConstants.SERVICES_VERSION_KEY, latestVersion);
			setSuccessResponse(responseDTO, "Updated Version", null);

		}
	}

	private void replaceWithBackUpApplication(ResponseDTO responseDTO, String previousVersion) {
		File file = new File(backUpPath);

		for (File backUpFolder : file.listFiles()) {
			if (backUpFolder.getName().contains(previousVersion)) {

				try {
					FileUtils.copyDirectory(new File(backUpFolder.getAbsolutePath() + File.separator + binFolder),
							new File(binFolder));
					FileUtils.copyDirectory(new File(backUpFolder.getAbsolutePath() + File.separator + libFolder),
							new File(libFolder));
					FileUtils.copyFile(new File(backUpFolder.getAbsolutePath() + File.separator + manifestFile),
							new File(manifestFile));

				} catch (io.mosip.kernel.core.exception.IOException e) {
					setErrorResponse(responseDTO, "Replaced with previous Binaroies", null);
				}
				break;

			}
		}
	}

}
