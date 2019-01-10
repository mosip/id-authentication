package io.mosip.kernel.idrepo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.mosip.kernel.idrepo.entity.UinBiometric;

/**
 * The Interface UinBiometricRepo.
 *
 * @author Manoj SP
 */
public interface UinBiometricRepo extends JpaRepository<UinBiometric, String> {
}
