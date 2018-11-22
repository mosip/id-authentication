import { Component, OnInit } from '@angular/core';

import { RegistrationService } from '../registration.service';
import { ActivatedRoute, Router, Params } from '@angular/router';

@Component({
  selector: 'app-file-upload',
  templateUrl: './file-upload.component.html',
  styleUrls: ['./file-upload.component.css']
})
export class FileUploadComponent implements OnInit {
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
  POADocuments = [
    {
      name: 'Passport',
      value: 'passport'
    },
    {
      name: 'Driving License',
      value: 'DL'
    },
    {
      name: 'Electricity Bill',
      value: 'EB'
    }
  ];

  documents = ['Document type POA', 'Document type POI', 'Document type POB', 'Document type POR'];

  fileToUpload: File = null;
  DataSent = false;
  applicants: any[] = [
    {
      name: 'name1',
      seq: 1
    },
    {
      name: 'name2',
      seq: 2
    },
    {
      name: 'name3',
      seq: 3
    },
    {
      name: 'name4',
      seq: 4
    }
  ];

  step = 1;

  constructor(private registration: RegistrationService, private router: Router, private route: ActivatedRoute) {}

  setStep(index: number) {
    this.step = index;
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

    for (const i of this.LOD) {
      this.fileName.push('');
    }
  }

  handleFileInput(files: FileList) {
    console.log('files', files, ' number:', this.documentIndex, 'name: ', files.item(0).name);
    const formData = new FormData();
    formData.append('documentString', JSON.stringify(this.documentString));
    formData.append('file', files.item(0));
    // this.registration.sendFile(formData).subscribe(response => {
    //   console.log(response);
    // });
    this.browseDisabled = false;
    // this.fileName.push(files.item(0).name);
    this.fileName[this.documentIndex] = files.item(0).name;
  }

  selectChange(event, index: number) {
    console.log('event from select :', event);
    this.fileType = event.source._id;
    this.browseDisabled = false;
    this.documentIndex = index;
  }

  openedChange(event, index: number) {
    console.log('event from select :', event);
    this.fileType = event.source._id;
    this.browseDisabled = false;
    this.documentIndex = index;
  }

  onFilesChange(fileList: FileList) {
    console.log(fileList);
  }
}
