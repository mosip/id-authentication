package io.mosip.registration.repositories.mastersync;

import java.util.List;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.mastersync.MasterReasonList;

/**
 * This class will handle the CRUD operation of reason list
 * 
 * @author Sreekar Chukka
 *
 */
public interface MasterSyncReasonListRepository extends BaseRepository<MasterReasonList, String> {

	List<MasterReasonList> findByLangCodeAndReasonCategoryCodeIn(String langCode, List<String> resonCatog);

}
