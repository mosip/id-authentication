package io.mosip.authentication.common.service.helper;

import io.mosip.authentication.common.service.util.LanguageUtil;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.spi.indauth.match.MatchType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class EntityInfoHelper {

    @Autowired
    private SeparatorHelper separatorHelper;

    @Autowired
    private EntityInfoMapHelper entityInfoMapHelper;

    @Autowired
    private LanguageUtil computeKeyHelper;

    /**
     * Gets the entity info as string.
     *
     * @param matchType  the match type
     * @param idEntity the id entity
     * @return the entity info as string
     * @throws IdAuthenticationBusinessException the id authentication business exception
     */
    public String getEntityInfoAsString(MatchType matchType, Map<String, List<IdentityInfoDTO>> idEntity)
            throws IdAuthenticationBusinessException {
        return getEntityInfoAsString(matchType, null, idEntity);
    }

    /**
     * Gets the entity info as string.
     *
     * Note: This method is not used during authentication match, so the
     * separator used in concatenation will not be used during the match.
     *
     * @param matchType the match type
     * @param langCode  the lang code
     * @param idEntity  the id entity
     * @return the entity info as string
     * @throws IdAuthenticationBusinessException the id authentication business
     *                                           exception
     */
    public String getEntityInfoAsString(MatchType matchType, String langCode,
                                               Map<String, List<IdentityInfoDTO>> idEntity) throws IdAuthenticationBusinessException {
        Map<String, String> entityInfo = getEntityInfoAsStringWithKey(matchType, langCode,
                        idEntity, null);
        if(entityInfo == null || entityInfo.isEmpty()) {
            return null;
        }
        return entityInfo.values().iterator().next();
    }

    public Map<String, String> getEntityInfoAsStringWithKey(MatchType matchType, String langCode,
                                                            Map<String, List<IdentityInfoDTO>> idEntity, String key) throws IdAuthenticationBusinessException {
        Map<String, String> entityInfoMap = entityInfoMapHelper.getIdEntityInfoMap(matchType, idEntity, langCode);
        if(entityInfoMap == null || entityInfoMap.isEmpty()) {
            return Map.of();
        }
        String actualKey = entityInfoMap.keySet().iterator().next();
        return Map.of(key == null ? actualKey :  computeKeyHelper.computeKey(key, entityInfoMap.keySet().iterator().next(), langCode) ,concatValues(separatorHelper.getSeparator(matchType.getIdMapping().getIdname()), entityInfoMap.values().toArray(new String[entityInfoMap.size()])));
    }

    /**
     * Concat values.
     *
     * @param values the values
     * @return the string
     */
    private String concatValues(String sep, String... values) {
        StringBuilder demoBuilder = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            String demo = values[i];
            if (null != demo && demo.length() > 0) {
                demoBuilder.append(demo);
                if (i < values.length - 1) {
                    demoBuilder.append(sep);
                }
            }
        }
        return demoBuilder.toString();
    }
}
