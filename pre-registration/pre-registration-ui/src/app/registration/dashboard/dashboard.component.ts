import { Component, OnInit, ChangeDetectorRef } from '@angular/core';

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
import { FileModel } from '../demographic/file.model';
import { BookingModelRequest } from 'src/app/shared/booking-request.model';

@Component({
  selector: 'app-registration',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashBoardComponent implements OnInit {
  userFile: FileModel;
  userFiles: any[] = [];
  tempFiles;
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
    private sharedService: SharedService,
    private changeDetectorRefs: ChangeDetectorRef
  ) {}

  ngOnInit() {
    sessionStorage.clear();
    this.route.params.subscribe((params: Params) => {
      this.loginId = params['id'];
    });
    this.initUsers();
  }

  initUsers() {
    this.regService.flushUsers();
    this.dataStorageService.getUsers(this.loginId).subscribe(
      (applicants: Applicant[]) => {
        console.log('applicant', applicants);
        if (applicants['response'] !== null) {
          sessionStorage.setItem('newApplicant', 'false');
          for (let index = 0; index < applicants['response'].length; index++) {
            const bookingRegistrationDTO = applicants['response'][index]['bookingRegistrationDTO'];
            let appointmentDateTime = '-';
            if (
              bookingRegistrationDTO !== null &&
              applicants['response'][index]['statusCode'].toLowerCase() === 'booked'
            ) {
              const date = applicants['response'][index].bookingRegistrationDTO.reg_date;
              const fromTime = applicants['response'][index].bookingRegistrationDTO.time_slot_from;
              const toTime = applicants['response'][index].bookingRegistrationDTO.time_slot_to;
              appointmentDateTime = date + ' ( ' + fromTime + ' - ' + toTime + ' )';
            }
            const applicant: Applicant = {
              applicationID: applicants['response'][index]['preId'],
              name: applicants['response'][index]['fullname'],
              appointmentDateTime: appointmentDateTime,
              status: applicants['response'][index]['statusCode'],
              regDto: bookingRegistrationDTO
            };
            this.users.push(applicant);
          }
        }
      },
      error => {
        console.log('error', error.error.err.errorCode);
        if ((error.error.err.errorCode = 'PRG_PAM_APP_005')) {
          sessionStorage.setItem('newApplicant', 'true');
          this.onNewApplication();
        } else {
          this.router.navigate(['error']);
        }
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
    this.router.navigate(['pre-registration', this.loginId, 'demographic']);
    this.isNewApplication = true;
  }

  openDialog(data, width) {
    const dialogRef = this.dialog.open(DialougComponent, {
      width: width,
      data: data
    });
    return dialogRef;
  }

  onDelete(element) {
    let data = {};
    if (element.status.toLowerCase() === 'booked') {
      data = {
        case: 'DISCARD',
        disabled: {
          radioButton1: false,
          radioButton2: false
        }
      };
    } else {
      data = {
        case: 'DISCARD',
        disabled: {
          radioButton1: false,
          radioButton2: true
        }
      };
    }
    let dialogRef = this.openDialog(data, `350px`);
    dialogRef.afterClosed().subscribe(selectedOption => {
      if (selectedOption && Number(selectedOption) === 1) {
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
            this.dataStorageService.deleteRegistration(element.applicationID).subscribe(
              response => {
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
      } else if (selectedOption && Number(selectedOption) === 2) {
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
            element.regDto.pre_registration_id = element.applicationID;
            this.dataStorageService.cancelAppointment(new BookingModelRequest(element.regDto)).subscribe(
              response => {
                const message = {
                  case: 'MESSAGE',
                  title: 'Success',
                  message: 'Action was completed successfully'
                };
                dialogRef = this.openDialog(message, '250px');
                const index = this.users.indexOf(element);
                this.dataSource.data[index].status = 'Pending_Appointment';
                this.dataSource.data[index].appointmentDateTime = '-';
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
      this.dataStorageService.getUserDocuments(preId).subscribe(response => {
        this.setUserFiles(response);
      });
      console.log('user files 2', this.userFiles);

      this.dataStorageService.getUser(preId).subscribe(
        response => {
          this.disableModifyDataButton = true;
          const identity = this.createIdentityJSON(response['response'][0].demographicDetails.identity);
          console.log('user model before', new UserModel(preId, identity, this.userFiles));
          this.regService.addUser(new UserModel(preId, identity, this.userFiles));
        },
        error => {
          this.disableModifyDataButton = false;
          this.fetchedDetails = true;
          console.log('error', error);
        },
        () => {
          this.fetchedDetails = true;
          // this.router.navigate(['demographic', '1'], { relativeTo: this.route });
          this.router.navigate(['pre-registration', this.loginId, 'demographic']);
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
        const regDto = this.selection.selected[index].regDto;
        const status = this.selection.selected[index].status;
        this.sharedService.addNameList({
          fullName: fullName,
          preRegId: preId,
          regDto: regDto,
          status: status
        });
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
    console.log('obj', obj);

    const identity = new IdentityModel(
      [new AttributeModel(obj.FullName[0].language, obj.FullName[0].label, obj.FullName[0].value)],
      [new AttributeModel(obj.dateOfBirth[0].language, obj.dateOfBirth[0].label, obj.dateOfBirth[0].value)],
      [new AttributeModel(obj.gender[0].language, obj.gender[0].label, obj.gender[0].value)],
      [new AttributeModel(obj.addressLine1[0].language, obj.addressLine1[0].label, obj.addressLine1[0].value)],
      [new AttributeModel(obj.addressLine2[0].language, obj.addressLine2[0].label, obj.addressLine2[0].value)],
      [new AttributeModel(obj.addressLine3[0].language, obj.addressLine3[0].label, obj.addressLine3[0].value)],
      [new AttributeModel(obj.region[0].language, obj.region[0].label, obj.region[0].value)],
      [new AttributeModel(obj.province[0].language, obj.province[0].label, obj.province[0].value)],
      [new AttributeModel(obj.city[0].language, obj.city[0].label, obj.city[0].value)],
      [
        new AttributeModel(
          obj.localAdministrativeAuthority[0].language,
          obj.localAdministrativeAuthority[0].label,
          obj.localAdministrativeAuthority[0].value
        )
      ],
      [new AttributeModel(obj.postalcode[0].language, obj.postalcode[0].label, obj.postalcode[0].value)],
      [new AttributeModel(obj.mobileNumber[0].language, obj.mobileNumber[0].label, obj.mobileNumber[0].value)],
      [new AttributeModel(obj.emailId[0].language, obj.emailId[0].label, obj.emailId[0].value)],
      [new AttributeModel(obj.CNEOrPINNumber[0].language, obj.CNEOrPINNumber[0].label, obj.CNEOrPINNumber[0].value)]
    );

    return identity;
  }

  setUserFiles(response) {
    console.log('user files fetched', response);

    this.userFile = response.response;
    this.userFiles.push(this.userFile);
  }
}
