import { Component, OnInit } from '@angular/core';

import { RegistrationService } from '../registration.service';
import { ActivatedRoute, Router, Params } from '@angular/router';

@Component({
  selector: 'app-file-upload',
  templateUrl: './file-upload.component.html',
  styleUrls: ['./file-upload.component.css']
})
export class FileUploadComponent implements OnInit {
  applicantName;
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
  applicants: any[] = [
    {
      name: 'Ravi Balaji',
      seq: 1,
      files: ['f1', 'f2', 'f3', 'f1']
    },
    {
      name: 'Shashank Agrawal',
      seq: 2,
      files: ['f4', 'f5', 'f6', 'f1']
    },
    {
      name: 'Agnitra Banerjee',
      seq: 3,
      files: ['f7', 'f8', 'f9', 'f1']
    },
    {
      name: 'Chacha Kumar',
      seq: 4,
      files: ['f10', 'f11', 'f12', 'f1']
    }
  ];

  step = 1;

  constructor(private registration: RegistrationService, private router: Router, private route: ActivatedRoute) {}

  setStep(applicant) {
    this.step = applicant.seq;
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
    this.route.params.subscribe((params: Params) => {
      this.numberOfApplicants = +params['id'];
    });
  }

  handleFileInput(event) {
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
