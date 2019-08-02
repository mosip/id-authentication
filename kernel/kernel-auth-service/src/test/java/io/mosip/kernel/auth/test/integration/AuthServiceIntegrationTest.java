package io.mosip.kernel.auth.test.integration;

import javax.naming.NamingEnumeration;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;

import org.springframework.test.web.servlet.MockMvc;

//@SpringBootTest(classes = AuthTestBootApplication.class)
//@RunWith(SpringRunner.class)
//@AutoConfigureMockMvc
public class AuthServiceIntegrationTest {

	//@Autowired
	MockMvc mockMvc;
	
	//@MockBean
	LdapContext ldapContext;
	
	//@MockBean
	NamingEnumeration<SearchResult> searchresult;

	//@Before
	public void setUp() {
      
	}
	
	

}
