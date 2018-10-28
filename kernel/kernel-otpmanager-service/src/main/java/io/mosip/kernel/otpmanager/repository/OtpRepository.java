package io.mosip.kernel.otpmanager.repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.otpmanager.entity.OtpEntity;

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
