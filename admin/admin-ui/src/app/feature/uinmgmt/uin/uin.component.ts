import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FormBuilder, FormControl, Validators } from '@angular/forms';
import { Observable } from 'rxjs/Observable';

@Component({
  selector: 'app-uin',
  templateUrl: './uin.component.html',
  styleUrls: ['./uin.component.css']
})
export class UinComponent implements OnInit {
  inputUin: String = " ";
  response: any = {};
  
  

  constructor(private inhttp: HttpClient, private formBuilder: FormBuilder) {

    let customHeaders: Headers = new Headers();
    customHeaders.append('Cookie', 'Authorization=Mosip-TokeneyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJpZGEiLCJtb2JpbGUiOiI4OTc2Mzk0ODU5IiwibWFpbCI6ImlkYUBtb3NpcC5pbyIsInJvbGUiOiJJRF9BVVRIRU5USUNBVElPTixNSVNQIiwibmFtZSI6ImlkYSIsInJJZCI6IjEwMDAxMTAwMDEwMDAwMTIwMTkwNTA2MDkyNTM1IiwiaWF0IjoxNTU3MzE5MDQzLCJleHAiOjE1NTczMjUwNDN9.2MZGBgJVnqao4kuy8gaPCmqrIJwm5tD9_TzbANN8Wrdty-3nlGH5j-ti3stqTOGxFjraPboMH3qUnHhoSsQ9hA');
    customHeaders.append('Content-Type', 'application/json;charset=UTF-8');
    customHeaders.append('Access-Control-Allow-Credentials', 'true');
    customHeaders.append('Access-Control-Allow-Origin', '*');
  }

  ngOnInit() {

  }


  uinStatusForm = this.formBuilder.group({
    uin: ['', Validators.required],
  })

  
  


  search() {
    console.log(this.uinStatusForm)

    this.inhttp.get("http://localhost:8098/v1/admin/uin/status/" + this.inputUin.trim())
      .subscribe((response) => {  
        this.response = response;
        console.log(response);
      });


  }

  refresh() {
    this.inputUin = "";
    this.response = null;
    this.response.response = null;
    this.response.errors = null;
    this.response.errors = "";
    this.response.errors[0].message = null;
    this.response.errors[0].message = "";
  }

  clearResponse() {
    if(this.inputUin.length === 0) {
      this.response = null;
    }
  }

}
