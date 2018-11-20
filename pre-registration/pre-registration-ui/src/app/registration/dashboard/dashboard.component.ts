import { Component, OnInit } from '@angular/core';

import { MatTableDataSource } from '@angular/material/table';
import { SelectionModel } from '@angular/cdk/collections';
import { Router, ActivatedRoute, Params } from '@angular/router';
import { DialougComponent } from '../dialoug/dialoug.component';
import { MatDialog } from '@angular/material';
import { RegistrationService } from '../registration.service';
import { Applicant } from './dashboard.modal';

@Component({
  selector: 'app-registration',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashBoardComponent implements OnInit {
  users: Applicant[] = [];
  // = [
  //   { applicationID: '1', name: 'Shashank', appointmentDateTime: '1.0079', status: 'Pending' },
  //   { applicationID: '2', name: 'Helium', appointmentDateTime: '4.0026', status: 'He' },
  //   { applicationID: '10', name: 'Neon', appointmentDateTime: '20.1797', status: 'Ne' },
  // ];

  displayedColumns: string[] = ['select', 'appId', 'name', 'dateTime', 'status', 'operation'];
  dataSource = new MatTableDataSource<Applicant>(this.users);
  selection = new SelectionModel<Applicant>(true, []);

  isNewApplication = false;
  loginId = '';
  isFetched = false;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    public dialog: MatDialog,
    private regService: RegistrationService
  ) {}

  ngOnInit() {
    this.route.params.subscribe((params: Params) => {
      this.loginId = params['id'];
    });
    this.initUsers();
  }

  initUsers() {
    this.regService.getUsers(this.loginId).subscribe(
      (applicants: Applicant[]) => {
        console.log(applicants);
        if (applicants['response'] !== null) {
          for (let index = 0; index < applicants['response'].length; index++) {
            this.users.push(
              new Applicant(
                applicants['response'][index]['preId'],
                applicants['response'][index]['firstname'],
                applicants['response'][index]['appointmentDate'],
                applicants['response'][index]['status_code']
              )
            );
          }
        }
        this.isFetched = true;
      },
      error => {
        this.isFetched = true;
      },
      () => {
        this.isFetched = true;
      }
    );
  }

  /** Whether the number of selected elements matches the total number of rows. */
  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.dataSource.data.length;
    return numSelected === numRows;
  }

  /** Selects all rows if they are not all selected; otherwise clear selection. */
  masterToggle() {
    this.isAllSelected() ? this.selection.clear() : this.dataSource.data.forEach(row => this.selection.select(row));
  }

  onNewApplication() {
    const data = {
      case: 'APPLICANTS'
    };
    const dialogRef = this.openDialog(data, `250px`);
    dialogRef.afterClosed().subscribe(numberOfApplicant => {
      if (numberOfApplicant != null) {
        this.router.navigate(['demographic', numberOfApplicant], { relativeTo: this.route });
        this.isNewApplication = true;
      }
    });
  }

  openDialog(data, width) {
    const dialogRef = this.dialog.open(DialougComponent, {
      width: width,
      data: data
    });
    return dialogRef;
  }

  onDelete(element) {
    const data = {
      case: 'DISCARD',
      disabled: {
        radioButton1: false,
        radioButton2: true
      }
    };
    let dialogRef = this.openDialog(data, `350px`);
    dialogRef.afterClosed().subscribe(selectedOption => {
      if (selectedOption) {
        console.log(selectedOption, element);
        const body = {
          case: 'CONFIRMATION',
          title: 'Confirm',
          message: 'The selected application will be deleted. Please confirm.',
          yesButtonText: 'Confirm',
          noButtonText: 'Cancel'
        };
        dialogRef = this.openDialog(body, '250px');
        dialogRef.afterClosed().subscribe(confirm => {
          if (confirm) {
            console.log(confirm);
            // Api will be called here, status will be checked and then message displayed
            const message = {
              case: 'MESSAGE',
              title: 'Success',
              message: 'Action was completed successfully'
            };
            dialogRef = this.openDialog(message, '250px');
            const index = this.users.indexOf(element);
            this.users.splice(index, 1);
            this.dataSource._updateChangeSubscription();
          } else {
            const message = {
              case: 'MESSAGE',
              title: 'Error',
              message: 'Action could not be completed'
            };
            dialogRef = this.openDialog(message, '250px');
          }
        });
      }
    });
  }

  onModifyData() {
    console.log('On modify data');
  }

  onModifyAppointment() {
    console.log('on modify appointment');
  }

  onHome() {
    this.router.navigate(['']);
  }
}
