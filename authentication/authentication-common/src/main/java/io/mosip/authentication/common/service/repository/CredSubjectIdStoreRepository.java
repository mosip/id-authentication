package io.mosip.authentication.common.service.repository;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.mosip.authentication.common.service.entity.CredSubjectIdStore;

/**
 * The Interface CredSubjectIdStoreRepository.
 *
 * @author Mahammed Taheer
 */

@Repository
public interface CredSubjectIdStoreRepository extends JpaRepository<CredSubjectIdStore, String> {

    List<CredSubjectIdStore> findAllByCsidKeyHash(String keyHash);
}
