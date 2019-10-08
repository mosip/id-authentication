/**
 * 
 */
package io.mosip.kernel.masterdata.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.dto.FoundationalTrustProviderDto;
import io.mosip.kernel.masterdata.entity.FoundationalTrustProvider;

/**
 * @author Ramadurai Pandian
 *
 */
@Repository
public interface FoundationalTrustProviderRepository extends BaseRepository<FoundationalTrustProvider,String>
{

	@Query("FROM foundational_trust_provider WHERE name = ?1 and email=?2 and address=?3 and certAlias=?4 and (isDeleted is null or isDeleted =false) AND isActive = true")
	FoundationalTrustProvider findByDetails(String name, String email, String address, String certAlias);

}
