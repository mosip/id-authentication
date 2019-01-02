package io.mosip.registration.processor.status.dao;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.registration.processor.status.entity.RegistrationStatusEntity;
import io.mosip.registration.processor.status.repositary.RegistrationRepositary;

/**	
 * The Class RegistrationStatusDao.
 *
 * @author Shashank Agrawal
 * @author Jyoti Prakash Nayak
 */
@Component
public class RegistrationStatusDao {

	/** The registration status repositary. */
	@Autowired
	RegistrationRepositary<RegistrationStatusEntity, String> registrationStatusRepositary;

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
	 * @param registrationStatusEntity
	 *            the registration status entity
	 * @return the registration status entity
	 */
	public RegistrationStatusEntity save(RegistrationStatusEntity registrationStatusEntity) {

		return registrationStatusRepositary.save(registrationStatusEntity);
	}

	/**
	 * Update.
	 *
	 * @param registrationStatusEntity
	 *            the registration status entity
	 * @return the registration status entity
	 */
	public RegistrationStatusEntity update(RegistrationStatusEntity registrationStatusEntity) {

		return registrationStatusRepositary.save(registrationStatusEntity);
	}

	/**
	 * Find by id.
	 *
	 * @param enrolmentId
	 *            the enrolment id
	 * @return the registration status entity
	 */
	public RegistrationStatusEntity findById(String enrolmentId) {
		Map<String, Object> params = new HashMap<>();
		String className = RegistrationStatusEntity.class.getSimpleName();

		String alias = RegistrationStatusEntity.class.getName().toLowerCase().substring(0, 1);

		String queryStr = SELECT_DISTINCT + alias + FROM + className + EMPTY_STRING + alias + WHERE + alias
				+ ".id=:registrationId" + EMPTY_STRING + AND + EMPTY_STRING + alias + ISACTIVE_COLON + ISACTIVE
				+ EMPTY_STRING + AND + EMPTY_STRING + alias + ISDELETED_COLON + ISDELETED;

		params.put("registrationId", enrolmentId);
		params.put(ISACTIVE, Boolean.TRUE);
		params.put(ISDELETED, Boolean.FALSE);

		List<RegistrationStatusEntity> registrationStatusEntityList = registrationStatusRepositary
				.createQuerySelect(queryStr, params);

		return !registrationStatusEntityList.isEmpty() ? registrationStatusEntityList.get(0) : null;
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
	public List<RegistrationStatusEntity> findbyfilesByThreshold(String statusCode, int threshholdTime) {
		Map<String, Object> params = new HashMap<>();
		String className = RegistrationStatusEntity.class.getSimpleName();

		String alias = RegistrationStatusEntity.class.getName().toLowerCase().substring(0, 1);
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

		return registrationStatusRepositary.createQuerySelect(queryStr, params);
	}

	/**
	 * Gets the enrolment status by status code.
	 *
	 * @param status
	 *            the status
	 * @return the enrolment status by status code
	 */
	public List<RegistrationStatusEntity> getEnrolmentStatusByStatusCode(String status) {

		Map<String, Object> params = new HashMap<>();
		String className = RegistrationStatusEntity.class.getSimpleName();

		String alias = RegistrationStatusEntity.class.getName().toLowerCase().substring(0, 1);

		String queryStr = SELECT_DISTINCT + alias + FROM + className + EMPTY_STRING + alias + WHERE + alias
				+ ".statusCode=:statusCode" + EMPTY_STRING + AND + EMPTY_STRING + alias + ISACTIVE_COLON + ISACTIVE
				+ EMPTY_STRING + AND + EMPTY_STRING + alias + ISDELETED_COLON + ISDELETED;

		params.put("statusCode", status);
		params.put(ISACTIVE, Boolean.TRUE);
		params.put(ISDELETED, Boolean.FALSE);
		return registrationStatusRepositary.createQuerySelect(queryStr, params);
	}

	/**
	 * Gets the by ids.
	 *
	 * @param ids
	 *            the ids
	 * @return the by ids
	 */
	public List<RegistrationStatusEntity> getByIds(List<String> ids) {

		Map<String, Object> params = new HashMap<>();
		String className = RegistrationStatusEntity.class.getSimpleName();

		String alias = RegistrationStatusEntity.class.getName().toLowerCase().substring(0, 1);
		String queryStr = SELECT_DISTINCT + alias + FROM + className + EMPTY_STRING + alias + WHERE + alias
				+ ".id IN :ids" + EMPTY_STRING + AND + EMPTY_STRING + alias + ISACTIVE_COLON + ISACTIVE + EMPTY_STRING
				+ AND + EMPTY_STRING + alias + ISDELETED_COLON + ISDELETED;
		params.put("ids", ids);
		params.put(ISACTIVE, Boolean.TRUE);
		params.put(ISDELETED, Boolean.FALSE);

		return registrationStatusRepositary.createQuerySelect(queryStr, params);
	}

}