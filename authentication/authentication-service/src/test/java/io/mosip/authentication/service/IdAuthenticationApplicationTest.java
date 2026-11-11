package io.mosip.authentication.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.UnsatisfiedDependencyException;

public class IdAuthenticationApplicationTest {

	@Disabled
	@Test
	public void test() {
		assertThrows(UnsatisfiedDependencyException.class, () -> {
		IdAuthenticationApplication.main(new String[] {});
			});
	}

}
