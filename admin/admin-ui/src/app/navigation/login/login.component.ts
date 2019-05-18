import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { LoginServiceService } from '../../shared/services/login-service.service';
@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  constructor(
    private router: Router,
    private formBuilder: FormBuilder,
    private loginService: LoginServiceService
  ) {}
  loginForm: FormGroup;
  authTypes: string[] = [];
  usernameValidity = 'Username is required';
  invaildUserName = false;
  errorCode = 'KER-ATH-003';
  ngOnInit() {
    this.loginForm = this.formBuilder.group({
      username: ['', [Validators.required]]
    });
  }

  onSubmit(username: string) {
    console.log(username);
    this.loginService.setUserName(username);
    this.loginService.setAuthTypes(this.authTypes);
    this.loginService.login().subscribe(({ response, errors }) => {
      if (errors.length === 0 || response != null) {
        console.log(response.authTypes);
        this.authTypes = response.authTypes;
        this.loginService.setAuthTypes(this.authTypes);
        this.router.navigate(['authenticate', username]);
      } else {
        console.log(errors[0].message);
        if (this.errorCode === errors[0].errorCode) {
          this.invaildUserName = true;
        }
      }
    });
  }
}
