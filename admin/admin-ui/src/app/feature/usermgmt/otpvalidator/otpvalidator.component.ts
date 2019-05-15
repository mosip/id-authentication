import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
@Component({
  selector: 'app-otpvalidator',
  templateUrl: './otpvalidator.component.html',
  styleUrls: ['./otpvalidator.component.css']
})
export class OtpvalidatorComponent implements OnInit, OnDestroy {
  otpAuthenticationForm: FormGroup;
  otpExpiryTimeReached: boolean;
  userMobileNumber: number;
  minutes: number;
  seconds: number;
  counter: number;
  interval: any;
  otpValidationStatus: string;


  constructor(private formBuilder: FormBuilder) {
    this.otpAuthenticationForm = this.formBuilder.group(
      {
        otp: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(6), Validators.pattern('[0-9]+')]]

      }
    );
  }


  ngOnInit() {
    this.otpExpiryTimeReached = false;
    this.startCountDown(120);
  }
  ngOnDestroy() {
    clearInterval(this.interval);
  }

  startCountDown(timeLeft: number): void {
    this.counter = 0;
    this.interval = setInterval(() => {
      document.getElementById('timer').innerHTML = this.convertSeconds(timeLeft - this.counter);
      this.counter++;
      if (this.counter > timeLeft) {
        clearInterval(this.interval);
        this.otpExpiryTimeReached = true;
      }
    }, 1000);
  }

  resendOTP() {
    this.otpExpiryTimeReached = false;
    clearInterval(this.interval);
    this.otpAuthenticationForm.get('otp').reset();
    this.startCountdown(120);
  }
  onSubmit() {
    // this.otpValidateModel.appId = 'admin';
    // this.otpValidateModel.otp = this.otpAuthenticationForm.get('otp').value;
    // this.otpValidateModel.userId = this.facadeService.getUserID();
    // this.requestModel = new RequestModel('id', 'v1', this.otpValidateModel, null);
    // this.facadeService.validateOTP(this.requestModel).subscribe(otpValidateResponse => {
    //   console.log(otpValidateResponse);
    //   this.otpValidationStatus = otpValidateResponse['response']['status'];
    //   if (this.otpValidationStatus === 'success') {
    //     this.router.navigate(['resetpassword']);
    //   }
    //   if (this.otpValidationStatus === 'failure') {
    //     alert('OTP Validation Failed');
    //   }
    // });
  }


  startCountdown(timeLeft: number): void {
    this.counter = 0;
    this.interval = setInterval(() => {
      document.getElementById('timer').innerHTML = this.convertSeconds(timeLeft - this.counter);
      this.counter++;
      if (this.counter > timeLeft) {
        clearInterval(this.interval);
        this.otpExpiryTimeReached = true;
      }
    }, 1000);
  }

  convertSeconds(timeLeft: number): string {
    this.minutes = Math.floor(timeLeft / 60);
    this.seconds = Math.floor(timeLeft % 60);
    return Number(this.minutes)
      .toLocaleString('en-US', { minimumIntegerDigits: 2 }) + ':' + Number(this.seconds).toLocaleString('en-US',
        { minimumIntegerDigits: 2 });
  }



}
