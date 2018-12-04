import { Component, OnInit, ViewChild, ElementRef, Input } from '@angular/core';
import { SharedService } from 'src/app/shared/shared.service';
import { NameList } from '../demographic/name-list.modal';
import { MatDialog } from '@angular/material';
import { DialougComponent } from '../../shared/dialoug/dialoug.component';
import { DataStorageService } from 'src/app/shared/data-storage.service';

@Component({
  selector: 'app-time-selection',
  templateUrl: './time-selection.component.html',
  styleUrls: ['./time-selection.component.css']
})
export class TimeSelectionComponent implements OnInit {

  @ViewChild('widgetsContent', { read: ElementRef }) public widgetsContent;
  @ViewChild('cardsContent', { read: ElementRef }) public cardsContent;
  @Input() registrationCenter: any;
  numbers: number[];
  selectedCard = 0;
  selectedTile = null;
  limit = 3;
  showAddButton = false;
  names: NameList[];
  deletedNames = [];
  availabilityData = [];
  cutoff = 1;
  days = 7;
  MONTHS = ['', 'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];

  constructor(private sharedService: SharedService, private dialog: MatDialog, private dataService: DataStorageService) { }

  ngOnInit() {
    this.numbers = Array(10).fill(0).map((x, i) => i); // [0,1,2,3,4]
    this.names = this.sharedService.getNameList();
    this.dataService.getAvailabilityData(this.registrationCenter.id).subscribe(response => {
      this.formatJson(response['response'].centerDetails);
    }, error => {
      console.log(error);
    });
  }

  public scrollRight(): void {
    this.widgetsContent.nativeElement.scrollTo({ left: (this.widgetsContent.nativeElement.scrollLeft + 230), behavior: 'smooth' });
  }

  public scrollLeft(): void {
    this.widgetsContent.nativeElement.scrollTo({ left: (this.widgetsContent.nativeElement.scrollLeft - 230), behavior: 'smooth' });
  }

  public scrollRightCard(): void {
    this.cardsContent.nativeElement.scrollTo({ left: (this.cardsContent.nativeElement.scrollLeft + 200), behavior: 'smooth' });
  }

  public scrollLeftCard(): void {
    this.cardsContent.nativeElement.scrollTo({ left: (this.cardsContent.nativeElement.scrollLeft - 200), behavior: 'smooth' });
  }

  dateSelected(index: number) {
    this.selectedTile = index;
    console.log('selected tile index', this.selectedTile);
    this.placeNamesInSlots();
  }

  cardSelected(index: number): void {
    this.selectedCard = index;
  }

  changeLimit(): void {
    this.limit += 2;
  }

  itemDelete(index: number): void {
    this.deletedNames.push(this.names[index]);
    this.names.splice(index, 1);
    console.log(index, 'item to be deleted from card', this.deletedNames);
    this.showAddButton = true;
  }

  openDialog() {
    const dialogRef = this.dialog.open(DialougComponent, {
      width: '400px',
      data: {
        case: 'SLOTS',
        title: 'Select names for the Slot',
        names: this.deletedNames
      }
    }).afterClosed().subscribe(addedList => {
      console.log(addedList);
      if (this.deletedNames.length === 0) {
        this.showAddButton = false;
      }
    });
  }

  formatJson(centerDetails: any) {
    centerDetails.forEach(element => {
      let sumAvailability = 0;
      element.timeSlots.forEach(slot => {
        sumAvailability += slot.availability;
        slot.names = [];
        slot.DisplayTime = slot.fromTime.split(':')[0] + ':' + slot.fromTime.split(':')[1];
        if (Number(slot.fromTime.split(':')[0]) < 12) {
          slot.DisplayTime += ' AM';
        } else {
          slot.DisplayTime += ' PM';
        }
      });
      element.TotalAvailable = sumAvailability;
      const cutOffDate = new Date();
      cutOffDate.setDate(cutOffDate.getDate() + this.cutoff);
      if (new Date(Date.parse(element.date)) < cutOffDate) {
        element.inActive = true;
      } else {
        element.inActive = false;
      }
      element.displayDate = element.date.split('-')[2] + ' ' + this.MONTHS[Number(element.date.split('-')[1])];
      if (!element.inActive) {
        this.availabilityData.push(element);
      }
    });
  }

  placeNamesInSlots() {
    let index = 0;
    console.log(this.names);
    this.availabilityData.forEach(data => {
      data.timeSlots.forEach(slot => {
        while (slot.names.length <= slot.availability && this.names.length !== 0) {
          slot.names.push(this.names[index]);
          this.names.splice(index, 1);
          index++;
        }
      });
    });
    console.log(this.availabilityData);
  }

}
