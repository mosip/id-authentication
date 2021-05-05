package io.mosip.authentication.common.service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.mosip.authentication.common.service.entity.PartnerMapping;

public interface PartnerMappingRepository extends JpaRepository<PartnerMapping, String> {

	Optional<PartnerMapping> findByPartnerId(String partnerId);
}
