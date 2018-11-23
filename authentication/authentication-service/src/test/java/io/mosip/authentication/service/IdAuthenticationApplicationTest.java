package io.mosip.authentication.service;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.UnsatisfiedDependencyException;

public class IdAuthenticationApplicationTest {

	@Ignore
	@Test(expected = UnsatisfiedDependencyException.class)
	public void test() {
		IdAuthenticationApplication app = new IdAuthenticationApplication();
		app.main(new String[] {});
	}

}
