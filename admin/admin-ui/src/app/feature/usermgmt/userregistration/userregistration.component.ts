import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
@Component({
  selector: 'app-userregistration',
  templateUrl: './userregistration.component.html',
  styleUrls: ['./userregistration.component.css']
})
export class UserregistrationComponent implements OnInit {

  constructor(private router: Router) { }

  ngOnInit() {
  }
  onSubmit(){

  }
  cancel(){
    this.router.navigateByUrl('');
  }
}
