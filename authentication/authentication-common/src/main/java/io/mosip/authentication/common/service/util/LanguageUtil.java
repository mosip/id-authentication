package io.mosip.authentication.common.service.util;

import io.mosip.authentication.common.service.helper.IdentityAttributesForMatchTypeHelper;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.authentication.core.spi.indauth.match.MatchType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.mosip.authentication.core.constant.IdAuthCommonConstants.LANG_CODE_SEPARATOR;

@Component
public class LanguageUtil {

    @Autowired
    private IdentityAttributesForMatchTypeHelper identityAttributesForMatchTypeHelper;

    @Autowired
    private IdInfoFetcher idInfoFetcher;

    /**
     * This method returns the list of  data capture languages.
     * These are used to send the notifications in data capture languages.
     *
     * @param matchType the match type
     * @param identityInfos the identity infos
     * @return the data captured languages
     * @throws IdAuthenticationBusinessException the id authentication business exception
     */
    public List<String> getDataCapturedLanguages(MatchType matchType, Map<String, List<IdentityInfoDTO>> identityInfos)
            throws IdAuthenticationBusinessException {
        List<String> propertyNames = identityAttributesForMatchTypeHelper.getIdMappingValue(matchType.getIdMapping(), matchType);
        Map<String, Map.Entry<String, List<IdentityInfoDTO>>> mappedIdEntity = matchType.mapEntityInfo(identityInfos,
                idInfoFetcher);
        return mappedIdEntity.get(propertyNames.get(0)).getValue().stream().map(IdentityInfoDTO::getLanguage)
                .collect(Collectors.toList());
    }

    public  String computeKey(String newKey, String originalKey, String langCode) {
        return langCode != null && originalKey.contains(LANG_CODE_SEPARATOR) ? newKey + LANG_CODE_SEPARATOR + langCode: originalKey;
    }
}
