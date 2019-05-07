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

	public ResponseDTO executeSqlFile();
}
