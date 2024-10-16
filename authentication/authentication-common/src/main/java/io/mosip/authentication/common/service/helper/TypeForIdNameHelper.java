package io.mosip.authentication.common.service.helper;

import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.spi.indauth.match.IdMapping;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Stream;

/**
 @author Kamesh Shekhar Prasad
 */

@Component
public class TypeForIdNameHelper {

    /**
     * Gets the type for id name.
     *
     * @param idName the id name
     * @param idMappings the id mappings
     * @return the type for id name
     */
    public Optional<String> getTypeForIdName(String idName, IdMapping[] idMappings) {
        return Stream.of(idMappings).filter(idmap -> {
            String thisId = idName.replaceAll("\\d", "");
            String thatId = idmap.getIdname().replace(IdAuthCommonConstants.UNKNOWN_COUNT_PLACEHOLDER, "");
            return thisId.equalsIgnoreCase(thatId);
        }).map(IdMapping::getType).findFirst();
    }
}
