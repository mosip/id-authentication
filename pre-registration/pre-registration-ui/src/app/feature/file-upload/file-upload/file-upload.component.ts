import { Component, OnInit, ElementRef } from '@angular/core';

import { ActivatedRoute, Router, Params } from '@angular/router';
import * as appConstants from '../../../app.constants';
import { DomSanitizer } from '@angular/platform-browser';
import { ViewChild } from '@angular/core';
import { FileModel } from 'src/app/shared/models/demographic-model/file.model';
import { UserModel } from 'src/app/shared/models/demographic-model/user.modal';
import { RegistrationService } from 'src/app/core/services/registration.service';
import { DataStorageService } from 'src/app/core/services/data-storage.service';
import { TranslateService } from '@ngx-translate/core';
import { SharedService } from '../../booking/booking.service';

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

  noneApplicant = {
    fullname: [
      {
        value: 'none'
      }
    ],
    preRegistrationId: ''
  };
  applicantType;
  sameAsselected = false;
  isModify: any;
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
  LOD: DocumentCategory[];
  fileIndex = -1;

  sameAs;

  JsonString = appConstants.DOCUMENT_UPLOAD_REQUEST_DTO;

  browseDisabled = true;

  // disabled = true;

  step = 0;
  multipleApplicants = false;
  allApplicants: any[] = [];
  constructor(
    private registration: RegistrationService,
    private dataStroage: DataStorageService,
    private router: Router,
    private route: ActivatedRoute,
    private domSanitizer: DomSanitizer,
    private sharedService: SharedService,
    private translate: TranslateService
  ) {
    console.log('CALIING FILE UPLOAD');

    this.translate.use(localStorage.getItem('langCode'));
    this.isModify = localStorage.getItem('modifyDocument');
  }

  ngOnInit() {
    let applicants;
    this.loginId = this.registration.getLoginId();
    this.getAllApplicants();
    this.allApplicants = [];
    this.sameAs = this.registration.getSameAs();
    applicants = this.sharedService.getAllApplicants();
    this.allApplicants = this.getApplicantsName(applicants);

    console.log('applicants', this.allApplicants);

    if (this.registration.getUsers().length > 0) {
      this.users[0] = this.registration.getUser(this.registration.getUsers().length - 1);
      if (!this.users[0].files[0]) {
        this.users[0].files[0] = [];
      } else {
        // this.sortUserFiles();
      }
    }

    if (this.registration.getUsers().length > 1) {
      this.multipleApplicants = true;
    }
    console.log('users', this.users);
    this.getApplicantTypeID();
    if (this.users[0].files[0].length != 0) {
      this.viewFirstFile();
    }
    let i = 0;
    for (let applicant of this.allApplicants) {
      if (applicant.preRegistrationId == this.users[0].preRegId) {
        this.allApplicants.splice(i, 1);
        this.allApplicants.push(this.noneApplicant);
      } else {
        i++;
      }
    }

    console.log('applicants', this.allApplicants);
  }

  getApplicantsName(applicants) {
    console.log('applicants', applicants);

    let i = 0;
    let j = 0;
    for (let applicant of applicants) {
      for (let name of applicant.fullname) {
        if (name.language != localStorage.getItem('langCode')) {
          console.log('lang code ', localStorage.getItem('langCode'));
          applicants[i].fullname.splice(j, 1);
        } else {
        }
        j++;
      }
      i++;
    }
    return applicants;
  }
  async getApplicantTypeID() {
    let DOCUMENT_CATEGORY_DTO = appConstants.DOCUMENT_CATEGORY_DTO;
    let re = /\//g;
    let DOB = this.users[0].request.demographicDetails.identity.dateOfBirth;

    for (let language of this.users[0].request.demographicDetails.identity.residenceStatus) {
      if (language.language === localStorage.getItem('langCode')) {
        DOCUMENT_CATEGORY_DTO.attributes[0].value = language.value;
      }
    }
    DOCUMENT_CATEGORY_DTO.attributes[2].value = this.users[0].request.demographicDetails.identity.gender[0].value;
    // DOB = DOB + 'T11:46:12.640Z';
    // DOB.replace('1', '-');

    DOCUMENT_CATEGORY_DTO.attributes[1].value = DOB.replace(/\//g, '-') + 'T11:46:12.640Z';
    console.log('document catergory dto', DOCUMENT_CATEGORY_DTO);

    await this.dataStroage.getApplicantType(DOCUMENT_CATEGORY_DTO).subscribe(response => {
      console.log('response from applicant type', response);
      this.getDocumentCategories(response['response'].applicantTypeCode);
      this.setApplicantType(response);
    });
  }

  async setApplicantType(response) {
    console.log(response);
    this.applicantType = await response['response'].applicationtypecode;
    console.log(this.applicantType);
  }

  async getDocumentCategories(applicantcode) {
    await this.dataStroage.getDocumentCategories(applicantcode).subscribe(res => {
      console.log('response form  document categories', res['documentCategories']);
      console.log(this.LOD);
      this.LOD = res['documentCategories'];
      console.log(this.applicantType);
      this.registration.setDocumentCategories(res['documentCategories']);
    });
  }

  async getAllApplicants() {
    await this.dataStroage.getUsers(this.loginId).subscribe(
      applicants => {
        this.sharedService.addApplicants(applicants);
      },
      err => {},
      () => {}
    );
  }

  sortUserFiles() {
    let sortedUserFiles;
    for (let document of this.LOD) {
      for (let file of this.users[0].files[0]) {
        if (document.code === file.doc_cat_code) {
          sortedUserFiles.push(file);
          break;
        }
      }
    }
    console.log('sorted file', sortedUserFiles);

    for (let i = 0; i <= this.users[0].files[0]; i++) {
      this.users[0].files[0][i] = sortedUserFiles[i];
    }
  }

  viewFirstFile() {
    this.fileIndex = 0;
    this.viewFile(this.users[0].files[0][0]);
  }

  viewFileByIndex(i) {
    this.viewFile(this.users[0].files[0][i]);
  }

  viewFile(file) {
    this.fileName = file.doc_name;
    this.fileByteArray = file.multipartFile;
    let i = 0;
    for (let x of this.users[0].files[0]) {
      i++;
      if (this.fileName === x.doc_name) {
        break;
      }
    }
    this.fileIndex = i - 1;
    if (this.fileByteArray) {
      this.fileUrl = this.domSanitizer.bypassSecurityTrustResourceUrl(
        'data:application/pdf;base64,' + this.fileByteArray
      );
    }
  }

  viewLastFile() {
    this.fileIndex = this.users[0].files[0].length - 1;
    this.viewFile(this.users[0].files[0][this.fileIndex]);
  }

  handleFileInput(event) {
    if (event.target.files[0].type === 'application/pdf') {
      if (event.target.files[0].name.length < 46) {
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
        alert('File name should not be more thaan 50 characters');
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

  selectChange(event, index: number) {
    this.documentType = event.source.placeholder;
    this.documentIndex = index;
  }

  openedChange(event, index: number) {
    this.documentType = this.LOD[index].code;
    this.documentIndex = index;
  }

  onFilesChange(fileList: FileList) {}

  removeFile(applicantIndex, file_cat_code) {
    let fileIndex = 0;
    for (let element of this.users[0].files[0]) {
      if (element.doc_cat_code == file_cat_code) {
        break;
      }
      fileIndex++;
    }

    this.dataStroage.deleteFile(this.users[applicantIndex].files[0][fileIndex].doc_id).subscribe(res => {
      this.users[applicantIndex].files[0].splice(fileIndex, 1);
      if (this.users[0].files[0].length == 0) {
        this.removeFilePreview();
      } else {
        this.viewLastFile();
      }
    });
    this.fileIndex--;
  }

  removeFilePreview() {
    this.fileName = '';
    this.fileUrl = this.domSanitizer.bypassSecurityTrustResourceUrl('');
  }
  setJsonString(event) {
    this.JsonString.request.doc_cat_code = this.documentType;
    this.JsonString.request.pre_registartion_id = this.users[0].preRegId;
  }

  sendFile(event) {
    this.formData.append(appConstants.DOCUMENT_UPLOAD_REQUEST_DTO_KEY, JSON.stringify(this.JsonString));
    this.formData.append(appConstants.DOCUMENT_UPLOAD_REQUEST_DOCUMENT_KEY, event.target.files.item(0));
    this.dataStroage.sendFile(this.formData).subscribe(
      response => {
        console.log('document response', response);

        this.updateUsers(response, event);
      },
      error => {
        alert('The file coul not be uploaded, please try again.');
        console.log(error);
      },
      () => {
        this.fileInputVariable.nativeElement.value = '';
      }
    );
    this.formData = new FormData();
  }

  updateUsers(fileResponse, event) {
    let i = 0;
    this.userFiles.doc_cat_code = fileResponse.response[0].documentCat;
    this.userFiles.doc_file_format = event.target.files[0].type;
    this.userFiles.doc_id = fileResponse.response[0].documnetId;
    this.userFiles.doc_name = event.target.files[0].name;
    this.userFiles.doc_typ_code = fileResponse.response[0].documentType;
    this.userFiles.multipartFile = this.fileByteArray;
    this.userFiles.prereg_id = this.users[0].preRegId;
    for (let file of this.users[0].files[0]) {
      if (file.doc_cat_code == this.userFiles.doc_cat_code) {
        this.removeFilePreview();
        this.users[this.step].files[0][i] = this.userFiles;
        this.fileIndex--;
        break;
      }
      i++;
    }
    if (i == this.users[0].files[0].length) {
      this.users[this.step].files[0].push(this.userFiles);
    }
    this.userFiles = new FileModel();
    this.registration.updateUser(this.step, this.users[this.step]);
    console.log('userrs', this.users);
    // this.sortUserFiles();
    this.nextFile();
  }

  openFile() {
    const file = new Blob(this.users[0].files[0][0].multipartFile, { type: 'application/pdf' });
    const fileUrl = URL.createObjectURL(file);
    window.open(fileUrl);
  }

  sameAsChange(event) {
    if (event.value == '') {
      console.log('none selected');
      this.sameAsselected = false;
    } else {
      this.registration.setSameAs(event.value);
      this.dataStroage.copyDocument('POA', event.value, this.users[0].preRegId).subscribe(
        response => {
          console.log('copy document', response);
          if (response['err'] == null) {
            this.removePOADocument();
          } else {
            alert('could not copy document');
          }
        },
        err => {
          console.log('error in copy document', err);
          alert('could not copy document');
        }
      );
      this.sameAsselected = true;
    }
  }
  removePOADocument() {
    this.userFiles = new FileModel();
    let i = 0;
    for (let file of this.users[0].files[0]) {
      if (file.doc_cat_code == 'POA') {
        this.users[0].files[0][i] = this.userFiles;
        i++;
      }
    }
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
    this.registration.changeMessage({ modifyUser: 'true' });
    const arr = this.router.url.split('/');
    arr.pop();
    arr.push('demographic');
    const url = arr.join('/');
    this.router.navigateByUrl(url);
  }
  onNext() {
    localStorage.setItem('modifyDocument', 'false');
    const arr = this.router.url.split('/');
    arr.pop();
    arr.push('summary');
    arr.push('preview');
    const url = arr.join('/');
    this.router.navigateByUrl(url);
  }

  nextFile() {
    this.fileIndex++;
    this.viewFileByIndex(this.fileIndex);
  }

  previousFile() {
    this.fileIndex--;
    this.viewFileByIndex(this.fileIndex);
  }
}

export interface DocumentCategory {
  code: string;
  description: string;
  isActive: string;
  langCode: string;
  name: string;
  documentTypes: {
    code: string;
    description: string;
    isActive: string;
    langCode: string;
    name: string;
  };
}

// console.log(ts.toJSON());
// # 2019-03-06T05:24:10.264Z
