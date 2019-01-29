package io.mosip.kernel.ldap;

import io.mosip.kernel.ldap.entities.LoginUser;
import io.mosip.kernel.ldap.entities.MosipUser;
import io.mosip.kernel.ldap.entities.OtpUser;
import io.mosip.kernel.ldap.entities.RolesResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
public class LdapController {

    @Autowired
    private LdapService ldapService;

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public MosipUser authenticateUser(@RequestBody LoginUser user) throws Exception {
        return ldapService.authenticateUser(user);
    }

    @RequestMapping(value = "/verify_otp_user", method = RequestMethod.POST)
    public MosipUser verifyOtpUser(@RequestBody OtpUser otpUser) throws Exception {
        return ldapService.verifyOtpUser(otpUser);
    }

    @RequestMapping(value = "/isAuthorized", method = RequestMethod.POST)
    public Collection<String> isAuthorized(@RequestBody LoginUser user) throws Exception {
        return ldapService.getRoles(user);
    }

    @RequestMapping(value = "/allroles", method = RequestMethod.GET)
    public RolesResponseDto getAllRoles() {
        return ldapService.getAllRoles();

    }

    @RequestMapping(value = "/userdetails/{userid}", method = RequestMethod.GET)
    public MosipUser getAllUserDetails(@PathVariable("userid") String user) {
        return ldapService.getUserDetails(user);
    }
}
