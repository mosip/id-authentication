package io.mosip.kernel.otpmanagerservice.repository;

import io.mosip.kernel.core.spi.dataaccess.repository.BaseRepository;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.otpmanagerservice.entity.OtpEntity;

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
