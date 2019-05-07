package io.mosip.registration.service.sql.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Service;

import com.ibatis.common.jdbc.ScriptRunner;

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
			File sqlFile = getSqlFile(this.getClass().getResource("/sql/" + version + "/").getPath());

			if (sqlFile.exists()) {
				// execute sql file
				try {

					runSqlFile(sqlFile);

				} catch (IOException | SQLException runtimeException) {
					runtimeException.printStackTrace();
					File rollBackFile = getSqlFile(
							this.getClass().getResource("/sql/" + version + "_rollback/").getPath());

					try {
						runSqlFile(rollBackFile);
					} catch (RuntimeException | IOException | SQLException exception) {

					}
					// Prepare Error Response
					setErrorResponse(responseDTO, "unable to execute sql file", null);
				}
			} else {
				// Update global param with current version
				globalParamService.update(RegistrationConstants.SERVICES_VERSION_KEY, version);
			}
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

		// Initialize ScriptRunner
		ScriptRunner scriptRunner = new ScriptRunner(derbyRegConnection, false, false);

		for (File file : sqlFile.listFiles()) {
			try (Reader reader = new BufferedReader(new FileReader(file))) {
				// Execute script
				scriptRunner.runScript(reader);
			}
		}

	}

}
