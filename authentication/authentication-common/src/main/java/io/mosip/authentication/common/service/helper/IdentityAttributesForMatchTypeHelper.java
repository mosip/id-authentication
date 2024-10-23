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
        String type = matchType.getCategory().getType();
        List<String> mappings = idMapping.getMappingFunction().apply(idMappingConfig, matchType);
        if (mappings != null && !mappings.isEmpty()) {
            List<String> fullMapping = new ArrayList<>();
            for (String mappingStr : mappings) {
                if (!Objects.isNull(mappingStr) && !mappingStr.isEmpty()) {
                    Optional<IdMapping> mappingInternal = IdMapping.getIdMapping(mappingStr, IdaIdMapping.values(), idMappingConfig);
                    if (mappingInternal.isPresent() && !idMapping.equals(mappingInternal.get())) {
                        List<String> internalMapping = getIdMappingValue(mappingInternal.get(), matchType);
                        fullMapping.addAll(internalMapping);
                    } else {
                        fullMapping.add(mappingStr);
                    }
                } else {
                    mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
                            IdAuthCommonConstants.VALIDATE, "IdMapping config is Invalid for Type -" + type);
                    throw new IdAuthenticationBusinessException(
                            IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(),
                            IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorMessage());
                }
            }
            return fullMapping;
        } else {
            mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
                    IdAuthCommonConstants.VALIDATE, "IdMapping config is Invalid for Type -" + type);
            throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(),
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
        String propertyName = idName != null ? idName : matchType.getIdMapping().getIdname();
        List<String> propertyNames;
        if (!matchType.isDynamic()) {
            if(matchType.getIdMapping().getIdname().equals(propertyName)) {
                try {
                    propertyNames = getIdMappingValue(matchType.getIdMapping(), matchType);
                } catch (IdAuthenticationBusinessException e) {
                    mosipLogger.debug(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
                            IdAuthCommonConstants.VALIDATE, "Ignoring : IdMapping config is Invalid for Type -" + matchType);
                    propertyNames = List.of();
                }
            } else {
                propertyNames = List.of();
            }

        } else {
            if (idMappingConfig.getDynamicAttributes().containsKey(propertyName)) {
                propertyNames = idMappingConfig.getDynamicAttributes().get(propertyName);
            } else {
                propertyNames = List.of(idName);
            }
        }
        return propertyNames;
    }
}
