import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { FormGroup, FormControl, Validators, FormArray } from '@angular/forms';
import { Time } from '@angular/common';

@Component({
  selector: 'app-demographic',
  templateUrl: './demographic.component.html',
  styleUrls: ['./demographic.component.css']
})
export class DemographicComponent implements OnInit {

  step = 0;
  numberOfApplicants: number;
  userForm: FormGroup;
  numbers: number[];

  isPrimary = false;
  fullName = '';
  gender = 'male';
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
    private router: Router) { }

  ngOnInit() {
    this.route.params
      .subscribe(
        (params: Params) => {
          this.numberOfApplicants = +params['id'];
          this.numbers = Array(this.numberOfApplicants).fill(0).map((x, i) => i); // [0,1,2,3,4]
          this.initForm();
        }
      );
  }

  initForm() {

    if (this.step === 0){
      console.log(this.step);
      
      this.isPrimary = true;
    }
    else{
      console.log(this.step + "else");
      
      this.isPrimary = false;
    }
    this.userForm = new FormGroup({
      'isPrimary': new FormControl(this.isPrimary),
      'fullName': new FormControl(this.fullName, Validators.required),
      'gender': new FormControl(this.gender),
      'addressLine1': new FormControl(this.addressLine1, Validators.required),
      'addressLine2': new FormControl(this.addressLine2),
      'addressLine3': new FormControl(this.addressLine3),
      'region': new FormControl(this.region, Validators.required),
      'province': new FormControl(this.province, Validators.required),
      'city': new FormControl(this.city, Validators.required),
      'localAdministrativeAuthority': new FormControl(this.localAdministrativeAuthority, Validators.required),
      'email': new FormControl(this.email, Validators.required),
      'age': new FormControl(this.age, Validators.required),
      'dob': new FormControl(this.dob),
      'postalCode': new FormControl(this.postalCode, Validators.required),
      'mobilePhone': new FormControl(this.mobilePhone, Validators.required),
      'pin': new FormControl(this.pin, Validators.required)
    })

    this.isPrimary = false;
  }


  setStep(index: number) {
    this.step = index;
  }

  nextStep() {
    this.saveForm();
    this.step++;
  }

  prevStep() {
    this.step--;
  }

  saveForm() {
    console.log("save Form");
    // console.log(this.userForm.get('gender'));
    console.log(this.userForm);
  }

  onGenderChange(value) {
    if (value.checked === true) {
      this.gender = 'female';
    } else {
      this.gender = 'male';
    }
  }

  onDOBChange(value) {
    console.log(value);
  }
}
