import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-acknowledgement',
  templateUrl: './acknowledgement.component.html',
  styleUrls: ['./acknowledgement.component.css']
})
export class AcknowledgementComponent implements OnInit {

  message = 'The Pre-Registration id and Appointment details have been sent to the registered email id and phone number';

  constructor() { }

  ngOnInit() {
  }

}
