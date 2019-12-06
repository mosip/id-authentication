/**
 * 
 */
package io.mosip.resident.dto;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author M1022006
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuthTypeStatusRequestDto extends BaseAuthRequestDTO {

	private String individualId;

	private String individualIdType;

	private List<AuthTypeStatus> request;

	private String requestTime;

	private String version;
}
