import { Component } from '@angular/core';
// import { MatTableDataSource, } from '@angular/material/table';
// import { SelectionModel } from '@angular/cdk/collections'
// import { DashboardElement } from '../registration/registration.modal';



// const ELEMENT_DATA: DashboardElement[] = [
//   { applicationID: '1', name: 'Shashank', appointmentDateTime: '1.0079', status: 'Pending' },
//   { applicationID: '2', name: 'Helium', appointmentDateTime: '4.0026', status: 'He' },
//   { applicationID: '3', name: 'Lithium', appointmentDateTime: '6.941', status: 'Li' },
//   { applicationID: '4', name: 'Beryllium', appointmentDateTime: '9.0122', status: 'Be' },
//   { applicationID: '5', name: 'Boron', appointmentDateTime: '10.811', status: 'B' },
//   { applicationID: '6', name: 'Carbon', appointmentDateTime: '12.0107', status: 'C' },
//   { applicationID: '7', name: 'Nitrogen', appointmentDateTime: '14.0067', status: 'N' },
//   { applicationID: '8', name: 'Oxygen', appointmentDateTime: '15.9994', status: 'O' },
//   { applicationID: '9', name: 'Fluorine', appointmentDateTime: '18.9984', status: 'F' },
//   { applicationID: '10', name: 'Neon', appointmentDateTime: '20.1797', status: 'Ne' },
// ];

/**
 * @title Table with selection
 */

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent {
  // displayedColumns: string[] = ['select', 'appId', 'name', 'dateTime', 'status'];
  // dataSource = new MatTableDataSource<DashboardElement>(ELEMENT_DATA);
  // selection = new SelectionModel<DashboardElement>(true, []);

  // /** Whether the number of selected elements matches the total number of rows. */
  // isAllSelected() {
  //   const numSelected = this.selection.selected.length;
  //   const numRows = this.dataSource.data.length;
  //   return numSelected === numRows;
  // }

  // /** Selects all rows if they are not all selected; otherwise clear selection. */
  // masterToggle() {
  //   this.isAllSelected() ?
  //     this.selection.clear() :
  //     this.dataSource.data.forEach(row => this.selection.select(row));
  // }
}