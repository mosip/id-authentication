package io.mosip.registration.repositories;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.dto.PolicyDTO;
import io.mosip.registration.entity.KeyStore;

/**
 * 
 * @author Brahmananda Reddy
 * @since 1.0.0
 *
 */
public interface PolicySyncRepository extends BaseRepository<KeyStore, String>{
	
	KeyStore findFirst1ByOrderByValidTillDtimesDesc();
	

}
