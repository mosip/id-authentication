import { Component, OnInit } from '@angular/core';
import {MatTableDataSource, MatDialog} from '@angular/material';
import {SelectionModel} from '@angular/cdk/collections';
import { DialougComponent } from '../../shared/dialoug/dialoug.component';
import { SharedService } from 'src/app/shared/shared.service';
import { DataStorageService } from 'src/app/shared/data-storage.service';
import { RegistrationCentre } from './registration-center-details.model';

let REGISTRATION_CENTRES: RegistrationCentre[] = [];

@Component({
  selector: 'app-center-selection',
  templateUrl: './center-selection.component.html',
  styleUrls: ['./center-selection.component.css']
})

export class CenterSelectionComponent implements OnInit {

  displayedColumns: string[] = ['select', 'name', 'addressLine1', 'contactPerson', 'centerTypeCode', 'contactPhone'];
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
  showMessage = false;
  enableNextButton = false;
  step = 0;
  showDescription = false;
  mapProvider = 'OSM';

  constructor(private dialog: MatDialog, private service: SharedService, private dataService: DataStorageService) { }

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
      this.showMap = false;
      this.dataService.getRegistrationCentersByName(this.locationType, this.text).subscribe(response => {
        console.log(response);
        if (response['registrationCenters'].length !== 0) {
          REGISTRATION_CENTRES = response['registrationCenters'];
          this.dataSource.data = REGISTRATION_CENTRES;
          this.showTable = true;
          this.selectedRow(REGISTRATION_CENTRES[0]);
          this.dispatchCenterCoordinatesList();
        } else {
          this.showMessage = true;
        }
      }, error => {
        this.showMessage = true;
      });
    }
  }

  plotOnMap() {
    this.showMap = true;
    this.service.changeCoordinates([Number(this.selectedCentre.longitude), Number(this.selectedCentre.latitude)]);
  }

  selectedRow(row) {
    this.selectedCentre = row;
    this.enableNextButton = true;
    console.log(row);
    this.plotOnMap();
  }

  getLocation() {

    if (navigator.geolocation) {
      this.showMap = false;
       navigator.geolocation.getCurrentPosition(position => {
         console.log(position);
        this.dataService.getNearbyRegistrationCenters(position.coords).subscribe(response => {
          console.log(response);
          if (response['registrationCenters'].length !== 0) {
            REGISTRATION_CENTRES = response['registrationCenters'];
            this.dataSource.data = REGISTRATION_CENTRES;
            this.showTable = true;
            this.selectedRow(REGISTRATION_CENTRES[0]);
            this.dispatchCenterCoordinatesList();
          } else {
            this.showMessage = true;
          }
        }, error => {
          this.showMessage = true;
        });
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

  dispatchCenterCoordinatesList() {
    const coords = [];
    REGISTRATION_CENTRES.forEach(centre => {
      const data = {
        id: centre.id,
        latitude: Number(centre.latitude),
        longitude: Number(centre.longitude)
      };
      coords.push(data);
    });
    this.service.listOfCenters(coords);
  }

}
