import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, Validators } from '@angular/forms';
import { GetContactService } from '../../shared/services/get-contact.service';

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.css']
})
export class ForgotPasswordComponent implements OnInit {

  userId: string;
  mobileNumber: number;
  constructor(private router: Router, private getContactService: GetContactService, private formBuilder: FormBuilder) { }
  forgotPasswordForm = this.formBuilder.group({
    mobileNumber: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(10), Validators.pattern('[0-9]+')]]
  });
  ngOnInit() {
  }
  onSubmit() {
    this.getContactService.setContactNumber(this.mobileNumber);
    this.router.navigate(['otpauthentication']);
  }
}
