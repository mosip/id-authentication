package org.mosip.kernel.otpmanagerservice.repository;

import org.mosip.kernel.core.spi.dataaccess.repository.BaseRepository;
import org.mosip.kernel.otpmanagerservice.entity.OtpEntity;

import org.springframework.stereotype.Repository;

/**
 * This interface extends BaseRepository which provides with the methods for
 * several CRUD operations.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@Repository
public interface OtpRepository extends BaseRepository<OtpEntity, String> {
}
