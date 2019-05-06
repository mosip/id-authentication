package io.mosip.admin.accountmgmt.controller;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.admin.accountmgmt.dto.PasswordDto;
import io.swagger.annotations.Api;

@RestController
@RequestMapping(value = "/accountmanagement")
@Api(tags = { "AccountManagement" })
public class AccountManagementController {

	@PostMapping("/changepassword")
	public String changePassword(@RequestBody @Valid PasswordDto passwordDto,String otpChannel) {
        return null;
	}

	@PostMapping("/resetPassoword")
	public void resetPassword(@RequestBody PasswordDto passwordDto,String otpChannel) {
	}
	
	@GetMapping("/forgotusername")
	public void forgotUsername(String userId,String phoneNumber) {
		
	}
	
	@GetMapping("/unblockaccount")
	public void unBlockAccount(String userId,String otpChannel) {
		
	}
}
