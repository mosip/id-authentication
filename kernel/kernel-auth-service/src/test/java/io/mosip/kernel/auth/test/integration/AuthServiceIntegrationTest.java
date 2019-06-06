package io.mosip.kernel.auth.test.integration;

import javax.naming.NamingEnumeration;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import io.mosip.kernel.auth.test.AuthTestBootApplication;

@SpringBootTest(classes = AuthTestBootApplication.class)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class AuthServiceIntegrationTest {

	@Autowired
	MockMvc mockMvc;
	
	@MockBean
	LdapContext ldapContext;
	
	@MockBean
	NamingEnumeration<SearchResult> searchresult;

	@Before
	public void setUp() {
      
	}
	
	

}
