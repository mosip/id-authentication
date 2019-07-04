import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-table',
  templateUrl: './table.component.html',
  styleUrls: ['./table.component.css']
})
export class TableComponent implements OnInit {

  @Input() data: any;

  keys = [];

  constructor() { }

  ngOnInit() {
    this.keys = Object.keys(this.data[0]);
  }
  
}
