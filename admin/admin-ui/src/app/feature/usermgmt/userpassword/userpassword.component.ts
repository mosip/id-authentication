import { RequestModel } from './../../../shared/models/request-model';
import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { Router, ActivatedRoute, Params } from '@angular/router';
import { UserPasswordModel } from '../../../shared/models/user-password-model';
import { UserregistrationService } from '../../../shared/services/userregistration.service';
@Component({
  selector: 'app-userpassword',
  templateUrl: './userpassword.component.html',
  styleUrls: ['./userpassword.component.css']
})
export class UserpasswordComponent implements OnInit {
  rid: string;
  userName: string;
  requestModel: any;
  submitPasswordValidation = false;
  requestObject = {} as UserPasswordModel;
  userpasswordCreationForm: FormGroup;
  constructor(private service: UserregistrationService,
    private activatedRoute: ActivatedRoute,
    private formBuilder: FormBuilder, private router: Router) {
    this.userpasswordCreationForm = this.formBuilder.group({
      password: ['', Validators.required],
      confPassword: ['', Validators.required]
    });
  }

  ngOnInit() {
    this.activatedRoute.queryParams.subscribe(params => {
      const userNameQueryParam = params['username'];
      this.rid = params['rid'];
      this.userName = userNameQueryParam;
    });
  }

  onVerification() {
    if (this.userpasswordCreationForm.get('password').value !== '' && this.userpasswordCreationForm.get('confPassword').value !== '') {
      if (this.userpasswordCreationForm.get('password').value.length !== this.userpasswordCreationForm.get('confPassword').value.length) {
        alert('password length not match');
        this.userpasswordCreationForm.reset();
        return;
      }
      if (this.userpasswordCreationForm.get('password').value.trim() !== this.userpasswordCreationForm.get('confPassword').value.trim()) {
        alert('password not match');
        this.userpasswordCreationForm.reset();
        return;
      }
      this.submitPasswordValidation = true;
    }
  }

  onSubmit() {
    if (!this.userpasswordCreationForm.valid) {
      alert('please fill all details');
      return;
    }
    this.requestObject.appId = 'admin';
    this.requestObject.password = this.userpasswordCreationForm.get('password').value.trim();
    this.requestObject.rid = this.rid;
    this.requestObject.userName = this.userName;
    this.requestModel = new RequestModel('id', 'v1', this.requestObject, null);
    this.service.passwordCreation(this.requestModel).subscribe(data => {
      if (data['response'] === null) {
        if (data['errors'] != null) {
          alert('error occur while creating password');
          this.userpasswordCreationForm.reset();
        }
        return;
      }
      alert('you have successfully registered');
      this.router.navigateByUrl('');
    }, error => {
      console.log(error);
    });
  }
}
