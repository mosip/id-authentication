package io.mosip.authentication.common.service.repository;

import static io.mosip.authentication.core.constant.IdAuthCommonConstants.PARTNER_API_KEY_DATA;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.PARTNER_API_KEY_POLICY_ID_DATA;

import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.mosip.authentication.common.service.entity.PartnerMapping;

public interface PartnerMappingRepository extends JpaRepository<PartnerMapping, String> {

	@Cacheable(value = PARTNER_API_KEY_DATA, unless ="#result == null")
    @Query("select pm from PartnerMapping pm where pm.partnerId = :partnerId and pm.apiKeyId = :apiKeyId")
	Optional<PartnerMapping> findByPartnerIdAndApiKeyId(@Param("partnerId") String partnerId, @Param("apiKeyId") String apiKeyId);

	@Cacheable(value = PARTNER_API_KEY_POLICY_ID_DATA, unless ="#result == null")
    @Query("select pm from PartnerMapping pm where pm.partnerId = :partnerId and pm.apiKeyId = :apiKeyId and pm.policyId = :policyId")
	Optional<PartnerMapping> findByPartnerIdAndApiKeyIdAndPolicyId(@Param("partnerId") String partnerId, @Param("apiKeyId") String apiKeyId, 
					@Param("policyId") String policyId);
}
