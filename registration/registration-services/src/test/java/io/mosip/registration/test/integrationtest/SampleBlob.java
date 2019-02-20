package io.mosip.registration.test.integrationtest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class SampleBlob {
	//static String key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA5DvVf_paK7VP7UIukEXTFN7InT3Nc5HyBK-LmbbpozLemQjo3h8_w0czNx90t_Hxyzijw6pFfq62UFQBa_87oOdZEF_b8N9PCr_eqKkqIU4NwvGnT5Mr62sZ2tPIA_l06BpEZ8OunYus-t-IFAQ1Xn4IPlfGV4oJfhUFaXsZVLAhvS4vbbcPPG8py4kRoJhuP-PdLpsDj3v4DOFDQC6WRvqNw1KLlBKy9xSmIndIHEzfBvU5YnM0MCQkvu7H4XmOqS_bHnrxCZZoeJT1ZHB9H2dlTLZiUsX0xsjsq_kYsZbmUYYas1aHTgL9TC-Jzpiq-9YXcfuIQwZjAZzHaR2xAwIDAQAB";
	  static String key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsHQsTDqdEw0HyjNY_Dllsibv4smAJYlhWPLk0Ir26Fn0vob7qOrtIUOTVIATOEQmZ9xP8onV81ySkkCA88RCC6VEW89FPHy5A1c6M1YbmL5tNAT_zGXhBs0QW8XGzKlnlXbun8ni7ffsMiaAYtzF9FKv4fh1YQh1OmOyRnezBbBPFoFN_SmKvAAsAdNzIpmM6dl-4j-joiVmiotuf-xirSPL5EzGQztX-C2Wn_H1NOinx04Zi8o8SlIhPfGcKzi7TnPRpEXNCmJYqAMzxO65mjEwKUh4dF9_tySa8WOlY94qgE9gie1fxGtxv6Mb-cxqtdwnI4Cu7gXNFO6OdkUfQwIDAQAB";
	
		public static void main(String[] args) {
			insertKey();
			//getKey();
		}
		
		public static void insertKey() {
			try {
				Connection con;
				PreparedStatement pre;
				Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
				con = DriverManager.getConnection("jdbc:derby:src/test/resources/testData/reg;bootPassword=mosip12345", "", "");
				InputStream inputStream = new ByteArrayInputStream(key.getBytes(Charset.forName("UTF-8")));
				
				pre = con.prepareStatement("update reg.key_store set public_key=?");
				pre.setBinaryStream(1, inputStream, (int) key.length());
				int count = pre.executeUpdate();

				con.commit();
				
				
				System.out.println("isUpdated? " + count);

				pre.close();
				con.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		public static void getKey() {
			try {
				Connection con;
				Statement pre;
				Class.forName("org.apache.derby.jdbc.EmbeddedDriver");

				con = DriverManager.getConnection("jdbc:derby:jdbc:derby:src/test/resources/testData/reg"
						+ ";bootPassword=mosip12345", "", "");
				
				InputStream inputStream = new ByteArrayInputStream(key.getBytes(Charset.forName("UTF-8")));
				
				pre = con.createStatement();
				ResultSet resultSet = pre.executeQuery("select public_key from reg.key_store");
				while(resultSet.next()) {
					
					byte[] keyBytes = resultSet.getBytes(1);
					
					String key1 = new String(keyBytes, StandardCharsets.UTF_8);
					
					System.out.println(key1);
					
					System.out.println(key.equals(key1));
				}
				con.commit();
				
				pre.close();
				con.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
}
