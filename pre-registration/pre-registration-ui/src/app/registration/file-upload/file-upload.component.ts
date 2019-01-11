import { Component, OnInit, ElementRef } from '@angular/core';

import { RegistrationService } from '../registration.service';
import { DataStorageService } from '../../shared/data-storage.service';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { UserModel } from '../demographic/modal/user.modal';
import { FileModel } from '../demographic/modal/file.model';
import * as appConstants from '../../app.constants';
import { DomSanitizer } from '@angular/platform-browser';
import { ViewChild } from '@angular/core';

@Component({
  selector: 'app-file-upload',
  templateUrl: './file-upload.component.html',
  styleUrls: ['./file-upload.component.css']
})
export class FileUploadComponent implements OnInit {
  @ViewChild('fileUpload')
  fileInputVariable: ElementRef;

  @ViewChild('docCatSelect')
  docCatSelect: ElementRef;

  fileName = '';
  fileByteArray;
  fileUrl;
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
  // JsonString = {
  //   id: 'mosip.pre-registration.document.upload',
  //   ver: '1.0',
  //   reqTime: '2019-01-02T11:01:31.211Z',
  //   request: {
  //     prereg_id: '21398510941906',
  //     doc_cat_code: 'POA',
  //     doc_typ_code: 'address',
  //     doc_file_format: 'pdf',
  //     status_code: 'Pending-Appoinment',
  //     upload_by: '9217148168',
  //     upload_DateTime: '2019-01-02T11:01:31.211Z'
  //   }
  // };

  JsonString = appConstants.DOCUMENT_UPLOAD_REQUEST_DTO;

  browseDisabled = true;

  // disabled = true;

  step = 0;
  multipleApplicants = false;
  allApplicants: UserModel[] = [];
  constructor(
    private registration: RegistrationService,
    private dataStroage: DataStorageService,
    private router: Router,
    private route: ActivatedRoute,
    private domSanitizer: DomSanitizer
  ) {}

  ngOnInit() {
    this.allApplicants = this.registration.getUsers();
    this.allApplicants.splice(-1, 1);
    if (this.registration.getUsers().length > 0) {
      this.users[0] = this.registration.getUser(this.registration.getUsers().length - 1);
      if (!this.users[0].files[0]) {
        this.users[0].files[0] = [];
      }
    }
    if (this.registration.getUsers().length > 1) {
      this.multipleApplicants = true;
    }
    console.log('users on init', this.users);
    this.route.params.subscribe((params: Params) => {
      this.loginId = params['id'];
    });
  }

  viewFile(file) {
    this.fileName = file.doc_name;
    this.fileByteArray = file.multipartFile;
    if (this.fileByteArray) {
      this.fileUrl = this.domSanitizer.bypassSecurityTrustResourceUrl(
        'data:application/pdf;base64,' + this.fileByteArray
      );
    }
  }

  handleFileInput(event) {
    if (event.target.files[0].type === 'application/pdf') {
      if (event.target.files[0].size < 1000000) {
        this.getBase64(event.target.files[0]).then(data => {
          this.fileByteArray = data;
          this.fileByteArray = this.fileByteArray.replace('data:application/pdf;base64,', '');
        });
        this.setJsonString(event);
        this.sendFile(event);
      } else {
        alert('file too big');
      }
    } else {
      alert('Wrong file type, please upload again');
    }
  }

  getBase64(file) {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.readAsDataURL(file);
      reader.onload = () => resolve(reader.result);
      reader.onerror = error => reject(error);
    });
  }

  handleFileDrop(fileList) {}

  selectChange(event, index: number) {
    this.documentType = event.source.placeholder;
    this.documentIndex = index;
    // this.docCatSelect.nativeElement.value = '';
  }

  openedChange(event, index: number) {
    this.documentIndex = index;
  }

  onFilesChange(fileList: FileList) {}

  removeFile(applicantIndex, fileIndex) {
    this.dataStroage.deleteFile(this.users[applicantIndex].files[0][fileIndex].doc_id).subscribe(res => {
      // this.users[applicantIndex].files[0][fileIndex] = '';
      this.users[applicantIndex].files[0].splice(fileIndex, 1);
      if (this.users[applicantIndex].files[0][fileIndex].doc_name === this.fileName) {
        // this.fileName = '';
        // this.fileByteArray = '';
      }
      // this.documentIndex = fileIndex;
    });

    console.log('users updated', this.users);
    // this.applicants[applicantIndex].files[fileIndex] = '';
  }

  setJsonString(event) {
    this.JsonString.request.doc_cat_code = this.documentType;
    this.JsonString.request.pre_registartion_id = this.users[0].preRegId;
    this.JsonString.request.doc_file_format = event.target.files[0].type;
    this.JsonString.request.upload_by = this.loginId;
  }

  sendFile(event) {
    this.formData.append(appConstants.DOCUMENT_UPLOAD_REQUEST_DTO_KEY, JSON.stringify(this.JsonString));
    this.formData.append(appConstants.DOCUMENT_UPLOAD_REQUEST_DOCUMENT_KEY, event.target.files.item(0));
    this.dataStroage.sendFile(this.formData).subscribe(
      response => {
        console.log('file response', response);

        this.updateUsers(response, event);
      },
      error => {
        console.log(error);
      },
      () => {
        this.fileInputVariable.nativeElement.value = '';
      }
    );
    this.formData = new FormData();
  }

  updateUsers(fileResponse, event) {
    this.userFiles.doc_cat_code = fileResponse.response[0].documentCat;
    this.userFiles.doc_file_format = event.target.files[0].type;
    this.userFiles.doc_id = fileResponse.response[0].documnetId;
    this.userFiles.doc_name = event.target.files[0].name;
    this.userFiles.doc_typ_code = fileResponse.response[0].documentType;
    this.userFiles.multipartFile = this.fileByteArray;
    this.userFiles.prereg_id = this.users[0].preRegId;

    this.users[this.step].files[0].push(this.userFiles);
    this.userFiles = new FileModel();
    this.registration.updateUser(this.step, this.users[this.step]);
    console.log('updated users array', this.users);
  }
  documentPreview(fileIndex) {
    this.fileByteArray = this.users[0].files[0][0].multipartFile;
    if (this.fileByteArray) {
      this.fileUrl = this.domSanitizer.bypassSecurityTrustResourceUrl(
        'data:application/pdf;base64,' + this.fileByteArray
      );
    }
  }

  openFile() {
    const file = new Blob(this.users[0].files[0][0].multipartFile, { type: 'application/pdf' });
    const fileUrl = URL.createObjectURL(file);
    window.open(fileUrl);
  }

  sameAsChange(event) {
    this.dataStroage.copyDocument('POA', this.users[0].preRegId, event.value).subscribe(response => {});
  }

  ifDisabled(category) {
    this.users[0].files[0].forEach(element => {
      if ((element.doc_cat_code = category)) {
        return true;
      }
    });
    return false;
  }

  onBack() {
    this.router.navigate(['../demographic'], { relativeTo: this.route });
  }
  onNext() {
    // this.router.navigate(['pre-registration', this.loginId, 'pick-center']);
    this.router.navigate(['../preview'], { relativeTo: this.route });
  }
}
