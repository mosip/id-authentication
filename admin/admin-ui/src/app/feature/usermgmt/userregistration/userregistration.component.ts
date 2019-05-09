import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import {FormGroup,FormBuilder,Validators } from '@angular/forms';
@Component({
  selector: 'app-userregistration',
  templateUrl: './userregistration.component.html',
  styleUrls: ['./userregistration.component.css']
})
export class UserregistrationComponent implements OnInit {
  submitted = false;
  userRegistrationForm:FormGroup;
  constructor(private router: Router,private formBuilder:FormBuilder) {
    this.userRegistrationForm = this.formBuilder.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      contactNumber:['',[Validators.required,Validators.pattern("^[0-9]*$")]],
      emailID:['',[Validators.required,Validators.email]],
      DOB:['',Validators.required],
      userName:['',Validators.required]
    });
   }
 
 
  ngOnInit() {
  }
  onSubmit(){
    console.log("form submitted");
this.submitted=true;
if(this.userRegistrationForm.invalid)
{
  return;
}
  }
  cancel(){
    this.router.navigateByUrl('');
  }
  get emailID(){
    return this.userRegistrationForm.get('emailID');
  }
}
