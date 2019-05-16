import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, Validators } from '@angular/forms';
import { FacadeService } from '../../shared/services/facade.service';
import { OtpSendModel } from '../../shared/models/otp-send-model';
import { RequestModel } from '../../shared/models/request-model';

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.css']
})
export class ForgotPasswordComponent implements OnInit {
  active: boolean;
  mobileNumber: number;
  errorMessage: string;
  userId: any;
  otpSendModel = {} as OtpSendModel;
  sendOTPStatus: string;
  requestModel: any;

  constructor(private facadeService: FacadeService, private router: Router, private formBuilder: FormBuilder) { }

  forgotPasswordForm = this.formBuilder.group({
    mobileNumber: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(10), Validators.pattern('[0-9]+')]]
  });

  ngOnInit() {
  }

  onSubmit() {
    this.active = true;
    this.facadeService.setContact(this.mobileNumber);
    this.facadeService.getUserNameFromPhoneNumber(this.mobileNumber).subscribe(result => {
      this.userId = result['response']['userName'];
      this.otpSendModel.appId = 'admin';
      this.otpSendModel.context = 'auth-otp';
      this.otpSendModel.otpChannel = ['email', 'mobile'];
      this.otpSendModel.templateVariables = null;
      this.otpSendModel.userId = this.userId;
      this.facadeService.setUserID(this.userId);
      this.otpSendModel.useridtype = 'USERID';
      this.requestModel = new RequestModel('id', 'v1', this.otpSendModel, null);
      this.facadeService.sendOTP(this.requestModel).subscribe(sendOTPResponse => {
        this.sendOTPStatus = sendOTPResponse['response']['status'];
        if (this.sendOTPStatus === 'success') {
          this.router.navigate(['otpauthentication']);
        } else {
          alert('OTP_SEND_FAILED');
        }
      });
    },
      error => {
        this.errorMessage = error;
      });
  }
}


