import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.css']
})
export class UsersComponent implements OnInit {

  constructor() { }
  data =[
    {
      'First Name': 'Ajay',
      'Last Name': 'J',
      'VID': '52.172.24.17',
      'Contact Number': '8660062352',
       Status: 'unassigned'
    },
    {
      'First Name': 'Akash',
      'Last Name': 'J',
      'VID': '52.172.24.17',
      'Contact Number': '9620189178',
       Status: 'unassigned'
    },
    {
      'First Name': 'Agnitra',
      'Last Name': 'Banerjee',
      'VID': '52.172.24.17',
      'Contact Number': '9087657853',
       Status: 'unassigned'
    },
    {
      'First Name': 'Ashish',
      'Last Name': 'Kumar',
      'VID': '52.172.24.17',
      'Contact Number': '9087654321',
       Status: 'unassigned'
    }
  ];
  recordNumber = this.data.length;
  ngOnInit() {
  }

}
