import { Component, OnInit } from '@angular/core';
import {FormBuilder, Validators, FormGroup, AbstractControl} from '@angular/forms';
import { Router } from '@angular/router';
import { passwordValidator } from '../../shared/validators/password.validator';

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.css']
})
export class ResetPasswordComponent implements OnInit {
  resetPasswordForm: FormGroup;
  passwordPattern = '^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$';

  constructor(private router: Router, private formBuilder: FormBuilder) { }
  ngOnInit() {
    this.resetPasswordForm = this.formBuilder.group({
      password: ['', [Validators.required, Validators.pattern(this.passwordPattern)]],
      confirmPassword: ['', [Validators.required]]
    }, {validator: [passwordValidator]});
  }

   lengthValidator(control: AbstractControl): {[key: string]: boolean} | null {
    const password = control.get('password');
      return password.value.length >= 8 ? { 'passWordLength': true } : null;
    }

  onSave() {
    this.router.navigate(['login']);
  }
}
