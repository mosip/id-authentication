/**
 * 
 */
package org.mosip.kernel.auditmanager.repository;

import org.mosip.kernel.auditmanager.entity.Audit;
import org.mosip.kernel.core.spi.dataaccess.repository.BaseRepository;

/**
 * Repository interface with data access and data modification functions on {@link Audit}
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
public interface AuditRepository extends BaseRepository<Audit, Long>{

}
