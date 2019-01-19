/**
 * 
 */
package io.mosip.kernel.auditmanager.repository;

import io.mosip.kernel.auditmanager.entity.Audit;
import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;

/**
 * Repository interface with data access and data modification functions on {@link Audit}
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
public interface AuditRepository extends BaseRepository<Audit, Long>{

}
