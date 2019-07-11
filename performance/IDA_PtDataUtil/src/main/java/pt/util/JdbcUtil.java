package pt.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcUtil {

	public JdbcUtil() {

	}

	public static String fetchVidForUin(String uin) {
		String vid = "";
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Connection connection = null;
		try {
			connection = DriverManager.getConnection("jdbc:postgresql://104.211.228.46:9001/mosip_ida", "idauser",
					"Mosip@dev123");
			String query = "select id from ida.vid where uin=?";
			PreparedStatement pst = connection.prepareStatement(query);
			pst.setString(1, uin);
			ResultSet resultSet = pst.executeQuery();
			while (resultSet.next()) {
				vid = resultSet.getString(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return vid;
	}

}
