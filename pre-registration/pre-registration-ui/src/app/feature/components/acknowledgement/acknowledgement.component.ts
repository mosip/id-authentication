import { Component, OnInit } from '@angular/core';
import * as html2pdf from 'html2pdf.js';
import { MatDialog } from '@angular/material';
import { SharedService } from '../../booking/booking.service';
import { DialougComponent } from 'src/app/shared/dialoug/dialoug.component';
import { TranslateService } from '@ngx-translate/core';
import { DataStorageService } from 'src/app/core/services/data-storage.service';
import { NotificationDtoModel } from 'src/app/shared/models/notification-model/notification-dto.model';
import { NameList } from 'src/app/shared/models/demographic-model/name-list.modal';

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
  //     addressLine1: 'Mindtree Limited',
  //     addressLine2: 'Global Village',
  //     contactPhone: '1234567890'
  //   },
  //   bookingData: '7 Jan 2019, 2:30pm'
  // }];
  secondaryLanguagelabels: any;
  secondaryLang = localStorage.getItem('secondaryLangCode');
  usersInfo = [];

  guidelines = ['Guidelines yet to be decided'];

  opt = {};

  fileBlob: Blob;

  notificationRequest = new FormData();

  constructor(private sharedService: SharedService, 
              private dialog: MatDialog, 
              private translate: TranslateService,
              private dataStorageService: DataStorageService) {
    this.translate.use(localStorage.getItem('langCode'));
  }


  
  ngOnInit() {
    this.usersInfo = this.sharedService.getNameList();
    this.opt = {
      filename: this.usersInfo[0].preRegId + '.pdf',
      image: { type: 'jpeg', quality: 0.98 },
      html2canvas: { scale: 1 },
      jsPDF: { unit: 'in', format: 'a4', orientation: 'landscape' }
    };
    this.usersInfo.forEach(user =>  this.generateQRCode(user));
    this.generateBlob();
    this.dataStorageService.getSecondaryLanguageLabels(this.secondaryLang).subscribe(response => {
      this.secondaryLanguagelabels = response['acknowledgement'];
    });
  }

  download() {
    const element = document.getElementById('print-section');
    html2pdf(element, this.opt);
  }

 generateBlob() {
    const element = document.getElementById('print-section');
    html2pdf()
      .from(element)
      .outputPdf('dataurlstring', this.opt)
      .then(response => {
        // convert base64 to raw binary data held in a string
        const byteString = atob(response.split(',')[1]);

        // separate out the mime component
        const mimeString = response
          .split(',')[0]
          .split(':')[1]
          .split(';')[0];

        // write the bytes of the string to an ArrayBuffer
        const arrayBuffer = new ArrayBuffer(byteString.length);

        const dataView = new DataView(arrayBuffer);
        this.fileBlob = new Blob([dataView], { type: mimeString });
      });
  }

  sendAcknowledgement() {
    const data = {
      case: 'APPLICANTS'
    };
    const dialogRef = this.dialog
      .open(DialougComponent, {
        width: '250px',
        data: data
      })
      .afterClosed()
      .subscribe(applicantNumber => {
        console.log(applicantNumber);
        this.sendNotification(applicantNumber);
      });
  }

  generateQRCode(name: NameList) {
    this.dataStorageService.generateQRCode(JSON.stringify(name)).subscribe(response => {
      console.log(response);
      const index = this.usersInfo.indexOf(name);
      this.usersInfo[index].qrCodeBlob = response['response'].qrcode;
    });
  }

  sendNotification(applicantNumber: string) {
    console.log(this.usersInfo);
    
    this.usersInfo.forEach(user => {
      console.log(user);
      const bookingData = user.bookingData.split(',');
      const notificationDto = new NotificationDtoModel(
        user.fullName,
        user.preRegId,
        bookingData[0] + bookingData[1],
        bookingData[2],
        isNaN(parseInt(applicantNumber.trim().charAt(0), 10)) ?  null : applicantNumber,
        isNaN(parseInt(applicantNumber.trim().charAt(0), 10)) ?  applicantNumber : null
        );
      this.notificationRequest.append('NotificationDTO', JSON.stringify(notificationDto));
      this.notificationRequest.append('langCode', localStorage.getItem('langCode'));
      this.notificationRequest.append('file', this.fileBlob);
      this.dataStorageService.sendNotification(this.notificationRequest).subscribe(response => {
        console.log(response);
        this.notificationRequest = new FormData();
      })
    });
  }
}
