package io.mosip.authentication.service;

import org.junit.Test;
import org.springframework.beans.factory.UnsatisfiedDependencyException;

public class IdAuthenticationApplicationTest {

    @Test(expected = UnsatisfiedDependencyException.class)
    public void test() {
	IdAuthenticationApplication app = new IdAuthenticationApplication();
	app.main(new String[] {});
    }

}
