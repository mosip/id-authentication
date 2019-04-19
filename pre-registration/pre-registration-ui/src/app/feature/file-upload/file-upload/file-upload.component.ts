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
import { RequestModel } from 'src/app/shared/models/request-model/RequestModel';
import { ConfigService } from 'src/app/core/services/config.service';

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
  sortedUserFiles: any[] = [];
  noneApplicant = {
    fullname: [
      {
        value: 'none'
      }
    ],
    preRegistrationId: ''
  };
  applicantType: string;
  allowedFilesHtml: string = '';
  allowedFileSize: string = '';
  sameAsselected: boolean = false;
  isModify: any;
  fileName: string = '';
  fileByteArray;
  fileUrl;
  applicantPreRegId: string;
  userFiles: FileModel = new FileModel();
  formData = new FormData();
  user: UserModel = new UserModel();
  users: UserModel[] = [];
  documentCategory: string;
  documentType: string;
  loginId: string;
  documentIndex: number;
  LOD: DocumentCategory[];
  fileIndex: number = -1;
  secondaryLanguagelabels: any;
  fileExtension: string = '';
  sameAs: string;
  disableNavigation: boolean = false;
  // JsonString = appConstants.DOCUMENT_UPLOAD_REQUEST_DTO;

  browseDisabled: boolean = true;

  // disabled = true;
  documentUploadRequestBody: DocumentUploadRequestDTO = {
    docCatCode: '',
    docTypCode: '',
    langCode: ''
  };

  documentCategoryDto: DocumentCategoryDTO = {
    attribute: '',
    value: ''
  };
  documentCategoryrequestDto: DocumentCategoryDTO[];
  documentRequest: RequestModel;
  step: number = 0;
  multipleApplicants: boolean = false;
  allApplicants: any[] = [];
  allowedFiles: string[];
  firstFile: Boolean = true;
  constructor(
    private registration: RegistrationService,
    private dataStroage: DataStorageService,
    private router: Router,
    private config: ConfigService,
    private domSanitizer: DomSanitizer,
    private sharedService: SharedService,
    private translate: TranslateService
  ) {
    this.translate.use(localStorage.getItem('langCode'));
    this.isModify = localStorage.getItem('modifyDocument');
  }

  ngOnInit() {
    this.getFileSize();
    this.allowedFiles = this.config
      .getConfigByKey(appConstants.CONFIG_KEYS.preregistration_document_alllowe_files)
      .split(',');
    this.getAllowedFileTypes(this.allowedFiles);
    // this.allowedFiles.toString();
    let applicants = [];
    this.loginId = this.registration.getLoginId();
    this.getAllApplicants(); //for same as in POA
    this.allApplicants = [];
    this.sameAs = this.registration.getSameAs();
    applicants = this.sharedService.getAllApplicants();
    this.allApplicants = this.getApplicantsName(applicants);

    this.dataStroage.getSecondaryLanguageLabels(localStorage.getItem('langCode')).subscribe(response => {
      if (response['message']) this.secondaryLanguagelabels = response['message'];
    });

    if (this.registration.getUsers().length > 0) {
      this.users[0] = this.registration.getUser(this.registration.getUsers().length - 1);
      console.log('users', this.users);
    }

    if (this.registration.getUsers().length > 1) {
      this.multipleApplicants = true;
    }
    this.getApplicantTypeID();
    // if (this.users[0].files[0] != null) {
    //   this.viewFirstFile();
    // }
    let i = 0;
    this.allApplicants.push(this.noneApplicant);
    let noneCount: Boolean = this.isNoneAvailable();

    for (let applicant of this.allApplicants) {
      if (applicant.preRegistrationId == this.users[0].preRegId) {
        this.allApplicants.splice(i, 1);
        this.allApplicants.push(this.noneApplicant);
        this.removeExtraNone();
      } else {
        i++;
      }
    }
    i = 0;
    if (!this.users[0].files[0]) {
      this.users[0].files[0] = [];
    } else {
      // this.sortUserFiles();
    }
  }

  getAllowedFileTypes(allowedFiles) {
    let i = 0;
    for (let file of allowedFiles) {
      if (i == 0) {
        this.allowedFilesHtml = this.allowedFilesHtml + file.substring(file.indexOf('/') + 1);
      } else {
        this.allowedFilesHtml = this.allowedFilesHtml + ',' + file.substring(file.indexOf('/') + 1);
      }
      i++;
    }
  }

  getFileSize() {
    this.allowedFileSize =
      (
        this.config.getConfigByKey(appConstants.CONFIG_KEYS.preregistration_document_alllowe_file_size) / 1000000
      ).toString() + 'mb';
  }
  removeExtraNone() {
    let i: number = 0;
    for (let applicant of this.allApplicants) {
      if (applicant.preRegistrationId == '') {
        this.allApplicants.splice(i, 1);
      }
      i++;
    }
  }

  isNoneAvailable() {
    let noneCount: number = 0;
    for (let applicant of this.allApplicants) {
      if (applicant.preRegistrationId == '') {
        noneCount++;
      }
    }
    return true;
  }

  sortUserFiles() {
    for (let document of this.LOD) {
      for (let file of this.users[0].files[0]) {
        if (document.code === file.doc_cat_code) {
          this.sortedUserFiles.push(file);
        }
      }
    }
    for (let i = 0; i <= this.users[0].files[0]; i++) {
      this.users[0].files[0][i] = this.sortedUserFiles[i];
    }
  }

  getApplicantsName(applicants) {
    let i = 0;
    let j = 0;
    for (let applicant of applicants) {
      for (let name of applicant.fullname) {
        if (name.language != localStorage.getItem('langCode')) {
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
    // let DOCUMENT_CATEGORY_DTO = appConstants.DOCUMENT_CATEGORY_DTO;
    let requestDTO: DocumentCategoryDTO = {
      attribute: '',
      value: ''
    };

    let DOBDTO: DocumentCategoryDTO = {
      attribute: '',
      value: ''
    };

    let genderDTO: DocumentCategoryDTO = {
      attribute: '',
      value: ''
    };

    let biometricDTO: DocumentCategoryDTO = {
      attribute: '',
      value: ''
    };

    let requestArray = {
      attributes: []
    };
    let DOCUMENT_CATEGORY_DTO: RequestModel;
    let DOB = this.users[0].request.demographicDetails.identity.dateOfBirth;

    requestDTO.attribute = appConstants.APPLICANT_TYPE_ATTRIBUTES.individualTypeCode;
    for (let language of this.users[0].request.demographicDetails.identity.residenceStatus) {
      if (language.language === localStorage.getItem('langCode')) {
        requestDTO.value = language.value;
      }
    }

    requestArray.attributes.push(requestDTO);

    DOBDTO.attribute = appConstants.APPLICANT_TYPE_ATTRIBUTES.dateofbirth;
    DOBDTO.value = DOB.replace(/\//g, '-') + 'T11:46:12.640Z';

    requestArray.attributes.push(DOBDTO);

    genderDTO.attribute = appConstants.APPLICANT_TYPE_ATTRIBUTES.genderCode;
    genderDTO.value = this.users[0].request.demographicDetails.identity.gender[0].value;

    requestArray.attributes.push(genderDTO);

    biometricDTO.attribute = appConstants.APPLICANT_TYPE_ATTRIBUTES.biometricAvailable;
    biometricDTO.value = false;

    requestArray.attributes.push(biometricDTO);

    // DOCUMENT_CATEGORY_DTO.request.attributes[2].value =
    // DOB = DOB + 'T11:46:12.640Z';
    // DOB.replace('1', '-');

    DOCUMENT_CATEGORY_DTO = new RequestModel(appConstants.IDS.applicantTypeId, requestArray, {});

    await this.dataStroage.getApplicantType(DOCUMENT_CATEGORY_DTO).subscribe(response => {
      if (response['error'] == null) {
        this.getDocumentCategories(response['response'].applicantType.applicantTypeCode);
        this.setApplicantType(response);
      } else {
        alert('Servers unavailable,please try again after some time');
      }
    });
  }

  async setApplicantType(response) {
    this.applicantType = await response['response'].applicationtypecode;
  }

  async getDocumentCategories(applicantcode) {
    await this.dataStroage.getDocumentCategories(applicantcode).subscribe(res => {
      if (res['error'] == null) {
        console.log('document Categories', res);

        this.LOD = res['response'].documentCategories;
        this.registration.setDocumentCategories(res['response'].documentCategories);
      } else {
        alert('Servers unavailable,please try again after some time');
      }
    });
  }

  async getAllApplicants() {
    await this.dataStroage.getUsers(this.loginId).subscribe(
      response => {
        if (response['error'] == null) {
          this.sharedService.addApplicants(response);
        } else {
          alert('Servers unavailable,please try again after some time');
        }
      },
      err => {},
      () => {}
    );
  }

  viewFirstFile() {
    this.fileIndex = 0;
    this.viewFile(this.users[0].files[0][0]);
  }

  viewFileByIndex(i: number) {
    this.viewFile(this.users[0].files[0][i]);
  }

  viewFile(file: FileModel) {
    console.log('file', file);

    this.fileName = file.docName;
    this.fileByteArray = file.multipartFile;
    let i = 0;
    for (let x of this.users[0].files[0]) {
      if (this.fileName === x.doc_name) {
        i++;
        break;
      }
    }
    if (this.firstFile) {
      this.fileIndex = i;
      this.firstFile = false;
    }
    this.fileExtension = file.docName.substring(file.docName.indexOf('.') + 1);
    if (this.fileByteArray) {
      console.log('file Extension', file.docName.substring(file.docName.indexOf('.') + 1));

      switch (file.docName.substring(file.docName.indexOf('.') + 1)) {
        case 'pdf':
          this.fileUrl = this.domSanitizer.bypassSecurityTrustResourceUrl(
            'data:application/pdf;base64,' + this.fileByteArray
          );
          break;
        case 'jpg':
          this.fileUrl = this.domSanitizer.bypassSecurityTrustResourceUrl(
            'data:image/jpeg;base64,' + this.fileByteArray
          );

        case 'png':
          this.fileUrl = this.domSanitizer.bypassSecurityTrustResourceUrl(
            'data:image/png;base64,' + this.fileByteArray
          );

          break;
      }
      // if (file.docName.substring(file.docName.indexOf('.') + 1) == 'pdf') {
      //   this.fileUrl = this.domSanitizer.bypassSecurityTrustResourceUrl(
      //     'data:application/pdf;base64,' + this.fileByteArray
      //   );
      // }
    }
  }

  viewLastFile() {
    this.fileIndex = this.users[0].files[0].length - 1;
    this.viewFile(this.users[0].files[0][this.fileIndex]);
  }
  /**
   *
   *
   * @param {*} event
   * @memberof FileUploadComponent
   */
  handleFileInput(event) {
    console.log('file input event', event);
    this.fileExtension = event.target.files[0].name.substring(event.target.files[0].name.indexOf('.') + 1);
    let allowedFileUploaded: Boolean = false;
    this.disableNavigation = true;
    for (let file of this.allowedFiles) {
      if (event.target.files[0].type === file) {
        allowedFileUploaded = true;
        if (
          event.target.files[0].name.length <
          this.config.getConfigByKey(appConstants.CONFIG_KEYS.preregistration_document_alllowe_file_name_lenght)
        ) {
          if (
            event.target.files[0].size <
            this.config.getConfigByKey(appConstants.CONFIG_KEYS.preregistration_document_alllowe_file_size)
          ) {
            this.getBase64(event.target.files[0]).then(data => {
              this.fileByteArray = data;
              switch (this.fileExtension) {
                case 'pdf':
                  this.fileByteArray = this.fileByteArray.replace('data:application/pdf;base64,', '');
                  break;
                case 'jpg':
                  this.fileByteArray = this.fileByteArray.replace('data:image/jpeg;base64,', '');
                  break;
                case 'png':
                  this.fileByteArray = this.fileByteArray.replace('data:image/png;base64,', '');
                  break;
              }
            });
            this.setJsonString(event);
            this.sendFile(event);
          } else {
            alert(this.secondaryLanguagelabels.uploadDocuments.msg4);
            this.disableNavigation = false;
          }
        } else {
          alert(this.secondaryLanguagelabels.uploadDocuments.msg5);
          this.disableNavigation = false;
        }
      }
    }
    if (!allowedFileUploaded) {
      alert(this.secondaryLanguagelabels.uploadDocuments.msg6);
      this.disableNavigation = false;
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
    this.documentCategory = event.source.placeholder;
    this.documentType = event.source.value;
    this.documentIndex = index;
  }

  openedChange(event, index: number) {
    this.documentCategory = this.LOD[index].code;
    this.documentIndex = index;
  }

  onFilesChange(fileList: FileList) {}

  removeFilePreview() {
    this.fileName = '';
    this.fileUrl = this.domSanitizer.bypassSecurityTrustResourceUrl('');
  }
  setJsonString(event) {
    this.documentUploadRequestBody.docCatCode = this.documentCategory;
    this.documentUploadRequestBody.langCode = localStorage.getItem('langCode');
    this.documentUploadRequestBody.docTypCode = this.documentType;
    this.documentRequest = new RequestModel(appConstants.IDS.documentUpload, this.documentUploadRequestBody, {});
    // this.documentRequest.doc_cat_code = this.documentType;
    // this.documentRequest.pre_registartion_id = this.users[0].preRegId;
  }

  sendFile(event) {
    // this.formData.append(appConstants.DOCUMENT_UPLOAD_REQUEST_DTO_KEY, JSON.stringify(this.JsonString));
    this.formData.append(appConstants.DOCUMENT_UPLOAD_REQUEST_DTO_KEY, JSON.stringify(this.documentRequest));
    this.formData.append(appConstants.DOCUMENT_UPLOAD_REQUEST_DOCUMENT_KEY, event.target.files.item(0));
    this.dataStroage.sendFile(this.formData, this.users[0].preRegId).subscribe(
      response => {
        if (response['errors'] == null) {
          this.updateUsers(response);
        } else {
          alert(response['errors'].errorCode + ' Invalid document format supported');
        }
      },
      error => {
        alert(this.secondaryLanguagelabels.uploadDocuments.msg7);
      },
      () => {
        this.fileInputVariable.nativeElement.value = '';
        this.disableNavigation = false;
      }
    );
    this.formData = new FormData();
  }

  updateUsers(fileResponse) {
    let i = 0;
    this.userFiles.docCatCode = fileResponse.response[0].docCatCode;
    this.userFiles.doc_file_format = fileResponse.response[0].docFileFormat;
    this.userFiles.documentId = fileResponse.response[0].documentId;
    this.userFiles.docName = fileResponse.response[0].docName;
    this.userFiles.docTypCode = fileResponse.response[0].docTypCode;
    this.userFiles.multipartFile = this.fileByteArray;
    this.userFiles.prereg_id = this.users[0].preRegId;
    for (let file of this.users[0].files[0]) {
      if (file.docCatCode == this.userFiles.docCatCode) {
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
    // this.sortUserFiles();
    // this.viewFileByIndex(this.fileIndex);
  }

  openFile() {
    const file = new Blob(this.users[0].files[0][0].multipartFile, { type: 'application/pdf' });
    const fileUrl = URL.createObjectURL(file);
    window.open(fileUrl);
  }

  sameAsChange(event) {
    if (event.value == '') {
      this.sameAsselected = false;
    } else {
      this.dataStroage.copyDocument(event.value, this.users[0].preRegId).subscribe(
        response => {
          if (response['errors'] == null) {
            this.registration.setSameAs(event.value);
            this.removePOADocument();
          } else {
            // alert(this.secondaryLanguagelabels.uploadDocuments.msg8);
            this.sameAs = this.registration.getSameAs();
            alert(response['errors'].message);
          }
        },
        err => {
          alert(this.secondaryLanguagelabels.uploadDocuments.msg8);
        }
      );
      this.sameAsselected = true;
    }
  }
  removePOADocument() {
    this.userFiles = new FileModel();
    let i = 0;
    for (let file of this.users[0].files[0]) {
      if (file.docCatCode == 'POA') {
        // this.users[0].files[0][i] = this.userFiles;
        this.users[0].files[0].splice(i, 1);
      }
      i++;
    }
  }

  ifDisabled(category) {
    this.users[0].files[0].forEach(element => {
      if ((element.docCatCode = category)) {
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

  nextFile(fileIndex: number) {
    this.fileIndex = fileIndex + 1;

    this.viewFileByIndex(this.fileIndex);
  }

  previousFile(fileIndex: number) {
    this.fileIndex = fileIndex - 1;
    this.viewFileByIndex(this.fileIndex);
  }
}

export interface DocumentUploadRequestDTO {
  docCatCode: string;
  docTypCode: string;
  langCode: string;
}

export interface DocumentCategoryDTO {
  attribute: string;
  value: any;
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
