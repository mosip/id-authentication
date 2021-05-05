package io.mosip.authentication.common.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.mosip.authentication.common.service.entity.ApiKeyData;

public interface ApiKeyDataRepository extends JpaRepository<ApiKeyData, String> {

}
