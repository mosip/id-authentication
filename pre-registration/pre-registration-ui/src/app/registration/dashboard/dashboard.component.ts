import { Component, OnInit } from '@angular/core';

import { MatTableDataSource } from '@angular/material/table';
import { SelectionModel } from '@angular/cdk/collections';
import { Router, ActivatedRoute, Params } from '@angular/router';
import { DialougComponent } from '../../shared/dialoug/dialoug.component';
import { MatDialog } from '@angular/material';

import { DataStorageService } from 'src/app/shared/data-storage.service';
import { RegistrationService } from '../registration.service';
import { SharedService } from 'src/app/shared/shared.service';
import { Applicant } from './dashboard.modal';
import { UserModel } from '../demographic/user.model';
import { AttributeModel } from '../demographic/attribute.model';
import { IdentityModel } from '../demographic/identity.model';

@Component({
  selector: 'app-registration',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashBoardComponent implements OnInit {
  disableModifyDataButton = true;
  disableModifyAppointmentButton = true;
  numSelected: number;
  numRows: number;
  fetchedDetails = true;
  modify = false;
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
    private dataStorageService: DataStorageService,
    private regService: RegistrationService,
    private sharedService: SharedService
  ) {}

  ngOnInit() {
    this.route.params.subscribe((params: Params) => {
      this.loginId = params['id'];
    });
    this.initUsers();
  }

  initUsers() {
    this.regService.flushUsers();
    this.dataStorageService.getUsers(this.loginId).subscribe(
      (applicants: Applicant[]) => {
        console.log(applicants);
        if (applicants['response'] !== null) {
          for (let index = 0; index < applicants['response'].length; index++) {
            const applicant: Applicant = {
              applicationID: applicants['response'][index]['preId'],
              name: applicants['response'][index]['fullname'],
              appointmentDateTime: applicants['response'][index]['appointmentDate'],
              status: applicants['response'][index]['statusCode']
            };
            this.users.push(applicant);
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
    this.numSelected = this.selection.selected.length;
    this.numRows = this.dataSource.data.length;
    return this.numSelected === this.numRows;
  }

  /** Selects all rows if they are not all selected; otherwise clear selection. */
  masterToggle() {
    this.isAllSelected() ? this.selection.clear() : this.dataSource.data.forEach(row => this.selection.select(row));
    if (this.isAllSelected()) {
      this.disableModifyDataButton = true;
    }
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
            this.dataStorageService.deleteRegistration(element.applicationID).subscribe(
              response => {
                console.log(response);
                const message = {
                  case: 'MESSAGE',
                  title: 'Success',
                  message: 'Action was completed successfully'
                };
                dialogRef = this.openDialog(message, '250px');
                const index = this.users.indexOf(element);
                this.users.splice(index, 1);
                this.dataSource._updateChangeSubscription();
              },
              error => {
                console.log(error);
                const message = {
                  case: 'MESSAGE',
                  title: 'Error',
                  message: 'Action could not be completed'
                };
                dialogRef = this.openDialog(message, '250px');
              }
            );
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

  onModifyData(flag: boolean) {
    if (flag && this.selection.selected.length === 1) {
      this.fetchedDetails = false;
      this.disableModifyDataButton = true;
      const preId = this.selection.selected[0].applicationID;
      this.dataStorageService.getUser(preId).subscribe(
        response => {
          this.disableModifyDataButton = true;
          const identity = this.createIdentityJSON(response['response'][0].demographicDetails.identity);
          this.regService.addUser(new UserModel(preId, identity, []));
        },
        error => {
          this.disableModifyDataButton = false;
          this.fetchedDetails = true;
          console.log(error);
        },
        () => {
          this.fetchedDetails = true;
          this.router.navigate(['demographic', '1'], { relativeTo: this.route });
        }
      );
    } else {
      this.numSelected = this.selection.selected.length;
      if (this.numSelected > 1 || this.numSelected === 0) {
        this.disableModifyDataButton = true;
      } else {
        this.disableModifyDataButton = false;
      }
    }
    this.modify = false;
  }

  onModifyAppointment(flag: boolean) {
    if (flag) {
      for (let index = 0; index < this.numSelected; index++) {
        const preId = this.selection.selected[index].applicationID;
        const fullName = this.selection.selected[index].name;
        this.sharedService.addNameList({ fullName: fullName, preRegId: preId });
      }
      this.router.navigate(['pick-center'], { relativeTo: this.route });
    }
    this.numSelected = this.selection.selected.length;
    if (this.numSelected === 0) {
      this.disableModifyAppointmentButton = true;
    } else {
      this.disableModifyAppointmentButton = false;
    }
  }

  private createIdentityJSON(obj) {
    const identity = new IdentityModel(
      [new AttributeModel(obj.FullName[1].language, obj.FullName[1].label, obj.FullName[1].value)],
      [new AttributeModel(obj.dateOfBirth[1].language, obj.dateOfBirth[1].label, obj.dateOfBirth[1].value)],
      [new AttributeModel(obj.gender[1].language, obj.gender[1].label, obj.gender[1].value)],
      [new AttributeModel(obj.addressLine1[1].language, obj.addressLine1[1].label, obj.addressLine1[1].value)],
      [new AttributeModel(obj.addressLine2[1].language, obj.addressLine2[1].label, obj.addressLine2[1].value)],
      [new AttributeModel(obj.addressLine3[1].language, obj.addressLine3[1].label, obj.addressLine3[1].value)],
      [new AttributeModel(obj.region[1].language, obj.region[1].label, obj.region[1].value)],
      [new AttributeModel(obj.province[1].language, obj.province[1].label, obj.province[1].value)],
      [new AttributeModel(obj.city[1].language, obj.city[1].label, obj.city[1].value)],
      [new AttributeModel(obj.postalcode[1].language, obj.postalcode[1].label, obj.postalcode[1].value)],
      [
        new AttributeModel(
          obj.localAdministrativeAuthority[1].language,
          obj.localAdministrativeAuthority[1].label,
          obj.localAdministrativeAuthority[1].value
        )
      ],
      [new AttributeModel(obj.emailId[1].language, obj.emailId[1].label, obj.emailId[1].value)],
      [new AttributeModel(obj.mobileNumber[1].language, obj.mobileNumber[1].label, obj.mobileNumber[1].value)],
      [new AttributeModel(obj.CNEOrPINNumber[1].language, obj.CNEOrPINNumber[1].label, obj.CNEOrPINNumber[1].value)],
      [new AttributeModel(obj.age[1].language, obj.age[1].label, obj.age[1].value)]
    );

    return identity;
  }
}
