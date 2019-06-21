package io.mosip.idrepository.core.dto;

import io.mosip.kernel.core.http.ResponseWrapper;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The Class IdResponseDTO - Response DTO for Id Repository Identity service.
 *
 * @author Manoj SP
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class IdResponseDTO extends ResponseWrapper<ResponseDTO> {

}
