package io.mosip.authentication.common.service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.mosip.authentication.common.service.entity.PartnerData;

public interface PartnerDataRepository extends JpaRepository<PartnerData, String> {

    Optional<PartnerData> findByPartnerId(String partnerId);
}
