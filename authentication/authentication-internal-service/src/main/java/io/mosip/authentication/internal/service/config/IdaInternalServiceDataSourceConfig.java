package io.mosip.authentication.internal.service.config;

/**
 * @author Neha Farheen
 *
 */

import io.mosip.kernel.dataaccess.hibernate.config.HibernateDaoConfig;

import java.util.Map;


//@Configuration
//@EnableJpaRepositories(
//		entityManagerFactoryRef = "entityManagerFactory",
//		basePackages = "io.mosip.kernel.keymanagerservice.repository.*",
//		repositoryBaseClass = HibernateRepositoryImpl.class)
//@EntityScan(
//		basePackages = { "io.mosip.authentication.common.service.entity",
//				"io.mosip.kernel.keymanagerservice.entity" }
//)
public class IdaInternalServiceDataSourceConfig extends HibernateDaoConfig {

	public Map<String, Object> jpaProperties() {
        return super.jpaProperties();
	}
}

