package io.mosip.registration.processor.quality.check.repository;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.spi.dataaccess.repository.BaseRepository;
import io.mosip.registration.processor.quality.check.entity.BaseQcuserEntity;

@Repository
public interface QcuserRegRepositary<T extends BaseQcuserEntity, E> extends BaseRepository<T, E> {

}