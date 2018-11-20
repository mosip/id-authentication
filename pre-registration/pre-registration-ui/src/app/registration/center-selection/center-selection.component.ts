import { Component, OnInit } from '@angular/core';
import {MatTableDataSource, MatDialog} from '@angular/material';
import {SelectionModel} from '@angular/cdk/collections';
import { DialougComponent } from '../dialoug/dialoug.component';

export interface RegistrationCentre {
  name: string;
  address: string;
  contact_person: string;
  centre_type: string;
  contact: number;
}

const REGISTRATION_CENTRES: RegistrationCentre[] = [
  {name: 'Centre 1', address: 'Dummy Address 1', contact_person: 'Person 1', centre_type: 'Permanent', contact: 9999999999},
  {name: 'Centre 2', address: 'Dummy Address 2', contact_person: 'Person 2', centre_type: 'Permanent', contact: 8888888888}
  ];


@Component({
  selector: 'app-center-selection',
  templateUrl: './center-selection.component.html',
  styleUrls: ['./center-selection.component.css']
})

export class CenterSelectionComponent implements OnInit {

  displayedColumns: string[] = ['select', 'name', 'address', 'contact_person', 'centre_type', 'contact'];
  dataSource = new MatTableDataSource<RegistrationCentre>(REGISTRATION_CENTRES);
  selection = new SelectionModel<RegistrationCentre>(true, []);


  locationTypes = [
    { value: 'province', viewValue: 'Province' },
    { value: 'city', viewValue: 'City' },
    { value: 'local_admin_authority', viewValue: 'Local Admin Authority' },
    { value: 'postal_code', viewValue: 'Postal Code'}
  ];

  locationType = null;
  text = null;
  showTable = false;
  selectedCentre = null;
  showMap = false;
  enableNextButton = false;
  step = 0;
  showDescription = false;

  constructor(private dialog: MatDialog) { }

  ngOnInit() {
  }

  setStep(index: number) {
    this.step = index;
  }

  nextStep() {
    this.step++;
    this.showDescription = true;
  }

  prevStep() {
    this.step--;
  }

  showResults() {
    if (this.locationType !== null && this.text !== null) {
      this.showTable = true;
      this.selectedRow(REGISTRATION_CENTRES[0]);
    }
  }

  plotOnMap() {
    this.showMap = true;
  }

  selectedRow(row) {
    this.selectedCentre = row;
    this.enableNextButton = true;
    console.log(row);
    this.plotOnMap();
  }

  getLocation() {

    if (navigator.geolocation) {
       navigator.geolocation.getCurrentPosition(position => {
         console.log(position);
         this.showTable = true;
         this.selectedRow(REGISTRATION_CENTRES[0]);
       });
    } else {
      alert('Location not suppored in this browser');
    }
  }

  makeBooking(): void {
    const data = {
      case: 'MESSAGE',
      title: 'Success',
      message: 'Action was completed successfully'
    };
   const dialogRef = this.dialog.open(DialougComponent, {
      width: '250px',
      data: data
    });
  }

}
