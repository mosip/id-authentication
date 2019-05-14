import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, Validators } from '@angular/forms';
import { FacadeService } from '../../shared/services/facade.service';

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.css']
})
export class ForgotPasswordComponent implements OnInit {
  mobileNumber: number;
  errorMessage: string;
  userId: any;

  constructor( private facadeService: FacadeService, private router: Router, private formBuilder: FormBuilder) { }

  forgotPasswordForm = this.formBuilder.group({
    mobileNumber: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(10), Validators.pattern('[0-9]+')]]
  });

  ngOnInit() {
  }

  onSubmit() {
    this.facadeService.setContact(this.mobileNumber);
    this.facadeService.getUserNameFromPhoneNumber(this.mobileNumber).subscribe(result => {
      this.userId = result['response']['userName'];
      // Call SendOTP
      this.router.navigate(['otpauthentication']);
    },
    error => this.errorMessage = error );
  }
}


