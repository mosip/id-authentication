package io.mosip.authentication.service.filter;

/**
 * Abstract base class for TSP Authentication filter.
 *
 * @author Loganathan Sekaran
 * @param <REQUEST_DTO> the generic type
 * @param <RESPONSE_DTO> the generic type
 */
public abstract class TSPAuthFilter<REQUEST_DTO, RESPONSE_DTO> extends BaseAuthFilter<REQUEST_DTO, RESPONSE_DTO, TSPInfo> {
}
