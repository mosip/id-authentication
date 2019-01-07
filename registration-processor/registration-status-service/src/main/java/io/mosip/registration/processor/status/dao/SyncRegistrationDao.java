package io.mosip.registration.processor.status.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import io.mosip.registration.processor.status.entity.SyncRegistrationEntity;
import io.mosip.registration.processor.status.repositary.RegistrationRepositary;
	
/**
 * The Class SyncRegistrationDao.
 *
 * @author Girish Yarru
 */
@Component
public class SyncRegistrationDao {

	/** The registration sync status. */
	@Autowired
	RegistrationRepositary<SyncRegistrationEntity, String> syncRegistrationRepository;

	/** The Constant AND. */
	public static final String AND = "AND";

	/** The Constant EMPTY_STRING. */
	public static final String EMPTY_STRING = " ";

	/** The Constant SELECT_DISTINCT. */
	public static final String SELECT_DISTINCT = "SELECT DISTINCT ";

	/** The Constant FROM. */
	public static final String FROM = " FROM  ";

	/** The Constant WHERE. */
	public static final String WHERE = " WHERE ";

	/** The Constant ISACTIVE. */
	public static final String ISACTIVE = "isActive";

	/** The Constant ISDELETED. */
	public static final String ISDELETED = "isDeleted";

	/** The Constant ISACTIVE_COLON. */
	public static final String ISACTIVE_COLON = ".isActive=:";

	/** The Constant ISDELETED_COLON. */
	public static final String ISDELETED_COLON = ".isDeleted=:";

	/**
	 * Save.
	 *
	 * @param syncRegistrationEntity
	 *            the sync registration entity
	 * @return the sync registration entity
	 */
	public SyncRegistrationEntity save(SyncRegistrationEntity syncRegistrationEntity) {

		return syncRegistrationRepository.save(syncRegistrationEntity);
	}

	/**
	 * Update.
	 *
	 * @param syncRegistrationEntity
	 *            the sync registration entity
	 * @return the sync registration entity
	 */
	public SyncRegistrationEntity update(SyncRegistrationEntity syncRegistrationEntity) {

		return syncRegistrationRepository.save(syncRegistrationEntity);
	}

	/**
	 * Find by id.
	 *
	 * @param registrationId
	 *            the registration id
	 * @return the sync registration entity
	 */
	public SyncRegistrationEntity findById(String registrationId) {
		Map<String, Object> params = new HashMap<>();
		String className = SyncRegistrationEntity.class.getSimpleName();

		String alias = SyncRegistrationEntity.class.getName().toLowerCase().substring(0, 1);

		String queryStr = SELECT_DISTINCT + alias + FROM + className + EMPTY_STRING + alias + WHERE + alias
				+ ".registrationId=:registrationId" + EMPTY_STRING + AND + EMPTY_STRING + alias + ISACTIVE_COLON
				+ ISACTIVE + EMPTY_STRING + AND + EMPTY_STRING + alias + ISDELETED_COLON + ISDELETED;

		params.put("registrationId", registrationId);
		params.put(ISACTIVE, Boolean.TRUE);
		params.put(ISDELETED, Boolean.FALSE);

		List<SyncRegistrationEntity> syncRegistrationEntityList = syncRegistrationRepository.createQuerySelect(queryStr,
				params);

		return !CollectionUtils.isEmpty(syncRegistrationEntityList) ? syncRegistrationEntityList.get(0) : null;
	}

}