package io.mosip.kernel.idrepo.interceptor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.binary.StringUtils;
import org.assertj.core.util.Arrays;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.idrepo.constant.IdRepoErrorConstants;
import io.mosip.kernel.core.idrepo.constant.RestServicesConstants;
import io.mosip.kernel.core.idrepo.exception.IdRepoAppException;
import io.mosip.kernel.core.idrepo.exception.IdRepoAppUncheckedException;
import io.mosip.kernel.core.idrepo.exception.RestServiceException;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.kernel.idrepo.config.IdRepoLogger;
import io.mosip.kernel.idrepo.dto.RestRequestDTO;
import io.mosip.kernel.idrepo.entity.Uin;
import io.mosip.kernel.idrepo.entity.UinHistory;
import io.mosip.kernel.idrepo.factory.RestRequestFactory;
import io.mosip.kernel.idrepo.helper.RestHelper;

/**
 * The Class IdRepoEntityInterceptor.
 *
 * @author Manoj SP
 */
@Component
public class IdRepoEntityInterceptor extends EmptyInterceptor {

	private static final String ID_REPO_ENTITY_INTERCEPTOR = "IdRepoEntityInterceptor";

	private static final String ID_REPO_SERVICE = "IdRepoService";

	private static final String UIN_DATA_HASH = "uinDataHash";

	private static final String DECRYPT = "decrypt";

	private static final String UIN_DATA = "uinData";

	/** The mosip logger. */
	private transient Logger mosipLogger = IdRepoLogger.getLogger(IdRepoEntityInterceptor.class);

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 4985336846122302850L;

	@Autowired
	private transient RestRequestFactory restFactory;
	
	@Autowired
	private transient RestHelper restHelper;
	
	/** The env. */
	@Autowired
	private transient Environment env;

	/** The mapper. */
	@Autowired
	private transient ObjectMapper mapper;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hibernate.EmptyInterceptor#onSave(java.lang.Object,
	 * java.io.Serializable, java.lang.Object[], java.lang.String[],
	 * org.hibernate.type.Type[])
	 */
	@Override
	public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
		try {

			if (entity instanceof Uin) {
				Uin uinEntity = (Uin) entity;
				byte[] encryptedData = encryptDecryptIdentity(
						CryptoUtil.encodeBase64(uinEntity.getUinData()).getBytes(), "encrypt");
				uinEntity.setUinData(encryptedData);
				List<Object> propertyNamesList = Arrays.asList(propertyNames);
				int indexOfData = propertyNamesList.indexOf(UIN_DATA);
				state[indexOfData] = encryptedData;
				return super.onSave(uinEntity, id, state, propertyNames, types);
			}
			if (entity instanceof UinHistory) {
				UinHistory uinHEntity = (UinHistory) entity;
				uinHEntity.setUinData(
						encryptDecryptIdentity(CryptoUtil.encodeBase64(uinHEntity.getUinData()).getBytes(), "encrypt"));
				return super.onSave(uinHEntity, id, state, propertyNames, types);
			}
		} catch (IdRepoAppException e) {
			mosipLogger.error(ID_REPO_SERVICE, ID_REPO_ENTITY_INTERCEPTOR, "onSave",
					"\n" + ExceptionUtils.getStackTrace(e));
			throw new IdRepoAppUncheckedException(IdRepoErrorConstants.ENCRYPTION_DECRYPTION_FAILED, e);
		}
		return super.onSave(entity, id, state, propertyNames, types);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hibernate.EmptyInterceptor#onLoad(java.lang.Object,
	 * java.io.Serializable, java.lang.Object[], java.lang.String[],
	 * org.hibernate.type.Type[])
	 */
	@Override
	public boolean onLoad(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
		try {
			if (entity instanceof Uin || entity instanceof UinHistory) {
				List<Object> propertyNamesList = Arrays.asList(propertyNames);
				int indexOfData = propertyNamesList.indexOf(UIN_DATA);
				state[indexOfData] = CryptoUtil
						.decodeBase64(new String(encryptDecryptIdentity((byte[]) state[indexOfData], DECRYPT)));

				if (!StringUtils.equals(hash((byte[]) state[indexOfData]),
						(String) state[propertyNamesList.indexOf(UIN_DATA_HASH)])) {
					throw new IdRepoAppUncheckedException(IdRepoErrorConstants.IDENTITY_HASH_MISMATCH);
				}
			}
		} catch (IdRepoAppException e) {
			mosipLogger.error(ID_REPO_SERVICE, ID_REPO_ENTITY_INTERCEPTOR, "onLoad",
					"\n" + ExceptionUtils.getStackTrace(e));
			throw new IdRepoAppUncheckedException(IdRepoErrorConstants.ENCRYPTION_DECRYPTION_FAILED, e);
		}
		return super.onLoad(entity, id, state, propertyNames, types);
	}

	@Override
	public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState,
			String[] propertyNames, Type[] types) {
		try {
			if (entity instanceof Uin) {
				Uin uinEntity = (Uin) entity;
				byte[] encryptedData = encryptDecryptIdentity(
						CryptoUtil.encodeBase64(uinEntity.getUinData()).getBytes(), "encrypt");
				List<Object> propertyNamesList = Arrays.asList(propertyNames);
				int indexOfData = propertyNamesList.indexOf(UIN_DATA);
				currentState[indexOfData] = encryptedData;
				return super.onFlushDirty(uinEntity, id, currentState, previousState, propertyNames, types);
			}
		} catch (IdRepoAppException e) {
			mosipLogger.error(ID_REPO_SERVICE, ID_REPO_ENTITY_INTERCEPTOR, "onSave",
					"\n" + ExceptionUtils.getStackTrace(e));
			throw new IdRepoAppUncheckedException(IdRepoErrorConstants.ENCRYPTION_DECRYPTION_FAILED, e);
		}
		return super.onFlushDirty(entity, id, currentState, previousState, propertyNames, types);
	}

	/**
	 * Encrypt identity.
	 *
	 * @param identity
	 *            the identity
	 * @param method
	 *            the method
	 * @return the byte[]
	 * @throws IdRepoAppException
	 *             the id repo app exception
	 */
	private byte[] encryptDecryptIdentity(byte[] identity, String method) throws IdRepoAppException {
		try {
			RestRequestDTO restRequest = null;
			ObjectNode request = new ObjectNode(mapper.getNodeFactory());
			request.put("applicationId", env.getProperty("application.id"));
			request.put("referenceId", env.getProperty("mosip.kernel.keymanager.refId"));
			request.put("timeStamp", DateUtils.formatDate(new Date(), env.getProperty("datetime.pattern")));
			request.put("data", new String(identity));

			if (method.equals("encrypt")) {
				restRequest = restFactory.buildRequest(RestServicesConstants.CRYPTO_MANAGER_ENCRYPT, restRequest, ObjectNode.class);
			} else {
				restRequest = restFactory.buildRequest(RestServicesConstants.CRYPTO_MANAGER_DECRYPT, request, ObjectNode.class);
			}
			
			ObjectNode response = restHelper.requestSync(restRequest);

			if (response.has("data")) {
				return response.get("data").asText().getBytes();
			} else {
				mosipLogger.error(ID_REPO_SERVICE, ID_REPO_ENTITY_INTERCEPTOR, "encryptDecryptIdentity",
						"No data block found in response");
				throw new IdRepoAppException(IdRepoErrorConstants.ENCRYPTION_DECRYPTION_FAILED);
			}
		} catch (RestServiceException e) {
			mosipLogger.error(ID_REPO_SERVICE, ID_REPO_ENTITY_INTERCEPTOR, "encryptDecryptIdentity",
					"\n" + ExceptionUtils.getStackTrace(e));
			throw new IdRepoAppException(IdRepoErrorConstants.ENCRYPTION_DECRYPTION_FAILED, e);
		}
	}

	/**
	 * Hash.
	 *
	 * @param identityInfo
	 *            the identity info
	 * @return the string
	 */
	private String hash(byte[] identityInfo) {
		return CryptoUtil.encodeBase64(HMACUtils.generateHash(identityInfo));
	}
}
