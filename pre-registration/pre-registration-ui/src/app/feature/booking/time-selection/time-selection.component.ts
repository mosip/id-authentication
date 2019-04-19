import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';

import { MatDialog } from '@angular/material';
import { DialougComponent } from '../../../shared/dialoug/dialoug.component';
import { DataStorageService } from 'src/app/core/services/data-storage.service';
import { Router, ActivatedRoute } from '@angular/router';
import { BookingModel } from '../center-selection/booking.model';

import { NameList } from 'src/app/shared/models/demographic-model/name-list.modal';
import { SharedService } from '../booking.service';
import { RegistrationService } from 'src/app/core/services/registration.service';
import { TranslateService } from '@ngx-translate/core';
import Utils from 'src/app/app.util';
import * as appConstants from '../../../app.constants';
import { ConfigService } from 'src/app/core/services/config.service';
import { RequestModel } from 'src/app/shared/models/request-model/RequestModel';

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
  // cutoff = 1;
  days: number;
  enableBookButton = false;
  activeTab = 'morning';
  bookingDataList = [];
  temp: NameList[];
  registrationCenterLunchTime = [];
  secondaryLang = localStorage.getItem('secondaryLangCode');
  secondaryLanguagelabels: any;
  showMorning: boolean;
  showAfternoon: boolean;
  disableContinueButton = false;

  constructor(
    private sharedService: SharedService,
    private dialog: MatDialog,
    private dataService: DataStorageService,
    private router: Router,
    private route: ActivatedRoute,
    private registrationService: RegistrationService,
    private translate: TranslateService,
    private configService: ConfigService
  ) {
    this.translate.use(localStorage.getItem('langCode'));
  }

  ngOnInit() {
    this.names = this.sharedService.getNameList();
    this.temp = this.sharedService.getNameList();
    console.log('ngOninit temp', this.temp);
    this.days = this.configService.getConfigByKey(appConstants.CONFIG_KEYS.preregistration_availability_noOfDays);
    if (this.temp[0]) {
      this.registrationCenterLunchTime = this.temp[0].registrationCenter.lunchEndTime.split(':');
    }
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
          element.showMorning = true;
        } else {
          slot.tag = 'afternoon';
          element.showAfternoon = true;
        }
        slot.displayTime = Number(fromTime[0]) > 12 ? Number(fromTime[0]) - 12 : fromTime[0];
        slot.displayTime += ':' + fromTime[1] + ' - ';
        slot.displayTime += Number(toTime[0]) > 12 ? Number(toTime[0]) - 12 : toTime[0];
        slot.displayTime += ':' + toTime[1];
      });
      element.TotalAvailable = sumAvailability;
      element.inActive = false;
      element.displayDate = Utils.getBookingDateTime(element.date, '', localStorage.getItem('langCode'));
        // element.date.split('-')[2] +
        // ' ' +
        // appConstants.MONTHS[Number(element.date.split('-')[1])] +
        // ', ' +
        // element.date.split('-')[0];
      element.displayDay = appConstants.DAYS[localStorage.getItem('langCode')][new Date(Date.parse(element.date)).getDay()];
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
    this.enableBucketTabs();
    console.log(this.availabilityData[this.selectedTile]);
  }

  enableBucketTabs() {
    const element = this.availabilityData[this.selectedTile];
    if (element.showMorning && element.showAfternoon) {
      this.tabSelected('morning');
    } else if (element.showMorning) {
      this.tabSelected('morning');
    } else {
      this.tabSelected('afternoon');
    }
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

  tabSelected(selection: string) {
    if (
      (selection === 'morning' && this.availabilityData[this.selectedTile].showMorning) ||
      (selection === 'afternoon' && this.availabilityData[this.selectedTile].showAfternoon)
    ) {
      this.activeTab = selection;
    }
    console.log(this.activeTab);
  }

  makeBooking(): void {
    this.disableContinueButton = true;
    this.bookingDataList = [];
    this.availabilityData.forEach(data => {
      data.timeSlots.forEach(slot => {
        if (slot.names.length !== 0) {
          slot.names.forEach(name => {
            const bookingData = new BookingModel(
              name.preRegId,
              this.registrationCenter.toString(),
              data.date,
              slot.fromTime,
              slot.toTime
            );
            this.bookingDataList.push(bookingData);
          });
        }
      });
    });
    if (this.bookingDataList.length === 0) {
      this.disableContinueButton = false;
      return;
    }
    const request = new RequestModel(appConstants.IDS.booking, this.bookingDataList);
    console.log('request being sent from time selection', request);
    this.dataService.makeBooking(request).subscribe(
      response => {
        console.log(response);
        if (!response['errors']) {
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
                const booking = this.bookingDataList.filter(element => element.preRegistrationId === name.preRegId);
                if (booking[0]) {
                  this.sharedService.addNameList(name);
                  const appointmentDateTime = booking[0].appointment_date + ',' + booking[0].time_slot_from;
                  this.sharedService.updateBookingDetails(name.preRegId, appointmentDateTime);
                }
              });
              this.sharedService.setSendNotification(true);
              const url = Utils.getURL(this.router.url, 'summary/acknowledgement', 2);
              this.router.navigateByUrl(url);
            });
        } else {
          this.showError();
        }
      },
      error => {
        console.log(error);
        this.showError();
      }
    );
  }

  showError() {
    this.disableContinueButton = false;
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

  navigateDashboard() {
    const routeParams = this.router.url.split('/');
    this.router.navigate(['dashboard']);
  }

  navigateBack() {
    this.sharedService.flushNameList();
    this.temp.forEach(name => {
      this.sharedService.addNameList(name);
    });
    const url = Utils.getURL(this.router.url, 'pick-center');
    // const routeParams = this.router.url.split('/');
    // this.router.navigate([routeParams[1], routeParams[2], 'booking', 'pick-center']);
    this.router.navigateByUrl(url);
  }
}
