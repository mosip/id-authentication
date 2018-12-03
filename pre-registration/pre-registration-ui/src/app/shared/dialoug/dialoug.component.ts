import { Component, OnInit, Inject, Input, ViewChild } from '@angular/core';
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
  invalidApplicantNumber = false;
  constructor(public dialogRef: MatDialogRef<DialougComponent>, @Inject(MAT_DIALOG_DATA) public data: DialogData) {}

  // tslint:disable-next-line:use-life-cycle-interface
  ngOnInit() {
    this.input = this.data;
  }

  onNoClick(): void {
    this.dialogRef.close();
  }

  onSubmit(): void {
    this.onNoClick();
    console.log('button clicked', this.selectedOption);
  }

  validNumberOfUsers() {
    if (this.applicantNumber > 10 || this.applicantNumber < 1) {
      this.invalidApplicantNumber = true;
    } else {
      this.invalidApplicantNumber = false;
    }
  }
}
