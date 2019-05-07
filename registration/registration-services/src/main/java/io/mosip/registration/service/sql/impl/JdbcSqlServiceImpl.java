package io.mosip.registration.service.sql.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Service;

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

	@Override
	public ResponseDTO executeSqlFile(String version) {
		ResponseDTO responseDTO = new ResponseDTO();

		// Get JDBC Connection
		derbyRegConnection = getConnection();

		if (derbyRegConnection != null) {
			executeSqlFile(responseDTO, version);
		} else {
			// Prepare Error Response as unable to esablish connection
			setErrorResponse(responseDTO, "unable to esablish connection", null);
		}
		return responseDTO;
	}

	private Connection getConnection() {

		// Get Connection
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

				try (Statement stmt = derbyRegConnection.createStatement()) {

					for (String stat : statments) {
						if (!stat.trim().equals("")) {

							stmt.executeUpdate(stat);

						}
					}

				}
			}
		}

		/*
		 * // Initialize ScriptRunner ScriptRunner scriptRunner = new
		 * ScriptRunner(derbyRegConnection, false, false);
		 * 
		 * for (File file : sqlFile.listFiles()) { try (Reader reader = new
		 * BufferedReader(new FileReader(file))) { // Execute script
		 * scriptRunner.runScript(reader); } }
		 */

	}

	private void executeSqlFile(ResponseDTO responseDTO, String version) {
		File sqlFile = getSqlFile(this.getClass().getResource("/sql/" + version + "/").getPath());

		if (sqlFile.exists()) {
			// execute sql file
			try {

				runSqlFile(sqlFile);

			} catch (RuntimeException | IOException | SQLException runtimeException) {
				File rollBackFile = getSqlFile(this.getClass().getResource("/sql/" + version + "_rollback/").getPath());

				try {
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
		} else {
			// Update global param with current version
			globalParamService.update(RegistrationConstants.SERVICES_VERSION_KEY, version);
		}
	}

}
