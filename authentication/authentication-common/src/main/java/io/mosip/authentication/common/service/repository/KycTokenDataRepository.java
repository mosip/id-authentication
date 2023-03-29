package io.mosip.authentication.common.service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.mosip.authentication.common.service.entity.KycTokenData;

/**
 * The Interface KycTokenDataRepository.
 *
 * @author Mahammed Taheer
 */

@Repository
public interface KycTokenDataRepository extends JpaRepository<KycTokenData, String> {

    Optional<KycTokenData> findByKycToken(String kycToken);
}
