import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  constructor(private router: Router,private formBuilder: FormBuilder) { }
  loginForm: FormGroup;
  ngOnInit() {
   this.loginForm = this.formBuilder.group({
     'username':['',Validators.compose([Validators.required])]
   });
  }
  onSubmit() {
    this.router.navigate(['authenticate']);
  }
  
}
