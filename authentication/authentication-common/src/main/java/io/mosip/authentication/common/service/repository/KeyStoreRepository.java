package io.mosip.authentication.common.service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.mosip.authentication.common.service.entity.KeyStore;


/**
 * This interface extends BaseRepository which provides with the methods for
 * several CRUD operations.
 * 
 * @author Nagarjuna
 * @since 1.0.0
 *
 */
@Repository
public interface KeyStoreRepository extends JpaRepository<KeyStore, String> {

	/**
	 * Function to find KeyStore by alias
	 * 
	 * @param alias alias
	 * @return KeyStore
	 */
	Optional<KeyStore> findByAlias(String alias);

}
