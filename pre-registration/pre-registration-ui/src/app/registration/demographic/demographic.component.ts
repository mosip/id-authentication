import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { FormGroup, FormControl, Validators } from '@angular/forms';
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
    let isPrimary = false;
    let fullName = '';
    let gender = '';
    let addressLine1 = '';
    let addressLine2 = '';
    let addressLine3 = '';
    let region = '';
    let province = '';
    let city = '';
    let localAdministrativeAuthority = '';
    let email = '';
    let dob: Time;
    let age: number;
    let postalCode: number;
    let mobilePhone: number;
    let pin: number;

    this.userForm = new FormGroup({
      'isPrimary' : new FormControl(isPrimary),
      'fullName': new FormControl(fullName, Validators.required),
      'gender': new FormControl(gender, Validators.required),
      'addressLine1': new FormControl(addressLine1, Validators.required),
      'addressLine2': new FormControl(addressLine2),
      'addressLine3': new FormControl(addressLine3),
      'region': new FormControl(region, Validators.required),
      'province': new FormControl(province, Validators.required),
      'city': new FormControl(city, Validators.required),
      'localAdministrativeAuthority': new FormControl(localAdministrativeAuthority, Validators.required),
      'email': new FormControl(email, Validators.required),
    });

  }


  setStep(index: number) {
    this.step = index;
  }

  nextStep() {
    if (this.step == this.numbers.length - 1)
      this.saveForm();
    this.step++;
  }

  prevStep() {
    this.step--;
  }

  saveForm() {
    console.log("save Form");

  }
}
