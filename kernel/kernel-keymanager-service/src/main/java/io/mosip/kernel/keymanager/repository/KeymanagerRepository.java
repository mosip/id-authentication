package io.mosip.kernel.keymanager.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.keymanager.entity.AliasMap;

/**
 * This interface extends BaseRepository which provides with the methods for
 * several CRUD operations.
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Repository
public interface KeymanagerRepository extends BaseRepository<AliasMap, String> {

	List<AliasMap> findByApplicationId(String applicationId);

	List<AliasMap> findByApplicationIdAndMachineId(String applicationId);
}
