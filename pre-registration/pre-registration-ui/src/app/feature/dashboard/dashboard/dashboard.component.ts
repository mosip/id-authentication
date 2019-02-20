import { Component, OnInit } from '@angular/core';

import { Router, ActivatedRoute, Params } from '@angular/router';
import { MatDialog, MatCheckboxChange } from '@angular/material';

import { TranslateService } from '@ngx-translate/core';
import { DataStorageService } from 'src/app/core/services/data-storage.service';
import { RegistrationService } from 'src/app/core/services/registration.service';
import { SharedService } from '../../booking/booking.service';
import { DialougComponent } from 'src/app/shared/dialoug/dialoug.component';
import { BookingModelRequest } from 'src/app/shared/booking-request.model';

import { FileModel } from 'src/app/shared/models/demographic-model/file.model';
import { Applicant } from 'src/app/shared/models/dashboard-model/dashboard.modal';
import { UserModel } from 'src/app/shared/models/demographic-model/user.modal';
import * as appConstants from '../../../app.constants';
import Utils from 'src/app/app.util';

@Component({
  selector: 'app-registration',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashBoardComponent implements OnInit {
  userFile: FileModel;
  userFiles: any[] = [];
  tempFiles;
  loginId = '';

  disableModifyDataButton = false;
  disableModifyAppointmentButton = true;
  fetchedDetails = true;
  modify = false;
  isNewApplication = false;
  isFetched = false;

  users: Applicant[] = [];
  selectedUsers: Applicant[] = [];

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    public dialog: MatDialog,
    private dataStorageService: DataStorageService,
    private regService: RegistrationService,
    private sharedService: SharedService,
    private translate: TranslateService
  ) {
    this.translate.use(localStorage.getItem('langCode'));
    localStorage.setItem('modifyDocument', 'false');
  }
  ngOnInit() {
    this.regService.changeMessage({ modifyUser: 'false' });
    this.route.params.subscribe((params: Params) => {
      this.loginId = params['id'];
    });
    this.initUsers();
  }

  initUsers() {
    this.regService.flushUsers();
    this.sharedService.flushNameList();
    this.getUsers();
  }

  private getUsers() {
    this.dataStorageService.getUsers(this.loginId).subscribe(
      (applicants: Applicant[]) => {
        console.log('applicants', applicants);

        if (
          applicants[appConstants.NESTED_ERROR] &&
          applicants[appConstants.NESTED_ERROR][appConstants.ERROR_CODE] ===
            appConstants.ERROR_CODES.noApplicantEnrolled
        ) {
          localStorage.setItem('newApplicant', 'true');
          this.onNewApplication();
        }

        if (applicants[appConstants.RESPONSE] !== null) {
          localStorage.setItem('newApplicant', 'false');
          this.sharedService.addApplicants(applicants);
          for (let index = 0; index < applicants[appConstants.RESPONSE].length; index++) {
            const applicant = this.createApplicant(applicants, index);
            this.users.push(applicant);
          }
        } else {
          localStorage.setItem('newApplicant', 'true');
          this.onNewApplication();
        }
      },
      error => {
        console.log(error);
        this.router.navigate(['error']);
        this.isFetched = true;
      },
      () => {
        this.isFetched = true;
      }
    );
  }

  private createAppointmentDateTime(applicant: any) {
    const bookingRegistrationDTO = applicant[appConstants.DASHBOARD_RESPONSE_KEYS.bookingRegistrationDTO.dto];
    const date = bookingRegistrationDTO[appConstants.DASHBOARD_RESPONSE_KEYS.bookingRegistrationDTO.regDate];
    const fromTime = bookingRegistrationDTO[appConstants.DASHBOARD_RESPONSE_KEYS.bookingRegistrationDTO.time_slot_from];
    const toTime = bookingRegistrationDTO[appConstants.DASHBOARD_RESPONSE_KEYS.bookingRegistrationDTO.time_slot_to];
    let appointmentDateTime = date + ' ( ' + fromTime + ' - ' + toTime + ' )';
    return appointmentDateTime;
  }

  private createApplicant(applicants: Applicant[], index: number) {
    const applicantResponse = applicants[appConstants.RESPONSE][index];
    const applicant: Applicant = {
      applicationID: applicantResponse[appConstants.DASHBOARD_RESPONSE_KEYS.applicant.preId],
      name: applicantResponse[appConstants.DASHBOARD_RESPONSE_KEYS.applicant.fullname],
      appointmentDateTime: applicantResponse[appConstants.DASHBOARD_RESPONSE_KEYS.bookingRegistrationDTO.dto]
        ? this.createAppointmentDateTime(applicantResponse)
        : '-',
      status: applicantResponse[appConstants.DASHBOARD_RESPONSE_KEYS.applicant.statusCode],
      regDto: applicantResponse[appConstants.DASHBOARD_RESPONSE_KEYS.bookingRegistrationDTO.dto]
    };
    return applicant;
  }

  onNewApplication() {
    if (this.loginId) {
      console.log('inside');
      // const url = Utils.getURL(this.router.url, 'pre-registration/' + this.loginId + '/demographic', 2);
      // this.router.navigateByUrl(url);
      // console.log(url);

      this.router.navigate(['pre-registration', this.loginId, 'demographic']);
      this.isNewApplication = true;
    } else {
      this.router.navigate(['/']);
    }
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
    let dialogRef = this.openDialog(data, `400px`);
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
                // this.dataSource._updateChangeSubscription();
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
                this.users[index].status = 'Pending Appointment';
                this.users[index].appointmentDateTime = '-';
                // this.dataSource.data[index].status = 'Pending_Appointment';
                // this.dataSource.data[index].appointmentDateTime = '-';
                // this.dataSource._updateChangeSubscription();
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

  onModifyInformation(preId: string) {
    this.regService.changeMessage({ modifyUser: 'true' });
    this.disableModifyDataButton = true;
    this.dataStorageService
      .getUserDocuments(preId)
      .subscribe(response => this.setUserFiles(response), error => console.log('response from modify data', error));

    this.dataStorageService.getUser(preId).subscribe(
      response => this.onModification(response, preId),
      error => {
        console.log('error', error);
        // this.disableModifyDataButton = false;
        // this.fetchedDetails = true;
        return this.router.navigate(['error']);
      }
    );
  }

  private onModification(response: any, preId: string) {
    const request = response[appConstants.RESPONSE][0];
    this.disableModifyDataButton = true;
    this.regService.addUser(new UserModel(preId, request, this.userFiles));
    this.fetchedDetails = true;
    this.router.navigate(['pre-registration', this.loginId, 'demographic']);
  }

  onSelectUser(user: Applicant, event: MatCheckboxChange) {
    // if (!event && user) {
    //   this.selectedUsers.length = 0;
    //   this.selectedUsers.push(user);
    // } else
    if (event && event.checked) {
      this.selectedUsers.push(user);
    } else {
      this.selectedUsers.splice(this.selectedUsers.indexOf(user));
    }
    if (this.selectedUsers.length > 0) {
      this.disableModifyAppointmentButton = false;
    } else {
      this.disableModifyAppointmentButton = true;
    }
  }

  onModifyMultipleAppointment() {
    for (let index = 0; index < this.selectedUsers.length; index++) {
      const preId = this.selectedUsers[index].applicationID;
      const fullName = this.selectedUsers[index].name;
      const regDto = this.selectedUsers[index].regDto;
      const status = this.selectedUsers[index].status;
      this.sharedService.addNameList({
        fullName: fullName,
        preRegId: preId,
        regDto: regDto,
        status: status
      });
    }
    const arr = this.router.url.split('/');
    const url = `/pre-registration/${arr.pop()}/booking/pick-center`;
    this.router.navigateByUrl(url);
  }

  onAcknowledgementView(applicationID: any) {
    console.log(applicationID);
  }

  setUserFiles(response) {
    // console.log('user files fetched', response);
    this.userFile = response[appConstants.RESPONSE];
    this.userFiles.push(this.userFile);
    // console.log('user files after pushing', this.userFiles);
  }

  getColor(value: string) {
    if (value === appConstants.APPLICATION_STATUS_CODES.pending) return 'orange';
    if (value === appConstants.APPLICATION_STATUS_CODES.booked) return 'green';
    if (value === appConstants.APPLICATION_STATUS_CODES.expired) return 'red';
  }

  getMargin(name: string) {
    if (name.length > 25) return '0px';
    else return '27px';
  }
}
