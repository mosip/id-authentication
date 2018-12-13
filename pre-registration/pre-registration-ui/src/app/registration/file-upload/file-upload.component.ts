import { Component, OnInit } from '@angular/core';

import { RegistrationService } from '../registration.service';
import { DataStorageService } from '../../shared/data-storage.service';
import { ActivatedRoute, Router, Params } from '@angular/router';

interface Applicant {
  name: string;
  files: ApplicantFiles[];
  preId: string;
}

interface ApplicantFiles {
  doc_cat_code: string;
  doc_file_format: string;
  doc_id: string;
  doc_name: string;
  doc_typ_code: string;
  multipartFile: any;
  prereg_id: string;
}

@Component({
  selector: 'app-file-upload',
  templateUrl: './file-upload.component.html',
  styleUrls: ['./file-upload.component.css']
})
export class FileUploadComponent implements OnInit {
  applicantFile: ApplicantFiles = {
    doc_cat_code: 'POR',
    doc_file_format: 'application/pdf',
    doc_id: '160',
    doc_name: 'ieltsreadinganswersheet.pdf',
    doc_typ_code: 'address',
    multipartFile: '',
    prereg_id: '35079431854826'
  };
  applicantName;
  documentType;
  loginId;
  applicant: Applicant = {
    name: '',
    files: [this.applicantFile],
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
  JsonString = {
    id: 'mosip.pre-registration.document.upload',
    ver: '1.0',
    reqTime: '2018-10-17T07:22:57.086+0000',
    request: {
      prereg_id: '21398510941906',
      doc_cat_code: 'POA',
      doc_typ_code: 'address',
      doc_file_format: 'pdf',
      status_code: 'Pending-Appoinment',
      upload_by: '9217148168',
      upload_DateTime: '2018-10-17T07:22:57.086+0000'
    }
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

  constructor(
    private registration: RegistrationService,
    private dataStroage: DataStorageService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  setStep(applicant, step) {
    this.step = step + 1;
    this.applicantName = applicant.name;
  }

  nextStep() {
    this.step++;
    this.router.navigate(['../pick-center'], { relativeTo: this.route });
  }

  prevStep() {
    this.step--;
  }

  ngOnInit() {
    this.route.params.subscribe((params: Params) => {
      this.loginId = params['id'];
    });
    this.registration.getUsers().forEach(element => {
      this.applicant.name = element.identity.FullName[0].value;
      this.applicant.preId = element.preRegId;
      this.applicants.push(this.applicant);
      console.log('element', element);
    });
  }

  handleFileInput(event) {
    const files = event.target.files;
    console.log(event.target.value);
    this.uploadedFile = event.target.value;
    console.log('files', event.target.files, ' number:', this.documentIndex, 'name: ', files.item(0).name);
    this.JsonString.request.prereg_id = this.applicant.preId;
    this.JsonString.request.doc_cat_code = this.documentType;
    this.JsonString.request.doc_file_format = files[0].type;
    this.JsonString.request.upload_by = this.loginId;
    const formData = new FormData();
    formData.append('JsonString', JSON.stringify(this.JsonString));
    formData.append('file', files.item(0));
    console.log('formData', formData);
    this.dataStroage.sendFile(formData).subscribe(response => {
      console.log(response);
    });
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
    const formData = new FormData();
    formData.append('JsonString', JSON.stringify(this.JsonString));
    formData.append('file', files.item(0));
    this.dataStroage.sendFile(formData).subscribe(response => {
      console.log(response);
    });
    for (const app of this.applicants) {
      if (app.name === this.applicantName) {
        app.files[this.documentIndex] = files[0];
      }
    }
  }

  selectChange(event, index: number) {
    console.log('event from select :', event);
    this.fileType = event.source._id;

    this.documentType = event.source.placeholder;
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
    // this.applicants[applicantIndex].files[fileIndex] = '';
  }
}
