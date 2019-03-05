import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';

import { MatDialog } from '@angular/material';
import { DialougComponent } from '../../../shared/dialoug/dialoug.component';
import { DataStorageService } from 'src/app/core/services/data-storage.service';
import { Router, ActivatedRoute } from '@angular/router';
import { BookingModelRequest } from 'src/app/shared/booking-request.model';
import { BookingModel } from '../center-selection/booking.model';

import { NameList } from 'src/app/shared/models/demographic-model/name-list.modal';
import { SharedService } from '../booking.service';
import { RegistrationService } from 'src/app/core/services/registration.service';
import { TranslateService } from '@ngx-translate/core';
import Utils from 'src/app/app.util';
import * as appConstants from '../../../app.constants';

@Component({
  selector: 'app-time-selection',
  templateUrl: './time-selection.component.html',
  styleUrls: ['./time-selection.component.css']
})
export class TimeSelectionComponent implements OnInit {
  @ViewChild('widgetsContent', { read: ElementRef }) public widgetsContent;
  @ViewChild('cardsContent', { read: ElementRef }) public cardsContent;
  registrationCenter: String;
  selectedCard = 0;
  selectedTile = 0;
  limit = [];
  showAddButton = false;
  names: NameList[];
  deletedNames = [];
  availabilityData = [];
  cutoff = 1;
  days = 7;
  enableBookButton = false;
  activeTab = 'morning';
  bookingDataList = [];
  temp: NameList[];
  registrationCenterLunchTime = [];
  secondaryLang = localStorage.getItem('secondaryLangCode');
  secondaryLanguagelabels: any;

  constructor(
    private sharedService: SharedService,
    private dialog: MatDialog,
    private dataService: DataStorageService,
    private router: Router,
    private route: ActivatedRoute,
    private registrationService: RegistrationService,
    private translate: TranslateService
  ) {
    this.translate.use(localStorage.getItem('langCode'));
  }

  ngOnInit() {
    this.names = this.sharedService.getNameList();
    this.temp = this.sharedService.getNameList();
    console.log(this.temp);
    this.registrationCenterLunchTime = this.temp[0].registrationCenter.lunchEndTime.split(':');
    this.sharedService.resetNameList();
    this.registrationCenter = this.registrationService.getRegCenterId();
    console.log(this.registrationCenter);
    console.log('in onInit', this.names);
    this.getSlotsforCenter(this.registrationCenter);

    this.dataService.getSecondaryLanguageLabels(localStorage.getItem('langCode')).subscribe(response => {
      this.secondaryLanguagelabels = response['timeSelection'].booking;
    });
  }

  public scrollRight(): void {
    this.widgetsContent.nativeElement.scrollTo({
      left: this.widgetsContent.nativeElement.scrollLeft + 100,
      behavior: 'smooth'
    });
  }

  public scrollLeft(): void {
    this.widgetsContent.nativeElement.scrollTo({
      left: this.widgetsContent.nativeElement.scrollLeft - 100,
      behavior: 'smooth'
    });
  }

  dateSelected(index: number) {
    this.selectedTile = index;
    console.log('selected tile index', this.selectedTile);
    this.placeNamesInSlots();
    this.enableBookButton = true;
  }

  cardSelected(index: number): void {
    this.selectedCard = index;
  }

  itemDelete(index: number): void {
    this.deletedNames.push(this.availabilityData[this.selectedTile].timeSlots[this.selectedCard].names[index]);
    this.availabilityData[this.selectedTile].timeSlots[this.selectedCard].names.splice(index, 1);
    console.log(index, 'item to be deleted from card', this.deletedNames);
    this.enableBookButton = false;
  }

  addItem(index: number): void {
    this.availabilityData[this.selectedTile].timeSlots[this.selectedCard].names.push(this.deletedNames[index]);
    this.deletedNames.splice(index, 1);
  }

  formatJson(centerDetails: any) {
    centerDetails.forEach(element => {
      let sumAvailability = 0;
      element.timeSlots.forEach(slot => {
        sumAvailability += slot.availability;
        slot.names = [];
        let fromTime = slot.fromTime.split(':');
        let toTime = slot.toTime.split(':');
        if (fromTime[0] < this.registrationCenterLunchTime[0]) {
          slot.tag = 'morning';
        } else {
          slot.tag = 'afternoon';
        }
        slot.displayTime = Number(fromTime[0]) > 12 ? Number(fromTime[0]) - 12 : fromTime[0];
        slot.displayTime += ':' + fromTime[1] + ' - ';
        slot.displayTime += Number(toTime[0]) > 12 ? Number(toTime[0]) - 12 : toTime[0];
        slot.displayTime += ':' + toTime[1];
      });
      element.TotalAvailable = sumAvailability;
      const cutOffDate = new Date();
      cutOffDate.setDate(cutOffDate.getDate() + this.cutoff);
      if (new Date(Date.parse(element.date)) < cutOffDate) {
        element.inActive = true;
      } else {
        element.inActive = false;
      }
      element.displayDate =
        element.date.split('-')[2] +
        ' ' +
        appConstants.MONTHS[Number(element.date.split('-')[1])] +
        ', ' +
        element.date.split('-')[0];
      element.displayDay = appConstants.DAYS[new Date(Date.parse(element.date)).getDay()];
      if (!element.inActive) {
        this.availabilityData.push(element);
      }
      console.log(this.availabilityData);
    });
    this.placeNamesInSlots();
  }

  placeNamesInSlots() {
    console.log('in plot function', this.names);
    this.availabilityData[this.selectedTile].timeSlots.forEach(slot => {
      if (this.names.length !== 0) {
        while (slot.names.length < slot.availability && this.names.length !== 0) {
          slot.names.push(this.names[0]);
          this.names.splice(0, 1);
        }
      }
    });
    console.log(this.availabilityData[this.selectedTile]);
  }

  getSlotsforCenter(id) {
    this.dataService.getAvailabilityData(id).subscribe(
      response => {
        console.log(response);
        if (response['response']) {
          this.formatJson(response['response'].centerDetails);
        }
      },
      error => {
        console.log(error);
      }
    );
  }

  tabSelected(selection) {
    this.activeTab = selection;
    console.log(this.activeTab);
  }

  makeBooking(): void {
    this.bookingDataList = [];
    this.availabilityData.forEach(data => {
      data.timeSlots.forEach(slot => {
        if (slot.names.length !== 0) {
          slot.names.forEach(name => {
            const bookingData = new BookingModel(
              this.registrationCenter.toString(),
              data.date,
              slot.fromTime,
              slot.toTime
            );
            console.log(name);
            const requestObject = {
              newBookingDetails: bookingData,
              oldBookingDetails: name.status ? (name.status.toLowerCase() !== 'booked' ? null : name.regDto) : null,
              preRegistrationId: name.preRegId
            };
            this.bookingDataList.push(requestObject);
          });
        }
      });
    });
    const request = new BookingModelRequest(this.bookingDataList);
    console.log('request being sent from time selection', request);
    this.dataService.makeBooking(request).subscribe(
      response => {
        console.log(response);
        const data = {
          case: 'MESSAGE',
          title: this.secondaryLanguagelabels.title_success,
          message: this.secondaryLanguagelabels.msg_success
        };
        const dialogRef = this.dialog
          .open(DialougComponent, {
            width: '350px',
            data: data
          })
          .afterClosed()
          .subscribe(() => {
            this.temp.forEach(name => {
              this.sharedService.addNameList(name);
              const booking = this.bookingDataList.filter(element => element.preRegistrationId === name.preRegId);
              const appointmentDateTime = Utils.getBookingDateTime(booking[0].newBookingDetails.appointment_date, booking[0].newBookingDetails.time_slot_from);
              this.sharedService.updateBookingDetails(name.preRegId, appointmentDateTime);
            });
            const arr = this.router.url.split('/');
            arr.pop();
            arr.pop();
            arr.push('summary');
            arr.push('acknowledgement');
            const url = arr.join('/');
            this.router.navigateByUrl(url);
            // this.router.navigate(['../acknowledgement'], { relativeTo: this.route });
          });
      },
      error => {
        console.log(error);
        const data = {
          case: 'MESSAGE',
          title: this.secondaryLanguagelabels.title_failure,
          message: this.secondaryLanguagelabels.msg_failure
        };
        const dialogRef = this.dialog.open(DialougComponent, {
          width: '350px',
          data: data
        });
      }
    );
  }

  navigateDashboard() {
    const routeParams = this.router.url.split('/');
    this.router.navigate(['dashboard', routeParams[2]]);
  }

  navigateBack() {
    const url = Utils.getURL(this.router.url, 'pick-center');
    // const routeParams = this.router.url.split('/');
    // this.router.navigate([routeParams[1], routeParams[2], 'booking', 'pick-center']);
    this.router.navigateByUrl(url);
  }
}
