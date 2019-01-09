package io.mosip.registration.repositories;

import java.util.List;
import java.util.Set;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.dao.AppRolePriorityDetails;
import io.mosip.registration.entity.AppRolePriority;
import io.mosip.registration.entity.AppRolePriorityId;

/**
 * The repository interface for {@link AppRolePriority} entity
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */
public interface AppRolePriorityRepository extends BaseRepository<AppRolePriority, AppRolePriorityId>{
	
	List<AppRolePriorityDetails> findByAppRolePriorityIdProcessNameAndAppRolePriorityIdRoleCodeInOrderByPriority(String processName, Set<String> roleList);

}
