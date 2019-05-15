import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
@Component({
  selector: 'app-userpassword',
  templateUrl: './userpassword.component.html',
  styleUrls: ['./userpassword.component.css']
})
export class UserpasswordComponent implements OnInit {
  userpasswordCreationForm: FormGroup;
  constructor(private formBuilder: FormBuilder, private router: Router) {
    this.userpasswordCreationForm = this.formBuilder.group({
      password: ['', Validators.required],
      confPassword: ['', Validators.required]
    });
  }
  ngOnInit() {
  }
  onSubmit() {

  }
}
