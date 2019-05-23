import { Component, OnInit, OnDestroy, OnChanges } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { LoginServiceService } from '../../shared/services/login-service.service';
import { OtpSendModel } from '../../shared/models/otp-send-model';
import { RequestModel } from '../../shared/models/request-model';
import {
  USER_ID_TYPE,
  APPLICATION_ID,
  APP_VERSION,
  OTP_CONTEXT,
  OTP_CHANNEL
} from '../../app.constants';
import { PasswordValidateDto } from '../../shared/models/passwordValidateDto';
import { OtpValidateDto } from '../../shared/models/otpValidateDto';

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
  otpSendDto: OtpSendModel;
  RequestDto: RequestModel;
  passwordValidateDto: PasswordValidateDto;
  otpValidationDto: OtpValidateDto;
  errorMessage: boolean;
  otpStatus = false;
  passwordStatus: boolean;
  otpErrorMessage: boolean;
  buttonDisabled = false;

  constructor(
    private router: Router,
    private formBuilder: FormBuilder,
    private loginService: LoginServiceService,
    private activatedroute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.authenticationForm = this.formBuilder.group({
      password: ['', Validators.compose([Validators.required])],
      otp: [
        '',
        Validators.compose([
          Validators.required,
          Validators.minLength(6),
          Validators.maxLength(6)
        ])
      ]
    });
    this.activatedroute.params.subscribe(param => (this.userId = param.userId));
    console.log(this.loginService.getAuthTypes());
    this.authTypes = this.loginService.getAuthTypes();
    this.displayPasswordAndOtp();
    this.sendOtp();
  }
  enableButton(): void {
    if (this.authTypes.includes('password') && this.authTypes.length === 1) {
      if (this.passwordStatus) {
        this.buttonDisabled = true;
      }
    }
    if (this.authTypes.length === 2) {
      if (this.otpStatus && this.passwordStatus) {
        this.buttonDisabled = true;
      }
    }
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
      this.otpSendDto = new OtpSendModel();
      this.otpSendDto.userId = this.userId;
      this.otpSendDto.otpChannel = OTP_CHANNEL;
      this.otpSendDto.appId = APPLICATION_ID;
      this.otpSendDto.useridtype = USER_ID_TYPE;
      this.otpSendDto.templateVariables = null;
      this.otpSendDto.context = OTP_CONTEXT;
      this.RequestDto = new RequestModel(
        'mosip.admin.authentication.sendotp',
        APP_VERSION,
        this.otpSendDto,
        null
      );
      console.log(this.RequestDto);
      this.startCountdown(120);
      this.loginService.sendOtp(this.RequestDto).subscribe(response => {
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
  validateUserPassword(password) {
    console.log(password);
    this.passwordValidateDto = new PasswordValidateDto();
    this.passwordValidateDto.userName = this.userId;
    this.passwordValidateDto.password = password;
    this.passwordValidateDto.appId = APPLICATION_ID;
    this.RequestDto = new RequestModel(
      'mosip.admin.authentication.useridPwd',
      APP_VERSION,
      this.passwordValidateDto,
      null
    );
    this.loginService
      .validateUserIdPassword(this.RequestDto)
      .subscribe(({ response, errors }) => {
        console.log(response);
        if (errors === null) {
          if (response.status === 'success') {
            this.passwordStatus = true;
            this.errorMessage = false;
            this.enableButton();
          } else {
            this.passwordStatus = false;
          }
        } else {
          console.log(errors);
          this.errorMessage = true;
        }
      });
  }
  validateUserOtp(otp) {
    console.log(otp);
    if (otp.length === 6 ) {
      this.otpValidationDto = new OtpValidateDto();
      this.otpValidationDto.userId = this.userId;
      this.otpValidationDto.otp = otp;
      this.otpValidationDto.appId = APPLICATION_ID;
      this.RequestDto = new RequestModel(
        'mosip.admin.authentication.useridOTP',
        APP_VERSION,
        this.otpValidationDto,
        null
      );
      this.loginService
        .verifyOtp(this.RequestDto)
        .subscribe(({ response, errors }) => {
          console.log(response);
          if (errors === null) {
            if (response.status === 'success') {
              this.otpStatus = true;
              this.otpErrorMessage = false;
              localStorage.setItem('userName', this.userId);
              localStorage.setItem('loggedIn', ' true ');
              this.enableButton();
            } else if (response.status === 'failure') {
              this.otpErrorMessage = true;
              this.otpStatus = false;
            }
          } else {
            this.otpErrorMessage = true;
          }
        });
    }
  }
  onSubmit(values) {
    if (this.authTypes.includes('password') && this.authTypes.length === 1) {
      if (this.passwordStatus) {
        localStorage.setItem('userName', this.userId);
        localStorage.setItem('loggedIn', ' true ');
        this.router.navigateByUrl('admin/dashboard');
      }
    } else if (
      this.authTypes.includes('password') &&
      this.authTypes.includes('otp') &&
      this.authTypes.length === 2
    ) {
      if (this.passwordStatus && this.otpStatus) {
        localStorage.setItem('userName', this.userId);
        localStorage.setItem('loggedIn', ' true ');
        this.router.navigateByUrl('admin/dashboard');
      }
    }
  }
  onForgotPassword() {
    this.router.navigateByUrl('forgotpassword');
  }
}
