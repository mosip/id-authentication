import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators, FormGroup, AbstractControl } from '@angular/forms';
import { Router } from '@angular/router';
import { passwordValidator } from '../../shared/validators/password.validator';
import { FacadeService } from '../../shared/services/facade.service';
import { ResetPasswordModel } from '../../shared/models/reset-password-model';
import { RequestModel } from '../../shared/models/request-model';

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.css']
})
export class ResetPasswordComponent implements OnInit {
  active: boolean;
  resetPasswordForm: FormGroup;
  passwordPattern = '^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$';
  resetPasswordModel = {} as ResetPasswordModel;
  resetPasswordStatus: string;
  errorMessage: string;
  requestModel: any;

  constructor(private router: Router, private formBuilder: FormBuilder, private facadeService: FacadeService) { }
  ngOnInit() {
    this.resetPasswordForm = this.formBuilder.group({
      password: ['', [Validators.required, Validators.pattern(this.passwordPattern)]],
      confirmPassword: ['', [Validators.required]]
    }, { validator: [passwordValidator] });
  }

  lengthValidator(control: AbstractControl): { [key: string]: boolean } | null {
    const password = control.get('password');
    return password.value.length >= 8 ? { 'passWordLength': true } : null;
  }

  onSave() {
    this.active = true;
    this.resetPasswordModel.newPassword = this.resetPasswordForm.get('password').value;
    this.resetPasswordModel.userId = this.facadeService.getUserID();
    this.requestModel = new RequestModel('id', 'v1', this.resetPasswordModel, null);
    this.facadeService.resetPassword(this.requestModel).subscribe(resetPasswordResponse => {
      this.resetPasswordStatus = resetPasswordResponse['response']['status'];
      if (this.resetPasswordStatus === 'Success') {
        this.router.navigate(['login']);
      } else {
        alert('RESET_PASSWORD_FAILED');
      }
    },
    error => {
      this.errorMessage = error;
    });
  }
}
