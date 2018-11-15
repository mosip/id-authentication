import { Component, OnInit, Output, EventEmitter, ViewChild } from '@angular/core';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { FormGroup, FormControl, Validators, FormArray } from '@angular/forms';
import { Time } from '@angular/common';
import { MatSlideToggleChange, MatButtonToggleChange, MatDatepickerInputEvent } from '@angular/material';
import { RegistrationService } from '../registration.service';

@Component({
  selector: 'app-demographic',
  templateUrl: './demographic.component.html',
  styleUrls: ['./demographic.component.css']
})
export class DemographicComponent implements OnInit {

  step = 0;
  @ViewChild('picker') date;
  showAge = false;
  showDOB = false;
  dateSelected: Date
  showDate = false;
  numberOfApplicants: number;
  userForm: FormGroup;
  numbers: number[];
  isDisabled = [];
  checked : boolean;
  editMode = false;


  isPrimary = false;
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

  constructor(private route: ActivatedRoute,
    private regService: RegistrationService) { }

  ngOnInit() {
    this.route.params
      .subscribe(
        (params: Params) => {
          this.numberOfApplicants = +params['id'];
          this.numbers = Array(this.numberOfApplicants).fill(0).map((x, i) => i); // [0,1,2,3,4]
          this.initForm();
        }
      );
    this.isDisabled[0] = true;
  }

  initForm() {
    if (this.step === 0) {
      this.isPrimary = true;
    }
    else {
      this.isPrimary = false;
    }
    this.userForm = new FormGroup({
      'isPrimary': new FormControl(this.isPrimary),
      'fullName': new FormControl(this.fullName, Validators.required),
      'gender': new FormControl(this.gender, Validators.required),
      'addressLine1': new FormControl(this.addressLine1, Validators.required),
      'addressLine2': new FormControl(this.addressLine2),
      'addressLine3': new FormControl(this.addressLine3),
      'region': new FormControl(this.region, Validators.required),
      'province': new FormControl(this.province, Validators.required),
      'city': new FormControl(this.city, Validators.required),
      'localAdministrativeAuthority': new FormControl(this.localAdministrativeAuthority, Validators.required),
      'email': new FormControl(this.email),
      'age': new FormControl(this.age),
      // 'dob': new FormControl(this.dob),
      'postalCode': new FormControl(this.postalCode, Validators.required),
      'mobilePhone': new FormControl(this.mobilePhone),
      'pin': new FormControl(this.pin)
    })
  }

  setStep(index: number) {
    this.step = index;
  }

  nextStep() {
    this.onSubmit();
    this.isDisabled[this.step] = true;
    this.step++;
    this.initForm();
  }

  prevStep() {
    this.editMode = true;
    this.step--;
  }

  onSubmit() {
    if (this.editMode) {
      // this.regService.updateRecipe(this.id, this.userForm.value);
    } else {
      // this.regService.addRecipe(this.userForm.value);
    }
    console.log(this.userForm.controls);

    console.log("save Form");
  }

  onGenderChange(gender: MatButtonToggleChange) {
    console.log(gender);
    this.userForm.controls.gender.patchValue(gender.value);
  }

  addDOB(event: MatDatepickerInputEvent<Date>) {
    console.log("date add " + event);
    this.showDate = true;
    this.dateSelected = event.value;
    this.userForm.addControl("dob", new FormControl(this.dateSelected));
  }

  onDOBChange(value: MatSlideToggleChange) {
    console.log(value);

    if (value.checked === true) {
      // this.checked = true;
      this.showAge = true;
      this.showDOB = false;
    } else {
      this.showAge = false;
      this.showDOB = true;
      console.log(Date.now())
      // var timeDiff = Math.abs(Date.now() - this.birthdate);
      //Used Math.floor instead of Math.ceil
      //so 26 years and 140 days would be considered as 26, not 27.
      // this.age = Math.floor((timeDiff / (1000 * 3600 * 24))/365);
      // this.checked = false;

    }
    // this.userForm.controls.dob.patchValue("female");
    // this.userForm.controls.age.patchValue("male");

  }
}
