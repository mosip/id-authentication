package io.mosip.authentication.common.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.mosip.authentication.common.service.entity.PolicyData;

public interface PolicyDataRepository extends JpaRepository<PolicyData, String> {

}
