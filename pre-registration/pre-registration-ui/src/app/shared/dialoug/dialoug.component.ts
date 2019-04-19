import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { AuthService } from 'src/app/auth/auth.service';
import { Location } from '@angular/common';
import { RegistrationService } from 'src/app/core/services/registration.service';
import * as appConstants from '../../app.constants';
import { ConfigService } from 'src/app/core/services/config.service';

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
    private config: ConfigService
  ) {}

  // tslint:disable-next-line:use-life-cycle-interface
  ngOnInit() {
    this.input = this.data;
    console.log('input', this.input);
  }

  onNoClick(): void {
    this.dialogRef.close();
  }

  onSubmit(): void {
    this.onNoClick();
    console.log('button clicked', this.selectedOption);
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
    console.log(email.value, mobile.value);
    if (!email.value && !mobile.value) {
      this.disableSend = true;
      this.invalidApplicantEmail = false;
      this.invalidApplicantNumber = false;
    }
    else if (email.value && !mobile.value && !this.invalidApplicantEmail)
      this.disableSend = false;
    else if (mobile.value && !email.value && !this.invalidApplicantNumber)
      this.disableSend = false;
    else if (!this.invalidApplicantEmail && !this.invalidApplicantNumber)
      this.disableSend = false;
  }

  onSelectCheckbox() {
    this.isChecked = !this.isChecked;
  }

  userRedirection() {
    if (localStorage.getItem('newApplicant') === 'true') {
      alert(this.input.alertMessageFirst);
      // this if for first time user, if he does not provide consent he will be logged out.
      this.authService.removeToken();
      this.location.back();
    } else if (localStorage.getItem('newApplicant') === 'false') {
      this.regService.currentMessage.subscribe(
        message => (this.message = message)
        //second case is when an existing applicant enters the application.
      );
      this.checkCondition = this.message['modifyUserFromPreview'];

      if (this.checkCondition === 'false') {
        alert(this.input.alertMessageThird);
        this.location.back();
      } else {
        alert(this.input.alertMessageSecond);
        this.location.back();
      }
    }
  }
}
