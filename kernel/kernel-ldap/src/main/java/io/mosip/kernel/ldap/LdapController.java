package io.mosip.kernel.ldap;

import io.mosip.kernel.ldap.entities.LoginUser;
import io.mosip.kernel.ldap.entities.MosipUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
public class LdapController {

    @Autowired
    private LdapService ldapService;

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public MosipUser authenticateUser(@RequestBody LoginUser user) throws Exception{
        return ldapService.authenticateUser(user);
    }

    @RequestMapping(value = "/isAuthorized", method = RequestMethod.POST)
    public Collection<String> isAuthorized(@RequestBody LoginUser user) throws Exception{
        return ldapService.getRoles(user);
    }
}
