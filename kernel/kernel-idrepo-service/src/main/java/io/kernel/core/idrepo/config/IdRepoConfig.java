package io.kernel.core.idrepo.config;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Manoj SP
 *
 */
@Configuration
@ConfigurationProperties("mosip.idrepo")
public class IdRepoConfig {
    
    @Autowired
    private ObjectMapper mapper;
    
    @Autowired
    private Environment env;

    private Map<String, Map<String, String>> db;
    
    private Map<String, String> status;
    
    private Map<String, String> id;

    public Map<String, Map<String, String>> getDb() {
	return db;
    }

    public void setDb(Map<String, Map<String, String>> db) {
	this.db = db;
    }
    
    public Map<String, String> getStatus() {
        return status;
    }

    public void setStatus(Map<String, String> status) {
        this.status = status;
    }

    public Map<String, String> getId() {
        return id;
    }

    public void setId(Map<String, String> id) {
        this.id = id;
    }

    @PostConstruct
    public void setup() {
	mapper.setDateFormat(new SimpleDateFormat(env.getProperty("datetime.pattern")));
    }
    
    @Bean
    public Map<String, String> id() {
	return Collections.unmodifiableMap(id);
    }
    
    @Bean 
    public Map<String, String> status() {
	return Collections.unmodifiableMap(status);
    }
    
    @Bean
    public RestTemplate restTemplate() {
	return new RestTemplate();
    }

    @Bean
    public Map<String, DataSource> dataSources() {
	Map<String, DataSource> dataSourceMap = db.entrySet().parallelStream()
		.collect(Collectors.toMap(Map.Entry::getKey, value -> buildDataSource(value.getValue())));
	return Collections.unmodifiableMap(dataSourceMap);
    }

    private DataSource buildDataSource(Map<String, String> dataSourceValues) {
	DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
	driverManagerDataSource.setUrl(dataSourceValues.get("url"));
	driverManagerDataSource.setUsername(dataSourceValues.get("username"));
	driverManagerDataSource.setPassword(dataSourceValues.get("password"));
	driverManagerDataSource.setDriverClassName(dataSourceValues.get("driverClassName"));
	return driverManagerDataSource;
    }
}
