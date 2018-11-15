/**
 * 
 */
package io.mosip.kernel.core.dataaccess.spi.config;

import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.orm.jpa.JpaDialect;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * This class declares the interface for @Bean methods related to data access
 * and will be processed by the Spring container to generate bean definitions
 * and service requests for those beans at runtime
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
public interface BaseDaoConfig {

	/**
	 * A factory for connections to the physical data source that this DataSource
	 * object represents. An alternative to the DriverManager facility, a DataSource
	 * object is the preferred means of getting a connection. An object that
	 * implements the DataSource interface will typically be registered with a
	 * naming service based on the Java™ Naming and Directory (JNDI) API.
	 * 
	 * @return The datasource bean
	 */
	DataSource dataSource();

	/**
	 * {@link FactoryBean} that creates a JPA {@link EntityManagerFactory} according
	 * to JPA's standard container bootstrap contract. This is the most powerful way
	 * to set up a shared JPA EntityManagerFactory in a Spring application context;
	 * the EntityManagerFactory can then be passed to JPA-based DAOs via dependency
	 * injection.
	 * 
	 * @return The LocalContainerEntityManagerFactoryBean
	 */
	LocalContainerEntityManagerFactoryBean entityManagerFactory();

	/**
	 * SPI interface that allows to plug in vendor-specific behavior into Spring's
	 * EntityManagerFactory creators. Serves as single configuration point for all
	 * vendor-specific properties.
	 * 
	 * @return The JpaVendorAdapter bean
	 */
	JpaVendorAdapter jpaVendorAdapter();

	/**
	 * SPI strategy that encapsulates certain functionality that standard JPA 2.1
	 * does not offer, such as access to the underlying JDBC Connection. This
	 * strategy is mainly intended for standalone usage of a JPA provider; most of
	 * its functionality is not relevant when running with JTA transactions.
	 * 
	 * @return The JpaDialect bean
	 */
	JpaDialect jpaDialect();

	/**
	 * This is the central interface in Spring's transaction infrastructure.
	 * Applications can use this directly, but it is not primarily meant as API:
	 * Typically, applications will work with either TransactionTemplate or
	 * declarative transaction demarcation through AOP.
	 * 
	 * @param entityManagerFactory
	 *            The {@link #entityManagerFactory()}
	 * @return The PlatformTransactionManager bean
	 */
	PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory);

	/**
	 * Specify JPA properties as a Map, to be passed into
	 * Persistence.createEntityManagerFactory.
	 * 
	 * @return The Map of JPA properties
	 */
	Map<String, Object> jpaProperties();

}