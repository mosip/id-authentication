package io.mosip.idrepository.identity.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.mosip.idrepository.identity.entity.UinDocument;

/**
 * The Interface UinDocumentRepo.
 *
 * @author Manoj SP
 */
public interface UinDocumentRepo extends JpaRepository<UinDocument, String> {
}
