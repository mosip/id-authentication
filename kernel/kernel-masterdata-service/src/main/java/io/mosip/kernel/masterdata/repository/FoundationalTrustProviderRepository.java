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

	@Query(value="select ftp from foundational_trust_provider ftp WHERE ftp.name = ?1 and ftp.email=?2 and ftp.address=?3 and ftp.certAlias=?4 and (ftp.isDeleted is null or ftp.isDeleted =false) AND ftp.isActive = true",nativeQuery=true)
	FoundationalTrustProvider findByDetails(String name, String email, String address, String certAlias);

}
