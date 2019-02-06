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
import AttributeModel from 'src/app/shared/models/demographic-model';
import { IdentityModel } from 'src/app/shared/models/demographic-model/identity.modal';
import { RequestModel } from 'src/app/shared/models/demographic-model/request.modal';
import { DemoIdentityModel } from 'src/app/shared/models/demographic-model/demo.identity.modal';
import { UserModel } from 'src/app/shared/models/demographic-model/user.modal';
import * as appConstants from '../../../app.constants';

@Component({
  selector: 'app-registration',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashBoardComponent implements OnInit {
  userFile: FileModel;
  userFiles: any[] = [];
  tempFiles;
  disableModifyDataButton = false;
  disableModifyAppointmentButton = true;
  fetchedDetails = true;
  modify = false;
  users: Applicant[] = [];
  selectedUsers: Applicant[] = [];
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
    private translate: TranslateService
  ) {
    this.translate.use(localStorage.getItem('langCode'));
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
    this.dataStorageService.getUsers(this.loginId).subscribe(
      (applicants: Applicant[]) => {
        console.log(applicants);
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
          for (let index = 0; index < applicants[appConstants.RESPONSE].length; index++) {
            const bookingRegistrationDTO =
              applicants[appConstants.RESPONSE][index][appConstants.DASHBOARD_RESPONSE_KEYS.bookingRegistrationDTO.dto];
            let appointmentDateTime = '-';
            if (
              bookingRegistrationDTO !== null &&
              applicants[appConstants.RESPONSE][index][
                appConstants.DASHBOARD_RESPONSE_KEYS.applicant.statusCode
              ].toLowerCase() === appConstants.APPLICATION_STATUS_CODES.booked.toLowerCase()
            ) {
              const date =
                applicants[appConstants.RESPONSE][index][
                  appConstants.DASHBOARD_RESPONSE_KEYS.bookingRegistrationDTO.dto
                ][appConstants.DASHBOARD_RESPONSE_KEYS.bookingRegistrationDTO.regDate];
              const fromTime =
                applicants[appConstants.RESPONSE][index][
                  appConstants.DASHBOARD_RESPONSE_KEYS.bookingRegistrationDTO.dto
                ][appConstants.DASHBOARD_RESPONSE_KEYS.bookingRegistrationDTO.time_slot_from];
              const toTime =
                applicants[appConstants.RESPONSE][index][
                  appConstants.DASHBOARD_RESPONSE_KEYS.bookingRegistrationDTO.dto
                ][appConstants.DASHBOARD_RESPONSE_KEYS.bookingRegistrationDTO.time_slot_to];
              appointmentDateTime = date + ' ( ' + fromTime + ' - ' + toTime + ' )';
            }
            const applicant: Applicant = {
              applicationID:
                applicants[appConstants.RESPONSE][index][appConstants.DASHBOARD_RESPONSE_KEYS.applicant.preId],
              name: applicants[appConstants.RESPONSE][index][appConstants.DASHBOARD_RESPONSE_KEYS.applicant.fullname],
              appointmentDateTime: appointmentDateTime,
              status:
                applicants[appConstants.RESPONSE][index][appConstants.DASHBOARD_RESPONSE_KEYS.applicant.statusCode],
              regDto: bookingRegistrationDTO
            };
            this.users.push(applicant);
          }
        } else {
          localStorage.setItem('newApplicant', 'true');
          this.onNewApplication();
        }
      },
      error => {
        console.log(error);
        // if (error.status < 400) {
        //   console.log('error');
        //   return this.router.navigate(['error']);
        // } else
        // if (
        //   error[appConstants.ERROR][appConstants.NESTED_ERROR] &&
        //   error[appConstants.ERROR][appConstants.NESTED_ERROR][appConstants.ERROR_CODE] ===
        //     appConstants.ERROR_CODES.noApplicantEnrolled
        // ) {
        //   sessionStorage.setItem('newApplicant', 'true');
        //   this.onNewApplication();
        // } else {
        this.router.navigate(['error']);
        // }
        this.isFetched = true;
      },
      () => {
        this.isFetched = true;
      }
    );
  }

  onNewApplication() {
    if (this.loginId) {
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
    this.dataStorageService.getUserDocuments(preId).subscribe(
      response => {
        this.setUserFiles(response);
      },
      error => {
        console.log('response from modify data', error);
      },
      () => {
        this.dataStorageService.getUser(preId).subscribe(
          response => {
            const request = this.createRequestJSON(response[appConstants.RESPONSE][0]);
            this.disableModifyDataButton = true;
            this.regService.addUser(new UserModel(preId, request, this.userFiles));
          },
          error => {
            console.log('error', error);
            this.disableModifyDataButton = false;
            this.fetchedDetails = true;
            return this.router.navigate(['error']);
          },
          () => {
            this.fetchedDetails = true;
            this.router.navigate(['pre-registration', this.loginId, 'demographic']);
          }
        );
      }
    );
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
    console.log(this.sharedService.getNameList());
    const arr = this.router.url.split('/');
    const url = `/pre-registration/${arr.pop()}/booking/pick-center`;
    this.router.navigateByUrl(url);
  }

  onAcknowledgementView(applicationID: any) {
    console.log(applicationID);
  }

  private createIdentityJSON(identityModal: IdentityModel) {
    const identity = new IdentityModel(
      identityModal.IDSchemaVersion,
      [
        new AttributeModel(identityModal.fullName[0].language, identityModal.fullName[0].value),
        new AttributeModel(identityModal.fullName[1].language, identityModal.fullName[1].value)
      ],
      identityModal.dateOfBirth,
      [
        new AttributeModel(identityModal.gender[0].language, identityModal.gender[0].value),
        new AttributeModel(identityModal.gender[1].language, identityModal.gender[1].value)
      ],
      [
        new AttributeModel(identityModal.addressLine1[0].language, identityModal.addressLine1[0].value),
        new AttributeModel(identityModal.addressLine1[1].language, identityModal.addressLine1[1].value)
      ],
      [
        new AttributeModel(identityModal.addressLine2[0].language, identityModal.addressLine2[0].value),
        new AttributeModel(identityModal.addressLine2[1].language, identityModal.addressLine2[1].value)
      ],
      [
        new AttributeModel(identityModal.addressLine3[0].language, identityModal.addressLine3[0].value),
        new AttributeModel(identityModal.addressLine3[1].language, identityModal.addressLine3[1].value)
      ],
      [
        new AttributeModel(identityModal.region[0].language, identityModal.region[0].value),
        new AttributeModel(identityModal.region[1].language, identityModal.region[1].value)
      ],
      [
        new AttributeModel(identityModal.province[0].language, identityModal.province[0].value),
        new AttributeModel(identityModal.province[1].language, identityModal.province[1].value)
      ],
      [
        new AttributeModel(identityModal.city[0].language, identityModal.city[0].value),
        new AttributeModel(identityModal.city[1].language, identityModal.city[1].value)
      ],
      [
        new AttributeModel(
          identityModal.localAdministrativeAuthority[0].language,
          identityModal.localAdministrativeAuthority[0].value
        ),
        new AttributeModel(
          identityModal.localAdministrativeAuthority[1].language,
          identityModal.localAdministrativeAuthority[1].value
        )
      ],
      identityModal.postalCode,
      identityModal.phone,
      identityModal.email,
      identityModal.CNIENumber
    );

    return identity;
  }

  private createRequestJSON(requestModal: RequestModel) {
    const identity = this.createIdentityJSON(requestModal.demographicDetails.identity);
    const req: RequestModel = {
      preRegistrationId: requestModal.preRegistrationId,
      createdBy: requestModal.createdBy,
      createdDateTime: requestModal.createdDateTime,
      updatedBy: this.loginId,
      updatedDateTime: '',
      langCode: requestModal.langCode,
      demographicDetails: new DemoIdentityModel(identity)
    };
    return req;
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
