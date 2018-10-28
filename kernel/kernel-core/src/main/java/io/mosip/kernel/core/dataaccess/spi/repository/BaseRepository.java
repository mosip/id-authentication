package io.mosip.kernel.core.dataaccess.spi.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Central repository marker interface. Captures the domain type to manage as
 * well as the domain type's id type. General purpose is to hold type
 * information as well as being able to discover interfaces that extend this one
 * during classpath scanning for easy Spring bean creation.
 * 
 * @author Dharmesh Khandelwal
 * @author Shashank Agrawal
 * @since 1.0.0
 * 
 * @param <E>
 *            the type of the entity to handle
 * @param <T>
 *            the type of the entity's identifier
 */
@NoRepositoryBean
public interface BaseRepository<E, T> extends JpaRepository<E, T> {

	/**
	 * Make an instance managed and persistent and synchronize the persistence
	 * context to the underlying database.
	 * 
	 * @param entity
	 *            entity instance
	 * @return the managed instance
	 */
	E create(E entity);

	/**
	 * Merge the state of the given entity into the current persistence context.
	 * 
	 * @param entity
	 *            entity instance
	 * @return the managed instance that the state was merged to
	 */
	E update(E entity);

	/**
	 * Find by primary key. Search for an entity of the specified class and primary
	 * key. If the entity instance is contained in the persistence context, it is
	 * returned from there.
	 * 
	 * @param entityClass
	 *            The entityClass
	 * @param id
	 *            primary key
	 * @return the found entity instance or null if the entity does not exist
	 */
	E findById(Class<E> entityClass, T id);

	/**
	 * Find all entities for the specified class
	 * 
	 * @param entityClass
	 *            The entityClass
	 * @return List of all entities found
	 */
	List<E> findAll(Class<E> entityClass);

	/**
	 * Remove the entity instance of the specified primary key
	 * 
	 * @param entityClass
	 *            The entityClass
	 * @param id
	 *            primary key
	 * @return primary key for which entity was removed
	 */
	T delete(Class<E> entityClass, T id);

	/**
	 * Create a SELECT Query for executing a Java Persistence query language
	 * statement and execute the query and return the query results as an untyped
	 * List.
	 * 
	 * @param qlString
	 *            a Java Persistence query string
	 * @param params
	 *            Map of parameters to bind an argument to a named parameter.
	 * @return a list of the results
	 */
	List<E> createQuerySelect(String qlString, Map<String, Object> params);

	/**
	 * Create a SELECT Query for executing a Java Persistence query language
	 * statement and execute the query and return the query results as an untyped
	 * List.
	 * 
	 * @param qlString
	 *            a Java Persistence query string
	 * @param params
	 *            Map of parameters to bind an argument to a named parameter
	 * @param limit
	 *            The maximum number of results to retrieve.
	 * @return a list of the results
	 */
	List<E> createQuerySelect(String qlString, Map<String, Object> params, int limit);

	/**
	 * Create an update or delete Query for executing a Java Persistence query
	 * language statement and execute the statement.
	 * 
	 * @param qlString
	 *            a Java Persistence query string
	 * @param params
	 *            Map of parameters to bind an argument to a named parameter
	 * @return the number of entities updated or deleted
	 */
	int createQueryUpdateOrDelete(String qlString, Map<String, Object> params);

	/**
	 * Create a SELECT TypedQuery for executing a Java Persistence query language
	 * named query. The select list of the query must contain only a single item,
	 * which must be assignable to the type specified by the resultClass argument.
	 * Execute the query and return the query results as an untyped List.
	 * 
	 * @param name
	 *            the name of a query defined in metadata
	 * @param entityClass
	 *            the type of the query result
	 * @param params
	 *            Map of parameters to bind an argument to a named parameter
	 * @return a list of the results
	 */
	List<E> createNamedQuerySelect(String name, Class<E> entityClass, Map<String, Object> params);

	/**
	 * Create a update or delete TypedQuery for executing a Java Persistence query
	 * language named query. The select list of the query must contain only a single
	 * item, which must be assignable to the type specified by the resultClass
	 * argument. Execute the statement.
	 * 
	 * @param name
	 *            the name of a query defined in metadata
	 * @param entityClass
	 *            the type of the query result
	 * @param params
	 *            Map of parameters to bind an argument to a named parameter
	 * @return the number of entities updated or deleted
	 */
	int createNamedQueryUpdateOrDelete(String name, Class<E> entityClass, Map<String, Object> params);

}
