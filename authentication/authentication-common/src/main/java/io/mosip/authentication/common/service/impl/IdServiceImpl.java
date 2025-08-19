package io.mosip.authentication.common.service.impl;

import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_AUTH_PARTNER_ID;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_ZERO_KNOWLEDGE_UNENCRYPTED_CREDENTIAL_ATTRIBUTES;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.hibernate.exception.JDBCConnectionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionException;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.entity.IdentityEntity;
import io.mosip.authentication.common.service.repository.AutnTxnRepository;
import io.mosip.authentication.common.service.repository.IdentityCacheRepository;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.id.service.IdService;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;

/**
 * The class validates the UIN and VID.
 *
 * @author Arun Bose
 * @author Rakesh Roshan
 */
@Service
public class IdServiceImpl implements IdService<AutnTxn> {

    private static final String TOKEN = "TOKEN";

    private static final String ID_HASH = "ID_HASH";

    private static final String BIOMETRICS = "biometrics";

    private static final String DEMOGRAPHICS = "demographics";

    /** The logger. */
    private static Logger logger = IdaLogger.getLogger(IdServiceImpl.class);

    /** The autntxnrepository. */
    @Autowired
    private AutnTxnRepository autntxnrepository;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private IdentityCacheRepository identityRepo;

    @Autowired
    private IdAuthSecurityManager securityManager;

    @Value("${" + IDA_ZERO_KNOWLEDGE_UNENCRYPTED_CREDENTIAL_ATTRIBUTES + ":#{null}" + "}")
    private String zkUnEncryptedCredAttribs;

    @Value("${"+ IDA_AUTH_PARTNER_ID  +"}")
    private String authPartherId;

    /*
     * To get Identity data from IDRepo based on UIN
     *
     * @see
     * org.mosip.auth.core.spi.idauth.service.IdAuthService#validateUIN(java.lang.
     * String)
     */
    @Override
    public Map<String, Object> getIdByUin(String uin, boolean isBio, Set<String> filterAttributes) throws IdAuthenticationBusinessException {
        return getIdentity(uin, isBio, filterAttributes);
    }

    /*
     * To get Identity data from IDRepo based on VID
     *
     * @see
     * org.mosip.auth.core.spi.idauth.service.IdAuthService#validateVID(java.lang.
     * String)
     */
    @Override
    public Map<String, Object> getIdByVid(String vid, boolean isBio, Set<String> filterAttributes) throws IdAuthenticationBusinessException {
        return getIdentity(vid, isBio, IdType.VID, filterAttributes);
    }

    /**
     * Process the IdType and validates the Idtype and upon validation reference Id
     * is returned in AuthRequestDTO.
     *
     * @param idvIdType idType
     * @param idvId     id-number
     * @param isBio the is bio
     * @return map map
     * @throws IdAuthenticationBusinessException the id authentication business
     *                                           exception
     */
    @Override
    public Map<String, Object> processIdType(String idvIdType, String idvId, boolean isBio, boolean markVidConsumed, Set<String> filterAttributes)
            throws IdAuthenticationBusinessException {
        Map<String, Object> idResDTO = null;
        if (idvIdType.equals(IdType.UIN.getType()) || idvIdType.equals(IdType.HANDLE.getType())) {
            try {
                idResDTO = getIdByUin(idvId, isBio, filterAttributes);
            } catch (IdAuthenticationBusinessException e) {
                logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), e.getErrorCode(), e.getErrorText());
                throw e;
            }
        } else if(idvIdType.equals(IdType.VID.getType())) {
            try {
                idResDTO = getIdByVid(idvId, isBio, filterAttributes);
            } catch (IdAuthenticationBusinessException e) {
                logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), e.getErrorCode(), e.getErrorText());
                throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.INVALID_VID, e);
            }

            if(markVidConsumed) {
                updateVIDstatus(idvId);
            }
        }
        return idResDTO;
    }

    /**
     * Store entry in Auth_txn table for all authentications.
     *
     * @param authTxn the auth txn
     * @throws IdAuthenticationBusinessException the id authentication business
     *                                           exception
     */
    public void saveAutnTxn(AutnTxn authTxn) throws IdAuthenticationBusinessException {
        autntxnrepository.saveAndFlush(authTxn);
    }

    /**
     * Gets the demo data.
     *
     * @param identity the identity
     * @return the demo data
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getDemoData(Map<String, Object> identity) {
        return Optional.ofNullable(identity.get("response"))
                .filter(obj -> obj instanceof Map)
                .map(obj -> ((Map<String, Object>)obj).get("identity"))
                .filter(obj -> obj instanceof Map)
                .map(obj -> (Map<String, Object>) obj)
                .orElseGet(Collections::emptyMap);
    }

    public Map<String, Object> getIdentity(String id, boolean isBio, Set<String> filterAttributes) throws IdAuthenticationBusinessException {
        return getIdentity(id, isBio, IdType.UIN, filterAttributes);
    }

    /**
     * Fetch data from Id Repo based on Individual's UIN / VID value and all UIN.
     *
     * @param id
     *            the uin
     * @param isBio
     *            the is bio
     * @return the idenity
     * @throws IdAuthenticationBusinessException
     *             the id authentication business exception
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getIdentity(String id, boolean isBio, IdType idType, Set<String> filterAttributes) throws IdAuthenticationBusinessException {
        final String hashedId = hashOrThrow(id, idType);

        try {
            logger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "getIdentity",
                    "Generated HASHID >> " + hashedId);

            // 1 query, projected & typed
            if (isBio) {
                final IdentityCacheRepository.FullIdentityView v = identityRepo.findFullViewById(hashedId)
                        .orElseThrow(() -> notFound(idType));
                validateNotExpired(v.getExpiryTimestamp(), idType);

                return buildResponseMap(id, hashedId, v.getToken(),
                        v.getDemographicData(), v.getBiometricData(),
                        filterAttributes);
            } else {
                final IdentityCacheRepository.DemoIdentityView v = identityRepo.findDemoViewById(hashedId)
                        .orElseThrow(() -> notFound(idType));
                validateNotExpired(v.getExpiryTimestamp(), idType);

                return buildResponseMap(id, hashedId, v.getToken(),
                        v.getDemographicData(), null,
                        filterAttributes);
            }
        } catch (DataAccessException | TransactionException | JDBCConnectionException e) {
            logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "getIdentity",
                    ExceptionUtils.getStackTrace(e));
            throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
        }
    }

    private String hashOrThrow(String id, IdType idType) throws IdAuthenticationBusinessException {
        try { return securityManager.hash(id); }
        catch (IdAuthenticationBusinessException e) {
            logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "hashOrThrow",
                    "Hash not found in DB");

            throw new IdAuthenticationBusinessException(
                    IdAuthenticationErrorConstants.ID_NOT_AVAILABLE.getErrorCode(),
                    String.format(IdAuthenticationErrorConstants.ID_NOT_AVAILABLE.getErrorMessage(), idType.getType(), e));
        }
    }

    private IdAuthenticationBusinessException notFound(IdType idType) {
        logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "notFound",
                "Id not found in DB");

        return new IdAuthenticationBusinessException(
                IdAuthenticationErrorConstants.ID_NOT_AVAILABLE.getErrorCode(),
                String.format(IdAuthenticationErrorConstants.ID_NOT_AVAILABLE.getErrorMessage(), idType.getType()));
    }

    private void validateNotExpired(LocalDateTime expiry, IdType idType) throws IdAuthenticationBusinessException {
        if (expiry != null && DateUtils.before(expiry, DateUtils.getUTCCurrentDateTime())) {
            logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "validateNotExpired",
                    idType.getType() + " expired/deactivated/revoked/blocked");

            final var err = (idType == IdType.UIN)
                    ? IdAuthenticationErrorConstants.UIN_DEACTIVATED_BLOCKED
                    : IdAuthenticationErrorConstants.VID_EXPIRED_DEACTIVATED_REVOKED;
            throw new IdAuthenticationBusinessException(err);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> buildResponseMap(
            String id, String hashedId, String token,
            byte[] demoBytes, byte[] bioBytes,
            Set<String> filterAttributes) throws DataAccessException, IdAuthenticationBusinessException {

        final Map<String, Object> out = new LinkedHashMap<>();
        final Set<String> filters = (filterAttributes == null ? Set.<String>of()
                : filterAttributes.stream().map(String::toLowerCase).collect(Collectors.toSet()));

        if (demoBytes != null && !filters.isEmpty()) {
            Map<String, String> demo = readJsonMap(demoBytes);              // typed mapping
            demo = demo.entrySet().stream()
                    .filter(e -> filters.contains(e.getKey().toLowerCase()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            out.put(BIOMETRICS, decryptConfiguredAttributes(id, demo));
        }

        if (bioBytes != null && !filters.isEmpty()) {
            Map<String, String> bio = readJsonMap(bioBytes);
            bio = bio.entrySet().stream()
                    .filter(e -> filters.contains(e.getKey().toLowerCase()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            out.put(BIOMETRICS, decryptConfiguredAttributes(id, bio));
        }

        out.put(TOKEN, token);
        out.put(ID_HASH, hashedId);

        logger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "buildResponseMap",
                "TOKEN in responseMap >> " + token);

        return out;
    }

    private Map<String, String> readJsonMap(byte[] bytes) {
        try { return mapper.readValue(bytes, Map.class); }
        catch (IOException e) {
            logger.error(IdAuthCommonConstants.SESSION_ID, getClass().getSimpleName(), "readJsonMap",
                    ExceptionUtils.getStackTrace(e));
            return Map.of();
        }
    }

    /**
     * Decrypt the attributes as per configuration.
     * @param id
     * @param dataMap
     * @return
     * @throws IdAuthenticationBusinessException
     */

    private Map<String, Object> decryptConfiguredAttributes(String id, Map<String, String> dataMap)
            throws IdAuthenticationBusinessException {
        if (dataMap == null || dataMap.isEmpty()) {
            logger.info("decryptConfiguredAttributes called with id={} but dataMap is null or empty", id);
            return Collections.emptyMap();
        }

        logger.info("decryptConfiguredAttributes invoked with id={} and dataMap={}", id, dataMap);

        // Build a Set<String> for O(1) lookups from the List<String> API
        final Set<String> zkUnEncryptedAttributes = new HashSet<>(getZkUnEncryptedAttributes());
        logger.info("zkUnEncryptedAttributes={}", zkUnEncryptedAttributes);

        final Map<String, String> toDecrypt = new HashMap<>();
        final Map<String, String> plain     = new HashMap<>();

        for (Map.Entry<String, String> e : dataMap.entrySet()) {
            final String key = e.getKey();
            final String val = e.getValue();
            logger.info("Processing entry: key={} value={}", key, val);

            if (key != null && zkUnEncryptedAttributes.contains(key.toLowerCase(Locale.ROOT))) {
                plain.put(key, val);
                logger.info("Attribute '{}' is unencrypted, added to plain map", key);
            } else {
                toDecrypt.put(key, val);
                logger.info("Attribute '{}' marked for decryption, added to toDecrypt map", key);
            }
        }

        logger.info("Plain attributes map={}", plain);
        logger.info("Attributes to decrypt map={}", toDecrypt);

        final Map<String, String> decrypted = toDecrypt.isEmpty()
                ? Collections.emptyMap()
                : securityManager.zkDecrypt(id, toDecrypt);

        logger.info("Decrypted attributes map={}", decrypted);

        final Map<String, Object> out = new LinkedHashMap<>(dataMap.size());
        for (Map.Entry<String, String> e : dataMap.entrySet()) {
            final String key = e.getKey();
            final String raw = plain.containsKey(key) ? plain.get(key) : decrypted.get(key);
            Object parsed = maybeParseJson(raw);
            out.put(key, parsed);
            logger.info("Final output entry: key={} raw={} parsed={}", key, raw, parsed);
        }

        logger.info("Final decryptedConfiguredAttributes output={}", out);
        return out;
    }


    private Object maybeParseJson(String val) {
        if (val == null) return null;
        final String s = val.trim();
        if (s.isEmpty()) return val;
        final char c = s.charAt(0);
        if (c == '{' || c == '[') {
            try { return mapper.readValue(s, Object.class); }
            catch (IOException e) {
                logger.error(IdAuthCommonConstants.SESSION_ID, getClass().getSimpleName(),
                        "decryptConfiguredAttributes.maybeParseJson", ExceptionUtils.getStackTrace(e));
                return val;
            }
        }
        return val;
    }

    /**
     * Get the list of attributes not to decrypt from config. Returns empty if no config is there
     * @return
     */
    private List<String> getZkUnEncryptedAttributes() {
        if (zkUnEncryptedCredAttribs == null || zkUnEncryptedCredAttribs.isBlank()) {
            return Collections.emptyList();
        }
        return Stream.of(zkUnEncryptedCredAttribs.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
//                .map(s -> s.toLowerCase(Locale.ROOT))
                .collect(Collectors.toList());
    }

    /**
     * Update VID dstatus.
     *
     * @param vid
     *            the vid
     * @throws IdAuthenticationBusinessException
     *             the id authentication business exception
     */
    private void updateVIDstatus(String vid) throws IdAuthenticationBusinessException {
        try {
            vid = securityManager.hash(vid);
            // Assumption : If transactionLimit is null, id is considered as Perpetual VID
            // If transactionLimit is nonNull, id is considered as Temporary VID

            //get entity
            Optional<IdentityEntity> entityOpt = identityRepo.findById(vid);
            if(entityOpt.isPresent()) {
                IdentityEntity entity =entityOpt.get();
                Integer transactionLimit = entity.getTransactionLimit();
                if (identityRepo.existsById(vid)
                        && Objects.nonNull(transactionLimit)){
                    int newTransactionLimit = transactionLimit-1;
                    if (newTransactionLimit>0) {
                        entity.setTransactionLimit(newTransactionLimit);
                        identityRepo.save(entity);
                    } else {
                        identityRepo.deleteById(vid);
                    }
                }
            }

        } catch (DataAccessException | TransactionException | JDBCConnectionException e) {
            logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "getIdentity",
                    ExceptionUtils.getStackTrace(e));
            throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
        }
    }

    @Override
    public String getToken(Map<String, Object> idResDTO) {
        return (String) idResDTO.get(TOKEN);
    }


    @Override
    public String getIdHash(Map<String, Object> idResDTO) {
        return (String) idResDTO.get(ID_HASH);

    }

    @Override
    public void checkIdKeyBindingPermitted(String idvId, String idvIdType) throws IdAuthenticationBusinessException {
        try {
            String idVidHash = securityManager.hash(idvId);
            logger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "checkIdKeyBindingPermitted",
                    "Checking Id Key Binding Permitted or not. IdVidHash: " + idVidHash);
            // Assumption : If transactionLimit is null, id is considered as Perpetual VID
            // If transactionLimit is nonNull, id is considered as Temporary VID
            // Duplicated identity data fetching from DB, because to avoid lot of if else conditions needs to be added in 
            // above getIdentity method. Above getIdentity method also includes data decryption logic. 
            List<Object[]> entityObjList = identityRepo.findTransactionLimitById(idVidHash);
            if(entityObjList.size() == 0) {
                logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "checkIdKeyBindingPermitted",
                        "Id not found in DB");
                throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.ID_NOT_AVAILABLE.getErrorCode(),
                        String.format(IdAuthenticationErrorConstants.ID_NOT_AVAILABLE.getErrorMessage()));
            }
            Object[] entityObjs = entityObjList.get(0);

            LocalDateTime expiryTimestamp = Objects.nonNull(entityObjs[1]) ? LocalDateTime.parse(String.valueOf(entityObjs[1])) : null;

            if (Objects.nonNull(expiryTimestamp)
                    && DateUtils.before(expiryTimestamp, DateUtils.getUTCCurrentDateTime())) {
                logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "checkIdKeyBindingPermitted",
                        idvIdType + " expired/deactivated/revoked/blocked");
                IdAuthenticationErrorConstants errorConstant;
                if (idvIdType.equals(IdType.UIN.getType())) {
                    errorConstant = IdAuthenticationErrorConstants.UIN_DEACTIVATED_BLOCKED;
                } else {
                    errorConstant = IdAuthenticationErrorConstants.VID_EXPIRED_DEACTIVATED_REVOKED;
                }
                throw new IdAuthenticationBusinessException(errorConstant);
            }

            int transactionLimit = Objects.nonNull(entityObjs[2]) ? Integer.parseInt(String.valueOf(entityObjs[2])) : -1;
            if (transactionLimit > 0) {
                logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "checkIdKeyBindingPermitted",
                        "Id not allowed for identity key binding.");
                throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.ID_KEY_BINDING_NOT_ALLOWED.getErrorCode(),
                        String.format(IdAuthenticationErrorConstants.ID_KEY_BINDING_NOT_ALLOWED.getErrorMessage()));
            }
        } catch (DataAccessException | TransactionException | JDBCConnectionException e) {
            logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "checkIdKeyBindingPermitted",
                    ExceptionUtils.getStackTrace(e));
            throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
        }
    }
}
