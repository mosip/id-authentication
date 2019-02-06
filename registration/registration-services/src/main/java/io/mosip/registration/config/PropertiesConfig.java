package io.mosip.registration.config;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Load properties from DB
 * 
 * @author Omsai Eswar M.
 *
 */
public class PropertiesConfig {
	
	private static final String GLOBAL_PARAM_PROPERTIES = 
			"SELECT NAME, VAL from MASTER.GLOBAL_PARAM where IS_ACTIVE=TRUE";
	private String KEY = "NAME";
	private String VALUE= "VAL";

	private JdbcTemplate jdbcTemplate;
	
	public PropertiesConfig() {
		
	}
	
	public PropertiesConfig(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public Map<String,Object> getDBProps() {
		return jdbcTemplate.query("select NAME, VAL from MASTER.GLOBAL_PARAM where IS_ACTIVE=true", 
				new ResultSetExtractor<Map<String,Object>>(){
		    @Override
		    public Map<String,Object> extractData(ResultSet rs) throws SQLException {
		        Map<String,Object> mapRet= new HashMap<>();
		        while(rs.next()){
		            mapRet.put(rs.getString(KEY),rs.getString(VALUE));
		        }
		        return mapRet;
		    }
		});
	}
}
