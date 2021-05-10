package io.mosip.authentication.common.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.mosip.authentication.common.service.entity.MispLicenseData;

public interface MispLicenseDataRepository extends JpaRepository<MispLicenseData, String> {

}
