import { Component, OnInit } from '@angular/core';
import * as html2pdf from 'html2pdf.js';
import { MatDialog } from '@angular/material';
import { BookingService } from '../../booking/booking.service';
import { DialougComponent } from 'src/app/shared/dialoug/dialoug.component';
import { TranslateService } from '@ngx-translate/core';
import { DataStorageService } from 'src/app/core/services/data-storage.service';
import { NotificationDtoModel } from 'src/app/shared/models/notification-model/notification-dto.model';
import Utils from 'src/app/app.util';
import * as appConstants from '../../../app.constants';
import { RequestModel } from 'src/app/shared/models/request-model/RequestModel';

@Component({
  selector: 'app-acknowledgement',
  templateUrl: './acknowledgement.component.html',
  styleUrls: ['./acknowledgement.component.css']
})
export class AcknowledgementComponent implements OnInit {
  // usersInfo = [{
  //   fullName: 'Agnitra Banerjee',
  //   preRegId: '1234',
  //   registrationCenter: {
  //     id: '10001',
  //     addressLine1: 'Mindtree Limited',
  //     addressLine2: 'Global Village',
  //     contactPhone: '1234567890'
  //   },
  //   bookingData: '7 Jan 2019, 2:30pm',
  //   qrCodeBlob: Blob,
  //   regDto: {
  //     registration_center_id: '10001',
  //     appointment_date: '2019-03-18',
  //     time_slot_from: '09:00'
  //   }
  // }];
  secondaryLanguagelabels: any;
  secondaryLang = localStorage.getItem('secondaryLangCode');
  usersInfo = [];
  secondaryLanguageRegistrationCenter: any;

  guidelines = [];

  opt = {};

  fileBlob: Blob;
  showSpinner : boolean = true;


  notificationRequest = new FormData();
  bookingDataPrimary = '';
  bookingDataSecondary = '';

  constructor(
    private bookingService: BookingService,
    private dialog: MatDialog,
    private translate: TranslateService,
    private dataStorageService: DataStorageService
  ) {
    this.translate.use(localStorage.getItem('langCode'));
  }

  async ngOnInit() {
    this.usersInfo = this.bookingService.getNameList();
    setTimeout(() => {
    this.showSpinner = false;
        }, 1250);

    this.opt = {
      filename: this.usersInfo[0].preRegId + '.pdf',
      image: { type: 'jpeg', quality: 0.98 },
      html2canvas: { scale: 1 },
      jsPDF: { unit: 'in', format: 'a4', orientation: 'landscape' }
    };

    await this.apiCalls();

    if (this.bookingService.getSendNotification()) {
      this.bookingService.resetSendNotification();
      this.automaticNotification();
    }
  }

  async apiCalls() {
    return new Promise(async (resolve, reject) => {
      if (!this.usersInfo[0].registrationCenter) {
        await this.getRegistrationCenterInPrimaryLanguage(
          this.usersInfo[0].regDto.registration_center_id,
          localStorage.getItem('langCode')
        );
        await this.getRegistrationCenterInSecondaryLanguage(
          this.usersInfo[0].regDto.registration_center_id,
          this.secondaryLang
        );
      } else {
        await this.getRegistrationCenterInSecondaryLanguage(
          this.usersInfo[0].registrationCenter.id,
          this.secondaryLang
        );
      }
      this.formatDateTime();

      await this.qrCodeForUser();

      this.dataStorageService.getSecondaryLanguageLabels(this.secondaryLang).subscribe(response => {
        this.secondaryLanguagelabels = response['acknowledgement'];
      });
      await this.getTemplate();
      resolve(true);
    });
  }

  async qrCodeForUser() {
    return new Promise((resolve, reject) => {
      this.usersInfo.forEach(async user => {
        await this.generateQRCode(user);
        if (this.usersInfo.indexOf(user) === this.usersInfo.length - 1) {
          resolve(true);
        }
      });
    });
  }

  formatDateTime() {
    for (let i = 0; i < this.usersInfo.length; i++) {
      if (!this.usersInfo[i].bookingData) {
        this.usersInfo[i].bookingDataPrimary = Utils.getBookingDateTime(
          this.usersInfo[i].regDto.appointment_date,
          this.usersInfo[i].regDto.time_slot_from,
          localStorage.getItem('langCode')
        );
        this.usersInfo[i].bookingTimePrimary = Utils.formatTime(this.usersInfo[i].regDto.time_slot_from);
        this.usersInfo[i].bookingDataSecondary = Utils.getBookingDateTime(
          this.usersInfo[i].regDto.appointment_date,
          this.usersInfo[i].regDto.time_slot_from,
          localStorage.getItem('secondaryLangCode')
        );
        this.usersInfo[i].bookingTimeSecondary = Utils.formatTime(this.usersInfo[i].regDto.time_slot_from);
      } else {
        const date = this.usersInfo[i].bookingData.split(',');
        this.usersInfo[i].bookingDataPrimary = Utils.getBookingDateTime(
          date[0],
          date[1],
          localStorage.getItem('langCode')
        );
        this.usersInfo[i].bookingTimePrimary = Utils.formatTime(date[1]);
        this.usersInfo[i].bookingDataSecondary = Utils.getBookingDateTime(
          date[0],
          date[1],
          localStorage.getItem('secondaryLangCode')
        );
        this.usersInfo[i].bookingTimeSecondary = Utils.formatTime(date[1]);
      }
    }
  }

  automaticNotification() {
    setTimeout(() => {
      this.sendNotification([], false);
    }, 500);
  }

  async getRegistrationCenterInSecondaryLanguage(centerId: string, langCode: string) {
    return new Promise((resolve, reject) => {
      this.dataStorageService.getRegistrationCenterByIdAndLangCode(centerId, langCode).subscribe(response => {
        this.secondaryLanguageRegistrationCenter = response['response']['registrationCenters'][0];
        resolve(true);
      });
    });
  }

  async getRegistrationCenterInPrimaryLanguage(centerId: string, langCode: string) {
    return new Promise((resolve, reject) => {
      this.dataStorageService.getRegistrationCenterByIdAndLangCode(centerId, langCode).subscribe(response => {
        this.usersInfo[0].registrationCenter = response['response']['registrationCenters'][0];
        resolve(true);
      });
    });
  }

  getTemplate() {
    return new Promise((resolve, reject) => {
      this.dataStorageService.getGuidelineTemplate('Onscreen-Acknowledgement').subscribe(response => {
        this.guidelines = response['response']['templates'][0].fileText.split('\n');
        resolve(true);
      });
    });
  }

  download() {
    const element = document.getElementById('print-section');
    html2pdf(element, this.opt);
  }

  async generateBlob() {
    const element = document.getElementById('print-section');
    return await html2pdf()
      .set(this.opt)
      .from(element)
      .outputPdf('dataurlstring');
  }

  async createBlob() {
    const dataUrl = await this.generateBlob();
    // convert base64 to raw binary data held in a string
    const byteString = atob(dataUrl.split(',')[1]);

    // separate out the mime component
    const mimeString = dataUrl
      .split(',')[0]
      .split(':')[1]
      .split(';')[0];

    // write the bytes of the string to an ArrayBuffer
    const arrayBuffer = new ArrayBuffer(byteString.length);

    var _ia = new Uint8Array(arrayBuffer);
    for (let i = 0; i < byteString.length; i++) {
      _ia[i] = byteString.charCodeAt(i);
    }

    const dataView = new DataView(arrayBuffer);
    return await new Blob([dataView], { type: mimeString });
  }

  sendAcknowledgement() {
    const data = {
      case: 'APPLICANTS'
    };
    const dialogRef = this.dialog
      .open(DialougComponent, {
        width: '350px',
        data: data
      })
      .afterClosed()
      .subscribe(applicantNumber => {
        if (applicantNumber !== undefined) {
          this.sendNotification(applicantNumber, true);
        }
      });
  }

  async generateQRCode(name) {
    return new Promise((resolve, reject) => {
      this.dataStorageService.generateQRCode(JSON.stringify(name)).subscribe(response => {
        const index = this.usersInfo.indexOf(name);
        this.usersInfo[index].qrCodeBlob = response['response'].qrcode;
        resolve(true);
      });
    });
  }

  async sendNotification(applicantNumber, additionalRecipient: boolean) {
    this.fileBlob = await this.createBlob();
    this.usersInfo.forEach(user => {
      const notificationDto = new NotificationDtoModel(
        user.fullName,
        user.preRegId,
        user.bookingDataPrimary,
        user.bookingTimePrimary,
        applicantNumber[1] === undefined ? null : applicantNumber[1],
        applicantNumber[0] === undefined ? null : applicantNumber[0],
        additionalRecipient,
        false
      );
      const model = new RequestModel(appConstants.IDS.notification, notificationDto);
      this.notificationRequest.append(appConstants.notificationDtoKeys.notificationDto, JSON.stringify(model).trim());
      this.notificationRequest.append(appConstants.notificationDtoKeys.langCode, localStorage.getItem('langCode'));
      this.notificationRequest.append(appConstants.notificationDtoKeys.file, this.fileBlob, `${user.preRegId}.pdf`);
      this.dataStorageService.sendNotification(this.notificationRequest).subscribe(response => {});
      this.notificationRequest = new FormData();
    });
  }
}
