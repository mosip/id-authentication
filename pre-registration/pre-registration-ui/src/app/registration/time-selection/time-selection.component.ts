import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { SharedService } from 'src/app/shared/shared.service';
import { NameList } from '../demographic/name-list.modal';
import { MatDialog } from '@angular/material';
import { DialougComponent } from '../../shared/dialoug/dialoug.component';

@Component({
  selector: 'app-time-selection',
  templateUrl: './time-selection.component.html',
  styleUrls: ['./time-selection.component.css']
})
export class TimeSelectionComponent implements OnInit {

  @ViewChild('widgetsContent', { read: ElementRef }) public widgetsContent;
  @ViewChild('cardsContent', { read: ElementRef }) public cardsContent;
  numbers: number[];
  selectedCard = 0;
  selectedTile = null;
  limit = 3;
  showAddButton = false;
  names: NameList[];
  deletedNames = [];

  constructor(private sharedService: SharedService, private dialog: MatDialog) { }

  ngOnInit() {
    this.numbers = Array(10).fill(0).map((x, i) => i); // [0,1,2,3,4]
    this.names = this.sharedService.getNameList();
    console.log(this.names);
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
    if (index % 3 !== 0) {
      this.selectedTile = index;
    }
  }

  cardSelected(index: number): void {
    this.selectedCard = index;
    console.log(this.selectedCard);
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
    });
  }

}
