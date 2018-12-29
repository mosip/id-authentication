import { Component, OnInit } from '@angular/core';

import { RegistrationService } from '../registration.service';
import { DataStorageService } from '../../shared/data-storage.service';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { UserModel } from '../demographic/user.model';
import { FileModel } from '../demographic/file.model';

@Component({
  selector: 'app-file-upload',
  templateUrl: './file-upload.component.html',
  styleUrls: ['./file-upload.component.css']
})
export class FileUploadComponent implements OnInit {
  applicantPreRegId;
  userFiles: FileModel = new FileModel();
  formData = new FormData();
  user: UserModel = new UserModel();
  users: UserModel[] = [];

  documentType;
  loginId;
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

  browseDisabled = true;

  // disabled = true;

  documents = ['Document type POA', 'Document type POI', 'Document type POB', 'Document type POR'];

  step = 0;

  constructor(
    private registration: RegistrationService,
    private dataStroage: DataStorageService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.users[0] = this.registration.getUser(this.registration.getUsers().length - 1);
    console.log('users on init', this.users);
    this.route.params.subscribe((params: Params) => {
      this.loginId = params['id'];
    });
    // this.users.forEach(element => {
    //   let i = 0;
    //   this.applicant.name = element.identity.FullName[0].value;
    //   this.applicant.preId = element.preRegId;
    //   element.files.forEach(fileElement => {
    //     this.applicant.files[i] = fileElement;
    //   });
    //   this.applicants.push(this.applicant);
    //   i++;
    // });
  }

  handleFileInput(event) {
    console.log('event', event.target.files);
    if (event.target.files[0].type === 'application/pdf') {
      this.setJsonString(event);
      this.sendFile(event);
      this.browseDisabled = false;
    } else {
      alert('Wrong file type, please upload again');
    }
  }

  handleFileDrop(fileList) {}

  selectChange(event, index: number) {
    console.log('select change');
    this.documentType = event.source.placeholder;
    this.browseDisabled = false;
    this.documentIndex = index;
  }

  openedChange(event, index: number) {
    console.log('open change');
    this.browseDisabled = false;
    this.documentIndex = index;
  }

  onFilesChange(fileList: FileList) {}

  removeFile(applicantIndex, fileIndex) {
    console.log(applicantIndex, ' ; ', fileIndex);

    this.dataStroage.deleteFile(this.users[applicantIndex].files[0][fileIndex].doc_id).subscribe(res => {
      console.log(res);
      this.users[applicantIndex].files[0][fileIndex] = '';
    });
    // this.applicants[applicantIndex].files[fileIndex] = '';
  }

  setJsonString(event) {
    this.JsonString.request.doc_cat_code = this.documentType;
    this.JsonString.request.prereg_id = this.user.preRegId;
    this.JsonString.request.doc_file_format = event.target.files[0].type;
    this.JsonString.request.upload_by = this.loginId;
    console.log('Json String', this.JsonString);
  }

  sendFile(event): any {
    this.formData.append('JsonString', JSON.stringify(this.JsonString));
    this.formData.append('file', event.target.files.item(0));
    this.dataStroage.sendFile(this.formData).subscribe(response => {
      // this.setApplicantsArray(this.fileResponse, event.target.files);
      console.log('response', response);
      this.updateUsers(response, event);
    });
    this.formData = new FormData();
  }

  updateUsers(fileResponse, event) {
    console.log('fileResponse from Update Users method', fileResponse);
    this.userFiles.doc_cat_code = this.documentType;
    this.userFiles.doc_file_format = event.target.files[0].type;
    this.userFiles.doc_id = fileResponse.response[0].documnetId;
    this.userFiles.doc_name = event.target.files[0].name;
    this.userFiles.doc_typ_code = fileResponse.response[0].documentType;
    this.userFiles.multipartFile = event.target.files[0];
    this.userFiles.prereg_id = this.users[0].preRegId;
    console.log('step:', this.step);

    console.log('users befor update', this.users);
    this.users.forEach(element => {
      if (element.files[0].length) {
        this.users[this.step].files[0][this.documentIndex] = this.userFiles;
        // element.files[0][this.documentIndex] = this.userFiles;
      } else {
        this.users[this.step].files[0].push(this.userFiles);
      }
    });
    this.userFiles = new FileModel();
    this.registration.updateUser(this.step, this.users[this.step]);
    console.log('userFiles updaated', this.users);
  }

  openFile() {
    console.log('open file called', this.users[0].files[0][0].multipartFile);
    const file = new Blob(this.users[0].files[0][0].multipartFile, { type: 'application/pdf' });
    const fileUrl = URL.createObjectURL(file);
    window.open(fileUrl);
  }
}
