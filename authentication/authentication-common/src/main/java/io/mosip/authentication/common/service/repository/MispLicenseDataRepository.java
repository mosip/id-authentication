package io.mosip.authentication.common.service.repository;

import static io.mosip.authentication.core.constant.IdAuthCommonConstants.MISP_LIC_DATA;

import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.mosip.authentication.common.service.entity.MispLicenseData;

public interface MispLicenseDataRepository extends JpaRepository<MispLicenseData, String> {
	
	@Cacheable(value = MISP_LIC_DATA,  unless ="#result == null")
    @Query("select ml from MispLicenseData ml where ml.licenseKey = :licenseKey")
	Optional<MispLicenseData> findByLicenseKey(@Param("licenseKey") String licenseKey);

}
