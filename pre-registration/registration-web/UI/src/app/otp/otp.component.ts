import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { AppService } from '../app.service';
import swal from 'sweetalert2';
import { DataExchangeService } from '../data-exchange.service';

@Component({
  selector: 'app-otp',
  templateUrl: './otp.component.html',
  styleUrls: ['./otp.component.css']
})
export class OtpComponent implements OnInit {
  selectedValue: string = "";
  otpValue: string = "";
  currentLoginType: string = "";
  loginDetails: object;
  OriginalValue: string;

  constructor(private route: ActivatedRoute, private appsvc: AppService, private router: Router
    , private dataExchange: DataExchangeService) { }

  ngOnInit() {
    // this.selectedValue=this.route.snapshot.queryParamMap.get('value');
    // this.currentLoginType=this.route.snapshot.queryParamMap.get('type');
    this.loginDetails = this.dataExchange.getLogin();
    this.selectedValue = this.loginDetails['value'];
    this.currentLoginType = this.loginDetails['type'];
    this.OriginalValue = this.loginDetails['original'];
  }

  validateOTP() {
    this.appsvc.validateOTP(this.OriginalValue, this.otpValue).subscribe(res => {
      sessionStorage.setItem('loginuser', this.OriginalValue);
      this.router.navigate(['/home']);
    }, error => {
      swal({
        type: 'error',
        title: 'Unauthorized',
        text: 'Invalid OTP entered',
        footer: 'Try again'
      });
    })
  }

  generateOTP() {
    console.log(this.OriginalValue);
    this.otpValue = "";
    this.appsvc.generateOTP(this.OriginalValue).subscribe(res => {
      console.log("Res ", res['_body']);
    }, error => {
      console.log(error['_body']);
    }
    );

  }
}
