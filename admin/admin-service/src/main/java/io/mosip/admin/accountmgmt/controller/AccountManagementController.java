package io.mosip.admin.accountmgmt.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.admin.accountmgmt.dto.PasswordDto;
import io.mosip.admin.accountmgmt.dto.UnBlockResponseDto;
import io.mosip.admin.accountmgmt.dto.UserNameDto;
import io.mosip.admin.accountmgmt.service.AccountManagementService;
import io.mosip.kernel.core.http.ResponseFilter;
import io.swagger.annotations.Api;


/**
 *  AccountManagementController.
 *  @author Srinivasan
 *  @since 1.0.0
 */
@RestController
@RequestMapping(value = "/accountmanagement")
@Api(tags = { "AccountManagement" })
public class AccountManagementController {

	/** The account management service. */
	@Autowired
	AccountManagementService accountManagementService;

	/**
	 * Change password.
	 *
	 * @param passwordDto the password dto
	 * @param otpChannel the otp channel
	 * @return the string
	 */
	@PostMapping("/changepassword")
	public String changePassword(@RequestBody @Valid PasswordDto passwordDto, String otpChannel) {
		return null;
	}

	/**
	 * Reset password.
	 *
	 * @param passwordDto the password dto
	 * @param otpChannel the otp channel
	 */
	@PostMapping("/resetPassword")
	public void resetPassword(@RequestBody PasswordDto passwordDto, String otpChannel) {
	}

	/**
	 * Forgot username.
	 *
	 * @param userId the user id
	 * @return the user name dto
	 */
	@ResponseFilter
	@GetMapping("/forgotusername")
	public UserNameDto forgotUsername(String userId) {
        return accountManagementService.getUserName(userId);
	}

	/**
	 * Un block account.
	 *
	 * @param userId the user id
	 */
	@GetMapping("/unblockaccount")
	public UnBlockResponseDto unBlockAccount(String userId) {
       return accountManagementService.unBlockUserName(userId);
	}
}
