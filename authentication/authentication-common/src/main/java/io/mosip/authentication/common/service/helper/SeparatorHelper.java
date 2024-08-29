package io.mosip.authentication.common.service.helper;

import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 @author Kamesh Shekhar Prasad
 */

@Component
public class SeparatorHelper {

    @Autowired
    private Environment env;

    public String getSeparator(String idname) {
        return env.getProperty(IdAuthConfigKeyConstants.IDA_ID_ATTRIBUTE_SEPARATOR_PREFIX + idname, IdAuthCommonConstants.DEFAULT_ID_ATTRIBUTE_SEPARATOR_VALUE);
    }
}
