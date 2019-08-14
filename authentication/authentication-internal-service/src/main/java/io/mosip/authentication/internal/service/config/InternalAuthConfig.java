package io.mosip.authentication.internal.service.config;


import java.util.Map;

import javax.annotation.PostConstruct;

import org.hibernate.Interceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import io.mosip.kernel.dataaccess.hibernate.config.HibernateDaoConfig;

/**
 * Class for defining configurations for the service.
 * 
 * @author Manoj SP
 *
 */
@Configuration
public class InternalAuthConfig extends HibernateDaoConfig {
	
	/**
	 * Ida transaction interceptor.
	 */
	@PostConstruct
	public void idaTransactionInterceptor() {
		System.err.println("IDA*******Tranaction config");
	}
	
	/** The interceptor. */
	@Autowired
	private Interceptor interceptor;

	/* (non-Javadoc)
	 * @see io.mosip.kernel.dataaccess.hibernate.config.HibernateDaoConfig#jpaProperties()
	 */
	@Override
	public Map<String, Object> jpaProperties() {
		Map<String, Object> jpaProperties = super.jpaProperties();
		jpaProperties.put("hibernate.ejb.interceptor", interceptor);
		return jpaProperties;
	}

	
//	/**
//	 * Entity manager factory.
//	 *
//	 * @param dataSource the data source
//	 * @return the local container entity manager factory bean
//	 */
//	@Bean
//	public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
//		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
//		em.setDataSource(dataSource);
//		em.setPackagesToScan("io.mosip.authentication.service.*");
//		JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
//		em.setJpaVendorAdapter(vendorAdapter);
//		em.setJpaPropertyMap(jpaProperties());
//		return em;
//	}
//
//	/**
//	 * Additional properties.
//	 *
//	 * @return the properties
//	 */
//	private Map<String, Object> additionalProperties() {
//		Map<String, Object> jpaProperties = new HashMap<>();
//		jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQL92Dialect");
//		jpaProperties.put("hibernate.temp.use_jdbc_metadata_defaults", Boolean.FALSE);
//		jpaProperties.put("hibernate.implicit_naming_strategy", SpringImplicitNamingStrategy.class.getName());
//		jpaProperties.put("hibernate.physical_naming_strategy", SpringPhysicalNamingStrategy.class.getName());
//		jpaProperties.put("hibernate.ejb.interceptor", interceptor);
//		return jpaProperties;
//	}
	
}
