import { RequestModel } from './../../../shared/models/request-model';
import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { OtpValidator } from '../../../shared/models/otp-validator-model';
import { Router, ActivatedRoute, Params } from '@angular/router';
import { UserregistrationService } from '../../../shared/services/userregistration.service';
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
  userName: string;
  rid: string;
  otpValidatorModel = {} as OtpValidator;
  requestModel: any;
  constructor(private router: Router, private formBuilder: FormBuilder, private activatedRoute: ActivatedRoute, private service: UserregistrationService) {
    this.otpAuthenticationForm = this.formBuilder.group(
      {
        otp: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(6), Validators.pattern('[0-9]+')]]

      }
    );
  }


  ngOnInit() {
    this.otpExpiryTimeReached = false;
    this.startCountDown(120);
    this.activatedRoute.queryParams.subscribe(params => {
      const userNameQueryParam = params['username'];
      this.rid = params['rid'];
      this.userName = userNameQueryParam;
    });
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
    this.otpValidatorModel.appId = 'admin';
    this.otpValidatorModel.userId = this.userName;
    this.otpValidatorModel.otp = this.otpAuthenticationForm.get('otp').value;
    this.requestModel = new RequestModel('id', 'v1', this.otpValidatorModel, null);
    this.service.otpValidator(this.requestModel).subscribe(data => {
      if (data['response'] === null) {
        if (data['errors'] != null) {
          alert('OTP authentication failed');
          this.otpAuthenticationForm.reset();
        }
        return;
      }
      if (data['response']['status'] === 'success') {
        alert('OTP verified successfully');
        this.router.navigateByUrl('/admin/usermgmt/createpassword?username=' + this.userName + '&rid=' + this.rid);
      }
      if (data['response']['status'] === 'failure') {
        alert('OTP Validation Failed');
      }
    }, error => {
      console.log(error);
    });

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
