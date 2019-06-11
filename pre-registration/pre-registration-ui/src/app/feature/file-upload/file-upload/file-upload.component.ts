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
import { BookingService } from '../../booking/booking.service';
import { RequestModel } from 'src/app/shared/models/request-model/RequestModel';
import { ConfigService } from 'src/app/core/services/config.service';
import { DialougComponent } from 'src/app/shared/dialoug/dialoug.component';
import { MatDialog } from '@angular/material';
import { FilesModel } from 'src/app/shared/models/demographic-model/files.model';

@Component({
  selector: 'app-file-upload',
  templateUrl: './file-upload.component.html',
  styleUrls: ['./file-upload.component.css']
})
export class FileUploadComponent implements OnInit {
  @ViewChild('fileUpload')
  fileInputVariable: ElementRef;

  @ViewChild('docCatSelect')
  viewFileTrue = false;
  docCatSelect: ElementRef;
  sortedUserFiles: any[] = [];
  applicantType: string;
  allowedFilesHtml: string = '';
  allowedFileSize: string = '';
  sameAsselected: boolean = false;
  isModify: any;
  fileName: string = '';
  fileByteArray;
  fileUrl;
  applicantPreRegId: string;
  file: FileModel = new FileModel();
  userFile: FileModel[] = [this.file];
  userFiles: FilesModel = new FilesModel(this.userFile);
  formData = new FormData();
  user: UserModel = new UserModel();
  users: UserModel[] = [];
  activeUsers: UserModel[] = [];
  documentCategory: string;
  documentType: string;
  loginId: string;
  documentIndex: number;
  LOD: DocumentCategory[];
  fileIndex: number = -1;
  fileUploadLanguagelabels: any;
  errorlabels: any;
  fileExtension: string = '';
  sameAs: string;
  disableNavigation: boolean = false;
  // JsonString = appConstants.DOCUMENT_UPLOAD_REQUEST_DTO;
  start: boolean = false;
  browseDisabled: boolean = true;

  // disabled = true;
  documentUploadRequestBody: DocumentUploadRequestDTO = {
    docCatCode: '',
    docTypCode: '',
    langCode: ''
  };
  files: FilesModel;
  documentCategoryDto: DocumentCategoryDTO = {
    attribute: '',
    value: ''
  };
  documentCategoryrequestDto: DocumentCategoryDTO[];
  documentRequest: RequestModel;
  step: number = 0;
  multipleApplicants: boolean = false;
  allApplicants: any[] = [];
  applicants: any[] = [];
  allowedFiles: string[];
  firstFile: Boolean = true;
  noneApplicant = {
    demographicMetadata: {
      fullname: [
        {
          language: '',
          value: 'None'
        }
      ]
    },
    preRegistrationId: ''
  };

  constructor(
    private registration: RegistrationService,
    private dataStroage: DataStorageService,
    private router: Router,
    private config: ConfigService,
    public domSanitizer: DomSanitizer,
    private bookingService: BookingService,
    private translate: TranslateService,
    private dialog: MatDialog
  ) {
    this.initiateComponent();
  }

  ngOnInit() {
    console.log('users', this.users);

    this.getFileSize();
    this.allowedFiles = this.config
      .getConfigByKey(appConstants.CONFIG_KEYS.preregistration_document_alllowe_files)
      .split(',');
    this.getAllowedFileTypes(this.allowedFiles);
    this.loginId = this.registration.getLoginId();
    // this.getAllApplicants(); //for same as in POA
    this.setApplicants();
    // this.allApplicants = [];
    this.sameAs = this.registration.getSameAs();
    this.dataStroage.getSecondaryLanguageLabels(localStorage.getItem('langCode')).subscribe(response => {
      if (response['message']) this.fileUploadLanguagelabels = response['message'];
      if (response['error']) this.errorlabels = response['error'];
    });

    if (this.registration.getUsers().length > 1) {
      this.multipleApplicants = true;
    }
    this.getApplicantTypeID();
    let i = 0;
    let fileModel: FileModel = new FileModel('', '', '', '', '', '', '');
    if (!this.users[0].files) {
      this.users[0].files = this.userFiles;
    } else {
      // this.sortUserFiles();
    }
  }

  /**
   *@description This method initialises the users array and the language set by the user.
   *@private
   * @memberof FileUploadComponent
   */
  private initiateComponent() {
    this.translate.use(localStorage.getItem('langCode'));
    this.isModify = localStorage.getItem('modifyDocument');
    if (this.registration.getUsers().length > 0) {
      this.users[0] = this.registration.getUser(this.registration.getUsers().length - 1);
      this.activeUsers = this.registration.getUsers();
    }
  }

  /**
   *@description method to change the current user to be shown as None value in the same as array.
   *@private
   * @memberof FileUploadComponent
   */
  private setNoneApplicant() {
    let i: number = 0;
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
  }
  /**
   *@description method to initialise the allowedFiles array used to show in the html page
   *
   * @param {string[]} allowedFiles
   * @memberof FileUploadComponent
   */
  getAllowedFileTypes(allowedFiles: string[]) {
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
  /**
   *@description method to set the value of allowed file size to be displayed in html
   *
   * @memberof FileUploadComponent
   */
  getFileSize() {
    this.allowedFileSize =
      (
        this.config.getConfigByKey(appConstants.CONFIG_KEYS.preregistration_document_alllowe_file_size) / 1000000
      ).toString() + 'mb';
  }

  /**
   *
   *@description after add applicant the allaplicants array contains an extra none.
   *This method removes this extra none.
   * @memberof FileUploadComponent
   */
  removeExtraNone() {
    let i: number = 0;
    for (let applicant of this.allApplicants) {
      if (applicant.preRegistrationId == '') {
        this.allApplicants.splice(i, 1);
      }
      i++;
    }
  }
  /**
   *@description method to check if none is available or not
   *
   * @returns
   * @memberof FileUploadComponent
   */
  isNoneAvailable() {
    let noneCount: number = 0;
    for (let applicant of this.allApplicants) {
      if (applicant.preRegistrationId == '') {
        noneCount++;
      }
    }
    return true;
  }
  /**
   *@description method to sorf the files in the users array according to the doccument categories in LOD
   *
   * @memberof FileUploadComponent
   */
  sortUserFiles() {
    for (let document of this.LOD) {
      for (let file of this.users[0].files.documentsMetaData) {
        if (document.code === file.docCatCode) {
          this.sortedUserFiles.push(file);
        }
      }
    }
    for (let i = 0; i <= this.users[0].files[0].documentsMetaData; i++) {
      this.users[0].files[0][i] = this.sortedUserFiles[i];
    }
  }

  /**
   *
   *@description method to get applicants name array to be shown in same as List.
   * @param {*} applicants
   * @returns
   * @memberof FileUploadComponent
   */
  getApplicantsName(applicants) {
    let i = 0;
    let j = 0;
    let allApplicants: any[] = [];
    console.log('applicants', applicants);

    allApplicants = applicants;
    for (let applicant of allApplicants) {
      for (let name of applicant.demographicMetadata) {
        if (name['fullname'].language != localStorage.getItem('langCode') && name.proofOfAddress == null) {
          allApplicants[i].demographicMetadata.fullname.splice(j, 1);
        } else {
        }
        j++;
      }
      i++;
    }
    console.log('allApplicants local', allApplicants);

    return allApplicants;
  }
  /**
   *
   *@description method to get the applicant type code to fetch the document cagtegories to be uploaded.
   * @memberof FileUploadComponent
   */
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
      if (response['errors'] == null) {
        this.getDocumentCategories(response['response'].applicantType.applicantTypeCode);
        this.setApplicantType(response);
      } else {
        // alert('Servers unavailable,please try again after some time');
        this.displayMessage('Error', this.errorlabels.error);
      }
    });
  }
  /**
   *@description method to set applicant type.
   *
   * @param {*} response
   * @memberof FileUploadComponent
   */
  async setApplicantType(response) {
    this.applicantType = await response['response'].applicationtypecode;
  }
  /**
   *@description method to get document catrgories from master data.
   *
   * @param {*} applicantcode
   * @memberof FileUploadComponent
   */
  async getDocumentCategories(applicantcode) {
    await this.dataStroage.getDocumentCategories(applicantcode).subscribe(res => {
      if (res['errors'] == null) {
        this.LOD = res['response'].documentCategories;
        this.registration.setDocumentCategories(res['response'].documentCategories);
        console.log('LOD', this.LOD);
      } else {
        this.displayMessage('Error', this.errorlabels.error);
      }
    });
  }

  /**
   *@description method to get the list of applicants to eb shown in same as options
   *
   * @memberof FileUploadComponent
   */
  async getAllApplicants() {
    await this.dataStroage.getUsers(this.loginId).subscribe(
      response => {
        if (response['errors'] == null) {
          this.bookingService.addApplicants(response['response']['basicDetails']);
        } else {
          this.displayMessage('Error', this.errorlabels.error);
        }
      },
      err => {
        this.displayMessage('Error', this.errorlabels.error);
      },
      () => {
        this.setApplicants();
      }
    );
  }
  /**
   *@description method to set the applicants array  used in same as options aray
   *
   * @memberof FileUploadComponent
   */
  setApplicants() {
    this.applicants = this.bookingService.getAllApplicants();
    this.updateApplicants();
    this.allApplicants = this.getApplicantsName(this.applicants);
    console.log('all applicants', this.allApplicants);

    this.setNoneApplicant();
  }

  updateApplicants() {
    let flag: boolean = false;
    let x: number = 0;
    for (let i of this.activeUsers) {
      for (let j of this.applicants) {
        if (i.preRegId == j.preRegistrationId) {
          flag = true;
          break;
        }
      }
      if (flag) {
        this.activeUsers.splice(x, 1);
      }
      x++;
    }
    let fullName: FullName = {
      language: '',
      value: ''
    };
    let user: Applicants = {
      preRegistrationId: '',
      demographicMetadata: {
        fullname: [fullName]
      }
    };
    let activeUsers: any[] = [];
    for (let i of this.activeUsers) {
      user.preRegistrationId = i.preRegId;
      user.demographicMetadata.fullname = i.request.demographicDetails.identity.fullName;
      activeUsers.push(user);
    }
    for (let i of activeUsers) {
      this.applicants.push(i);
    }
  }

  /**
   *@description method to preview the first file.
   *
   * @memberof FileUploadComponent
   */
  viewFirstFile() {
    this.fileIndex = 0;
    this.viewFile(this.users[0].files[0].documentsMetaData[0]);
  }
  /**
   *@description method to preview file by index.
   *
   * @param {number} i
   * @memberof FileUploadComponent
   */
  viewFileByIndex(i: number) {
    this.viewFile(this.users[0].files.documentsMetaData[i]);
  }

  setByteArray(fileByteArray) {
    this.fileByteArray = fileByteArray;
  }

  /**
   *@description method to preview a specific file.
   *
   * @param {FileModel} file
   * @memberof FileUploadComponent
   */
  viewFile(fileMeta: FileModel) {
    this.viewFileTrue = true;
    this.fileIndex = 0;
    this.disableNavigation = true;
    this.dataStroage.getFileData(fileMeta.documentId, this.users[0].preRegId).subscribe(
      res => {
        if (!res['errors']) {
          this.setByteArray(res['response'].document);
        } else {
          this.displayMessage('Error', this.errorlabels.error);
          this.start = false;
        }
      },
      error => {},
      () => {
        this.fileName = fileMeta.docName;
        let i = 0;
        for (let x of this.users[0].files.documentsMetaData) {
          if (this.fileName === x.docName) {
            break;
          }
          i++;
        }
        this.fileIndex = i;
        this.fileExtension = fileMeta.docName.substring(fileMeta.docName.indexOf('.') + 1);
        if (this.fileByteArray) {
          switch (fileMeta.docName.substring(fileMeta.docName.indexOf('.') + 1)) {
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
        }
        this.disableNavigation = false;
      }
    );
  }

  /**
   *@description method to preview last available file.
   *
   * @memberof FileUploadComponent
   */
  viewLastFile() {
    this.fileIndex = this.users[0].files[0].documentsMetaData.length - 1;
    this.viewFile(this.users[0].files[0].documentsMetaData[this.fileIndex]);
  }

  /**
   *@description method gets called when a file has been uploaded from the html.
   *
   * @param {*} event
   * @memberof FileUploadComponent
   */
  handleFileInput(event) {
    console.log('event file input', event);

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
            this.displayMessage('Error', this.fileUploadLanguagelabels.uploadDocuments.msg1);
            this.disableNavigation = false;
          }
        } else {
          this.displayMessage('Error', this.fileUploadLanguagelabels.uploadDocuments.msg5);
          this.disableNavigation = false;
        }
      }
    }
    if (!allowedFileUploaded) {
      this.displayMessage('Error', this.fileUploadLanguagelabels.uploadDocuments.msg3);
      this.disableNavigation = false;
    }
  }

  /**
   *@description method to get base 64 of a file
   *
   * @param {*} file
   * @returns
   * @memberof FileUploadComponent
   */
  getBase64(file) {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.readAsDataURL(file);
      reader.onload = () => resolve(reader.result);
      reader.onerror = error => reject(error);
    });
  }

  /**
   *@description method called when the docuemnt type option has been changed in a document category
   *
   * @param {*} event
   * @param {number} index
   * @memberof FileUploadComponent
   */
  selectChange(event, index: number) {
    this.documentCategory = event.source.placeholder;
    this.documentType = event.source.value;
    console.log('document type', this.documentType);

    this.documentIndex = index;
  }

  /**
   *@description method called when the docuemnt type option has been opened in a document category
   *
   * @param {*} event
   * @param {number} index
   * @memberof FileUploadComponent
   */
  openedChange(event, index: number) {
    this.documentCategory = this.LOD[index].code;
    this.documentIndex = index;
  }

  onFilesChange(fileList: FileList) {}
  /**
   *@description method to remove the preview of a file.
   *
   * @memberof FileUploadComponent
   */
  removeFilePreview() {
    this.fileName = '';
    this.fileUrl = this.domSanitizer.bypassSecurityTrustResourceUrl('');
  }

  /**
   *@description method to set the Json string required to send the file to server.
   *
   * @param {*} event
   * @memberof FileUploadComponent
   */
  setJsonString(event) {
    this.documentUploadRequestBody.docCatCode = this.documentCategory;
    this.documentUploadRequestBody.langCode = localStorage.getItem('langCode');
    this.documentUploadRequestBody.docTypCode = this.documentType;
    this.documentRequest = new RequestModel(appConstants.IDS.documentUpload, this.documentUploadRequestBody, {});
  }

  /**
   *@description method to send the file to the server.
   *
   * @param {*} event
   * @memberof FileUploadComponent
   */
  sendFile(event) {
    this.formData.append(appConstants.DOCUMENT_UPLOAD_REQUEST_DTO_KEY, JSON.stringify(this.documentRequest));
    this.formData.append(appConstants.DOCUMENT_UPLOAD_REQUEST_DOCUMENT_KEY, event.target.files.item(0));
    console.log('formdata', this.formData);

    this.dataStroage.sendFile(this.formData, this.users[0].preRegId).subscribe(
      response => {
        if (response['errors'] == null) {
          this.updateUsers(response);
        } else {
          this.displayMessage('Error', this.errorlabels.error);
        }
      },
      error => {
        this.displayMessage('Error', this.fileUploadLanguagelabels.uploadDocuments.msg7);
      },
      () => {
        this.fileInputVariable.nativeElement.value = '';
        this.disableNavigation = false;
      }
    );
    this.formData = new FormData();
  }

  /**
   *@description method to update the users array after a file has been uploaded.
   *
   * @param {*} fileResponse
   * @memberof FileUploadComponent
   */
  updateUsers(fileResponse) {
    let i = 0;
    this.file = new FileModel();
    this.userFile[0] = this.file;
    this.userFile[0].docCatCode = fileResponse.response.docCatCode;
    this.userFile[0].doc_file_format = fileResponse.response.docFileFormat;
    this.userFile[0].documentId = fileResponse.response.docId;
    this.userFile[0].docName = fileResponse.response.docName;
    this.userFile[0].docTypCode = fileResponse.response.docTypCode;
    this.userFile[0].multipartFile = this.fileByteArray;
    this.userFile[0].prereg_id = this.users[0].preRegId;

    for (let file of this.users[0].files.documentsMetaData) {
      if (file.docCatCode == this.userFile[0].docCatCode || file.docCatCode == null || file.docCatCode == '') {
        this.users[this.step].files.documentsMetaData[i] = this.userFile[0];
        break;
      }
      i++;
    }
    if (i == this.users[0].files.documentsMetaData.length) {
      this.users[this.step].files.documentsMetaData.push(this.userFile[0]);
    }
    this.userFile = [];
    this.registration.updateUser(this.step, this.users[this.step]);
  }

  openFile() {
    const file = new Blob(this.users[0].files[0][0].multipartFile, { type: 'application/pdf' });
    const fileUrl = URL.createObjectURL(file);
    window.open(fileUrl);
  }

  /**
   *@description method called when a same as option has been selected.
   *
   * @param {*} event
   * @memberof FileUploadComponent
   */
  sameAsChange(event) {
    this.disableNavigation = true;
    if (event.value == '') {
      this.sameAsselected = false;
    } else {
      this.dataStroage.copyDocument(event.value, this.users[0].preRegId).subscribe(
        response => {
          if (response['errors'] == null) {
            this.registration.setSameAs(event.value);
            this.removePOADocument();
            this.updateUsers(response);
          } else {
            this.sameAs = this.registration.getSameAs();
            this.sameAsselected = false;
            this.displayMessage('Error', this.fileUploadLanguagelabels.uploadDocuments.msg9);
          }
        },
        err => {
          this.displayMessage('Error', this.fileUploadLanguagelabels.uploadDocuments.msg8);
        }
      );
      this.sameAsselected = true;
    }
    this.disableNavigation = false;
  }

  /**
   *@description method to remove the POA document from users array when same as option has been selected.
   *
   * @memberof FileUploadComponent
   */
  removePOADocument() {
    this.userFiles = new FilesModel();
    let i = 0;
    if (this.users[0].files.documentsMetaData) {
      for (let file of this.users[0].files.documentsMetaData) {
        if (file.docCatCode == 'POA') {
          this.users[0].files.documentsMetaData.splice(i, 1);
        }
        i++;
      }
    }
  }

  ifDisabled(category) {
    this.users[0].files[0].documentsMetaData.forEach(element => {
      if ((element.docCatCode = category)) {
        return true;
      }
    });
    return false;
  }

  /**
   *@description method called when back button has been clicked.
   *
   * @memberof FileUploadComponent
   */
  onBack() {
    this.registration.changeMessage({ modifyUser: 'true' });
    const arr = this.router.url.split('/');
    arr.pop();
    arr.push('demographic');
    const url = arr.join('/');
    this.router.navigateByUrl(url);
  }

  /**
   *@description method called when next button has been clicked.
   *
   * @memberof FileUploadComponent
   */
  onNext() {
    localStorage.setItem('modifyDocument', 'false');
    const arr = this.router.url.split('/');
    arr.pop();
    arr.push('summary');
    arr.push('preview');
    const url = arr.join('/');
    this.router.navigateByUrl(url);
  }

  /**
   *@description method to preview the next file in the html page
   *
   * @param {number} fileIndex
   * @memberof FileUploadComponent
   */
  nextFile(fileIndex: number) {
    this.fileIndex = fileIndex + 1;
    this.viewFileByIndex(this.fileIndex);
  }

  /**
   *@description method to preview the previous file in the html page
   *
   * @param {number} fileIndex
   * @memberof FileUploadComponent
   */
  previousFile(fileIndex: number) {
    this.fileIndex = fileIndex - 1;
    this.viewFileByIndex(this.fileIndex);
  }

  /**
   *@description method to set and display error message.
   *
   * @param {string} title
   * @param {string} message
   * @memberof FileUploadComponent
   */
  displayMessage(title: string, message: string) {
    const messageObj = {
      case: 'MESSAGE',
      title: title,
      message: message
    };
    this.openDialog(messageObj, '250px');
  }
  /**
   *@description method to open dialog box to show the error message
   *
   * @param {*} data
   * @param {*} width
   * @returns
   * @memberof FileUploadComponent
   */
  openDialog(data, width) {
    const dialogRef = this.dialog.open(DialougComponent, {
      width: width,
      data: data
    });
    return dialogRef;
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

export interface Applicants {
  bookingMetadata?: string;
  preRegistrationId: string;
  demographicMetadata: DemographicMetaData;
  statusCode?: string;
}
export interface FullName {
  language: string;
  value: string;
}
export interface ProofOfAddress {
  docId: string;
  docName: string;
  docCatCode: string;
  docTypCode: string;
  docFileFormat?: string;
}

export interface DemographicMetaData {
  fullname?: FullName[];
  postalCode?: string;
  proofOfAddress?: ProofOfAddress;
}
