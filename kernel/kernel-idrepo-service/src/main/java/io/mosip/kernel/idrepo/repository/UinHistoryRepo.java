package io.mosip.kernel.idrepo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.mosip.kernel.idrepo.entity.UinHistory;

/**
 * The Interface UinHistoryRepo.
 *
 * @author Manoj SP
 */
public interface UinHistoryRepo extends JpaRepository<UinHistory, String> {

}
