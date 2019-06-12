import { Component, OnInit, ViewChild, ElementRef, OnDestroy } from '@angular/core';

import { MatDialog } from '@angular/material';
import { DialougComponent } from '../../../shared/dialoug/dialoug.component';
import { DataStorageService } from 'src/app/core/services/data-storage.service';
import { Router } from '@angular/router';
import { BookingModel } from '../center-selection/booking.model';

import { NameList } from 'src/app/shared/models/demographic-model/name-list.modal';
import { BookingService } from '../booking.service';
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
export class TimeSelectionComponent implements OnInit, OnDestroy {
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
  days: number;
  disableAddButton = false;
  activeTab = 'morning';
  bookingDataList = [];
  temp: NameList[];
  registrationCenterLunchTime = [];
  secondaryLang = localStorage.getItem('secondaryLangCode');
  secondaryLanguagelabels: any;
  errorlabels: any;
  showMorning: boolean;
  showAfternoon: boolean;
  disableContinueButton = false;
  DAYS: any;

  constructor(
    private bookingService: BookingService,
    private dialog: MatDialog,
    private dataService: DataStorageService,
    private router: Router,
    private registrationService: RegistrationService,
    private translate: TranslateService,
    private configService: ConfigService
  ) {
    // smoothscroll.polyfill();
    this.translate.use(localStorage.getItem('langCode'));
  }

  ngOnInit() {
    this.names = this.bookingService.getNameList();
    this.temp = this.bookingService.getNameList();
    this.days = this.configService.getConfigByKey(appConstants.CONFIG_KEYS.preregistration_availability_noOfDays);
    if (this.temp[0]) {
      this.registrationCenterLunchTime = this.temp[0].registrationCenter.lunchEndTime.split(':');
    }
    this.bookingService.resetNameList();
    this.registrationCenter = this.registrationService.getRegCenterId();
    this.getSlotsforCenter(this.registrationCenter);

    this.dataService.getSecondaryLanguageLabels(localStorage.getItem('langCode')).subscribe(response => {
      this.secondaryLanguagelabels = response['timeSelection'].booking;
      this.errorlabels = response['error'];
      this.DAYS = response['DAYS'];
    });
  }

  public scrollRight(): void {
    // this.widgetsContent.nativeElement.scrollBy({
    //   left: this.widgetsContent.nativeElement.scrollLeft + 100,
    //   behavior: 'smooth'
    // });
    this.widgetsContent.nativeElement.scrollTo({
      left: this.widgetsContent.nativeElement.scrollLeft + 100,
      behavior: 'smooth'
    });
  }

  public scrollLeft(): void {
    // this.widgetsContent.nativeElement.scrollBy({
    //   left: this.widgetsContent.nativeElement.scrollLeft - 100,
    //   behavior: 'smooth'
    // });
    this.widgetsContent.nativeElement.scrollTo({
      left: this.widgetsContent.nativeElement.scrollLeft - 100,
      behavior: 'smooth'
    });
  }

  dateSelected(index: number) {
    this.selectedTile = index;
    this.placeNamesInSlots();
    this.cardSelected(0);
  }

  cardSelected(index: number): void {
    this.selectedCard = index;
    this.canAddApplicant(this.availabilityData[this.selectedTile].timeSlots[this.selectedCard]);
  }

  itemDelete(index: number): void {
    this.deletedNames.push(this.availabilityData[this.selectedTile].timeSlots[this.selectedCard].names[index]);
    this.availabilityData[this.selectedTile].timeSlots[this.selectedCard].names.splice(index, 1);
    this.canAddApplicant(this.availabilityData[this.selectedTile].timeSlots[this.selectedCard]);
  }

  addItem(index: number): void {
    if (this.canAddApplicant(this.availabilityData[this.selectedTile].timeSlots[this.selectedCard])) {
      this.availabilityData[this.selectedTile].timeSlots[this.selectedCard].names.push(this.deletedNames[index]);
      this.deletedNames.splice(index, 1);
    }
  }

  canAddApplicant(slot: any): boolean {
    if (slot.availability > slot.names.length) {
      this.disableAddButton = false;
      return true;
    } else {
      this.disableAddButton = true;
      return false;
    }
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
      element.displayDay = this.DAYS[new Date(Date.parse(element.date)).getDay()];
      if (!element.inActive) {
        this.availabilityData.push(element);
      }
    });
    this.placeNamesInSlots();
  }

  placeNamesInSlots() {
    this.availabilityData[this.selectedTile].timeSlots.forEach(slot => {
      if (this.names.length !== 0) {
        while (slot.names.length < slot.availability && this.names.length !== 0) {
          slot.names.push(this.names[0]);
          this.names.splice(0, 1);
        }
      }
    });
    this.enableBucketTabs();
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
        if (response['response']) {
          this.formatJson(response['response'].centerDetails);
        } else if (response[appConstants.NESTED_ERROR]) {
          this.displayMessage('Error', this.errorlabels.error , '');
        }
      },
      (error) => {
        this.displayMessage('Error', this.errorlabels.error , error);
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
  }

  getNames(): string {

    const x = [];

    this.deletedNames.forEach(name => {
      x.push(name.fullName);
    });

    return x.join(', ');
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
    const obj = {
      bookingRequest: this.bookingDataList
    };
    const request = new RequestModel(appConstants.IDS.booking, obj);
    if(this.deletedNames.length!==0){
      const data = {
        case: 'CONFIRMATION',
        title:'',
        message: this.secondaryLanguagelabels.deletedApplicant1[0] + ' ' + this.getNames() + ' ' + this.secondaryLanguagelabels.deletedApplicant1[1] + '?',
        yesButtonText: this.secondaryLanguagelabels.yesButtonText,
        noButtonText:  this.secondaryLanguagelabels.noButtonText
      };
      const dialogRef = this.dialog
      .open(DialougComponent, {
        width: '350px',
        data: data,
        disableClose: true
      })
       dialogRef.afterClosed().subscribe(selectedOption => {
        if (selectedOption) {
          this.bookingOperation(request);
        } else {
          this.disableContinueButton = false;
          return;
        }
      });
    }
    else {
      this.bookingOperation(request);
    }

  }

  bookingOperation(request){
    this.dataService.makeBooking(request).subscribe(
      response => {
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
                  this.bookingService.addNameList(name);
                  const appointmentDateTime = booking[0].appointment_date + ',' + booking[0].time_slot_from;
                  this.bookingService.updateBookingDetails(name.preRegId, appointmentDateTime);
                }
              });
              this.bookingService.setSendNotification(true);
              const url = Utils.getURL(this.router.url, 'summary/acknowledgement', 2);
              this.router.navigateByUrl(url);
            });
        } else {
          this.displayMessage('Error', this.errorlabels.error , '');
        }
      },
      (error) => {
        this.displayMessage('Error', this.errorlabels.error , error);
      }
    );
  }

  displayMessage(title: string, message: string , error: any) {
    this.disableContinueButton = false;
    if(error && (error[appConstants.ERROR][appConstants.NESTED_ERROR][0].errorCode === appConstants.ERROR_CODES.tokenExpired))
    {
        message = this.errorlabels.tokenExpiredLogout;
        title = '';
    }
    const messageObj = {
      case: 'MESSAGE',
      title: title,
      message: message
    };
    this.openDialog(messageObj, '250px');
  }
  openDialog(data, width) {
    const dialogRef = this.dialog.open(DialougComponent, {
      width: width,
      data: data
    });
    return dialogRef;
  }

  navigateDashboard() {
    this.router.navigate(['dashboard']);
  }

  reloadData() {
    this.bookingService.flushNameList();
    this.temp.forEach(name => {
      this.bookingService.addNameList(name);
    });
  }

  navigateBack() {
    this.reloadData();
    const url = Utils.getURL(this.router.url, 'pick-center');
    this.router.navigateByUrl(url);
  }

  ngOnDestroy() {
    if (!this.bookingService.getSendNotification())
      this.reloadData();
  }
}
