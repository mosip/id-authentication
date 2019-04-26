package io.mosip.kernel.auth.entities;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Ramadurai Pandian
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MosipUserSaltList {
	List<UserDetailsSalt> mosipUserSaltList;
}
