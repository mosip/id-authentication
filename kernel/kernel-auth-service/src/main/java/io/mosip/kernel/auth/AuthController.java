package io.mosip.kernel.auth;

import io.mosip.kernel.auth.config.MosipEnvironment;
import io.mosip.kernel.auth.entities.*;
import io.mosip.kernel.auth.entities.otp.OtpUser;
import io.mosip.kernel.auth.entities.otp.OtpValidateRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private MosipEnvironment mosipEnvironment;

    @RequestMapping(value = "/authenticate/unpwd", method = RequestMethod.POST)
    public ResponseEntity<MosipUser> authenticateUser(@RequestBody LoginUser user) throws Exception {
        MosipUserWithToken mosipUserWithToken = authService.authenticateUser(user);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(mosipEnvironment.getAuthTokenHeader(), mosipUserWithToken.getToken());
        return new ResponseEntity(mosipUserWithToken.getMosipUser(), responseHeaders, HttpStatus.OK);
    }

    @RequestMapping(value = "/authenticate/otp", method = RequestMethod.POST)
    public ResponseEntity<MosipUser> authenticateWithOtp(@RequestBody OtpUser user) throws Exception {
        MosipUserWithToken mosipUserWithToken = authService.authenticateWithOtp(user);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(mosipEnvironment.getAuthTokenHeader(), mosipUserWithToken.getToken());
        return new ResponseEntity(mosipUserWithToken.getMosipUser(), responseHeaders, HttpStatus.OK);
    }

    @RequestMapping(value = "/authenticate/unpwdotp", method = RequestMethod.POST)
    public ResponseEntity<MosipUser> authenticateUserWithOtp(@RequestBody LoginUser user) throws Exception {
        MosipUserWithToken mosipUserWithToken = authService.authenticateUserWithOtp(user);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(mosipEnvironment.getAuthTokenHeader(), mosipUserWithToken.getToken());
        return new ResponseEntity(mosipUserWithToken.getMosipUser(), responseHeaders, HttpStatus.OK);
    }

    @RequestMapping(value = "/verify_otp", method = RequestMethod.POST)
    public ResponseEntity<MosipUser> verifyOtp(@RequestBody OtpValidateRequestDto otpValidateRequestDto, @RequestHeader("Authorization") String token) throws Exception {
        MosipUserWithToken mosipUserWithToken = authService.verifyOtp(otpValidateRequestDto, token);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(mosipEnvironment.getAuthTokenHeader(), mosipUserWithToken.getToken());
        return new ResponseEntity(mosipUserWithToken.getMosipUser(), responseHeaders, HttpStatus.OK);
    }

    @RequestMapping(value = "/validate_token", method = RequestMethod.GET)
    public ResponseEntity<MosipUser> validateToken(@RequestHeader("Authorization") String token) throws Exception {
        MosipUserWithToken mosipUserWithToken = authService.validateToken(token);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(mosipEnvironment.getAuthTokenHeader(), mosipUserWithToken.getToken());
        return new ResponseEntity(mosipUserWithToken.getMosipUser(), responseHeaders, HttpStatus.OK);
    }

    @RequestMapping(value = "/{userName}/logout", method = RequestMethod.GET)
    public ResponseEntity<Boolean> logout(@PathVariable String userName, @RequestHeader("Authorization") String token) throws Exception {
        Boolean isLoggedOut = authService.logout(userName, token);
        HttpHeaders responseHeaders = new HttpHeaders();
        return new ResponseEntity(isLoggedOut, responseHeaders, HttpStatus.OK);
    }
}
