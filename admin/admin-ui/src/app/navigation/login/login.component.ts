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

  constructor(private router: Router,private formBuilder: FormBuilder , private loginService: LoginServiceService) { }
  loginForm: FormGroup;
  usernameValidity:string = "Username is required" ;
  ngOnInit() {
   this.loginForm = this.formBuilder.group({
     'username':['',Validators.compose([Validators.required])]
   });
  }
  onSubmit(userId : String) {
    console.log(userId);
    this.loginService.login(userId).subscribe(res=>{
      this.router.navigate(['authenticate'])},
      error=>{

      }
      );
    }

}
