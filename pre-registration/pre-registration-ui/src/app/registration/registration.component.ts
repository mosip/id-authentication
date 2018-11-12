import { Component, OnInit } from '@angular/core';

import { MatTableDataSource, } from '@angular/material/table';
import { SelectionModel } from '@angular/cdk/collections'
import { Applicant } from '../registration/registration.modal';
import { Router, ActivatedRoute, Params } from '@angular/router';
import { DialougComponent } from './dialoug/dialoug.component';
import { MatDialog } from '@angular/material';
import { RegistrationService } from './registration.service';


@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css']
})
export class RegistrationComponent implements OnInit {


  users: Applicant[] = [];
  // = [
  //   { applicationID: '1', name: 'Shashank', appointmentDateTime: '1.0079', status: 'Pending' },
  //   { applicationID: '2', name: 'Helium', appointmentDateTime: '4.0026', status: 'He' },
  //   { applicationID: '10', name: 'Neon', appointmentDateTime: '20.1797', status: 'Ne' },
  // ];



  isNewApplication: boolean = false;
  loginId = '';
  isFetched = false;

  constructor(private router: Router,
    private route: ActivatedRoute,
    public dialog: MatDialog,
    private regService: RegistrationService) { }

  ngOnInit() {
    this.route.params
      .subscribe(
        (params: Params) => {
          this.loginId = params['id'];
        }
      );
    this.initUsers();
  }

  initUsers() {
    this.regService.getUsers().
      subscribe(
        (applicants: Applicant[]) => {
          for (let user of applicants) {
            this.users.push(new Applicant(user['pre-registration-id'],
              user['fullname'], user['appointment_dtimesz'], user['status_code']));
          }
          this.isFetched = true;
        }
      );;
  }

  displayedColumns: string[] = ['select', 'appId', 'name', 'dateTime', 'status', 'operation'];
  dataSource = new MatTableDataSource<Applicant>(this.users);
  selection = new SelectionModel<Applicant>(true, []);

  /** Whether the number of selected elements matches the total number of rows. */
  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.dataSource.data.length;
    return numSelected === numRows;
  }

  /** Selects all rows if they are not all selected; otherwise clear selection. */
  masterToggle() {
    this.isAllSelected() ?
      this.selection.clear() :
      this.dataSource.data.forEach(row => this.selection.select(row));
  }

  onNewApplication() {
    this.openDialog();
  }


  openDialog(): void {
    const dialogRef = this.dialog.open(DialougComponent, {
      width: '250px',
    });

    dialogRef.afterClosed().subscribe(numberOfApplicant => {
      if (numberOfApplicant != null) {
        this.router.navigate(['demographic', numberOfApplicant], { relativeTo: this.route });
        this.isNewApplication = true;
      }
    });
  }

  onDelete() {
    console.log('On delete');

  }

  onModifyData() {
    console.log('On modify data');
  }

  onModifyAppointment() {
    console.log('on modify appointment');
  }

  onHome() {
    this.router.navigate([''])
  }
}
