import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { UserregistrationService } from '../../../shared/services/userregistration.service';
import { UserRegistrationRequestModel } from '../../../shared/models/user-registration-model';
import { RequestModel } from '../../../shared/models/request-model';
import { analyzeAndValidateNgModules } from '@angular/compiler';
import { DatePipe } from '@angular/common';
@Component({
  selector: 'app-userregistration',
  templateUrl: './userregistration.component.html',
  styleUrls: ['./userregistration.component.css']
})
export class UserregistrationComponent implements OnInit {
  submitted = false;
  maxDob = new Date();
  now: Date;
  requestObject = {} as UserRegistrationRequestModel;
  requestDTO: any;
  originUrl: string;
  userRegistrationForm: FormGroup;
  genderResponseObject = [];
  languageCode = 'eng';
  constructor(private router: Router, private formBuilder: FormBuilder, private service: UserregistrationService) {
    this.userRegistrationForm = this.formBuilder.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      contactNumber: ['', [Validators.required, Validators.pattern('^[0-9]*$')]],
      emailID: ['', [Validators.required, Validators.email]],
      DOB: ['', Validators.required],
      userName: ['', Validators.required],
      gender: ['', Validators.required],
      roles: ['', Validators.required]
    });
  }


  ngOnInit() {
    this.originUrl = window.location.origin;
    this.service.getGenderTypes().subscribe(data => {
      this.genderResponseObject = data['response']['genderType'];
    });
  }

  onSubmit() {
    if (!this.userRegistrationForm.valid) {
      alert('please fill all the requirements');
      return;
    }
    this.requestObject.firstName = this.userRegistrationForm.get('firstName').value;
    this.requestObject.userName = this.userRegistrationForm.get('userName').value;
    this.requestObject.lastName = this.userRegistrationForm.get('lastName').value;
    this.requestObject.contactNo = this.userRegistrationForm.get('contactNumber').value;
    this.now = this.userRegistrationForm.get('DOB').value;
    this.requestObject.dateOfBirth = this.dateFormatter(this.now);
    this.requestObject.emailID = this.userRegistrationForm.get('emailID').value;
    this.requestObject.gender = this.userRegistrationForm.get('gender').value;
    this.requestObject.role = this.userRegistrationForm.get('roles').value;
    this.requestObject.appId = 'admin';
    this.requestObject.ridValidationUrl = this.originUrl + '/admin-ui/#/admin/usermgmt/ridverification';
    this.requestDTO = new RequestModel('id', 'v1', this.requestObject, null);
    console.log(this.requestDTO);
    this.service.registerUser(this.requestDTO).subscribe(data => {
      if (data['response'] === null) {
        if (data['errors'] != null) {
          for (let i = 0; i < data['errors'].length; i++) {
            alert(data['errors'][i]['message']);
          }
          this.userRegistrationForm.reset();
          return;
        }
      }
      alert('Detail submitted successfully');
      console.log(data);
      this.router.navigateByUrl('admin/dashboard');
    }, error => {
      console.log(error);
      alert('error occur');
    });

  }

  dateFormatter(date: Date) {
    const pipe = new DatePipe('en-US');
    const formattedDate = pipe.transform(date, 'yyyy-MM-dd');
    return formattedDate;
  }
  cancel() {
    this.router.navigateByUrl('');
  }
  get emailID() {
    return this.userRegistrationForm.get('emailID');
  }

  isValid(langCode: any) {
    if (langCode === this.languageCode) {
      return true;
    } else {
      return false;
    }
  }
}
