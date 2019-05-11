package io.mosip.admin.accountmgmt.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.admin.accountmgmt.dto.PasswordDto;
import io.mosip.admin.accountmgmt.dto.StatusResponseDto;
import io.mosip.admin.accountmgmt.dto.UserNameDto;
import io.mosip.admin.accountmgmt.service.AccountManagementService;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseFilter;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.swagger.annotations.Api;

/**
 * AccountManagementController.
 * 
 * @author Srinivasan
 * @since 1.0.0
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
	 * @param passwordDto
	 *            the password dto
	 * @param otpChannel
	 *            the otp channel
	 * @return the string
	 */
	@ResponseFilter
	@PostMapping("/changepassword")
	public ResponseWrapper<StatusResponseDto> changePassword(@RequestBody @Valid RequestWrapper<PasswordDto> passwordDto) {
		ResponseWrapper<StatusResponseDto> responseWrapper= new ResponseWrapper<>();
		responseWrapper.setResponse(accountManagementService.changePassword(passwordDto.getRequest()));
		return responseWrapper;
	}

	/**
	 * Reset password.
	 *
	 * @param passwordDto
	 *            the password dto
	 * @param otpChannel
	 *            the otp channel
	 */
	@PostMapping("/resetPassword")
	public ResponseWrapper<StatusResponseDto> resetPassword(@RequestBody RequestWrapper<PasswordDto> passwordDto) {
		ResponseWrapper<StatusResponseDto> responseWrapper= new ResponseWrapper<>();
		responseWrapper.setResponse(accountManagementService.resetPassword(passwordDto.getRequest()));
		return responseWrapper;
	}

	/**
	 * Forgot username.
	 *
	 * @param userId
	 *            the user id
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
	 * @param userId
	 *            the user id
	 */
	@ResponseFilter
	@GetMapping("/unblockaccount")
	public StatusResponseDto unBlockAccount(String userId) {
		return accountManagementService.unBlockUserName(userId);
	}

	@ResponseFilter
	@GetMapping("/username/{mobilenumber}")
	public UserNameDto getUserName(@PathVariable("mobilenumber") String mobile) throws Exception {
		return accountManagementService.getUserNameBasedOnMobileNumber(mobile);
	}

}
