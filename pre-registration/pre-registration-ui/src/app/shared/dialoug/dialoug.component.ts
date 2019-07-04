import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { AuthService } from 'src/app/auth/auth.service';
import { Location } from '@angular/common';
import { RegistrationService } from 'src/app/core/services/registration.service';
import * as appConstants from '../../app.constants';
import { ConfigService } from 'src/app/core/services/config.service';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material';
import { RouterExtService } from '../router/router-ext.service';

export interface DialogData {
  case: number;
}

@Component({
  selector: 'app-dialoug',
  templateUrl: './dialoug.component.html',
  styleUrls: ['./dialoug.component.css']
})
export class DialougComponent implements OnInit {
  input;
  message = {};
  selectedOption = null;
  confirm = true;
  isChecked = true;
  applicantNumber;
  checkCondition;
  applicantEmail;
  inputList = [];
  invalidApplicantNumber = false;
  invalidApplicantEmail = false;
  selectedName: any;
  addedList = [];
  disableAddButton = true;
  disableSend = true;

  constructor(
    public dialogRef: MatDialogRef<DialougComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData | any,
    private authService: AuthService,
    private location: Location,
    private regService: RegistrationService,
    private config: ConfigService,
    private router: Router,
    private dialogBox: MatDialog,
    private routerService: RouterExtService
  ) {}

  ngOnInit() {
    this.input = this.data;
  }

  onNoClick(): void {
    this.dialogRef.close();
  }

  onSubmit(): void {
    this.onNoClick();
  }

  validateMobile() {
    const re = new RegExp(this.config.getConfigByKey(appConstants.CONFIG_KEYS.mosip_regex_phone));
    if (re.test(String(this.applicantNumber).toLowerCase())) {
      this.inputList[1] = this.applicantNumber;
      this.invalidApplicantNumber = false;
      this.disableSend = false;
    } else {
      this.invalidApplicantNumber = true;
      this.disableSend = true;
    }
  }

  validateEmail() {
    const re = new RegExp(this.config.getConfigByKey(appConstants.CONFIG_KEYS.mosip_regex_email));
    if (re.test(String(this.applicantEmail).toLowerCase())) {
      this.inputList[0] = this.applicantEmail;
      this.invalidApplicantEmail = false;
      this.disableSend = false;
    } else {
      this.invalidApplicantEmail = true;
      this.disableSend = true;
    }
  }

  enableButton(email, mobile) {
    if (!email.value && !mobile.value) {
      this.disableSend = true;
      this.invalidApplicantEmail = false;
      this.invalidApplicantNumber = false;
    } else if (email.value && !mobile.value && !this.invalidApplicantEmail) this.disableSend = false;
    else if (mobile.value && !email.value && !this.invalidApplicantNumber) this.disableSend = false;
    else if (!this.invalidApplicantEmail && !this.invalidApplicantNumber) this.disableSend = false;
  }

  onSelectCheckbox() {
    this.isChecked = !this.isChecked;
  }

  async userRedirection() {
    if (localStorage.getItem('newApplicant') === 'true') {
      await this.firstPopUp();
    } else {
      this.regService.currentMessage.subscribe(message => (this.message = message));
      this.checkCondition = this.message['modifyUserFromPreview'];

      if (this.checkCondition === 'false') {
        await this.thirdPopUp();
      } else {
        await this.secondPopUp();
      }
    }
  }

  firstPopUp() {
    const data = {
      case: 'MESSAGE',
      message: this.input.alertMessageFirst
    };
    this.dialogBox
      .open(DialougComponent, {
        width: '460px',
        data: data
      })
      .afterClosed()
      .subscribe(() => this.loggingUserOut());
  }

  secondPopUp() {
    const data = {
      case: 'MESSAGE',
      message: this.input.alertMessageSecond
    };
    this.dialogBox
      .open(DialougComponent, {
        width: '460px',
        data: data
      })
      .afterClosed()
      .subscribe(() => this.redirectingUser());
  }

  thirdPopUp() {
    const data = {
      case: 'MESSAGE',
      message: this.input.alertMessageThird
    };
    this.dialogBox
      .open(DialougComponent, {
        width: '460px',
        data: data
      })
      .afterClosed()
      .subscribe(() => this.redirectingUser());
  }

  loggingUserOut() {
    this.authService.onLogout();
  }
  redirectingUser() {
    let url = this.routerService.getPreviousUrl();
    if (url.includes('preview')) this.router.navigate(['pre-registration/summary/preview']);
    else this.router.navigate(['dashboard']);
  }
}
