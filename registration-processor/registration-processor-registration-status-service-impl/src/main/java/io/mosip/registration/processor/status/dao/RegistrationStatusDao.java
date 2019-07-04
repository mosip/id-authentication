package io.mosip.registration.processor.status.dao;

import java.time.LocalDateTime;
import java.util.Arrays;
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

	/** The Constant SELECT_DISTINCT. */
	public static final String SELECT = "SELECT ";

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

	public static final String SELECT_COUNT = "SELECT COUNT(*)";

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

	/**
	 * Gets the un processed packets.
	 *
	 * @param fetchSize
	 *            the fetch size
	 * @param elapseTime
	 *            the elapse time
	 * @param reprocessCount
	 *            the reprocess count
	 * @param status
	 *            the status
	 * @return the un processed packets
	 */
	public List<RegistrationStatusEntity> getUnProcessedPackets(Integer fetchSize, long elapseTime,
			Integer reprocessCount, List<String> status) {

		Map<String, Object> params = new HashMap<>();
		String className = RegistrationStatusEntity.class.getSimpleName();
		String alias = RegistrationStatusEntity.class.getName().toLowerCase().substring(0, 1);
		LocalDateTime timeDifference = LocalDateTime.now().minusSeconds(elapseTime);

		String queryStr = SELECT_DISTINCT + alias + FROM + className + EMPTY_STRING + alias + WHERE + alias
				+ ".latestTransactionStatusCode IN :status" + EMPTY_STRING + AND + EMPTY_STRING + alias
				+ ".regProcessRetryCount<=" + ":reprocessCount" + EMPTY_STRING + AND + EMPTY_STRING + alias
				+ ".latestTransactionTimes<" + ":timeDifference";

		params.put("status", status);
		params.put("reprocessCount", reprocessCount);
		params.put("timeDifference", timeDifference);

		return registrationStatusRepositary.createQuerySelect(queryStr, params, fetchSize);
	}

	public Integer getUnProcessedPacketsCount(long elapseTime, Integer reprocessCount, List<String> status) {

		Map<String, Object> params = new HashMap<>();
		String className = RegistrationStatusEntity.class.getSimpleName();
		String alias = RegistrationStatusEntity.class.getName().toLowerCase().substring(0, 1);
		LocalDateTime timeDifference = LocalDateTime.now().minusSeconds(elapseTime);

		String queryStr = SELECT_DISTINCT + alias + FROM + className + EMPTY_STRING + alias + WHERE + alias
				+ ".latestTransactionStatusCode IN :status" + EMPTY_STRING + AND + EMPTY_STRING + alias
				+ ".regProcessRetryCount<=" + ":reprocessCount" + EMPTY_STRING + AND + EMPTY_STRING + alias
				+ ".latestTransactionTimes<" + ":timeDifference";

		params.put("status", status);
		params.put("reprocessCount", reprocessCount);
		params.put("timeDifference", timeDifference);
		List<RegistrationStatusEntity> unprocessedPackets = registrationStatusRepositary.createQuerySelect(queryStr,
				params);

		return unprocessedPackets.size();

	}

	public Boolean checkUinAvailabilityForRid(String rid) {
		Boolean uinAvailable = false;
		Map<String, Object> params = new HashMap<>();
		String className = RegistrationStatusEntity.class.getSimpleName();
		String alias = RegistrationStatusEntity.class.getName().toLowerCase().substring(0, 1);

		String queryStr = SELECT + alias + FROM + className + EMPTY_STRING + alias + WHERE + alias + ".id = :rid " + AND
				+ " " + alias + ".statusCode = :status_Code";
		params.put("rid", rid);
		params.put("status_Code", "PROCESSED");
		List<RegistrationStatusEntity> unprocessedPackets = registrationStatusRepositary.createQuerySelect(queryStr,
				params);
		if (!unprocessedPackets.isEmpty()) {
			uinAvailable = true;
		}
		return uinAvailable;

	}

}