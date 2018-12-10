import { Component, OnInit, ViewChild } from '@angular/core';
import {MatTableDataSource, MatDialog} from '@angular/material';
import {SelectionModel} from '@angular/cdk/collections';
import { DialougComponent } from '../../shared/dialoug/dialoug.component';
import { SharedService } from 'src/app/shared/shared.service';
import { DataStorageService } from 'src/app/shared/data-storage.service';
import { RegistrationCentre } from './registration-center-details.model';
import { TimeSelectionComponent } from '../time-selection/time-selection.component';
import { BookingModel } from './booking.model';
import { BookingModelRequest } from './booking-request.model';
import { Router, ActivatedRoute } from '@angular/router';

let REGISTRATION_CENTRES: RegistrationCentre[] = [];

@Component({
  selector: 'app-center-selection',
  templateUrl: './center-selection.component.html',
  styleUrls: ['./center-selection.component.css']
})

export class CenterSelectionComponent implements OnInit {

  @ViewChild(TimeSelectionComponent)
  timeSelectionComponent: TimeSelectionComponent;

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
  bookingDataList = [];
  step = 0;
  showDescription = false;
  mapProvider = 'OSM';

  constructor(
    private dialog: MatDialog,
    private service: SharedService,
    private dataService: DataStorageService,
    private router: Router,
    private route: ActivatedRoute) { }

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
    this.bookingDataList = [];
    this.timeSelectionComponent.availabilityData.forEach(data => {
      data.timeSlots.forEach(slot => {
        if (slot.names.length !== 0) {
          slot.names.forEach(name => {
            const bookingData = new BookingModel(this.selectedCentre.id, data.date, slot.fromTime, slot.toTime);
            const requestObject = {
              newBookingDetails: bookingData,
              oldBookingDetails: null,
              pre_registration_id: name.preRegId
            };
            this.bookingDataList.push(requestObject);
          });
        }
      });
    });
    const request = new BookingModelRequest(this.bookingDataList);
    console.log(request);
    this.dataService.makeBooking(request).subscribe(() => {
        const data = {
            case: 'MESSAGE',
            title: 'Success',
            message: 'Action was completed successfully'
          };
        const dialogRef = this.dialog.open(DialougComponent, {
            width: '250px',
            data: data
          }).afterClosed().subscribe(() => {
            this.router.navigate(['../confirmation'], { relativeTo: this.route });
          });
        }, error => {
          console.log(error);
          const data = {
              case: 'MESSAGE',
              title: 'Failure',
              message: 'Action could not be completed'
            };
          const dialogRef = this.dialog.open(DialougComponent, {
              width: '250px',
              data: data
            });
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
