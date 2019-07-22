package io.mosip.authentication.fw.util;

import java.sql.Connection;    
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
 

/**
 * DB Connection and perform query operation for ida automation utility
 * 
 * @author Vignesh
 *
 */
public class DbConnection {
	private static final Logger DBCONNECTION_LOGGER = Logger.getLogger(DbConnection.class);

	/**
	 * Kernel db connection to get generated otp value
	 * 
	 * @return dbConnection
	 */
	public static Connection getKernelDbConnection() {
		try {
			Class.forName("org.postgresql.Driver");
			Connection connection = DriverManager.getConnection(
					RunConfigUtil.objRunConfig.getDbKernelUrl() + "/" + RunConfigUtil.objRunConfig.getDbKernelTableName(), RunConfigUtil.objRunConfig.getDbKernelUserName(),
					RunConfigUtil.objRunConfig.getDbKernelPwd());
			return connection;
		} catch (Exception e) {
			DBCONNECTION_LOGGER.error("Execption in db connection: " + e);
			return null;
		}
	}
	
	/**
	 * Ida db connection
	 * 
	 * @return dbConnection
	 */
	public static Connection getIdaDbConnection() {
		try {
			Class.forName("org.postgresql.Driver");
			Connection connection = DriverManager.getConnection(
					RunConfigUtil.objRunConfig.getDbIdaUrl() + "/" + RunConfigUtil.objRunConfig.getDbIdaTableName(), RunConfigUtil.objRunConfig.getDbIdaUserName(),
					RunConfigUtil.objRunConfig.getDbIdaPwd());
			return connection;
		} catch (Exception e) {
			DBCONNECTION_LOGGER.error("Execption in db connection: " + e);
			return null;
		}
	}
	
	/**
	 * Audit db connection
	 * 
	 * @return dbConnection
	 */
	public static Connection getAuditDbConnection() {
		try {
			Class.forName("org.postgresql.Driver");
			Connection connection = DriverManager.getConnection(
					RunConfigUtil.objRunConfig.getDbAuditUrl() + "/" + RunConfigUtil.objRunConfig.getDbAuditTableName(), RunConfigUtil.objRunConfig.getDbAuditUserName(),
					RunConfigUtil.objRunConfig.getDbAuditPwd());
			return connection;
		} catch (Exception e) {
			DBCONNECTION_LOGGER.error("Execption in db connection: " + e);
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
	
	public static Map<String, String> getDataForQuery(String query, String moduleName) {
		Statement stmt = null;
		try {
			if (moduleName.equals("KERNEL"))
				stmt = getKernelDbConnection().createStatement();
			else if (moduleName.equals("IDA"))
				stmt = getIdaDbConnection().createStatement();
			else if (moduleName.equals("AUDIT"))
				stmt = getAuditDbConnection().createStatement();
			else if (moduleName.equals("IDREPO"))
				stmt = getIdrepoDbConnection().createStatement();
			DBCONNECTION_LOGGER.info("Query: " +query);
			if (query.toLowerCase().startsWith("delete".toLowerCase())) {
				stmt.executeUpdate(query);
				Map<String, String> returnMap = new HashMap<String, String>();
				returnMap.put(query, "true");
				return returnMap;
			} else {
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
			}
		} catch (Exception e) {
			DBCONNECTION_LOGGER.error("Execption in execution statement: " + e);
			return null;
		}
	}
	
	/**
	 * Idrepo db connection
	 * 
	 * @return dbConnection
	 */
	public static Connection getIdrepoDbConnection() {
		try {
			Class.forName("org.postgresql.Driver");
			Connection connection = DriverManager.getConnection(
					RunConfigUtil.objRunConfig.getDbIdrepoUrl() + "/"
							+ RunConfigUtil.objRunConfig.getDbIdrepoTableName(),
					RunConfigUtil.objRunConfig.getDbIdrepoUserName(), RunConfigUtil.objRunConfig.getDbIdrepoPwd());
			return connection;
		} catch (Exception e) {
			DBCONNECTION_LOGGER.error("Execption in db connection: " + e);
			return null;
		}
	}
}
