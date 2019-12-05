/**
 * 
 */
package io.mosip.resident.dto;

import java.util.List;

import lombok.Data;

/**
 * @author M1022006
 *
 */
@Data
public class AuthTypeStatusRequestDto extends BaseAuthRequestDTO {

	private String individualId;

	private String individualIdType;

	private List<AuthTypeStatus> authTypes;
}
