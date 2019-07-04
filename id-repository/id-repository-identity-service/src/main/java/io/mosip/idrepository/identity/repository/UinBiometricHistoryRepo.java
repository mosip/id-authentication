package io.mosip.idrepository.identity.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.mosip.idrepository.identity.entity.UinBiometricHistory;

/**
 * The Interface UinBiometricHistoryRepo.
 *
 * @author Manoj SP
 */
public interface UinBiometricHistoryRepo extends JpaRepository<UinBiometricHistory, String> {

}
