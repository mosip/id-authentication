package io.mosip.authentication.common.service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.mosip.authentication.common.service.entity.OIDCClientData;

/**
 * The Interface OIDCClientData.
 *
 * @author Mahammed Taheer
 */

@Repository
public interface OIDCClientDataRepository extends JpaRepository<OIDCClientData, String> {

    Optional<OIDCClientData> findByClientId(String clientId);
}
