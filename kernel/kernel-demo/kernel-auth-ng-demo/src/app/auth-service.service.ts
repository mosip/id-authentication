import { AuthResponse } from './authresponse';
import { Application } from './application';
import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse, HttpHeaders } from '@angular/common/http';
import { LoginUser } from './loginuser';
import { ToastrService } from 'ngx-toastr';
import { OtpRequest } from './otprequest';
import { OtpResponse } from './otpresponse';




@Injectable({
  providedIn: 'root'
})
export class AuthServiceService {

Token: string;

  constructor(private http: HttpClient, private toastr: ToastrService) {

   }

   getToken(loginUser: LoginUser) {
    this.http.post('http://localhost:9090/login',
      loginUser, {observe: 'response'}
      ).subscribe((res: HttpResponse<Object>) => {
        this.toastr.success('Got The Token!', 'Token is Stored In Cookies!');
        },
        error => {
          this.toastr.error(error.message);
        });

   }

   getApplication(applicationId: string) {
    this.http.get('http://localhost:8080/application/' + applicationId,
    { observe: 'response'}).subscribe((res: HttpResponse<AuthResponse>) => {
      this.toastr.success('Got The Application!', res.body.message);
    },
    error => {
      this.toastr.error(error.message);
    });

   }

   createApplication(application: Application) {
    this.http.post('http://localhost:8080/application', application,
    { observe: 'response'}).subscribe((res: HttpResponse<AuthResponse>) => {
      console.log(res);
      this.toastr.success('Created The Application!', res.body.message);
        },
        error => {
          console.log(error);
          this.toastr.error(error.message);
        });

   }

   putApplication(application: Application) {
    this.http.put('http://localhost:8080/application', application,
    { observe: 'response'}).subscribe((res: HttpResponse<AuthResponse>) => {
      this.toastr.success('Updated The Application!', res.body.message);
        },
        error => {
          this.toastr.error(error.message);
        });

   }

   deleteApplication(applicationId: string) {
    this.http.delete('http://localhost:8080/application/' + applicationId,
    { observe: 'response'}).subscribe((res: HttpResponse<AuthResponse>) => {
      this.toastr.success('Deleted The Application!', res.body.message);
        },
        error => {
          this.toastr.error(error.message);
        });

   }

   otp(otpRequest: OtpRequest) {
    this.http.post('http://localhost:8080/otp', otpRequest,
    { observe: 'response'}).subscribe((res: HttpResponse<OtpResponse>) => {
      this.toastr.success(res.body.otp, res.body.status);
        },
        error => {
          this.toastr.error(error.message);
        });

   }

   logout() {
    this.http.post('http://localhost:9090/logout',
    { observe: 'response'}).subscribe((res: HttpResponse<AuthResponse>) => {
      this.toastr.success('LOGOUT!', 'Token has been invalidated successfully');
        },
        error => {
          console.log(error);
          this.toastr.error(error.message);
        });

   }
}
