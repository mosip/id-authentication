import { Component, OnInit } from '@angular/core';
import { DataStorageService } from 'src/app/shared/data-storage.service';
import { RegistrationService } from '../registration.service';
import { Router, ActivatedRoute } from '@angular/router';
import { UserModel } from '../demographic/modal/user.modal';

@Component({
  selector: 'app-preview',
  templateUrl: './preview.component.html',
  styleUrls: ['./preview.component.css']
})
export class PreviewComponent implements OnInit {
  previewData: any;
  secondaryLanguagelabels: any;
  secondaryLanguage;
  user: UserModel;
  files = [];

  constructor(
    private dataStorageService: DataStorageService,
    private route: ActivatedRoute,
    private router: Router,
    private registrationService: RegistrationService
  ) {}

  ngOnInit() {
   this.user = this.registrationService.getUsers()[this.registrationService.getUsers().length - 1]; 
   console.log(this.user);
      this.previewData = this.user.request.demographicDetails.identity;
      const now = new Date();
      const born = new Date(this.previewData.dateOfBirth[0].value);
      const years = Math.floor((now.getTime() - born.getTime()) / (365.25 * 24 * 60 * 60 * 1000));
      this.previewData.age = years;
      let address = this.previewData.addressLine1[0].value 
                      + (this.previewData.addressLine2[0].value ? ', ' + this.previewData.addressLine2[0].value : '')
                      + (this.previewData.addressLine3[0].value ? ', ' + this.previewData.addressLine3[0].value : '');
      this.previewData.primaryAddress = address;
      address = this.previewData.addressLine1[1].value 
                  + (this.previewData.addressLine2[1].value ? ', ' + this.previewData.addressLine2[1].value : '')
                  + (this.previewData.addressLine3[1].value ? ', ' + this.previewData.addressLine3[1].value : '');
      this.previewData.secondaryAddress = address;
      console.log(this.previewData);
      if (this.previewData['fullName'][1].language === 'ARB') {
        this.secondaryLanguage = 'ar';
      }
    this.dataStorageService
      .getSecondaryLanguageLabels(this.secondaryLanguage || this.previewData['fullName'][1].language)
      .subscribe(response => {
        this.secondaryLanguagelabels = response['preview'];
        console.log(this.secondaryLanguagelabels);
      });
    this.files = this.registrationService.getUsers()[this.registrationService.getUsers().length - 1].files[0];
  }

  modifyDemographic() {
    const routeParams = this.router.url.split('/');
    this.router.navigate([routeParams[1], routeParams[2], 'demographic']);
    localStorage.setItem('newApplicant', 'false');
    this.registrationService.changeMessage({ modifyUser: 'true' });
  }

  modifyDocument() {
    this.navigateBack();
  }

  navigateDashboard() {
    const routeParams = this.router.url.split('/');
    // this.router.navigate(['dashboard', routeParams[2]]);
    this.router.navigate(['../demographic'], { relativeTo: this.route });
    // sessionStorage.setItem('newApplicant', 'true');
    localStorage.setItem('newApplicant', 'false');
    this.registrationService.changeMessage({ modifyUser: 'false' });
  }

  navigateBack() {
    this.router.navigate(['../file-upload'], { relativeTo: this.route });
  }

  navigateNext() {
    this.router.navigate(['../pick-center'], { relativeTo: this.route });
  }
}
