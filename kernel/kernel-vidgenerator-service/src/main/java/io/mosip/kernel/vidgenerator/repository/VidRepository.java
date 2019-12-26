package io.mosip.kernel.vidgenerator.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.mosip.kernel.vidgenerator.entity.VidEntity;

public interface VidRepository extends JpaRepository<VidEntity, String> {

	@Query(value = "select v.vid,v.vid_status,v.expiry_dtimes,v.cr_by, v.cr_dtimes, v.del_dtimes, v.is_deleted, v.upd_by, v.upd_dtimes from kernel.vid v where v.vid_status=? limit 1", nativeQuery = true)
	VidEntity findFirstByStatus(String status);

	long countByStatusAndIsDeletedFalse(String status);

	List<VidEntity> findByStatusAndIsDeletedFalse(String status);

	@Modifying
	@Query(value = "UPDATE kernel.vid SET vid_status=:status, upd_by=:contextUser, upd_dtimes=:uptimes where vid=:vid", nativeQuery = true)
	void updateVid(@Param("status") String status, @Param("contextUser") String contextUser,
			@Param("uptimes") LocalDateTime uptimes, @Param("vid") String vid);
}
