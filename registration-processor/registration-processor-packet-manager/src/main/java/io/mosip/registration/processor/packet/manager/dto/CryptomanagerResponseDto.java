package io.mosip.registration.processor.packet.manager.dto;

import io.mosip.kernel.core.http.ResponseWrapper;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * @author Girish Yarru
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "Model representing a Crypto-Manager-Service Response")
public class CryptomanagerResponseDto extends ResponseWrapper<DecryptResponseDto> {

}
