import { Component, OnInit, ViewChild, ElementRef, Input } from '@angular/core';
import { SharedService } from 'src/app/shared/shared.service';
import { NameList } from '../demographic/modal/name-list.modal';
import { MatDialog } from '@angular/material';
import { DialougComponent } from '../../shared/dialoug/dialoug.component';
import { DataStorageService } from 'src/app/shared/data-storage.service';
import { TranslateService } from '@ngx-translate/core';
import * as constants from '../../app.constants';

@Component({
  selector: 'app-time-selection',
  templateUrl: './time-selection.component.html',
  styleUrls: ['./time-selection.component.css']
})
export class TimeSelectionComponent implements OnInit {
  @ViewChild('widgetsContent', { read: ElementRef }) public widgetsContent;
  @ViewChild('cardsContent', { read: ElementRef }) public cardsContent;
  @Input() registrationCenter: any;
  selectedCard = 0;
  selectedTile = 0;
  limit = [];
  showAddButton = false;
  names: NameList[];
  deletedNames = [];
  availabilityData = [];
  cutoff = 1;
  days = 7;
  MONTHS = ['', 'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
  DAYS = ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'];
  enableBookButton = false;
  activeTab = 'morning';

  constructor(
    private sharedService: SharedService,
    private dialog: MatDialog,
    private dataService: DataStorageService,
    private translate: TranslateService
  ) {}

  ngOnInit() {
    if (localStorage.getItem('langCode')) {
      this.translate.use(localStorage.getItem('langCode'));
    }
    this.names = constants.nameList;
 //   this.sharedService.resetNameList();
    console.log('in onInit', this.names);
    this.getSlotsforCenter(1);
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

  // openDialog() {
  //   const dialogRef = this.dialog
  //     .open(DialougComponent, {
  //       width: '400px',
  //       data: {
  //         case: 'SLOTS',
  //         title: 'Select names for the Slot',
  //         names: this.deletedNames
  //       }
  //     })
  //     .afterClosed()
  //     .subscribe(addedList => {
  //       addedList.forEach(item => {
  //         // tslint:disable-next-line:max-line-length
  //         if (
  //           this.availabilityData[this.selectedTile].timeSlots[this.selectedCard].names.length <
  //           this.availabilityData[this.selectedTile].timeSlots[this.selectedCard].availability
  //         ) {
  //           this.availabilityData[this.selectedTile].timeSlots[this.selectedCard].names.push(item);
  //         } else {
  //           this.deletedNames.push(item);
  //         }
  //       });
  //       if (this.deletedNames.length === 0) {
  //         this.showAddButton = false;
  //         this.enableBookButton = true;
  //       }
  //     });
  // }

  formatJson(centerDetails: any) {
    centerDetails.forEach(element => {
      let sumAvailability = 0;
      element.timeSlots.forEach(slot => {
        sumAvailability += slot.availability;
        slot.names = [];
        let fromTime = slot.fromTime.split(':');
        let toTime = slot.toTime.split(':');
        slot.displayTime = Number(fromTime[0]) > 12 ? Number(fromTime[0]) - 12 : fromTime[0];
        slot.displayTime += ':' + fromTime[1] + ' - ';
        slot.displayTime += Number(toTime[0]) > 12 ? Number(fromTime[0]) - 12 : fromTime[0];
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
        this.MONTHS[Number(element.date.split('-')[1])] +
        ', ' +
        element.date.split('-')[0];
      element.displayDay = this.DAYS[new Date(Date.parse(element.date)).getDay()];
      if (!element.inActive) {
        this.availabilityData.push(element);
      }
      console.log(this.availabilityData);
      this.placeNamesInSlots();
    });
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
  }
}
