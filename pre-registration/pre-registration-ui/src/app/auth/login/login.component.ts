import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { DialougComponent } from 'src/app/shared/dialoug/dialoug.component';
import { MatDialog } from '@angular/material';
import { AuthService } from '../auth.service';
import { DataStorageService } from 'src/app/core/services/data-storage.service';
import { RegistrationService } from 'src/app/core/services/registration.service';
import { ConfigService } from 'src/app/core/services/config.service';
import * as appConstants from '../../app.constants';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  languages: string[] = [];

  inputPlaceholderContact = 'Email ID or Phone Number';
  inputPlaceholderOTP = 'Enter OTP';
  disableBtn = false;
  timer: any;
  secondaryLangCode = 'ar';
  secondaryDir = 'rtl';
  inputContactDetails = '';
  inputOTP: string;
  selectedLanguage = '';
  langCode = 'ara';
  dir = 'ltr';
  showSendOTP = true;
  showResend = false;
  showVerify = false;
  showContactDetails = true;
  showOTP = false;
  secondaryLanguagelabels: any;
  loggedOutLang: string;
  errorMessage: string;
  minutes: string;
  seconds: string;
  showSpinner = true;

  constructor(
    private authService: AuthService,
    private router: Router,
    private translate: TranslateService,
    private dialog: MatDialog,
    private dataService: DataStorageService,
    private regService: RegistrationService,
    private configService: ConfigService
  ) {
    const loggedOut = localStorage.getItem('loggedOut');
    this.loggedOutLang = localStorage.getItem('loggedOutLang');
    localStorage.clear();
    localStorage.setItem('loggedOut', loggedOut);
    localStorage.setItem('langCode', this.langCode);

    this.showMessage();
  }

  ngOnInit() {
    this.showSpinner = true;
    if (localStorage.getItem('langCode')) {
      this.langCode = localStorage.getItem('langCode');
      if (this.loggedOutLang) {
        this.translate.use(this.loggedOutLang);
      } else {
        this.translate.use('ara');
      }
    }
    localStorage.setItem('loggedIn', 'false');
    this.loadConfigs();
  }

  loginIdValidator() {
    this.errorMessage = undefined;
    const modes = this.configService.getConfigByKey('mosip.login.mode');
    const emailRegex = new RegExp(this.configService.getConfigByKey('mosip.regex.email'));
    const phoneRegex = new RegExp(this.configService.getConfigByKey('mosip.regex.phone'));
    if (modes === 'email,mobile') {
      if (!(emailRegex.test(this.inputContactDetails) || phoneRegex.test(this.inputContactDetails))) {
        this.errorMessage = 'Invalid Email or Mobile Number entered';
      }
    } else if (modes === 'email') {
      if (!emailRegex.test(this.inputContactDetails)) {
        this.errorMessage = 'Invalid email Entered';
      }
    } else if (modes === 'mobile') {
      if (!phoneRegex.test(this.inputContactDetails)) {
        this.errorMessage = 'Invalid email Entered';
      }
    } 
    console.log('errorMessage', this.errorMessage);
  }


  loadConfigs() {
    this.dataService.getConfig().subscribe(response => {
      this.configService.setConfig(response);
      this.setTimer();
      this.loadLanguagesWithConfig();
    }, error => {
      this.router.navigate(['error']);
    });
  }

  loadLanguagesWithConfig () {
    const primaryLang = this.configService.getConfigByKey('mosip.primary-language');
    const secondaryLang = this.configService.getConfigByKey('mosip.secondary-language');
    if (appConstants.languageMapping[primaryLang] && appConstants.languageMapping[secondaryLang]) {
      this.languages.push(appConstants.languageMapping[primaryLang].langName);
      this.languages.push(appConstants.languageMapping[secondaryLang].langName);
    }
    this.translate.addLangs([primaryLang, secondaryLang]);
    this.showSpinner = false;
  }

  setTimer() {
    const time = Number(this.configService.getConfigByKey('mosip.kernel.otp.expiry-time'));
    console.log('time', this.configService.getConfigByKey('mosip.kernel.otp.expiry-time'));
    const minutes = time / 60;
    const seconds = time % 60;
    if (minutes < 10) {
      this.minutes = '0' + minutes;
    } else {
      this.minutes = String(minutes);
    }
    if (seconds < 10) {
      this.seconds = '0' + seconds;
    } else {
      this.seconds = String(seconds);
    }
  }

  showMessage() {
    if (this.loggedOutLang) {
      this.dataService.getSecondaryLanguageLabels(this.loggedOutLang).subscribe(async response => {
        this.secondaryLanguagelabels = response['login']['logout_msg'];
        localStorage.removeItem('loggedOutLang');
        localStorage.removeItem('loggedOut');
        const data = {
          case: 'MESSAGE',
          message: this.secondaryLanguagelabels
        };
        this.dialog.open(DialougComponent, {
          width: '350px',
          data: data
        });
      });
    }
  }

  changeLanguage(): void {
    if (this.selectedLanguage === 'English') {
      this.langCode = 'eng';
      this.secondaryLangCode = 'ara';
      this.dir = 'ltr';
      this.secondaryDir = 'rtl';
    } else if (this.selectedLanguage === 'French') {
      this.langCode = 'fra';
      this.dir = 'ltr';
      this.secondaryLangCode = 'ara';
      this.secondaryDir = 'rtl';
    } else if (this.selectedLanguage === 'Arabic') {
      this.langCode = 'ara';
      this.dir = 'rtl';
      this.secondaryLangCode = 'fra';
      this.secondaryDir = 'ltr';
    }
    this.translate.use(this.langCode);
    localStorage.setItem('langCode', this.langCode);
    localStorage.setItem('secondaryLangCode', this.secondaryLangCode);
    localStorage.setItem('dir', this.dir);
    localStorage.setItem('secondaryDir', this.secondaryDir);
  }

  showVerifyBtn() {
    if (this.inputOTP.length === Number(this.configService.getConfigByKey('mosip.kernel.otp.default-length'))) {
      this.showVerify = true;
      this.showResend = false;
    } else {
      this.showResend = true;
      this.showVerify = false;
    }
  }

  submit(): void {
    this.loginIdValidator();
    if ((this.showSendOTP || this.showResend) && this.errorMessage === undefined) {
      this.showResend = true;
      this.showOTP = true;
      this.showSendOTP = false;
      this.showContactDetails = false;

      const timerFn = () => {
        let secValue = Number(document.getElementById('secondsSpan').innerText);
        const minValue = Number(document.getElementById('minutesSpan').innerText);

        if (secValue === 0) {
          secValue = 60;
          if (minValue === 0) {
            // redirecting to initial phase on completion of timer
            this.showContactDetails = true;
            this.showSendOTP = true;
            this.showResend = false;
            this.showOTP = false;
            this.showVerify = false;
            document.getElementById('minutesSpan').innerText = this.minutes;

            document.getElementById('timer').style.visibility = 'hidden';
            clearInterval(this.timer);
            return;
          }

          document.getElementById('minutesSpan').innerText = '0' + (minValue - 1);
        }

        if (secValue === 10 || secValue < 10) {
          document.getElementById('secondsSpan').innerText = '0' + --secValue;
        } else {
          document.getElementById('secondsSpan').innerText = --secValue + '';
        }
      };

      // update of timer value on click of resend
      if (document.getElementById('timer').style.visibility === 'visible') {
        document.getElementById('secondsSpan').innerText = this.seconds;
        document.getElementById('minutesSpan').innerText = this.minutes;
      } else {
        // initial set up for timer
        document.getElementById('timer').style.visibility = 'visible';
        this.timer = setInterval(timerFn, 1000);
      }

      this.dataService.sendOtp(this.inputContactDetails).subscribe(response => {
        console.log(response);
      })

      // dynamic update of button text for Resend and Verify
    } else if (this.showVerify && this.errorMessage === undefined) {
      this.dataService.verifyOtp(this.inputContactDetails, this.inputOTP).subscribe(response => {
        console.log(response);
        if (!response['errors']) {
          clearInterval(this.timer);
          localStorage.setItem('loggedIn', 'true');
          this.authService.setToken();

          this.regService.setLoginId(this.inputContactDetails);
          this.router.navigate(['dashboard']);
        } else {
          console.log(response['error']);
          this.showOtpMessage();
        }
      }, error => {
        this.showOtpMessage();
      });
    }
  }

  showOtpMessage() {
    this.dataService.getSecondaryLanguageLabels(localStorage.getItem('langCode')).subscribe(response => {
      const message = {
        case: 'MESSAGE',
        message: response['message']['login']['msg3']
      };
      const dialogRef = this.dialog.open(DialougComponent, {
        width: '350px',
        data: message
      });
    });
  }
}
