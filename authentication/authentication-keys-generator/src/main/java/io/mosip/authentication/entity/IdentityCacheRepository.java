package io.mosip.authentication.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository class for Identity Cache table
 * 
 * @author Loganathan Sekar
 *
 */
@Repository
public interface IdentityCacheRepository extends JpaRepository<IdentityEntity, String> {
	
	default IdentityEntity findById(String id, boolean isBio) {
		if (isBio) {
			return getOne(id);
		} else {
			Object[] demoDataById = findDemoDataById(id).get(0);
			IdentityEntity entity = new IdentityEntity();
			entity.setId(String.valueOf(demoDataById[0]));
			entity.setDemographicData((byte[]) demoDataById[1]);
			entity.setExpiryTimestamp(LocalDateTime.parse(String.valueOf(demoDataById[2])));
			entity.setTransactionLimit(Objects.nonNull(demoDataById[3]) ? Integer.parseInt(String.valueOf(demoDataById[3])) : null);
			return entity;
		}
	}

	@Query("SELECT i.id, i.demographicData, i.expiryTimestamp, i.transactionLimit, i.crBy, i.crDTimes, "
			+ "i.updBy, i.updDTimes, i.isDeleted, i.delDTimes FROM IdentityEntity i where i.id = :id")
	List<Object[]> findDemoDataById(@Param("id") String id);
}
