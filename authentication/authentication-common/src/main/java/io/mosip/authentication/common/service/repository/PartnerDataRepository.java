package io.mosip.authentication.common.service.repository;

import static io.mosip.authentication.core.constant.IdAuthCommonConstants.PARTNER_DATA;

import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.mosip.authentication.common.service.entity.PartnerData;

public interface PartnerDataRepository extends JpaRepository<PartnerData, String> {

    @Cacheable(value = PARTNER_DATA, unless ="#result == null")
    @Query("select pd from PartnerData pd where pd.partnerId = :partnerId")
    Optional<PartnerData> findByPartnerId(@Param("partnerId") String partnerId);
}
