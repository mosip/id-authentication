import {Component, OnInit, AfterViewInit} from '@angular/core';
import {Router, ActivatedRoute} from '@angular/router';

import {Page4Service} from './page4.service';

@Component({styleUrls: ['page4.style.css'], selector: 'my-page4', templateUrl: './page4.template.html', providers: [Page4Service]})

export class Page4Component {
  constructor(private page4Service : Page4Service, private router : Router, private route : ActivatedRoute) {}
  person = {
    firstName: '',
    lastName: '',
    age: '',
    address: ''
  };
  selectedIdName;
  selectedId;
  ids = [];
  generationKey;
  validationKey;
  generatedOTP;
  validatedOTP;
  otpRes='';

  ngOnInit() {
    this
      .page4Service
      .getCenters()
      .then(centers => {
        console.log(centers)
        this.ids = centers
      });
  }
  getCenters(){
    this
    .page4Service
    .getCenters()
    .then(centers => {
      console.log(centers)
      this.ids = centers
    });
  }
  onIdSelected() {
    this.selectedId = this
      .ids
      .find(id => id.enrollmentCenterName === this.selectedIdName);
  }

  submit() {

    console.log(this.person, this.selectedId)
    this
      .page4Service
      .submit({firstName: this.person.firstName, lastName: this.person.lastName, age: this.person.age, address: this.person.address, enrollmentId: this.selectedId.enrollmentId})
      .then((response) => {
        console.log(response)

      })
  }

  generateOTP() {

    console.log(this.generationKey)
    this
      .page4Service
      .generateOTP({key: this.generationKey})
      .then((response) => {
        this.generatedOTP=response.otp;
        console.log(response)
      })
  }
  validateOTP() {
    if(this.validationKey&&this.validationKey.trim()!=''&&this.validatedOTP&&this.validatedOTP.trim()!=''){
    console.log(this.validationKey,this.validatedOTP)
    this
      .page4Service
      .validateOTP(this.validationKey,this.validatedOTP)
      .then((response) => {
        this.otpRes=response.message
      })
    }
  }
  securityDemo() {
    this
      .page4Service
      .securityDemo()
      .then((response) => {
        console.log(response)
      })
  }
  zipDemo() {
    this
      .page4Service
      .zipDemo()
      .then((response) => {
        console.log(response)
      })
  }
  jsonDemo() {
    this
      .page4Service
      .jsonDemo()
      .then((response) => {
        console.log(response)
      })
  }
  daoDemo() {
    this
      .page4Service
      .daoDemo()
      .then((response) => {
        console.log(response)
      })
  }
}
