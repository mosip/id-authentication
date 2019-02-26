import { Component, OnInit, Inject } from '@angular/core';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';

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
  selectedOption = null;
  confirm = true;
  applicantNumber;
  applicantEmail;
  inputList = [];
  invalidApplicantNumber = false;
  invalidApplicantEmail = false;
  selectedName: any;
  addedList = [];
  disableAddButton = true;
  constructor(public dialogRef: MatDialogRef<DialougComponent>, @Inject(MAT_DIALOG_DATA) public data: DialogData) {}

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
    if ((!isNaN(this.applicantNumber) && this.applicantNumber.length === 10)) {
      this.inputList[1] = this.applicantNumber;
      this.invalidApplicantNumber = false;
    } else {
      this.invalidApplicantNumber = true;
    }
  }

  validateEmail() {
    const re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    if (re.test(String(this.applicantEmail).toLowerCase())) {
      this.inputList[0] = this.applicantEmail;
      this.invalidApplicantEmail = false;
    } else {
      this.invalidApplicantEmail = true;
    }
  }

  addToList() {
    this.addedList.push(this.selectedName);
    this.input.names.splice(this.input.names.indexOf(this.selectedName), 1);
    this.selectedName = {};
    this.disableAddButton = true;
  }

  itemDelete(item: any) {
    this.input.names.push(item);
    this.addedList.splice(this.addedList.indexOf(item), 1);
  }

  enableButton() {
    this.disableAddButton = false;
  }
}
