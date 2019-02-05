package io.mosip.registration.config;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

/**
 * Load properties from DB
 * 
 * @author Omsai Eswar M.
 *
 */
public class PropertiesConfig {

	private JdbcTemplate jdbcTemplate;
	
	public PropertiesConfig() {
		
	}
	
	public PropertiesConfig(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public Map<String,Object> getDBProps() {
		
		Map<String, Object> globalParams = jdbcTemplate.query("select NAME, VAL from MASTER.GLOBAL_PARAM where IS_ACTIVE=true", 
				new ResultSetExtractor<Map<String,Object>>(){
		    @Override
		    public Map<String,Object> extractData(ResultSet rs) throws SQLException {
		        Map<String,Object> mapRet= new HashMap<>();
		        while(rs.next()){
		            mapRet.put(rs.getString("name"),rs.getString("val"));
		        }
		        return mapRet;
		    }
		});
		
		//System.out.println(globalParams.size());
		return globalParams;
	}

	/*
	@Autowired
	public void setConfigurableEnvironment(ConfigurableEnvironment env) {
		this.env = env;
	}

	@PostConstruct
	public void init() {
		MutablePropertySources propertySources = env.getPropertySources();
		Map<String, Object> dbPropertiesMap = globalParamService.getGlobalParams();

		// from configurationRepository get values and fill map
		propertySources.addFirst(new MapPropertySource("DB_PROPS", dbPropertiesMap));
	}

	@Autowired
	private StandardEnvironment environment;

	@Scheduled(fixedRate = 60000)
	public void reload() {
		MutablePropertySources propertySources = environment.getPropertySources();
		
		PropertySource<?> resourcePropertySource = propertySources.get("DB_PROPS");
		
		Properties properties = new Properties();
		Map<String, Object> dbPropertiesMap = globalParamService.getGlobalParams();
		properties.putAll(dbPropertiesMap);
		
		// from configurationRepository get values and fill map
		propertySources.replace("DB_PROPS",	new PropertiesPropertySource("DB_PROPS", properties));
	}*/
}
