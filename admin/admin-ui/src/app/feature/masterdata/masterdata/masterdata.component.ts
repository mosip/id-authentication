import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-masterdata',
  templateUrl: './masterdata.component.html',
  styleUrls: ['./masterdata.component.css']
})
export class MasterdataComponent implements OnInit {

  constructor() { }

  data = [
    {
      name: 'Agnitra',
      age: 20
    },
    {
      name: 'ajay',
      age: 20
    }
  ];

  ngOnInit() {
  }

}
