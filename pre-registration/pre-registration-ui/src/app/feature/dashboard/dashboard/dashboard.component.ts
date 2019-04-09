import { Component, OnInit } from '@angular/core';

import { Router } from '@angular/router';
import { MatDialog, MatCheckboxChange } from '@angular/material';

import { TranslateService } from '@ngx-translate/core';
import { DataStorageService } from 'src/app/core/services/data-storage.service';
import { RegistrationService } from 'src/app/core/services/registration.service';
import { SharedService } from '../../booking/booking.service';
import { AutoLogoutService } from 'src/app/core/services/auto-logout.service';

import { DialougComponent } from 'src/app/shared/dialoug/dialoug.component';

import { FileModel } from 'src/app/shared/models/demographic-model/file.model';
import { Applicant } from 'src/app/shared/models/dashboard-model/dashboard.modal';
import { UserModel } from 'src/app/shared/models/demographic-model/user.modal';
import * as appConstants from '../../../app.constants';
import Utils from 'src/app/app.util';
import { ConfigService } from 'src/app/core/services/config.service';
import { RequestModel } from 'src/app/shared/models/request-model/RequestModel';

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
  message = {};

  secondaryLanguagelabels: any;
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
    public dialog: MatDialog,
    private dataStorageService: DataStorageService,
    private regService: RegistrationService,
    private sharedService: SharedService,
    private autoLogout: AutoLogoutService,
    private translate: TranslateService,
    private configService: ConfigService
  ) {
    this.translate.use(localStorage.getItem('langCode'));
    localStorage.setItem('modifyDocument', 'false');
  }
  ngOnInit() {
    console.log('IN DASHBOARD');

    this.regService.changeMessage({ modifyUser: 'false' });
    this.loginId = this.regService.getLoginId();
    this.initUsers();

    this.autoLogout.currentMessageAutoLogout.subscribe(message => (this.message = message));
    if (!this.message['timerFired']) {
      this.autoLogout.keepWatching();
    }

    this.dataStorageService.getSecondaryLanguageLabels(localStorage.getItem('langCode')).subscribe(response => {
      if (response['dashboard']) this.secondaryLanguagelabels = response['dashboard'].discard;
      console.log(this.secondaryLanguagelabels);
    });
    this.regService.setSameAs('');
  }

  initUsers() {
    this.regService.flushUsers();
    this.sharedService.flushNameList();
    this.getUsers();
  }

  getUsers() {
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

        if (applicants[appConstants.RESPONSE] && applicants[appConstants.RESPONSE] !== null) {
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

  createApplicant(applicants: any, index: number) {
    const applicantResponse = applicants[appConstants.RESPONSE][index];
    let primaryIndex = 0;
    let secondaryIndex = 1;
    let lang = applicantResponse[appConstants.DASHBOARD_RESPONSE_KEYS.applicant.fullname][0]['language'];
    if (lang !== localStorage.getItem('langCode')) {
      primaryIndex = 1;
      secondaryIndex = 0;
    }
    const applicant: Applicant = {
      applicationID: applicantResponse[appConstants.DASHBOARD_RESPONSE_KEYS.applicant.preId],
      name: applicantResponse[appConstants.DASHBOARD_RESPONSE_KEYS.applicant.fullname][primaryIndex]['value'],
      appointmentDateTime: applicantResponse[appConstants.DASHBOARD_RESPONSE_KEYS.bookingRegistrationDTO.dto]
        ? this.createAppointmentDateTime(applicantResponse)
        : '-',
      status: applicantResponse[appConstants.DASHBOARD_RESPONSE_KEYS.applicant.statusCode],
      regDto: applicantResponse[appConstants.DASHBOARD_RESPONSE_KEYS.bookingRegistrationDTO.dto],
      nameInSecondaryLanguage:
        applicantResponse[appConstants.DASHBOARD_RESPONSE_KEYS.applicant.fullname][secondaryIndex]['value'],
      postalCode: applicantResponse[appConstants.DASHBOARD_RESPONSE_KEYS.applicant.postalCode]
    };

    return applicant;
  }

  onNewApplication() {
    if (this.loginId) {
      this.router.navigate(['pre-registration', 'demographic']);
      console.log('OUT DASHBOARD IN DEMOGRAPHIC');
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

  radioButtonsStatus(status: string) {
    let data = {};
    if (status.toLowerCase() === 'booked') {
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
    return data;
  }

  confirmationDialog() {
    const body = {
      case: 'CONFIRMATION',
      title: this.secondaryLanguagelabels.title_confirm,
      message: this.secondaryLanguagelabels.msg_confirm,
      yesButtonText: this.secondaryLanguagelabels.button_confirm,
      noButtonText: this.secondaryLanguagelabels.button_cancel
    };
    const dialogRef = this.openDialog(body, '250px');
    return dialogRef;
  }

  deletePreregistration(element: any) {
    this.dataStorageService.deleteRegistration(element.applicationID).subscribe(
      response => {
        if (!response['err']) {
          this.displayMessage(this.secondaryLanguagelabels.title_success, this.secondaryLanguagelabels.msg_deleted);
          const index = this.users.indexOf(element);
          this.users.splice(index, 1);
        } else {
          this.displayMessage(
            this.secondaryLanguagelabels.title_error,
            this.secondaryLanguagelabels.msg_could_not_deleted
          );
        }
      },
      error => {
        console.log(error);
        this.displayMessage(
          this.secondaryLanguagelabels.title_error,
          this.secondaryLanguagelabels.msg_could_not_deleted
        );
      }
    );
  }

  cancelAppointment(element: any) {
    element.regDto.pre_registration_id = element.applicationID;
    this.dataStorageService
      .cancelAppointment(new RequestModel(appConstants.IDS.cancelAppointment, element.regDto), element.applicationID)
      .subscribe(
        response => {
          if (!response['errors']) {
            this.displayMessage(this.secondaryLanguagelabels.title_success, this.secondaryLanguagelabels.msg_deleted);
            const index = this.users.indexOf(element);
            this.users[index].status = 'Pending Appointment';
            this.users[index].appointmentDateTime = '-';
          } else {
            this.displayMessage(
              this.secondaryLanguagelabels.title_error,
              this.secondaryLanguagelabels.msg_could_not_deleted
            );
          }
        },
        error => {
          console.log(error);
          this.displayMessage(
            this.secondaryLanguagelabels.title_error,
            this.secondaryLanguagelabels.msg_could_not_deleted
          );
        }
      );
  }

  onDelete(element) {
    let data = this.radioButtonsStatus(element.status);
    let dialogRef = this.openDialog(data, `400px`);
    dialogRef.afterClosed().subscribe(selectedOption => {
      if (selectedOption && Number(selectedOption) === 1) {
        dialogRef = this.confirmationDialog();
        dialogRef.afterClosed().subscribe(confirm => {
          if (confirm) {
            this.deletePreregistration(element);
          } else {
            this.displayMessage(
              this.secondaryLanguagelabels.title_error,
              this.secondaryLanguagelabels.msg_could_not_deleted
            );
          }
        });
      } else if (selectedOption && Number(selectedOption) === 2) {
        dialogRef = this.confirmationDialog();
        dialogRef.afterClosed().subscribe(confirm => {
          if (confirm) {
            this.cancelAppointment(element);
          } else {
            this.displayMessage(
              this.secondaryLanguagelabels.title_error,
              this.secondaryLanguagelabels.msg_could_not_deleted
            );
          }
        });
      }
    });
  }

  displayMessage(title: string, message: string) {
    const messageObj = {
      case: 'MESSAGE',
      title: title,
      message: message
    };
    this.openDialog(messageObj, '250px');
  }

  onModifyInformation(user: Applicant) {
    const preId = user.applicationID;
    this.regService.changeMessage({ modifyUser: 'true' });
    this.disableModifyDataButton = true;
    this.dataStorageService
      .getUserDocuments(preId)
      .subscribe(response => this.setUserFiles(response), error => console.log('response from modify data', error));
    this.addtoNameList(user);
    console.log(this.sharedService.getNameList());

    console.log('preid', preId);

    this.dataStorageService.getUser(preId).subscribe(
      response => {
        console.log('RESPONSE [Modify Information]', response);

        this.onModification(response, preId);
      },
      error => {
        console.log('error', error);
        return this.router.navigate(['error']);
      }
    );
  }

  private onModification(response: any, preId: string) {
    const request = response[appConstants.RESPONSE][0];
    this.disableModifyDataButton = true;
    this.regService.addUser(new UserModel(preId, request, this.userFiles));
    this.fetchedDetails = true;
    this.router.navigate(['pre-registration', 'demographic']);
  }

  onSelectUser(user: Applicant, event: MatCheckboxChange) {
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
      this.addtoNameList(this.selectedUsers[index]);
      console.log('index', index);
    }
    let url = '';
    url = Utils.getURL(this.router.url, 'pre-registration/booking/pick-center');
    this.router.navigateByUrl(url);
  }

  onAcknowledgementView(user: Applicant) {
    this.addtoNameList(user);
    let url = '';
    url = Utils.getURL(this.router.url, 'pre-registration/summary/acknowledgement');
    this.router.navigateByUrl(url);
  }

  addtoNameList(user: Applicant) {
    const preId = user.applicationID;
    const fullName = user.name;
    const regDto = user.regDto;
    const status = user.status;
    const postalCode = user.postalCode;
    const nameInSecondaryLanguage = user.nameInSecondaryLanguage;
    this.sharedService.addNameList({
      fullName: fullName,
      preRegId: preId,
      regDto: regDto,
      status: status,
      postalCode: postalCode,
      fullNameSecondaryLang: nameInSecondaryLanguage
    });
  }

  setUserFiles(response) {
    console.log('user files', response);

    this.userFile = response[appConstants.RESPONSE];
    this.userFiles.push(this.userFile);
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

  isBookingAllowed(appointmentDateTime: string) {
    const dateform = new Date(appointmentDateTime);
    if (dateform.toDateString() !== 'Invalid Date') {
      let date1: string = appointmentDateTime;
      let date2: string = new Date(Date.now()).toString();
      let diffInMs: number = Date.parse(date1) - Date.parse(date2);
      let diffInHours: number = diffInMs / 1000 / 60 / 60;
      if (diffInHours < this.configService.getConfigByKey(appConstants.CONFIG_KEYS.preregistration_timespan_rebook))
        return true;
      else return false;
    }
    return false;
  }
}
