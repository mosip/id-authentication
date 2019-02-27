package io.mosip.registration.test.integrationtest;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.naming.spi.DirStateFactory.Result;


public class DBUtil {

	private static String dbURL = "jdbc:derby:"+System.getProperty("user.dir") +"\\reg;bootPassword=mosip12345";
	private static Connection conn = null;
	private static Statement stmt = null;
	private static Properties prop = loadPropertiesFile();

	public static void createConnection() {
		try {
			Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();
			// Get a connection
			conn = DriverManager.getConnection(dbURL);
			System.out.println(conn.toString());
		} catch (Exception except) {
			except.printStackTrace();
		}
	}

		public static List<String> get_selectQuery(String selectquery) {
	
		boolean status = false;
		List<String> Ids=new ArrayList<String>(100);
		try {
			stmt=conn.createStatement();
			ResultSet results = stmt.executeQuery(selectquery);
                    while(results.next())
            {
                String restName = results.getString(1);
                System.out.println(restName);
                	Ids.add(restName);
            }
            results.close();
            stmt.close();
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return Ids;
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
			input = new FileInputStream("src\\test\\resources\\testData\\DB_Queries\\Queries.properties");
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


	public static void main(String[] args) throws SQLException {
		createConnection();
		
		
	}
}
