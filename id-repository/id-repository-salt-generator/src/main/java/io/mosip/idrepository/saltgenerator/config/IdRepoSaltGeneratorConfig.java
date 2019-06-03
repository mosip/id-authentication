package io.mosip.idrepository.saltgenerator.config;

import static io.mosip.idrepository.saltgenerator.constant.IdRepoSaltGeneratorConstant.DATASOURCE_DRIVERCLASSNAME;
import static io.mosip.idrepository.saltgenerator.constant.IdRepoSaltGeneratorConstant.DATASOURCE_PASSWORD;
import static io.mosip.idrepository.saltgenerator.constant.IdRepoSaltGeneratorConstant.DATASOURCE_URL;
import static io.mosip.idrepository.saltgenerator.constant.IdRepoSaltGeneratorConstant.DATASOURCE_USERNAME;
import static io.mosip.idrepository.saltgenerator.constant.IdRepoSaltGeneratorConstant.DB_SCHEMA_NAME;
import static io.mosip.idrepository.saltgenerator.constant.IdRepoSaltGeneratorConstant.PACKAGE_TO_SCAN;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import io.mosip.idrepository.saltgenerator.constant.IdRepoSaltGeneratorConstant;
import io.mosip.kernel.core.util.StringUtils;

/**
 * @author Manoj SP
 *
 */
@Configuration
@EnableTransactionManagement
public class IdRepoSaltGeneratorConfig {
	
	@Autowired
	private Environment env;
	
	@Autowired
	private PhysicalNamingStrategyResolver namingResolver;
	
	@Bean
	public BatchConfigurer batchConfig() {
		return new DefaultBatchConfigurer(null) {
			
			@Override
			public void setDataSource(DataSource dataSource) {
				// override to do not set datasource even if a datasource exist.
		        // initialize will use a Map based JobRepository (instead of database)
			}
		};
	}
	
	@Bean
	@Primary
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(dataSource());
		em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		em.setPackagesToScan(PACKAGE_TO_SCAN.getValue());
		em.setJpaPropertyMap(additionalProperties());
		return em;
	}
	
	@Bean
	@Primary
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		String module = IdRepoSaltGeneratorConstant
				.valueOf(StringUtils.upperCase(env.getProperty(DB_SCHEMA_NAME.getValue()))).getValue();
		dataSource.setUrl(env.getProperty(String.format(DATASOURCE_URL.getValue(), module)));
		dataSource.setUsername(env.getProperty(String.format(DATASOURCE_USERNAME.getValue(), module)));
		dataSource.setPassword(env.getProperty(String.format(DATASOURCE_PASSWORD.getValue(), module)));
		dataSource.setDriverClassName(env.getProperty(String.format(DATASOURCE_DRIVERCLASSNAME.getValue(), module)));
		return dataSource;
	}
	
	@Bean
	@Primary
	public JpaTransactionManager jpaTransactionManager(EntityManagerFactory emf) {
		return new JpaTransactionManager(emf);
	}
	
	private Map<String, Object> additionalProperties() {
		Map<String, Object> jpaProperties = new HashMap<>();
		jpaProperties.put("hibernate.implicit_naming_strategy", SpringImplicitNamingStrategy.class.getName());
		jpaProperties.put("hibernate.physical_naming_strategy", namingResolver);
		jpaProperties.replace("hibernate.dialect", "org.hibernate.dialect.PostgreSQL92Dialect");
		return jpaProperties;
	}
	
}
