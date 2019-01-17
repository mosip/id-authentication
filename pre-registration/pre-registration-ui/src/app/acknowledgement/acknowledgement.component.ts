import { Component, OnInit } from "@angular/core";
import { RegistrationService } from "../registration/registration.service";
import { SharedService } from "../shared/shared.service";

@Component({
  selector: "app-acknowledgement",
  templateUrl: "./acknowledgement.component.html",
  styleUrls: ["./acknowledgement.component.css"]
})
export class AcknowledgementComponent implements OnInit {
  message2 =
    'The Pre-Registration id and Appointment details have been sent to the registered email id and phone number';
  message1 = 'Appointment Confirmed';
  usersInfo = [];

  constructor(private sharedService: SharedService) {}

  ngOnInit() {
    this.usersInfo = this.sharedService.getNameList();
    console.log('acknowledgement component', this.sharedService.getNameList());
  }
}
