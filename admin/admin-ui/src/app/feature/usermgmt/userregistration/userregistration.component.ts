import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import {FormGroup,FormBuilder,Validators } from '@angular/forms';
import { UserregistrationService } from '../../../shared/services/userregistration.service';
import { UserRegistrationRequestModel } from '../../../shared/Models/user-registration-model';
import { RequestModel } from '../../../shared/Models/Request-model';
import { analyzeAndValidateNgModules } from '@angular/compiler';
import { DatePipe } from '@angular/common';
@Component({
  selector: 'app-userregistration',
  templateUrl: './userregistration.component.html',
  styleUrls: ['./userregistration.component.css']
})
export class UserregistrationComponent implements OnInit {
  submitted = false;
  maxDob=new Date();
  now:Date;
  requestObject = {} as UserRegistrationRequestModel;
  requestDTO:any;
  userRegistrationForm:FormGroup;
  constructor(private router: Router,private formBuilder:FormBuilder,private service:UserregistrationService) {
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
    this.service.getGenderTypes().subscribe(data=>{
      console.log(data);
    });
  }

onSubmit(){
  this.requestObject.firstName=this.userRegistrationForm.get('firstName').value;
this.requestObject.userName=this.userRegistrationForm.get('userName').value;
this.requestObject.lastName=this.userRegistrationForm.get('lastName').value;
this.requestObject.contactNo=this.userRegistrationForm.get('contactNumber').value
this.now=this.userRegistrationForm.get('DOB').value;
this.requestObject.dateOfBirth=this.dateFormatter(this.now);
this.requestObject.emailID=this.userRegistrationForm.get('emailID').value;
this.requestObject.gender="Male";
this.requestObject.role="Admin";
this.requestDTO=new RequestModel("id","v1",this.requestObject,null);
console.log(this.requestDTO);
this.service.registerUser(this.requestDTO).subscribe(data=>{
  console.log(data);
},error=>{
  console.log(error);
});
}

dateFormatter(date:Date){
  const pipe = new DatePipe('en-US');
  let formattedDate = pipe.transform(date, 'yyyy-MM-ddTHH:mm:ss.SSS');
  formattedDate = formattedDate + 'Z';
  return formattedDate;
}
  cancel(){
    this.router.navigateByUrl('');
  }
  get emailID(){
    return this.userRegistrationForm.get('emailID');
  }

}
