import { Component, OnInit } from '@angular/core';
import { GetContactService } from '../../shared/services/get-contact.service';
import {FormBuilder, Validators} from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-otp-authentication',
  templateUrl: './otp-authentication.component.html',
  styleUrls: ['./otp-authentication.component.css']
})
export class OtpAuthenticationComponent implements OnInit {
  userMobileNumber: number;
  constructor(private getContactService: GetContactService, private router: Router, private formBuilder: FormBuilder) { }
  otpAuthenticationForm = this.formBuilder.group({
    otp: ['', [Validators.required, Validators.pattern('[0-9]+')] ]
  });
  ngOnInit() {
    this.userMobileNumber = this.getContactService.getContactNumber();
  }
  onVerify() {
    this.router.navigate(['resetpassword']);
  }
}
