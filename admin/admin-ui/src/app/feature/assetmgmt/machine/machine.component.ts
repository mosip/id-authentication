import { Component, OnInit, Output, EventEmitter } from '@angular/core';


@Component({
  selector: 'app-machine',
  templateUrl: './machine.component.html',
  styleUrls: ['./machine.component.css']
})
export class MachineComponent implements OnInit {
  
  constructor() { }
  searchArray =[];
  set: any;
  data =[
    {
      'Machine Name': 'A-COEO12',
      'MAC Address': '00:09:a9:o8:16:89',
      'IP Address': '52.172.24.17',
      'Serial Number': 'BT30657432F',
      Status: 'unassigned'
    },
    {
      'Machine Name': 'A-COEO13',
      'MAC Address': '00:09:a9:o8:16:89',
      'IP Address': '52.172.24.17',
      'Serial Number': 'BT30657432F',
      Status: 'unassigned'
    },
    {
      'Machine Name': 'A-COEO14',
      'MAC Address': '00:09:a9:o8:16:89',
      'IP Address': '52.172.24.17',
      'Serial Number': 'BT30657432F',
      Status: 'unassigned'
    },
    {
      'Machine Name': 'A-COEO15',
      'MAC Address': '00:09:a9:o8:16:89',
      'IP Address': '52.172.24.17',
      'Serial Number': 'BT30657432F',
      Status: 'unassigned'
    }
  ];
  tableData = this.data; 
  recordNumber = this.data.length;

  ngOnInit() {
    
  }
   
  search(searchKey) {
    let value = searchKey.value;
    this.searchArray = [];
    if (value.length >= 0) {
      for (let i = 0; i < this.data.length; i++) {
        let arr = Object.values(this.data[i]);
        console.log(arr);
        for (let j = 0; j < arr.length; j++) {
          if (arr[j].includes(value)) {
            this.searchArray.push(this.data[i]);
            this.set = new Set(this.searchArray);
            this.tableData = this.set;
          }
        }
      }
    } else {
      this.tableData = this.data;
    }
  }

}