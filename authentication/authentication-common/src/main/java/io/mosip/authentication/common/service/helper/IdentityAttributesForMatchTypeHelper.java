package io.mosip.authentication.common.service.helper;

import io.mosip.authentication.common.service.config.IDAMappingConfig;
import io.mosip.authentication.common.service.impl.match.IdaIdMapping;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.indauth.match.IdMapping;
import io.mosip.authentication.core.spi.indauth.match.MatchType;
import io.mosip.kernel.core.logger.spi.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 @author Kamesh Shekhar Prasad
 */

@Component
public class IdentityAttributesForMatchTypeHelper {

    /** The mosip logger. */
    private static Logger mosipLogger = IdaLogger.getLogger(IdentityAttributesForMatchTypeHelper.class);

    /** The id mapping config. */
    @Autowired
    private IDAMappingConfig idMappingConfig;

    /**
     * Gets the id mapping value.
     *
     * @param idMapping the id mapping
     * @param matchType the match type
     * @return the id mapping value
     * @throws IdAuthenticationBusinessException the id authentication business exception
     */
    public List<String> getIdMappingValue(IdMapping idMapping, MatchType matchType)
            throws IdAuthenticationBusinessException {

        mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
                "getIdMappingValue", "Method called with idMapping=" + idMapping + ", matchType=" + matchType);
//        Method called with idMapping=FACE, matchType=FACE

        String type = matchType.getCategory().getType();
        mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
                "getIdMappingValue", "Resolved category type=" + type);
//        Resolved category type=bio

        List<String> mappings = idMapping.getMappingFunction().apply(idMappingConfig, matchType);
        mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
                "getIdMappingValue", "Fetched mappings=" + mappings);
//        Fetched mappings=[FACE__8]

        if (mappings != null && !mappings.isEmpty()) {
            List<String> fullMapping = new ArrayList<>();
            mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
                    "getIdMappingValue", "Processing " + mappings.size() + " mapping entries.");
//            Processing 1 mapping entries.

            for (String mappingStr : mappings) {
                mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
                        "getIdMappingValue", "Processing mappingStr=" + mappingStr);
//                Processing mappingStr=FACE__8

                if (!Objects.isNull(mappingStr) && !mappingStr.isEmpty()) {
                    Optional<IdMapping> mappingInternal = IdMapping.getIdMapping(
                            mappingStr, IdaIdMapping.values(), idMappingConfig);
                    mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
                            "getIdMappingValue", "Found internal mapping for '" + mappingStr + "' = " + mappingInternal);
//                    Found internal mapping for 'FACE__8' = Optional.empty

                    if (mappingInternal.isPresent() && !idMapping.equals(mappingInternal.get())) {
                        mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
                                "getIdMappingValue", "Recursively fetching internal mapping for '" + mappingStr + "'");
                        List<String> internalMapping = getIdMappingValue(mappingInternal.get(), matchType);
                        mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
                                "getIdMappingValue", "Fetched internal mapping values=" + internalMapping);
                        fullMapping.addAll(internalMapping);
                    } else {
                        mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
                                "getIdMappingValue", "Adding mappingStr to final list: " + mappingStr);
                        fullMapping.add(mappingStr);
                    }
                } else {
                    mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
                            IdAuthCommonConstants.VALIDATE,
                            "IdMapping config is Invalid for Type -" + type + ", null or empty mapping string found.");
                    throw new IdAuthenticationBusinessException(
                            IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(),
                            IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorMessage());
                }
            }

            mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
                    "getIdMappingValue", "Final fullMapping list=" + fullMapping);
            return fullMapping;

        } else {
            mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
                    IdAuthCommonConstants.VALIDATE, "IdMapping config is Invalid for Type -" + type + ", mappings list is null or empty.");
            throw new IdAuthenticationBusinessException(
                    IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(),
                    IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorMessage());
        }
    }


    /**
     * Gets the property names for match type.
     *
     * @param matchType the match type
     * @param idName the id name
     * @return the property names for match type
     */
    public List<String> getIdentityAttributesForMatchType(MatchType matchType, String idName) {
        mosipLogger.info(IdAuthCommonConstants.SESSION_ID,
                this.getClass().getSimpleName(),
                "getIdentityAttributesForMatchType",
                "Method called with matchType=" + matchType + ", idName=" + idName);
//        Method called with matchType=FACE, idName=Face

        String propertyName = idName != null ? idName : matchType.getIdMapping().getIdname();
        mosipLogger.info(IdAuthCommonConstants.SESSION_ID,
                this.getClass().getSimpleName(),
                "getIdentityAttributesForMatchType",
                "Resolved propertyName=" + propertyName);
//        Resolved propertyName=Face

        List<String> propertyNames;

        if (!matchType.isDynamic()) {
            mosipLogger.info(IdAuthCommonConstants.SESSION_ID,
                    this.getClass().getSimpleName(),
                    "getIdentityAttributesForMatchType",
                    "MatchType is static (non-dynamic).");

            if (matchType.getIdMapping().getIdname().equals(propertyName)) {
                mosipLogger.info(IdAuthCommonConstants.SESSION_ID,
                        this.getClass().getSimpleName(),
                        "getIdentityAttributesForMatchType",
                        "propertyName matches IdMapping idname, fetching IdMapping values.");
                try {
                    propertyNames = getIdMappingValue(matchType.getIdMapping(), matchType);
                    mosipLogger.info(IdAuthCommonConstants.SESSION_ID,
                            this.getClass().getSimpleName(),
                            "getIdentityAttributesForMatchType",
                            "Fetched propertyNames from IdMapping=" + propertyNames);
//                    Fetched propertyNames from IdMapping=[FACE__8]
                } catch (IdAuthenticationBusinessException e) {
                    mosipLogger.debug(IdAuthCommonConstants.SESSION_ID,
                            this.getClass().getSimpleName(),
                            IdAuthCommonConstants.VALIDATE,
                            "Ignoring: IdMapping config is invalid for Type - " + matchType);
                    propertyNames = List.of();
                    mosipLogger.info(IdAuthCommonConstants.SESSION_ID,
                            this.getClass().getSimpleName(),
                            "getIdentityAttributesForMatchType",
                            "Set propertyNames to empty list due to exception.");
                }
            } else {
                propertyNames = List.of();
                mosipLogger.info(IdAuthCommonConstants.SESSION_ID,
                        this.getClass().getSimpleName(),
                        "getIdentityAttributesForMatchType",
                        "propertyName does not match IdMapping idname, returning empty list.");
            }
        } else {
            mosipLogger.info(IdAuthCommonConstants.SESSION_ID,
                    this.getClass().getSimpleName(),
                    "getIdentityAttributesForMatchType",
                    "MatchType is dynamic.");

            if (idMappingConfig.getDynamicAttributes().containsKey(propertyName)) {
                propertyNames = idMappingConfig.getDynamicAttributes().get(propertyName);
                mosipLogger.info(IdAuthCommonConstants.SESSION_ID,
                        this.getClass().getSimpleName(),
                        "getIdentityAttributesForMatchType",
                        "Dynamic attributes found for propertyName=" + propertyName + ", values=" + propertyNames);
            } else {
                propertyNames = List.of(idName);
                mosipLogger.info(IdAuthCommonConstants.SESSION_ID,
                        this.getClass().getSimpleName(),
                        "getIdentityAttributesForMatchType",
                        "No dynamic attributes found for propertyName=" + propertyName + ", defaulting to [" + idName + "]");
            }
        }

        mosipLogger.info(IdAuthCommonConstants.SESSION_ID,
                this.getClass().getSimpleName(),
                "getIdentityAttributesForMatchType",
                "Returning propertyNames=" + propertyNames);

        return propertyNames;
    }

}
