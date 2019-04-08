package io.mosip.kernel.auth.login.service.controller;

import java.net.HttpCookie;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.auth.login.service.dto.AuthNResponse;
import io.mosip.kernel.auth.login.service.dto.LoginUserDTO;
import io.mosip.kernel.auth.login.service.service.AuthDemoService;




@CrossOrigin(origins={"http://localhost:4200"},allowCredentials="true",exposedHeaders= {"Set-Cookie"})
@RestController
public class KernelAuthDemoLoginController {
	
	@Autowired
	private AuthDemoService authDemoService;

	
	@PostMapping(value = "/login")
	public ResponseEntity<AuthNResponse> authenticateUseridPwd(@RequestBody LoginUserDTO request,
			HttpServletResponse res){
		AuthNResponse authNResponse = null;
		ResponseEntity<AuthNResponse> authResponseDto = authDemoService.authenticateUser(request);
	    Cookie cookie=null;
		if (authResponseDto != null) {
			List<HttpCookie> cookies= HttpCookie.parse(authResponseDto.getHeaders().get("Set-Cookie").get(0));
			for(HttpCookie co:cookies) {
				if(co.getName().contains("Authorization")) {
					cookie = new Cookie("Authorization", co.getValue());
					cookie.setMaxAge(6000000);
					cookie.setHttpOnly(true);
					cookie.setSecure(false);
					cookie.setPath("/");
				}
			}
			authNResponse = new AuthNResponse();
			res.addCookie(cookie);
			authNResponse.setMessage(authResponseDto.getBody().getMessage());
		}
		return new ResponseEntity<>(authNResponse, HttpStatus.OK);
	}
	

	@PostMapping("/logout")
	public ResponseEntity<AuthNResponse> logoutUser(HttpServletRequest request, HttpServletResponse res) {
        ResponseEntity<AuthNResponse> reso=authDemoService.logoutUser(request, res);
		AuthNResponse authNResponse = new AuthNResponse();
		authNResponse.setMessage(reso.getBody().getMessage());
		
		return new ResponseEntity<>(authNResponse, HttpStatus.OK);
	}
	
	


}
