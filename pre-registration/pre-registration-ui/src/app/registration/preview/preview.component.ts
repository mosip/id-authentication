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
    this.user = { ...this.registrationService.getUser(this.registrationService.getUsers().length - 1) };
    console.log(this.user);
    this.previewData = this.user.request.demographicDetails.identity;
    const now = new Date();
    const born = new Date(this.previewData.dateOfBirth[0].value);
    const years = Math.floor((now.getTime() - born.getTime()) / (365.25 * 24 * 60 * 60 * 1000));
    this.previewData.age = years;
    let address =
      this.previewData.addressLine1[0].value +
      (this.previewData.addressLine2[0].value ? ', ' + this.previewData.addressLine2[0].value : '') +
      (this.previewData.addressLine3[0].value ? ', ' + this.previewData.addressLine3[0].value : '');
    this.previewData.primaryAddress = address;
    address =
      this.previewData.addressLine1[1].value +
      (this.previewData.addressLine2[1].value ? ', ' + this.previewData.addressLine2[1].value : '') +
      (this.previewData.addressLine3[1].value ? ', ' + this.previewData.addressLine3[1].value : '');
    this.previewData.secondaryAddress = address;
    this.previewData.region[0].name = this.locCodeToName(
      this.previewData.region[0].value,
      this.previewData.region[0].language
    );
    this.previewData.region[1].name = this.locCodeToName(
      this.previewData.region[1].value,
      this.previewData.region[1].language
    );
    this.previewData.province[0].name = this.locCodeToName(
      this.previewData.province[0].value,
      this.previewData.province[0].language
    );
    this.previewData.province[1].name = this.locCodeToName(
      this.previewData.province[1].value,
      this.previewData.province[1].language
    );
    this.previewData.city[0].name = this.locCodeToName(
      this.previewData.city[0].value,
      this.previewData.city[0].language
    );
    this.previewData.city[1].name = this.locCodeToName(
      this.previewData.city[1].value,
      this.previewData.city[1].language
    );
    this.previewData.localAdministrativeAuthority[0].name = this.locCodeToName(
      this.previewData.localAdministrativeAuthority[0].value,
      this.previewData.localAdministrativeAuthority[0].language
    );
    this.previewData.localAdministrativeAuthority[1].name = this.locCodeToName(
      this.previewData.localAdministrativeAuthority[1].value,
      this.previewData.localAdministrativeAuthority[1].language
    );
    console.log(this.previewData);
    if (this.previewData['fullName'][1].language === 'ara') {
      this.secondaryLanguage = 'ara';
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

  private locCodeToName(locationCode: string, language: string): string {
    const locations = this.user.location;
    const locationName = locations.filter(
      location => location.languageCode === language && location.valueCode === locationCode
    );
    return locationName[0].valueCode;
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
