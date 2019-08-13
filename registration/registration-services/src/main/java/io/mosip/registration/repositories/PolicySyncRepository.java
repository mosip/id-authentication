package io.mosip.registration.repositories;

import java.util.List;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.KeyStore;

/**
 * Interface for {@link KeyStore}
 * 
 * @author Brahmananda Reddy
 * @since 1.0.0
 *
 */
public interface PolicySyncRepository extends BaseRepository<KeyStore, String> {

	KeyStore findFirst1ByOrderByValidTillDtimesDesc();

	List<KeyStore> findByRefIdOrderByValidTillDtimesDesc(String refId);

}
