import { Component, OnInit } from '@angular/core';
import * as html2pdf from 'html2pdf.js';
import { MatDialog } from '@angular/material';
import { SharedService } from '../../booking/booking.service';
import { DialougComponent } from 'src/app/shared/dialoug/dialoug.component';
import { TranslateService } from '@ngx-translate/core';

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

  usersInfo = [];

  guidelines = ['Guidelines yet to be decided'];

  opt = {};

  constructor(private sharedService: SharedService, private dialog: MatDialog, private translate: TranslateService) {
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
    console.log('acknowledgement component', this.sharedService.getNameList());
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
        const blob = new Blob([dataView], { type: mimeString });
        console.log(blob);
        return blob;
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
        console.log(this.generateBlob());
        // Api call here
      });
  }
}
