package io.mosip.idrepository.identity.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.mosip.idrepository.identity.entity.UinBiometric;

/**
 * The Interface UinBiometricRepo.
 *
 * @author Manoj SP
 */
public interface UinBiometricRepo extends JpaRepository<UinBiometric, String> {
}
