import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-devices',
  templateUrl: './devices.component.html',
  styleUrls: ['./devices.component.css']
})
export class DevicesComponent implements OnInit {

  constructor() { }

  data =[
    {
      'Device Name':'D-COE11',
      'Device Type':'Iris Scanner',
      'MAC Address': '00:09:a9:o8:16:89',
      'IP Address': '52.172.24.17',
      Status: 'unassigned'
    },
    {
      'Device Name':'D-COE13',
      'Device Type':'Finger Print Scanner',
      'MAC Address': '00:09:a9:o8:16:89',
      'IP Address': '52.172.24.17',
      Status: 'unassigned'
    },
    {
      'Device Name':'D-COE14',
      'Device Type':'Iris Scanner',
      'MAC Address': '00:09:a9:o8:16:89',
      'IP Address': '52.172.24.17',
      Status: 'unassigned'
    },
    { 'Device Name':'D-COE12',
      'Device Type':'Document Printer',
      'Machine Name': 'A-COEO15',
      'MAC Address': '00:09:a9:o8:16:89',
      'IP Address': '52.172.24.17',
      status: 'unassigned'
    }
  ];
  recordNumber = this.data.length;
  ngOnInit() {
  }

}
