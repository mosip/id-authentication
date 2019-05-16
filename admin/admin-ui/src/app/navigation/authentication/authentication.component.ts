import { Component, OnInit, ViewChild, AfterViewInit } from "@angular/core";
import { Router, ActivatedRoute } from "@angular/router";
import { FormGroup, FormBuilder, Validators } from "@angular/forms";
import { LoginServiceService } from "../../shared/services/login-service.service";
import { OtpSendModel } from "../../shared/models/otp-send-model";
import { RequestModel } from "../../shared/models/request-model";

@Component({
  selector: "app-authentication",
  templateUrl: "./authentication.component.html",
  styleUrls: ["./authentication.component.css"]
})
export class AuthenticationComponent implements OnInit {
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
  RequestDto: RequestModel;
  otpChannel: string[] = ["email", "mobile"];
  passwordValidationRequest = {
    userName: "",
    password: "",
    appId: "admin"
  };
  otpValidation = {
    appId:'admin',
    otp: "",
    userId: ""
    
  };
  errorMessage: boolean;
  otpStatus: boolean = false;
  passwordStatus: boolean;

  constructor(
    private router: Router,
    private formBuilder: FormBuilder,
    private loginService: LoginServiceService,
    private activateroute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.authenticationForm = this.formBuilder.group({
      password: ["", Validators.compose([Validators.required])],
      otp: ["", Validators.compose([Validators.required])]
    });
    this.activateroute.params.subscribe(param => (this.userId = param.userId));
    console.log(this.loginService.getAuthTypes());
    this.authTypes = this.loginService.getAuthTypes();
    this.displayPasswordAndOtp();
    this.sendOtp();
  }

  ngOnDestroy(): void {
    clearInterval(this.interval);
  }

  displayPasswordAndOtp() {
    if (this.authTypes.includes("password")) {
      this.showPassword = true;
    }
    if (this.authTypes.includes("otp")) {
      this.showOtp = true;
    }
  }
  sendOtp(): void {
    if (this.authTypes.includes("otp")) {
      this.otpSendModel.userId = this.userId;
      this.otpSendModel.otpChannel = this.otpChannel;
      this.otpSendModel.appId = "admin";
      this.otpSendModel.useridtype = "USERID";
      this.otpSendModel.templateVariables = null;
      this.otpSendModel.context = "auth-otp";
      this.RequestDto = new RequestModel(
        "mosip.admin.authentication.sendotp",
        "1.0",
        this.otpSendModel,
        null
      );
      this.startCountdown(120);
      this.loginService.sendOtp(this.RequestDto).subscribe(response => {
        console.log(response);
      });
    }
  }
  startCountdown(timeLeft: number): void {
    this.counter = 0;
    this.interval = setInterval(() => {
      document.getElementById("timer").innerHTML = this.convertSeconds(
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
      Number(this.minutes).toLocaleString("en-US", {
        minimumIntegerDigits: 2
      }) +
      ":" +
      Number(this.seconds).toLocaleString("en-US", { minimumIntegerDigits: 2 })
    );
  }
  onSubmit(values) {
    if (this.authTypes.includes("password") && this.authTypes.length == 1) {
      console.log(values);
      this.passwordValidationRequest.userName = this.userId;
      this.passwordValidationRequest.password = values["password"];
      this.RequestDto = new RequestModel(
        "mosip.admin.authentication.useridPwd",
        "v1",
        JSON.parse(JSON.stringify(this.passwordValidationRequest)),
        null
      );
      this.loginService
        .validateUserIdPassword(this.RequestDto)
        .subscribe(response => {
          if (response["errors"] === null) {
            if (response["response"].status === "success") {
              this.router.navigateByUrl("admin/dashboard");
            }
          } else {
            console.log(response);
            this.errorMessage = true;
          }
        });
    } else {
      this.passwordValidationRequest.userName = this.userId;
      this.passwordValidationRequest.password = values["password"];
      this.RequestDto = new RequestModel(
        "mosip.admin.authentication.useridPwd",
        "v1",
        JSON.parse(JSON.stringify(this.passwordValidationRequest)),
        null
      );
      this.loginService
        .validateUserIdPassword(this.RequestDto)
        .subscribe(response => {
          console.log(response);
          if (response["errors"] === null) {
            if (response["response"].status === "success") {
              this.passwordStatus = true;
            } else {
              this.passwordStatus = false;
            }
          }else{
            this.errorMessage = true; 
          }
        });
      this.otpValidation.userId = this.userId;
      this.otpValidation.otp = values["otp"];
      this.RequestDto = new RequestModel(
        "mosip.admin.authentication.useridOTP",
        "v1",
        JSON.parse(JSON.stringify(this.otpValidation)),
        null
      );
      this.loginService.verifyOtp(this.RequestDto).subscribe(response => {
        console.log(response);
        if (response["errors"] === null) {
          if (response["response"].status === "success") {
            console.log(response);
            this.otpStatus = true;
          } else {
            this.otpStatus = false;
          }
        }
      });
    }
    if(this.passwordStatus && this.otpStatus){
      this.router.navigateByUrl('admin/dashboard');
    }
  }
  onForgotPassword() {
    this.router.navigateByUrl("forgotpassword");
  }
}
