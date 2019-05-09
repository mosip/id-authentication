import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import {FormGroup,FormBuilder,Validators } from '@angular/forms';
import { UserregistrationService } from '../../../shared/services/userregistration.service';
@Component({
  selector: 'app-userregistration',
  templateUrl: './userregistration.component.html',
  styleUrls: ['./userregistration.component.css']
})
export class UserregistrationComponent implements OnInit {
  submitted = false;
  maxDob=new Date();
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
    
  }
  onSubmit(){
    console.log("form submitted");
this.submitted=true;
if(this.userRegistrationForm.invalid)
{
  return;
}
this.service.getGenderTypes().subscribe(data=>{
  console.log(data);
});
  }
  cancel(){
    this.router.navigateByUrl('');
  }
  get emailID(){
    return this.userRegistrationForm.get('emailID');
  }

}
