import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, Validators } from '@angular/forms';
import { GetContactService } from '../../shared/services/get-contact.service';
import { AccountManagementService } from '../../shared/services/account-management.service';
import { FacadeService } from '../../shared/services/facade.service';

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.css']
})
export class ForgotPasswordComponent implements OnInit {
  mobileNumber: number;
  errorMessage: string;

  constructor(private accountManagementService: AccountManagementService, private facadeService: FacadeService,
    private router: Router, private formBuilder: FormBuilder) { }

  forgotPasswordForm = this.formBuilder.group({
    mobileNumber: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(10), Validators.pattern('[0-9]+')]]
  });

  ngOnInit() {
  }

  onSubmit() {
    this.facadeService.setContact(this.mobileNumber);
    this.router.navigate(['otpauthentication']);
  }

  getUserName(phoneNumber: number): string {
    // .subscribe(data => this.object = data);
    this.accountManagementService.getUserNameFromPhoneNumber(phoneNumber).subscribe(result => {
      console.log(result);
    },
    error => this.errorMessage = error );
    return '';
  }
}
