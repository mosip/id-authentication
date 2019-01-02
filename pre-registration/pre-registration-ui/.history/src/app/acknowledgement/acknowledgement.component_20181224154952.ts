import { Component, OnInit } from '@angular/core';

export interface UserInfo {
  name: string;
  preRegId: string;
  regCenter: string;
  centreContactNumber: number;
  appointmentDTime: string;
}

@Component({
  selector: 'app-acknowledgement',
  templateUrl: './acknowledgement.component.html',
  styleUrls: ['./acknowledgement.component.css']
})
export class AcknowledgementComponent implements OnInit {

message2 = 'The Pre-Registration id and Appointment details have been sent to the registered email id and phone number';
message1 = 'Appointment Confirmed';

userInfo: UserInfo = {
  name : 'Deepak Sharma',
  preRegId : '#LI12345678',
  regCenter : 'Gopalan Arcade, Mysore Road',
  centreContactNumber : 1234567890,
  appointmentDTime : '09 Jan 2019, 4:00 pm'
};

  constructor() {}

  ngOnInit() {
  }

}
