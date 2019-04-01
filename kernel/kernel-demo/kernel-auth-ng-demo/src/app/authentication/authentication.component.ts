import { OtpResponse } from './../otpresponse';
import { OtpRequest } from './../otprequest';
import { Application } from './../application';
import { AuthRequest } from './../authrequest';
import { Component, OnInit } from '@angular/core';
import { AuthServiceService } from '../auth-service.service';
import { LoginUser } from '../loginuser';
@Component({
  selector: 'app-authentication',
  templateUrl: './authentication.component.html',
  styleUrls: ['./authentication.component.css']
})
export class AuthenticationComponent  {

  loginuser: LoginUser;
  request: AuthRequest;
  application: Application;
  otpRequest: OtpRequest;

  constructor(private service: AuthServiceService) {

  }


  getToken() {
  this.request = new AuthRequest('prereguser', 'prereguser', 'PREREGISTRATION');
    this.loginuser = new LoginUser('mosip.authentication.useridPwd',
    new Date('2019-01-24T30:27:48.628Z'), '1.0', this.request);
    this.service.getToken(this.loginuser);
  }

  getApplication() {
    this.service.getApplication('PREREGISTRATION');
  }

  createApplication() {
  this.application = new Application('PREREGISTRATION');
    this.service.createApplication(this.application);
  }

  putApplication() {
    this.application = new Application('PREREGISTRATION');
    this.service.putApplication(this.application);
  }

  deleteApplication() {
    this.service.deleteApplication('PREREGISTRATION');
  }

  otp() {
    this.otpRequest = new OtpRequest('7865768694');

    this.service.otp(this.otpRequest);
  }

  logout() {
   this.service.logout();
    }
}
