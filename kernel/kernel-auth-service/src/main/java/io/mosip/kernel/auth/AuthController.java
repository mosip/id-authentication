package io.mosip.kernel.auth;

import io.mosip.kernel.auth.config.MosipEnvironment;
import io.mosip.kernel.auth.dto.*;
import io.mosip.kernel.auth.dto.otp.OtpUserDto;
import io.mosip.kernel.auth.dto.otp.OtpValidateRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private MosipEnvironment mosipEnvironment;

    @RequestMapping(value = "/authenticate/unpwd", method = RequestMethod.POST)
    public ResponseEntity<MosipUserDto> authenticateUser(@RequestBody LoginUserDto user) throws Exception {
        MosipUserWithTokenDto mosipUserWithTokenDto = authService.authenticateUser(user);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(mosipEnvironment.getAuthTokenHeader(), mosipUserWithTokenDto.getToken());
        return new ResponseEntity(mosipUserWithTokenDto.getMosipUserDto(), responseHeaders, HttpStatus.OK);
    }

    @RequestMapping(value = "/authenticate/otp", method = RequestMethod.POST)
    public ResponseEntity<MosipUserDto> authenticateWithOtp(@RequestBody OtpUserDto user) throws Exception {
        MosipUserWithTokenDto mosipUserWithTokenDto = authService.authenticateWithOtp(user);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(mosipEnvironment.getAuthTokenHeader(), mosipUserWithTokenDto.getToken());
        return new ResponseEntity(mosipUserWithTokenDto.getMosipUserDto(), responseHeaders, HttpStatus.OK);
    }

    @RequestMapping(value = "/authenticate/unpwdotp", method = RequestMethod.POST)
    public ResponseEntity<MosipUserDto> authenticateUserWithOtp(@RequestBody LoginUserDto user) throws Exception {
        MosipUserWithTokenDto mosipUserWithTokenDto = authService.authenticateUserWithOtp(user);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(mosipEnvironment.getAuthTokenHeader(), mosipUserWithTokenDto.getToken());
        return new ResponseEntity(mosipUserWithTokenDto.getMosipUserDto(), responseHeaders, HttpStatus.OK);
    }

    @RequestMapping(value = "/verify_otp", method = RequestMethod.POST)
    public ResponseEntity<MosipUserDto> verifyOtp(@RequestBody OtpValidateRequestDto otpValidateRequestDto, @RequestHeader("Authorization") String token) throws Exception {
        MosipUserWithTokenDto mosipUserWithTokenDto = authService.verifyOtp(otpValidateRequestDto, token);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(mosipEnvironment.getAuthTokenHeader(), mosipUserWithTokenDto.getToken());
        return new ResponseEntity(mosipUserWithTokenDto.getMosipUserDto(), responseHeaders, HttpStatus.OK);
    }

    @RequestMapping(value = "/validate_token", method = RequestMethod.GET)
    public ResponseEntity<MosipUserDto> validateToken(@RequestHeader("Authorization") String token) throws Exception {
        MosipUserWithTokenDto mosipUserWithTokenDto = authService.validateToken(token);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(mosipEnvironment.getAuthTokenHeader(), mosipUserWithTokenDto.getToken());
        return new ResponseEntity(mosipUserWithTokenDto.getMosipUserDto(), responseHeaders, HttpStatus.OK);
    }

    @RequestMapping(value = "/{userName}/logout", method = RequestMethod.GET)
    public ResponseEntity<Boolean> logout(@PathVariable String userName, @RequestHeader("Authorization") String token) throws Exception {
        Boolean isLoggedOut = authService.logout(userName, token);
        HttpHeaders responseHeaders = new HttpHeaders();
        return new ResponseEntity(isLoggedOut, responseHeaders, HttpStatus.OK);
    }

    @RequestMapping(value = "/roles", method = RequestMethod.GET)
    public ResponseEntity<RolesListDto> getAllRoles(@RequestHeader("Authorization") String token) throws Exception {
        MosipUserWithTokenDto mosipUserWithTokenDto = authService.validateToken(token);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(mosipEnvironment.getAuthTokenHeader(), mosipUserWithTokenDto.getToken());
        RolesListDto rolesListDto = authService.getAllRoles();
        return new ResponseEntity(rolesListDto, responseHeaders, HttpStatus.OK);
    }

    @RequestMapping(value = "/userdetails", method = RequestMethod.POST)
    public ResponseEntity<MosipUserListDto> getListOfUsersDetails(@RequestHeader("Authorization") String token, @RequestBody List<String> userDetails) throws Exception {
        MosipUserWithTokenDto mosipUserWithTokenDto = authService.validateToken(token);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(mosipEnvironment.getAuthTokenHeader(), mosipUserWithTokenDto.getToken());
        MosipUserListDto mosipUsers = authService.getListOfUsersDetails(userDetails);
        return new ResponseEntity(mosipUsers, responseHeaders, HttpStatus.OK);
    }
}
