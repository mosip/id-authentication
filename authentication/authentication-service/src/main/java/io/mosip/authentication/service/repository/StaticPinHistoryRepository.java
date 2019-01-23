package io.mosip.authentication.service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.mosip.authentication.service.entity.StaticPinHistoryEntity;
import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
/**
 *  This is a repository class for entity {@link StaticPinHistoryEntity}.
 *  
 * @author Prem Kumar
 *
 */
@Repository
public interface StaticPinHistoryRepository extends BaseRepository<StaticPinHistoryEntity, String> {
//	@Query("insert into StaticPinHistoryEntity(pin) value(:staticPinValue) where uin=:uinValue")
//	Optional<String> findStaticByUin(@Param("staticPinValue") String staticPinValue,@Param("uinValue") String uin);
}
