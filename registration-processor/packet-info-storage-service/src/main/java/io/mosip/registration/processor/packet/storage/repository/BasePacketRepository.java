package io.mosip.registration.processor.packet.storage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.spi.dataaccess.repository.BaseRepository;
import io.mosip.registration.processor.packet.storage.entity.BasePacketEntity;
/**
 * 
 * @author Girish Yarru
 *
 * @param <E>
 * @param <T>
 */
@Repository
public interface BasePacketRepository<E extends BasePacketEntity<?>, T> extends BaseRepository<E, T> {

	@Query("SELECT qcUser FROM QcuserRegistrationIdEntity qcUser WHERE qcUser.id.usrId=:qcuserId")
	public List<E> findByUserId(@Param("qcuserId") String qcuserId);
	
	@Query("SELECT ape,ade FROM ApplicantPhotographEntity ape, ApplicantDemographicEntity ade"
            + " WHERE ade.id.regId=:regId")
    public List<Object[]> getApplicantInfo(@Param("regId") String regId);
}
