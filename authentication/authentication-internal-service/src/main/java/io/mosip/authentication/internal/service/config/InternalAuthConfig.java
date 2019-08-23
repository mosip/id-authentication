package io.mosip.authentication.internal.service.config;

import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.INTERNAL_ALLOWED_AUTH_TYPE;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import io.mosip.authentication.common.service.config.IdAuthConfig;
import io.mosip.authentication.common.service.impl.match.BioAuthType;

/**
 * Class for defining configurations for the service.
 * 
 * @author Manoj SP
 *
 */
@Configuration
public class InternalAuthConfig extends IdAuthConfig {
	
	/**
	 * Ida transaction interceptor.
	 */
	@PostConstruct
	public void idaTransactionInterceptor() {
		System.err.println("IDA*******Tranaction config");
	}
	
	@Autowired
	protected Environment environment;


	protected boolean isFingerAuthEnabled() {
		return (environment.getProperty(INTERNAL_ALLOWED_AUTH_TYPE).contains(BioAuthType.FGR_MIN.getConfigKey())
				|| environment.getProperty(INTERNAL_ALLOWED_AUTH_TYPE).contains(BioAuthType.FGR_IMG.getConfigKey()));
	}
	
	protected boolean isFaceAuthEnabled() {
		return environment.getProperty(INTERNAL_ALLOWED_AUTH_TYPE).contains(BioAuthType.FACE_IMG.getConfigKey());
	}
	
	protected boolean isIrisAuthEnabled() {
		return environment.getProperty(INTERNAL_ALLOWED_AUTH_TYPE).contains(BioAuthType.IRIS_IMG.getConfigKey());
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
