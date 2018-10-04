package io.mosip.registration.processor.status.dao;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.registration.processor.status.entity.RegistrationStatusEntity;
import io.mosip.registration.processor.status.entity.SyncRegistrationEntity;
import io.mosip.registration.processor.status.repositary.RegistrationStatusRepositary;
import io.mosip.registration.processor.status.repositary.SyncRegistrationRepository;


/**
 * The Class RegistrationStatusDao.
 *
 * @author Horteppa
 */
@Component
public class SyncRegistrationDao {

	/** The registration sync status. */
	@Autowired
	SyncRegistrationRepository syncRegistrationRepository;

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
	 *            the registration status entity
	 * @return the sync registration entity
	 */
	public SyncRegistrationEntity save(SyncRegistrationEntity syncRegistrationEntity) {

		return syncRegistrationRepository.save(syncRegistrationEntity);
	}

	/**
	 * Update.
	 *
	 * @param syncRegistrationEntity
	 *            the registration status entity
	 * @return the sync registration entity
	 */
	public SyncRegistrationEntity update(SyncRegistrationEntity syncRegistrationEntity) {

		return syncRegistrationRepository.save(syncRegistrationEntity);
	}

	/**
	 * Find by id.
	 *
	 * @param registrationId
	 *            the enrolment id
	 * @return the sync registration entity
	 */
	public SyncRegistrationEntity findById(String registrationId) {
		Map<String, Object> params = new HashMap<>();
		String className = RegistrationStatusEntity.class.getSimpleName();

		String alias = SyncRegistrationEntity.class.getName().toLowerCase().substring(0, 1);

		String queryStr = SELECT_DISTINCT + alias + FROM + className + EMPTY_STRING + alias + WHERE + alias
				+ ".registrationId=:registrationId" + EMPTY_STRING + AND + EMPTY_STRING + alias + ISACTIVE_COLON
				+ ISACTIVE + EMPTY_STRING + AND + EMPTY_STRING + alias + ISDELETED_COLON + ISDELETED;

		params.put("registrationId", registrationId);
		params.put(ISACTIVE, Boolean.TRUE);
		params.put(ISDELETED, Boolean.FALSE);

		List<SyncRegistrationEntity> syncRegistrationEntityList = syncRegistrationRepository
				.createQuerySelect(queryStr, params);

		return !syncRegistrationEntityList.isEmpty() ? syncRegistrationEntityList.get(0) : null;
	}

	/**
	 * Findbyfiles by threshold.
	 *
	 * @param statusCode
	 *            the status code
	 * @param threshholdTime
	 *            the threshhold time
	 * @return the list
	 */
	public List<SyncRegistrationEntity> findbyfilesByThreshold(String statusCode, int threshholdTime) {
		Map<String, Object> params = new HashMap<>();
		String className = SyncRegistrationEntity.class.getSimpleName();

		String alias = SyncRegistrationEntity.class.getName().toLowerCase().substring(0, 1);
		LocalDateTime localDateTime = LocalDateTime.now();

		LocalDateTime expiredDateTime = localDateTime.minusMinutes(threshholdTime);
		String queryStr = SELECT_DISTINCT + alias + FROM + className + EMPTY_STRING + alias + WHERE + alias
				+ ".statusCode=:statusCode" + EMPTY_STRING + AND + EMPTY_STRING + alias + ".updateDateTime < :date"
				+ EMPTY_STRING + AND + EMPTY_STRING + alias + ISACTIVE_COLON + ISACTIVE + EMPTY_STRING + AND
				+ EMPTY_STRING + alias + ISDELETED_COLON + ISDELETED;

		params.put("statusCode", statusCode);
		params.put("date", expiredDateTime);
		params.put(ISACTIVE, Boolean.TRUE);
		params.put(ISDELETED, Boolean.FALSE);

		return syncRegistrationRepository.createQuerySelect(queryStr, params);
	}

	/**
	 * Gets the registration sync status by status code.
	 *
	 * @param status
	 *            the status
	 * @return the registration sync status by status code
	 */
	public List<SyncRegistrationEntity> getEnrolmentStatusByStatusCode(String status) {

		Map<String, Object> params = new HashMap<>();
		String className = RegistrationStatusEntity.class.getSimpleName();

		String alias = RegistrationStatusEntity.class.getName().toLowerCase().substring(0, 1);

		String queryStr = SELECT_DISTINCT + alias + FROM + className + EMPTY_STRING + alias + WHERE + alias
				+ ".statusCode=:statusCode" + EMPTY_STRING + AND + EMPTY_STRING + alias + ISACTIVE_COLON + ISACTIVE
				+ EMPTY_STRING + AND + EMPTY_STRING + alias + ISDELETED_COLON + ISDELETED;

		params.put("statusCode", status);
		params.put(ISACTIVE, Boolean.TRUE);
		params.put(ISDELETED, Boolean.FALSE);
		return syncRegistrationRepository.createQuerySelect(queryStr, params);
	}

	/**
	 * Gets the by ids.
	 *
	 * @param ids
	 *            the ids
	 * @return the by ids
	 */
	public List<SyncRegistrationEntity> getByIds(List<String> ids) {

		Map<String, Object> params = new HashMap<>();
		String className = SyncRegistrationEntity.class.getSimpleName();

		String alias = SyncRegistrationEntity.class.getName().toLowerCase().substring(0, 1);
		String queryStr = SELECT_DISTINCT + alias + FROM + className + EMPTY_STRING + alias + WHERE + alias
				+ ".registrationId IN :ids" + EMPTY_STRING + AND + EMPTY_STRING + alias + ISACTIVE_COLON + ISACTIVE
				+ EMPTY_STRING + AND + EMPTY_STRING + alias + ISDELETED_COLON + ISDELETED;
		params.put("ids", ids);
		params.put(ISACTIVE, Boolean.TRUE);
		params.put(ISDELETED, Boolean.FALSE);

		return syncRegistrationRepository.createQuerySelect(queryStr, params);
	}

}