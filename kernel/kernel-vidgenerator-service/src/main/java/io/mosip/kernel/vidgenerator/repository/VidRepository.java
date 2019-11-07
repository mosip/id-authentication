package io.mosip.kernel.vidgenerator.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import io.mosip.kernel.vidgenerator.entity.VidEntity;

public interface VidRepository extends JpaRepository<VidEntity, String> {
	
	VidEntity findFirstByStatus(String status);
	
	long countByStatusAndIsDeletedFalse(String status);
	
	List<VidEntity> findByStatusAndIsDeletedFalse(String status);
}
