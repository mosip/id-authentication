import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { LoginServiceService } from '../../shared/services/login-service.service';
import { OtpSendModel } from '../../shared/models/otp-send-model';
import { RequestModel } from '../../shared/models/request-model';
import { PasswordValidateModel } from '../../shared/models/password-validate-model';
import {
  applicationVersion,
  appId,
  userIdType,
  loginOtpContext
} from '../../app.constants';

@Component({
  selector: 'app-authentication',
  templateUrl: './authentication.component.html',
  styleUrls: ['./authentication.component.css']
})
export class AuthenticationComponent implements OnInit, OnDestroy {
  minutes: number;
  seconds: number;
  counter: number;
  interval: any;
  authenticationForm: FormGroup;
  loggedIn: boolean;
  userId: string;
  authTypes: string[];
  showPassword: boolean;
  showOtp: boolean;
  otpSendModel = {} as OtpSendModel;
  requestDto: RequestModel;
  otpChannel: string[] = ['email', 'mobile'];
  passwordValidationRequest: PasswordValidateModel;
  otpValidation = {
    appId: 'admin',
    otp: '',
    userId: ''
  };
  errorMessage: boolean;
  otpStatus = false;
  passwordStatus: boolean;
  otpErrorMessage: boolean;

  constructor(
    private router: Router,
    private formBuilder: FormBuilder,
    private loginService: LoginServiceService,
    private activatedroute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.authenticationForm = this.formBuilder.group({
      password: ['', Validators.compose([Validators.required])],
      otp: ['', Validators.compose([Validators.required])]
    });
    this.activatedroute.params.subscribe(param => (this.userId = param.userId));
    console.log(this.loginService.getAuthTypes());
    this.authTypes = this.loginService.getAuthTypes();
    this.displayPasswordAndOtp();
    this.sendOtp();
  }

  ngOnDestroy(): void {
    clearInterval(this.interval);
  }

  displayPasswordAndOtp() {
    if (this.authTypes.includes('password')) {
      this.showPassword = true;
    }
    if (this.authTypes.includes('otp')) {
      this.showOtp = true;
    }
  }
  sendOtp(): void {
    if (this.authTypes.includes('otp')) {
      this.otpSendModel.userId = this.userId;
      this.otpSendModel.otpChannel = this.otpChannel;
      this.otpSendModel.appId = appId;
      this.otpSendModel.useridtype = userIdType;
      this.otpSendModel.templateVariables = null;
      this.otpSendModel.context = loginOtpContext;
      this.requestDto = new RequestModel(
        'mosip.admin.authentication.sendotp',
        applicationVersion,
        this.otpSendModel,
        null
      );
      this.startCountdown(120);
      this.loginService.sendOtp(this.requestDto).subscribe(response => {
        console.log(response);
      });
    }
  }
  startCountdown(timeLeft: number): void {
    this.counter = 0;
    this.interval = setInterval(() => {
      document.getElementById('timer').innerHTML = this.convertSeconds(
        timeLeft - this.counter
      );
      this.counter++;
      if (this.counter > timeLeft) {
        clearInterval(this.interval);
      }
    }, 1000);
  }
  convertSeconds(timeLeft: number): string {
    this.minutes = Math.floor(timeLeft / 60);
    this.seconds = Math.floor(timeLeft % 60);
    return (
      Number(this.minutes).toLocaleString('en-US', {
        minimumIntegerDigits: 2
      }) +
      ':' +
      Number(this.seconds).toLocaleString('en-US', { minimumIntegerDigits: 2 })
    );
  }
  onSubmit(values) {
    if (this.authTypes.includes('password') && this.authTypes.length === 1) {
      console.log(values);
      this.passwordValidationRequest = new PasswordValidateModel();
      this.passwordValidationRequest.userName = this.userId;
      this.passwordValidationRequest.password = values['password'];
      this.passwordValidationRequest.appId = appId;
      this.requestDto = new RequestModel(
        'mosip.admin.authentication.useridPwd',
        applicationVersion,
        JSON.parse(JSON.stringify(this.passwordValidationRequest)),
        null
      );
      console.log(this.requestDto);
      this.loginService
        .validateUserIdPassword(this.requestDto)
        .subscribe(({ response, errors }) => {
          if (errors === null || response != null) {
            if (response.status === 'success') {
              this.router.navigateByUrl('admin/dashboard');
            } else {
              console.log(errors);
              this.errorMessage = true;
            }
          }
        });
    } else {
      this.passwordValidationRequest = new PasswordValidateModel();
      this.passwordValidationRequest.appId = appId;
      this.passwordValidationRequest.userName = this.userId;
      this.passwordValidationRequest.password = values['password'];
      this.requestDto = new RequestModel(
        'mosip.admin.authentication.useridPwd',
        applicationVersion,
        JSON.parse(JSON.stringify(this.passwordValidationRequest)),
        null
      );
      this.loginService
        .validateUserIdPassword(this.requestDto)
        .subscribe(response => {
          console.log(response);
          if (response['errors'] === null) {
            if (response['response'].status === 'success') {
              this.passwordStatus = true;
            } else {
              this.passwordStatus = false;
            }
          } else {
            this.errorMessage = true;
          }
        });
      this.otpValidation.userId = this.userId;
      this.otpValidation.otp = values['otp'];
      this.requestDto = new RequestModel(
        'mosip.admin.authentication.useridOTP',
        applicationVersion,
        JSON.parse(JSON.stringify(this.otpValidation)),
        null
      );
      this.loginService.verifyOtp(this.requestDto).subscribe(response => {
        console.log(response);
        if (response['errors'] === null) {
          if (response['response'].status === 'success') {
            this.otpStatus = true;
          } else {
            this.otpStatus = false;
          }
        } else {
          console.log(response['errors']);
          this.otpErrorMessage = true;
        }
      });
    }
    if (this.passwordStatus && this.otpStatus) {
      this.router.navigateByUrl('admin/dashboard');
    }
  }
  onForgotPassword() {
    this.router.navigateByUrl('forgotpassword');
  }
}
