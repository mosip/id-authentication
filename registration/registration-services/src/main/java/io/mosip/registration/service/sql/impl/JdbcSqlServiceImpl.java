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
	public ResponseDTO executeSqlFile() {
		ResponseDTO responseDTO = new ResponseDTO();
		
		//Get JDBC Connection
		derbyRegConnection = getConnection();
		
		if (derbyRegConnection != null) {
			File sqlFile = getLatestVersionSqlFile();

			if (sqlFile.exists()) {
				// execute sql file
				try {
					runSqlFile(sqlFile);
				} catch (RuntimeException | IOException | SQLException runtimeException) {
					File rollBackFile = getRollBackSqlFile();

					try {
						runSqlFile(rollBackFile);
					} catch (RuntimeException | IOException | SQLException exception) {

					}
					// TODO Prepare Error Response
					setErrorResponse(responseDTO, "unable to execute sql file", null);
				}
			} else {
				// TODO Update global param with current version
				//globalParamService.update(null, null);
			}
		} else {
			// Prepare Error Response as unable to esablish connection
			setErrorResponse(responseDTO, "unable to esablish connection", null);
		}
		return null;
	}

	private File getRollBackSqlFile() {
		// TODO Auto-generated method stub
		return null;
	}

	private Connection getConnection() {

		return DataSourceUtils.getConnection((DataSource) applicationContext.getBean("dataSource"));

	}

	private File getLatestVersionSqlFile() {
		// TODO Auto-generated method stub
		return new File("C:\\Users\\M1044402\\Desktop\\dummySql.sql");
	}

	private void runSqlFile(File sqlFile) throws IOException, SQLException {

		// Initialize object for ScripRunner
		ScriptRunner sr = new ScriptRunner(derbyRegConnection, false, false);

		try (Reader reader = new BufferedReader(new FileReader(sqlFile))) {
			// Exctute script
			sr.runScript(reader);
		}

	}

}
