package io.mosip.kernel.pridgenerator.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.pridgenerator.entity.PridEntity;

@Repository
public interface PridRepository extends JpaRepository<PridEntity, String> {

	@Query(value = "select v.prid,v.prid_status,v.cr_by, v.cr_dtimes, v.del_dtimes, v.is_deleted, v.upd_by, v.upd_dtimes from kernel.prid v where v.prid_status=? limit 1", nativeQuery = true)
	PridEntity findFirstByStatus(String status);

	long countByStatusAndIsDeletedFalse(String status);

	List<PridEntity> findByStatusAndIsDeletedFalse(String status);

	@Modifying
	@Query(value = "UPDATE kernel.prid SET prid_status=:status, upd_by=:contextUser, upd_dtimes=:uptimes where prid=:prid", nativeQuery = true)
	void updatePrid(@Param("status") String status, @Param("contextUser") String contextUser,
			@Param("uptimes") LocalDateTime uptimes, @Param("prid") String prid);
}
