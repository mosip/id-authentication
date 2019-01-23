package io.mosip.authentication.service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.mosip.authentication.service.entity.AutnTxn;
import io.mosip.authentication.service.entity.StaticPinEntity;
import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
/**
 *  This is a repository class for entity {@link StaticPinEntity}.
 *  
 * @author Prem Kumar
 *
 */
@Repository
public interface StaticPinRepository extends BaseRepository<StaticPinEntity, String> {
	
//	@Query("insert into StaticPinEntity(pin) value(:staticPinValue) where uin=:uinValue")
//	Optional<String> findStaticByUin(@Param("staticPinValue") String staticPinValue,@Param("uinValue") String uin);
}
