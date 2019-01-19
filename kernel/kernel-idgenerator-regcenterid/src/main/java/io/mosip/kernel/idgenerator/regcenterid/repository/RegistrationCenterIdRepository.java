package io.mosip.kernel.idgenerator.regcenterid.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.idgenerator.regcenterid.entity.RegistrationCenterId;

/**
 * Repository class for {@link RegistrationCenterId}
 * 
 * @author Sagar Mahaptra
 * @since 1.0.0
 *
 */
@Repository
public interface RegistrationCenterIdRepository extends BaseRepository<RegistrationCenterId, Integer> {
	/**
	 * This method triggers query to find the last generated registration center id.
	 * 
	 * @return the last generated registration center id.
	 */
	@Query(value = "select rc.reg_center_id FROM ids.rcid rc where rc.reg_center_id = (select max(rc.reg_center_id) from ids.rcid rc) ", nativeQuery = true)
	public RegistrationCenterId findMaxRegistrationCenterId();
}
