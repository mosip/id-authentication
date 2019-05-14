import { Component, OnInit, OnDestroy } from '@angular/core';
import { GetContactService } from '../../shared/services/get-contact.service';

import { FacadeService } from '../../shared/services/facade.service';
import {FormBuilder, Validators} from '@angular/forms';
import { Router } from '@angular/router';

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

  constructor(private facadeService: FacadeService, private router: Router, private formBuilder: FormBuilder) { }

  otpAuthenticationForm = this.formBuilder.group({
    otp: ['', [Validators.required, Validators.pattern('[0-9]+')] ]
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
    this.router.navigate(['resetpassword']);
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
