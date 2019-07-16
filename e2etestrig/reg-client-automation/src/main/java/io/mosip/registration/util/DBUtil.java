package io.mosip.registration.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

public class DBUtil {

	// private static String dbURL = "jdbc:derby:" + System.getProperty("user.dir")
	// + "/src/test/resources/testData/reg;bootPassword=mosip12345";

	private static String dbURL = "jdbc:derby:" + System.getProperty("user.dir") + "/reg;bootPassword=mosip12345";
	/** Logger available to subclasses */
	private static Logger logger = Logger.getLogger(DBUtil.class);
	private static Connection conn = null;
	private static Statement stmt = null;
	// private static Properties prop = loadPropertiesFile();

	public static void createConnection() {
		try {
			Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();
			// Get a connection
			logger.info(dbURL);
			conn = DriverManager.getConnection(dbURL);
			logger.info(conn.toString());
		} catch (Exception except) {
			except.printStackTrace();
		}
	}

	public static void closeConnection() {

		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static Properties loadPropertiesFile() {
		Properties prop = new Properties();
		InputStream input = null;
		try {
			logger.info("====  " + System.getProperty("user.dir")
					+ "/src/test/resources/testData/DB_Queries/Queries.properties");
			input = new FileInputStream(
					System.getProperty("user.dir") + "/src/test/resources/testData/DB_Queries/Queries.properties");
			// load a properties file
			prop.load(input);
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return prop;

	}

	public static int updateQuery(String updateQuery) throws SQLException {
		int val = 0;
		createConnection();

		Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
		val = stmt.executeUpdate(updateQuery);

		stmt.close();
		conn.commit();
		conn.close();

		return val;

	}

	public static void updateValueInDB(String val, String Query) {
		createConnection();
		try {
			Query = Query.replace("value", val);
			logger.info("Query==== " + Query);
			PreparedStatement prestmt = conn.prepareStatement(Query);
			int count = prestmt.executeUpdate();
			conn.commit();

			if (count == 1) {
				// globalParamService.getGlobalParams();
				logger.info("Updated AUDIT_LOG_DELETION_CONFIGURED_DAYS to " + val);
			}

			prestmt.close();
			conn.close();

		} catch (Exception e) {
			logger.info("Unable to update database");
		}
	}

	/**
	 * @param query
	 * @return a List of String type, containing Ids
	 * @throws SQLException
	 */
	public static List<String> executeQuery(String query) throws SQLException {
		createConnection();
		ResultSet resultSet = null;
		List<String> ids = new ArrayList<String>();
		try {
			Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			resultSet = stmt.executeQuery(query);

			while (resultSet.next()) {
				ids.add(resultSet.getString(1));
			}
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ids;
	}

	public static String getPreRegIdFromDB() {
		String preRegID = null;
		String query = "select prereg_id from reg.pre_registration_list";
		createConnection();
		ResultSet resultSet = null;
		Statement stmt = null;
		try {
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			resultSet = stmt.executeQuery(query);
			if (resultSet.next()) {
				logger.info("Pre-Registration ID fetched from database");
				preRegID = resultSet.getString("PREREG_ID");
			}
		} catch (SQLException e) {

		} finally {
			try {
				stmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}

		return preRegID;
	}

	public static Set<String> get_selectQuery(String selectquery) {

		createConnection();
		boolean status = false;
		Set<String> Ids = new HashSet<String>();
		try {
			stmt = conn.createStatement();
			ResultSet results = stmt.executeQuery(selectquery);
			while (results.next()) {
				String restName = results.getString(1);
				logger.info(restName);
				Ids.add(restName);
			}
			results.close();
			stmt.close();
			conn.commit();
			conn.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return Ids;
	}
	
	
	
	public static boolean checkRegID(String regID,String selectquery) {

		createConnection();
		boolean status = false;
		Set<String> Ids = new HashSet<String>();
		try {
			stmt = conn.createStatement();
			ResultSet results = stmt.executeQuery(selectquery);
			while (results.next()) {
				String restName = results.getString(1);
				Ids.add(restName);
				
			}
			status=Ids.contains(regID);
			results.close();
			stmt.close();
			conn.commit();
			conn.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return status;
	}

	public static void main(String[] args) throws SQLException {
		createConnection();

	}

}
