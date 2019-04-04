package io.mosip.kernel.ldap;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.ldap.dto.LoginUserDto;
import io.mosip.kernel.ldap.dto.MosipUserDto;
import io.mosip.kernel.ldap.dto.MosipUserListDto;
import io.mosip.kernel.ldap.dto.OtpUserDto;
import io.mosip.kernel.ldap.dto.RolesListDto;

/**
 * @author Sabbu Uday Kumar
 * @since 1.0.0
 */
@RestController
public class LdapController {

	@Autowired
	private LdapService ldapService;

	@RequestMapping(value = "/authenticate", method = RequestMethod.POST)
	public MosipUserDto authenticateUser(@RequestBody LoginUserDto user) throws Exception {
		return ldapService.authenticateUser(user);
	}

	@RequestMapping(value = "/verify_otp_user", method = RequestMethod.POST)
	public MosipUserDto verifyOtpUser(@RequestBody OtpUserDto otpUserDto) throws Exception {
		return ldapService.verifyOtpUser(otpUserDto);
	}

	@RequestMapping(value = "/allroles", method = RequestMethod.GET)
	public RolesListDto getAllRoles() {
		return ldapService.getAllRoles();

	}

	@RequestMapping(value = "/userdetails", method = RequestMethod.POST)
	public MosipUserListDto getListOfUsersDetails(@RequestBody List<String> users) throws Exception {
		return ldapService.getListOfUsersDetails(users);
	}
}
