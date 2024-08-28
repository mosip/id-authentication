package io.mosip.authentication.common.service.util;

import org.springframework.stereotype.Component;

import static io.mosip.authentication.core.constant.IdAuthCommonConstants.LANG_CODE_SEPARATOR;

@Component
public class ComputeKeyUtil {

    public  String computeKey(String newKey, String originalKey, String langCode) {
        return langCode != null && originalKey.contains(LANG_CODE_SEPARATOR) ? newKey + LANG_CODE_SEPARATOR + langCode: originalKey;
    }
}
