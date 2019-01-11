import { Component, OnInit } from "@angular/core";

@Component({
  selector: "app-acknowledgement",
  templateUrl: "./acknowledgement.component.html",
  styleUrls: ["./acknowledgement.component.css"]
})
export class AcknowledgementComponent implements OnInit {
  message2 =
    'The Pre-Registration id and Appointment details have been sent to the registered email id and phone number';
  message1 = 'Appointment Confirmed';
  usersInfo = [
    {
      userInfo : [
        {
          key: 'Name',
          value: 'Deepak Sharma'
        },
        {
          key: 'Pre Registration ID',
          value: '#LI12345678'
        },
        {
          key: 'Center Center',
          value: 'Gopalan Arcade, Mysore Road'
        },
        {
          key: 'Center Contact Number',
          value: 1234567890
        },
        {
          key: 'Appointment Date & Time',
          value: '09 Jan 2019, 4:00 pm'
        }
      ],
      qrCode: 'assets/img/QR_code.png',
      guidelines : ['Item 1', 'Item 2', 'Item 3', 'Item 4', 'Item 5', 'Item 6', 'Item 7', 'Item 8', 'Item 9', 'Item 10']
    },
    {
      userInfo : [
        {
          key: 'Name',
          value: 'Deepak Sharma'
        },
        {
          key: 'Pre Registration ID',
          value: '#LI12345678'
        },
        {
          key: 'Center Center',
          value: 'Gopalan Arcade, Mysore Road'
        },
        {
          key: 'Center Contact Number',
          value: 1234567890
        },
        {
          key: 'Appointment Date & Time',
          value: '09 Jan 2019, 4:00 pm'
        }
      ],
      qrCode: 'assets/img/QR_code.png',
      guidelines : ['Item 1', 'Item 2', 'Item 3', 'Item 4', 'Item 5', 'Item 6', 'Item 7', 'Item 8', 'Item 9', 'Item 10']
    }
  ];

  constructor() {}

  ngOnInit() {
  }
}
