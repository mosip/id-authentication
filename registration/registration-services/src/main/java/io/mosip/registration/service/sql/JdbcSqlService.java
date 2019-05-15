package io.mosip.registration.service.sql;

import io.mosip.registration.dto.ResponseDTO;

/**
 * Execute Sql files service management
 * 
 * @author YASWANTH S
 * @since 1.0.0
 *
 */
public interface JdbcSqlService {

	/**
	 * Execute sql file
	 * 
	 * @param version
	 *            services version
	 * @return Response of sql update
	 */
	public ResponseDTO executeSqlFile(String version);
}
