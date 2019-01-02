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
message1 = 'appointment confirmed';

userInfo: UserInfo = {
  name : '',
  preRegId : '',
  regCenter : '',
  centreContactNumber : 1234567890,
  appointmentDTime : ''
};

  constructor() {}

  ngOnInit() {
  }

}
