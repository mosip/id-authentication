package io.mosip.authentication.service.kyc.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;

/**
 * Utility class for locale and language code mapping operations.
 */

public class LocaleMappingUtil {

    private LocaleMappingUtil() {
    }

    /**
     * Maps locales to their two-letter language codes.
     *
     * @param locales Set of locales to be mapped
     * @return Map of locale to its two-letter language code
     */
    public static Map<String, String> localesMapping(Set<String> locales) {
        return locales.stream()
                .filter(locale -> locale.trim().length() > 0)
                .collect(Collectors.toMap(
                    locale -> locale,
                    locale -> locale.substring(0, 2)
                ));
    }

    /**
     * Maps language codes from IdentityInfoDTO list.
     *
     * @param idInfoList List of IdentityInfoDTO containing language information
     * @return Map of two-letter language code to full language code
     */
    public static Map<String, String> langCodeMapping(List<IdentityInfoDTO> idInfoList) {
        return Optional.ofNullable(idInfoList)
                .map(infoList -> infoList.stream()
                .filter(idInfo -> Objects.nonNull(idInfo.getLanguage()))
                .collect(Collectors.toMap(
                    idInfo -> idInfo.getLanguage().substring(0,2),
                    idInfo -> idInfo.getLanguage())))
                .orElse(new HashMap<>());
    }
} 