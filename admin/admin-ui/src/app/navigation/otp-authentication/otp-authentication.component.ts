import { Component, OnInit, OnDestroy } from '@angular/core';

import { FacadeService } from '../../shared/services/facade.service';
import {FormBuilder, Validators} from '@angular/forms';
import { Router } from '@angular/router';

import {OtpValidateModel} from '../../shared/models/otp-validate-model';
import {RequestModel} from '../../shared/models/request-model';

@Component({
  selector: 'app-otp-authentication',
  templateUrl: './otp-authentication.component.html',
  styleUrls: ['./otp-authentication.component.css']
})
export class OtpAuthenticationComponent implements OnInit, OnDestroy {
  otpExpiryTimeReached: boolean;
  userMobileNumber: number;
  minutes: number;
  seconds: number;
  counter: number;
  interval: any;
  otpValidationStatus: string;
  otpValidateModel = {} as OtpValidateModel;
  requestModel: any;

  constructor(private facadeService: FacadeService, private router: Router, private formBuilder: FormBuilder) { }

  otpAuthenticationForm = this.formBuilder.group({
    otp: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(6), Validators.pattern('[0-9]+')] ]
  });

  ngOnInit() {
    this.otpExpiryTimeReached = false;
    this.startCountdown(120);
    this.userMobileNumber = this.facadeService.getContact();
  }

  ngOnDestroy(): void {
    clearInterval(this.interval);
  }

  onVerify() {
    this.otpValidateModel.appId = 'admin';
    this.otpValidateModel.otp = this.otpAuthenticationForm.get('otp').value;
    this.otpValidateModel.userId = this.facadeService.getUserID();
    this.requestModel = new RequestModel('id', 'v1', this.otpValidateModel, null);
    this.facadeService.validateOTP(this.requestModel).subscribe(otpValidateResponse => {
      console.log(otpValidateResponse);
      this.otpValidationStatus = otpValidateResponse['response']['status'];
      if (this.otpValidationStatus === 'success') {
        this.router.navigate(['resetpassword']);
      }
      if (this.otpValidationStatus === 'failure') {
        alert('OTP Validation Failed');
      }
    });
  }

  startCountdown(timeLeft: number): void {
    this.counter = 0;
    this.interval = setInterval(() => {
      document.getElementById('timer').innerHTML = this.convertSeconds(timeLeft - this.counter);
      this.counter++;
      if (this.counter > timeLeft ) {
        clearInterval(this.interval);
        this.otpExpiryTimeReached = true;
      }
    }, 1000);
  }

  convertSeconds(timeLeft: number): string {
    this.minutes = Math.floor(timeLeft / 60);
    this.seconds = Math.floor(timeLeft % 60);
    return Number(this.minutes)
    .toLocaleString('en-US', {minimumIntegerDigits: 2}) + ':' + Number(this.seconds).toLocaleString('en-US', {minimumIntegerDigits: 2});
   }

   resendOTP() {
    this.otpExpiryTimeReached = false;
    clearInterval(this.interval);
    this.otpAuthenticationForm.get('otp').reset();
    this.startCountdown(120);
   }
}
