import { Component, OnInit } from '@angular/core';

import { RegistrationService } from '../registration.service';
import { ActivatedRoute, Router, Params } from '@angular/router';

interface Applicant {
  name: string;
  files: any[];
  preId: string;
}

@Component({
  selector: 'app-file-upload',
  templateUrl: './file-upload.component.html',
  styleUrls: ['./file-upload.component.css']
})
export class FileUploadComponent implements OnInit {
  applicantName;
  applicant: Applicant = {
    name: '',
    files: [],
    preId: ''
  };
  uploadedFile;
  numberOfApplicants = 0;
  fileType;
  fileName = [];
  documentIndex;
  LOD = [
    {
      document_name: 'POA',
      valid_docs: [
        {
          name: 'passport',
          value: 'passport'
        },
        {
          name: 'Electricity Bill',
          value: 'Electricity Bill'
        },
        {
          name: 'Passbook',
          value: 'Passbook'
        }
      ]
    },
    {
      document_name: 'POI',
      valid_docs: [
        {
          name: 'passport',
          value: 'passport'
        },
        {
          name: 'Bank Account',
          value: 'Bank Account'
        }
      ]
    },
    {
      document_name: 'POB',
      valid_docs: [
        {
          name: 'passport',
          value: 'passport'
        },
        {
          name: 'Voter ID Card',
          value: 'Voter ID Card'
        }
      ]
    },
    {
      document_name: 'POR',
      valid_docs: [
        {
          name: 'passport',
          value: 'passport'
        },
        {
          name: 'Birth Certificate',
          value: 'Birth Certificate'
        }
      ]
    }
  ];
  documentString = {
    prereg_id: '59276903416082',
    doc_cat_code: 'POA',
    doc_typ_code: 'address',
    doc_file_format: 'pdf',
    status_code: 'Pending-Appoinment',
    lang_code: '1233',
    upd_by: '9217148168',
    cr_by: 'Rajath'
  };

  POAFileName = '';
  POIFileName = '';
  POBFileName = '';
  PORFileName = '';

  browseDisabled = true;

  // disabled = true;

  documents = ['Document type POA', 'Document type POI', 'Document type POB', 'Document type POR'];

  fileToUpload: File = null;
  DataSent = false;
  applicants: Applicant[] = [];
  step = 1;

  constructor(private registration: RegistrationService, private router: Router, private route: ActivatedRoute) {}

  setStep(applicant, step) {
    this.step = step + 1;
    this.applicantName = applicant.name;
  }

  nextStep() {
    this.step++;
    if (this.step === 5) {
      this.router.navigate(['../pick-center'], { relativeTo: this.route });
    }
  }

  prevStep() {
    this.step--;
  }

  ngOnInit() {
    this.registration.getUsers().forEach(element => {
      this.applicant.name = element.identity.FullName[0].value;
      this.applicant.preId = element.preRegId;
      this.applicants.push(this.applicant);
      console.log(element);
    });
  }

  handleFileInput(event) {
    // console.log(event, 'event from drag and drop');
    // const files = event;
    // for (const app of this.applicants) {
    //   if (app.name === this.applicantName) {
    //     app.files[this.documentIndex] = files.item(0);
    //   }
    // }
    const files = event.target.files;
    console.log(event.target.value);
    this.uploadedFile = event.target.value;
    console.log('files', event.target.files, ' number:', this.documentIndex, 'name: ', files.item(0).name);
    const formData = new FormData();
    formData.append('documentString', JSON.stringify(this.documentString));
    formData.append('file', files.item(0));
    // this.registration.sendFile(formData).subscribe(response => {
    //   console.log(response);
    // });
    this.browseDisabled = false;
    for (const app of this.applicants) {
      if (app.name === this.applicantName) {
        app.files[this.documentIndex] = files.item(0);
      }
    }
  }

  handleFileDrop(fileList) {
    console.log(fileList, 'event from drag and drop');
    const files = fileList;
    for (const app of this.applicants) {
      if (app.name === this.applicantName) {
        app.files[this.documentIndex] = files[0];
      }
    }
  }

  selectChange(event, index: number) {
    console.log('event from select :', event);
    this.fileType = event.source._id;
    this.browseDisabled = false;
    this.documentIndex = index;
  }

  openedChange(event, index: number) {
    console.log('event from select :', event);
    this.browseDisabled = false;
    this.documentIndex = index;
  }

  onFilesChange(fileList: FileList) {
    console.log(fileList);
  }

  removeFile(applicantIndex, fileIndex) {
    this.applicants[applicantIndex].files[fileIndex] = '';
  }
}
