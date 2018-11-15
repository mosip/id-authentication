import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Params } from '@angular/router';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { MatButtonToggleChange, MatDatepickerInputEvent } from '@angular/material';

import { RegistrationService } from '../registration.service';
import { DemoLabels } from './demographic.labels';

@Component({
  selector: 'app-demographic',
  templateUrl: './demographic.component.html',
  styleUrls: ['./demographic.component.css']
})
export class DemographicComponent implements OnInit {
  step = 0;
  demo = new DemoLabels(
    'Full Name',
    'Date Of Birth',
    'gender',
    'Address Line 1',
    'Address Line 2',
    'Address Line 3',
    'Region',
    'Province',
    'City',
    'Postal Code',
    'Local Administrative Authority',
    'Email Id',
    'Mobile Number',
    'CNE/PIN Number',
    'Age'
  );
  ageOrDobPref = 'dob';
  showCalender = true;
  dateSelected: string;
  showDate = false;
  numberOfApplicants: number;
  userForm: FormGroup;
  numbers: number[];
  isDisabled = [];
  checked: boolean;
  editMode = false;

  isPrimary = 'false';
  fullName = '';
  gender = '';
  addressLine1 = '';
  addressLine2 = '';
  addressLine3 = '';
  region = '';
  province = '';
  city = '';
  localAdministrativeAuthority = '';
  email = '';
  dob: Date;
  age: number;
  postalCode: number;
  mobilePhone: number;
  pin: number;

  constructor(private route: ActivatedRoute, private regService: RegistrationService) {}

  ngOnInit() {
    this.route.params.subscribe((params: Params) => {
      this.numberOfApplicants = +params['id'];
      this.numbers = Array(this.numberOfApplicants)
        .fill(0)
        .map((x, i) => i);
      this.initForm();
    });
    this.isDisabled[0] = true;
  }

  initForm() {
    if (this.step === 0) {
      this.isPrimary = 'true';
    } else {
      this.isPrimary = 'false';
    }
    this.userForm = new FormGroup({
      isPrimary: new FormControl(this.isPrimary),
      fullName: new FormControl(this.fullName, Validators.required),
      gender: new FormControl(this.gender),
      addressLine1: new FormControl(this.addressLine1, Validators.required),
      addressLine2: new FormControl(this.addressLine2),
      addressLine3: new FormControl(this.addressLine3),
      region: new FormControl(this.region),
      province: new FormControl(this.province),
      city: new FormControl(this.city),
      localAdministrativeAuthority: new FormControl(this.localAdministrativeAuthority),
      email: new FormControl(this.email),
      age: new FormControl(this.age),
      dob: new FormControl(this.dob),
      postalCode: new FormControl(this.postalCode, Validators.required),
      mobilePhone: new FormControl(this.mobilePhone),
      pin: new FormControl(this.pin)
    });
  }

  setStep(index: number) {
    this.step = index;
  }

  nextStep() {
    this.onSubmit().subscribe(
      response => {
        console.log(response);
      },
      error => console.log(error),
      () => {
        this.isDisabled[this.step] = true;
        this.step++;
      }
    );
  }

  prevStep() {
    this.editMode = true;
    this.step--;
  }

  onSubmit() {
    if (this.editMode) {
      // this.regService.updateUser(this.id, this.userForm.value);
    } else {
      // this.regService.addUser(this.userForm.value);
    }
    return this.regService.addUser(this.userForm.value);
    //  .subscribe(response => {
    //     console.log(response);
    // this.isDisabled[this.step] = true;
    // this.step++;
    //   });
  }

  onGenderChange(gender: MatButtonToggleChange) {
    console.log(gender);
    this.userForm.controls.gender.patchValue(gender.value);
  }

  addDOB(event: MatDatepickerInputEvent<Date>) {
    const pipe = new DatePipe('en-US');
    const formattedDate = pipe.transform(event.value, 'dd/MM/yyyy');
    console.log('test date' + formattedDate);
    this.dateSelected = formattedDate;
    this.showDate = true;
  }

  onDOBChange(value) {
    console.log(value);
    if (value === 'age') {
      this.showCalender = false;
    }
    if (value === 'dob') {
      this.showCalender = true;
    }
  }
}
