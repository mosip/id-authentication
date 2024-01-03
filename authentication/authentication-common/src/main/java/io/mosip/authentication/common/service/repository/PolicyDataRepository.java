package io.mosip.authentication.common.service.repository;

import static io.mosip.authentication.core.constant.IdAuthCommonConstants.POLICY_DATA;

import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.mosip.authentication.common.service.entity.PolicyData;

public interface PolicyDataRepository extends JpaRepository<PolicyData, String> {

    @Cacheable(value = POLICY_DATA, unless ="#result == null")
    @Query("select pd from PolicyData pd where pd.policyId = :policyId")
    Optional<PolicyData> findByPolicyId(@Param("policyId") String policyId);

}
