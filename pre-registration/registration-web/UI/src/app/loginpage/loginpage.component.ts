import { Component, OnInit } from '@angular/core';
import { AppService } from '../app.service';
import { Router } from '@angular/router';
import swal from 'sweetalert2';
import { FormControl, Validators } from '@angular/forms';
import * as constants from '../Constants';
import { DataExchangeService } from '../data-exchange.service';

@Component({
  selector: 'app-loginpage',
  templateUrl: './loginpage.component.html',
  styleUrls: ['./loginpage.component.css']
})
export class LoginpageComponent implements OnInit {
  flag: boolean;
  type: string;

  constructor(private appsvc: AppService, private router: Router, private dataExchange: DataExchangeService) {
  }

  userLoginPref: FormControl = new FormControl('',
    [Validators.required, 
    Validators.email, 
    Validators.pattern(constants.NUMBER_PATTERN)]);

  ngOnInit() {
  }
  
  typeIdentifierHandler(): string {
    if (!this.userLoginPref.hasError('email') && !this.userLoginPref.hasError('required'))
      return "email";
    else if (!this.userLoginPref.hasError('pattern') && !this.userLoginPref.hasError('required'))
      return "mobile number";
    else return "invalid";
  }

  generateOTP() {
    this.type = this.typeIdentifierHandler();
    if (this.type !== "invalid") {
      this.dataExchange.setLogin(this.userLoginPref.value, this.type);
      {
        this.appsvc.generateOTP(this.userLoginPref.value).subscribe(res => {
          console.log("Res ", res['_body']);
          this.router.navigate(['/otp']);
        }, error => {
          console.log(error['_body']);
          swal({
            type: 'error',
            title: 'Unauthorized',
            text: 'Invalid login credentials',
            footer: 'Try again with correct credentials'
          });
        }
        );
      }
    }
    else {
      swal({
        type: 'error',
        title: '',
        text: 'Invalid login credentials',
        footer: 'Try again with correct credentials'
      });
    }
  }
}
