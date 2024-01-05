package io.mosip.authentication.common.service.repository;

import static io.mosip.authentication.core.constant.IdAuthCommonConstants.OIDC_CLIENT_DATA;

import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.mosip.authentication.common.service.entity.OIDCClientData;

/**
 * The Interface OIDCClientData.
 *
 * @author Mahammed Taheer
 */

@Repository
public interface OIDCClientDataRepository extends JpaRepository<OIDCClientData, String> {

    @Cacheable(value = OIDC_CLIENT_DATA,  unless ="#result == null")
    @Query("select oi from OIDCClientData oi where oi.clientId = :clientId")
    Optional<OIDCClientData> findByClientId(@Param("clientId") String clientId);
}
