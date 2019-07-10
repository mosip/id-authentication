package io.mosip.registration.config;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.WeakHashMap;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

/**
 * Load properties from DB
 * 
 * @author Omsai Eswar M.
 *
 */
public class PropertiesConfig {
	
	private static final String GLOBAL_PARAM_PROPERTIES = 
			"SELECT CODE, VAL FROM REG.GLOBAL_PARAM WHERE IS_ACTIVE=TRUE AND VAL IS NOT NULL";
	
	private static final String KEY = "CODE";
	private static final String VALUE= "VAL";

	private JdbcTemplate jdbcTemplate;
	
	public PropertiesConfig() {
	}
	
	public PropertiesConfig(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	/**
	 * Fetch all the active global param values from the DB and set it in a map
	 * @return Collection of Global param values
	 */
	public Map<String,Object> getDBProps() {
		return jdbcTemplate.query(GLOBAL_PARAM_PROPERTIES, new ResultSetExtractor<Map<String,Object>>(){
		    @Override
		    public Map<String,Object> extractData(ResultSet globalParamResultset) throws SQLException {
		        Map<String,Object> globalParamProps= new WeakHashMap<>();
		        while(globalParamResultset.next()){
		        	globalParamProps.put(globalParamResultset.getString(KEY),globalParamResultset.getString(VALUE));
		        }
		        return globalParamProps;
		    }
		});
	}	
}
