package io.mosip.registration.service.sql.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
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
	private GlobalParamService globalParamService;

	
	// TODO move to application.properties
	private String backUpPath = "D://mosip/AutoBackUp";

	private static String libFolder = "lib";
	private String binFolder = "bin";

	private String manifestFile = "MANIFEST.MF";

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public ResponseDTO executeSqlFile(String latestVersion, String previousVersion) {
		ResponseDTO responseDTO = new ResponseDTO();

		
		URL resource = this.getClass().getResource("/sql/" + latestVersion + "/");
		if (resource != null) {

			File sqlFile = getSqlFile(resource.getPath());

			// execute sql file
			try {

				runSqlFile(sqlFile);

			} catch (RuntimeException | IOException  runtimeException) {

				try {
					File rollBackFile = getSqlFile(
							this.getClass().getResource("/sql/" + latestVersion + "_rollback/").getPath());

					if (rollBackFile.exists()) {
						runSqlFile(rollBackFile);
					}
				} catch (RuntimeException | IOException  exception) {
					// Prepare Error Response
					setErrorResponse(responseDTO, RegistrationConstants.SQL_EXECUTION_FAILURE, null);

				}
				// Prepare Error Response
				setErrorResponse(responseDTO, RegistrationConstants.SQL_EXECUTION_FAILURE, null);

				// Replace with backup
				rollbackSetup(responseDTO, previousVersion);

			}
		}

		else {
			// Update global param with current version
			globalParamService.update(RegistrationConstants.SERVICES_VERSION_KEY, latestVersion);
			setSuccessResponse(responseDTO, "Updated Version", null);

		}

		return responseDTO;
	}

	

	private File getSqlFile(String path) {

		// Get File
		return FileUtils.getFile(path);

	}

	private void runSqlFile(File sqlFile) throws IOException {

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

						jdbcTemplate.execute(stat);
						
					}
				}

			}

		}

	}

	private void rollbackSetup(ResponseDTO responseDTO, String previousVersion) {
		File file = FileUtils.getFile(FilenameUtils.getFullPath(backUpPath), FilenameUtils.getName(backUpPath));

		boolean isBackUpCompleted = false;
		for (File backUpFolder : file.listFiles()) {
			if (backUpFolder.getName().contains(previousVersion)) {

				try {
					FileUtils.copyDirectory(
							FileUtils.getFile(backUpFolder.getAbsolutePath(), FilenameUtils.getName(binFolder)),
							FileUtils.getFile(FilenameUtils.getName(binFolder)));
					FileUtils.copyDirectory(
							FileUtils.getFile(backUpFolder.getAbsolutePath(), FilenameUtils.getName(libFolder)),
							FileUtils.getFile(FilenameUtils.getName(libFolder)));
					FileUtils.copyFile(
							FileUtils.getFile(backUpFolder.getAbsolutePath(), FilenameUtils.getName(manifestFile)),
							FileUtils.getFile(FilenameUtils.getName(manifestFile)));

					isBackUpCompleted = true;
					setErrorResponse(responseDTO, RegistrationConstants.BACKUP_PREVIOUS_SUCCESS, null);
				} catch (io.mosip.kernel.core.exception.IOException exception) {
					setErrorResponse(responseDTO, RegistrationConstants.BACKUP_PREVIOUS_FAILURE, null);
				}
				break;

			}
		}

		if (!isBackUpCompleted) {
			setErrorResponse(responseDTO, RegistrationConstants.BACKUP_PREVIOUS_FAILURE, null);
		}
	}

}
