package io.mosip.authentication.fw.dbUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
//import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;

import io.mosip.authentication.fw.util.RunConfig;


/**
 * DB Connection and perform query operation for ida automation utility
 * 
 * @author Vignesh
 *
 */
public class DbConnection {
	private static Logger logger = Logger.getLogger(DbConnection.class);

	/**
	 * Kernel db connection to get generated otp value
	 * 
	 * @return dbConnection
	 */
	public Connection getKernelDbConnection() {
		try {
			Class.forName("org.postgresql.Driver");
			Connection connection = DriverManager.getConnection(
					RunConfig.getDbUrl() + "/" + RunConfig.getDbKernelTableName(), RunConfig.getDbKernelUserName(),
					RunConfig.getDbKernelPwd());
			return connection;
		} catch (Exception e) {
			logger.error("Execption in db connection: " + e);
			return null;
		}
	}
	
	/**
	 * Ida db connection to get generated otp value
	 * 
	 * @return dbConnection
	 */
	public Connection getIdaDbConnection() {
		try {
			Class.forName("org.postgresql.Driver");
			Connection connection = DriverManager.getConnection(
					RunConfig.getDbUrl() + "/" + RunConfig.getDbIdaTableName(), RunConfig.getDbIdaUserName(),
					RunConfig.getDbIdaPwd());
			return connection;
		} catch (Exception e) {
			logger.error("Execption in db connection: " + e);
			return null;
		}
	}
	
	/**
	 * Ida db connection to get generated otp value
	 * 
	 * @return dbConnection
	 */
	public Connection getAuditDbConnection() {
		try {
			Class.forName("org.postgresql.Driver");
			Connection connection = DriverManager.getConnection(
					RunConfig.getDbUrl() + "/" + RunConfig.getDbAuditTableName(), RunConfig.getDbAuditUserName(),
					RunConfig.getDbAuditPwd());
			return connection;
		} catch (Exception e) {
			logger.error("Execption in db connection: " + e);
			return null;
		}
	}
	
	/**
	 * Execute query to get generated otp value
	 * 
	 * @param query
	 * @param moduleName
	 * @return otp record
	 */
	public Map<String, String> getDataForQuery(String query, String moduleName) {
		Statement stmt = null;
		try {
			if (moduleName.equals("KERNEL"))
				stmt = getKernelDbConnection().createStatement();
			else if (moduleName.equals("IDA"))
				stmt = getIdaDbConnection().createStatement();
			else if (moduleName.equals("AUDIT"))
				stmt = getAuditDbConnection().createStatement();
			ResultSet rs = stmt.executeQuery(query);
			ResultSetMetaData md = rs.getMetaData();
			int columns = md.getColumnCount();
			Map<String, Object> row = new HashMap<String, Object>();
			while (rs.next()) {
				for (int i = 1; i <= columns; i++) {
					row.put(md.getColumnName(i), rs.getObject(i));
				}
			}
			stmt.close();
			Map<String, String> returnMap = new HashMap<String, String>();
			for (Entry<String, Object> entry : row.entrySet()) {
				if (entry.getValue().toString().equals(null) || entry.getValue().toString() == null)
					returnMap.put(entry.getKey(), "null");
				else
					returnMap.put(entry.getKey(), entry.getValue().toString());
			}
			return returnMap;
		} catch (Exception e) {
			logger.error("Execption in execution statement: " + e);
			return null;
		}
	}
}
