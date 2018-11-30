package io.kernel.idrepo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import io.kernel.idrepo.IdRepoApplication;

/**
 * The Class IdRepoApplicationTest.
 *
 * @author Manoj SP
 */
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@RunWith(SpringRunner.class)
@WebMvcTest
@ActiveProfiles("test")
public class IdRepoApplicationTest {

	/**
	 * Test id repo application.
	 */
	@Test
	public void testIdRepoApplication() {
		IdRepoApplication.main(new String[] { "" });
	}
}
