package io.mosip.authentication.core.spi.indauth.match;

import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.logger.spi.Logger;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The Interface IdMapping.
 *
 * @author Dinesh Karuppiah.T
 */

public interface IdMapping {
    /** The mosip logger. */
     Logger mosipLogger = IdaLogger.getLogger(IdMapping.class);
	/**
	 * Method to get ID name.
	 *
	 * @return the idname
	 */
	public String getIdname();

	/**
	 * Method to get Mapping Function.
	 *
	 * @return the mapping function
	 */
	public BiFunction<MappingConfig, MatchType, List<String>> getMappingFunction();

	/**
	 * Method to get ID Mapping.
	 *
	 * @param name the name
	 * @param values the values
	 * @param idMappingConfig the id mapping config
	 * @return the id mapping
	 */
    public static Optional<IdMapping> getIdMapping(String name, IdMapping[] values, MappingConfig idMappingConfig) {
        mosipLogger.info(IdAuthCommonConstants.SESSION_ID, IdMapping.class.getSimpleName(),
                "getIdMapping", "Input name: " + name);

        if (values != null) {
            mosipLogger.info(IdAuthCommonConstants.SESSION_ID, IdMapping.class.getSimpleName(),
                    "getIdMapping", "Input values length: " + values.length);

            // Log all values in the array
            String allValues = Stream.of(values)
                    .map(v -> "Idname=" + v.getIdname() + ", Type=" + v.getType() + ", SubType=" + v.getSubType())
                    .collect(Collectors.joining(" | "));
            mosipLogger.info(IdAuthCommonConstants.SESSION_ID, IdMapping.class.getSimpleName(),
                    "getIdMapping", "All values: " + allValues);
        } else {
            mosipLogger.info(IdAuthCommonConstants.SESSION_ID, IdMapping.class.getSimpleName(),
                    "getIdMapping", "Input values is null");
        }

        mosipLogger.info(IdAuthCommonConstants.SESSION_ID, IdMapping.class.getSimpleName(),
                "getIdMapping", "Input idMappingConfig: " + idMappingConfig);

        Optional<IdMapping> idMappingOpt = Stream.of(values)
                .filter(m -> {
                    boolean match = m.getIdname().equalsIgnoreCase(name);
                    mosipLogger.info(IdAuthCommonConstants.SESSION_ID, IdMapping.class.getSimpleName(),
                            "getIdMapping", "Checking value: " + m.getIdname() + " match=" + match);
//                    Checking value: Face match=false
                    return match;
                })
                .findAny();

        mosipLogger.info(IdAuthCommonConstants.SESSION_ID, IdMapping.class.getSimpleName(),
                "getIdMapping", "idMappingOpt present: " + idMappingOpt.isPresent());

        if (idMappingOpt.isEmpty()) {
            Map<String, List<String>> dynamicAttributes = idMappingConfig.getDynamicAttributes();
            mosipLogger.info(IdAuthCommonConstants.SESSION_ID, IdMapping.class.getSimpleName(),
                    "getIdMapping", "dynamicAttributes keys: " + dynamicAttributes.keySet());

            Optional<IdMapping> dynamicMapping = dynamicAttributes.entrySet()
                    .stream()
                    .filter(entry -> {
                        boolean match = entry.getKey().equals(name);
                        mosipLogger.info(IdAuthCommonConstants.SESSION_ID, IdMapping.class.getSimpleName(),
                                "getIdMapping", "Checking dynamic entry key: " + entry.getKey() + " match=" + match);
                        return match;
                    })
                    .<IdMapping>map(entry -> {
                        mosipLogger.info(IdAuthCommonConstants.SESSION_ID, IdMapping.class.getSimpleName(),
                                "getIdMapping", "Creating new DynamicIdMapping for key: " + entry.getKey() +
                                        " values=" + entry.getValue());
                        return new DynamicIdMapping(name, entry.getValue());
                    })
                    .findAny();

            mosipLogger.info(IdAuthCommonConstants.SESSION_ID, IdMapping.class.getSimpleName(),
                    "getIdMapping", "Returning dynamicMapping present: " + dynamicMapping.isPresent());
            return dynamicMapping;

        } else {
            mosipLogger.info(IdAuthCommonConstants.SESSION_ID, IdMapping.class.getSimpleName(),
                    "getIdMapping", "Returning static idMappingOpt");
            return idMappingOpt;
        }
    }


    /**
	 * Gets the sub id mappings.
	 *
	 * @return the sub id mappings
	 */
	public Set<IdMapping> getSubIdMappings();
	
	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType();
	
	/**
	 * Gets the sub type.
	 *
	 * @return the sub type
	 */
	public String getSubType();
	

}
