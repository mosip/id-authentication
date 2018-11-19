import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Params } from '@angular/router';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { MatButtonToggleChange, MatDatepickerInputEvent } from '@angular/material';

import { RegistrationService } from '../registration.service';
import { DemoLabels } from './demographic.labels';
import { IdentityModel } from './identity.model';
import { AttributeModel } from './attribute.model';
import { RequestModel } from './request.model';
import { DemoIdentityModel } from './Demo.Identity.model';

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
  ageOrDobPref = '';
  showCalender: boolean;
  dateSelected: Date;
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
  dob: ' ';
  age: ' ';
  postalCode: '';
  mobilePhone: '';
  pin: ' ';

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
      age: new FormControl('', Validators.required),
      dob: new FormControl('', Validators.required),
      postalCode: new FormControl(this.postalCode, Validators.required),
      mobilePhone: new FormControl(''),
      pin: new FormControl('')
    });
  }

  setStep(index: number) {
    this.step = index;
  }

  nextStep() {
    this.onSubmit();
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

    const identity = new IdentityModel(
      [new AttributeModel('en', this.demo.fullName, this.userForm.controls.fullName.value)],
      [new AttributeModel('en', this.demo.dateOfBirth, this.userForm.controls.dob.value)],
      [new AttributeModel('en', this.demo.gender, this.userForm.controls.gender.value)],
      [new AttributeModel('en', this.demo.addressLine1, this.userForm.controls.addressLine1.value)],
      [new AttributeModel('en', this.demo.addressLine2, this.userForm.controls.addressLine2.value)],
      [new AttributeModel('en', this.demo.addressLine3, this.userForm.controls.addressLine3.value)],
      [new AttributeModel('en', this.demo.region, this.userForm.controls.region.value)],
      [new AttributeModel('en', this.demo.province, this.userForm.controls.province.value)],
      [new AttributeModel('en', this.demo.city, this.userForm.controls.city.value)],
      [new AttributeModel('en', this.demo.postalCode, this.userForm.controls.postalCode.value)],
      [
        new AttributeModel(
          'en',
          this.demo.localAdministrativeAuthority,
          this.userForm.controls.localAdministrativeAuthority.value
        )
      ],
      [new AttributeModel('en', this.demo.emailId, this.userForm.controls.email.value)],
      [new AttributeModel('en', this.demo.mobileNumber, this.userForm.controls.mobilePhone.value)],
      [new AttributeModel('en', this.demo.CNEOrPINNumber, this.userForm.controls.pin.value)],
      [new AttributeModel('en', this.demo.age, this.userForm.controls.age.value)]
    );

    const req = new RequestModel(
      '74297024836182',
      'shashank',
      '',
      'updatedBy',
      '',
      'status201',
      'lang-201',
      new DemoIdentityModel(identity)
    );

    this.regService.addUser(req).subscribe(
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

  onGenderChange(gender: MatButtonToggleChange) {
    this.userForm.controls.gender.patchValue(gender.value);
  }

  addDOB(event: MatDatepickerInputEvent<Date>) {
    this.dateSelected = event.value;
    this.showDate = true;
  }

  onDOBChange(value) {
    console.log(value);
    if (value === 'age') {
      this.showCalender = false;
      // this.userForm.controls.dob.reset();
      // this.userForm.controls.dob.patchValue('');
      this.userForm.controls.dob.clearValidators();
    }
    if (value === 'dob') {
      this.showCalender = true;
      // this.userForm.controls.age.reset();
      // this.userForm.controls.age.patchValue('');
      this.userForm.controls.age.clearValidators();
    }
  }
}
