import { Component, OnInit } from '@angular/core';

import { RegistrationService } from '../registration.service';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-file-upload',
  templateUrl: './file-upload.component.html',
  styleUrls: ['./file-upload.component.css']
})
export class FileUploadComponent implements OnInit {
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
  POAFU = false;
  POIFU = false;
  POBFU = false;
  PORFU = false;

  disabled = true;
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

  documents = ['POA', 'POI', 'POB', 'POR'];

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

  ngOnInit() {}

  handleFileInput(files: FileList) {
    const formData = new FormData();
    formData.append('documentString', JSON.stringify(this.documentString));
    formData.append('file', files.item(0));
    this.registration.sendFile(formData).subscribe(response => {
      console.log(response);
    });
  }

  selectChange(event) {
    console.log(event.source._id);
    const id = event.source._id;
    if (id === 'POA') {
      this.POAFU = true;
    }
    if (id === 'POI') {
      this.POIFU = true;
    }
    if (id === 'POB') {
      this.POBFU = true;
    }
    if (id === 'POR') {
      this.PORFU = true;
    }
  }
}
