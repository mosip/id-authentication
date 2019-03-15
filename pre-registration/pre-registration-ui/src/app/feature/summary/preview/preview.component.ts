import { Component, OnInit } from '@angular/core';
import { DataStorageService } from 'src/app/core/services/data-storage.service';
import { Router, ActivatedRoute } from '@angular/router';
import { UserModel } from 'src/app/shared/models/demographic-model/user.modal';
import { RegistrationService } from 'src/app/core/services/registration.service';
import { TranslateService } from '@ngx-translate/core';
import Utils from 'src/app/app.util';

@Component({
  selector: 'app-preview',
  templateUrl: './preview.component.html',
  styleUrls: ['./preview.component.css']
})
export class PreviewComponent implements OnInit {
  previewData: any;
  secondaryLanguagelabels: any;
  primaryLanguage;
  secondaryLanguage;
  user: UserModel;
  files = [];

  constructor(
    private dataStorageService: DataStorageService,
    private route: ActivatedRoute,
    private router: Router,
    private registrationService: RegistrationService,
    private translate: TranslateService
  ) {
    this.translate.use(localStorage.getItem('langCode'));
    localStorage.setItem('modifyDocument', 'false');
  }

  ngOnInit() {
    this.primaryLanguage = localStorage.getItem('langCode');
    this.secondaryLanguage = localStorage.getItem('secondaryLangCode');
    this.user = { ...this.registrationService.getUser(this.registrationService.getUsers().length - 1) };
    console.log(this.user);
    this.previewData = this.user.request.demographicDetails.identity;
    this.calculateAge();
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
    this.previewData.gender[0].name = this.locCodeToName(
      this.previewData.gender[0].value,
      this.previewData.gender[0].language
    );
    this.previewData.gender[1].name = this.locCodeToName(
      this.previewData.gender[1].value,
      this.previewData.gender[1].language
    );
    console.log(this.previewData);
    this.getSecondaryLanguageLabels();
    this.files = this.user.files[0];
  }

  getSecondaryLanguageLabels() {
    this.dataStorageService
      .getSecondaryLanguageLabels(localStorage.getItem('secondaryLangCode'))
      .subscribe(response => {
        this.secondaryLanguagelabels = response['preview'];
        console.log(this.secondaryLanguagelabels);
      });
  }

  calculateAge() {
    const now = new Date();
    const born = new Date(this.previewData.dateOfBirth);
    const years = Math.floor((now.getTime() - born.getTime()) / (365.25 * 24 * 60 * 60 * 1000));
    this.previewData.age = years;
  }

  modifyDemographic() {
    const url = Utils.getURL(this.router.url, 'demographic', 2);
    this.registrationService.changeMessage({ modifyUserFromPreview: 'true' });
    this.router.navigateByUrl(url);
    // const routeParams = this.router.url.split('/');

    // this.router.navigate([routeParams[1], routeParams[2], 'demographic']);
    // localStorage.setItem('newApplicant', 'false');
  }

  modifyDocument() {
    localStorage.setItem('modifyDocument', 'true');
    this.navigateBack();
  }

  private locCodeToName(locationCode: string, language: string): string {
    const locations = this.user.location;
    const locationName = locations.filter(
      location => location.languageCode === language && location.valueCode === locationCode
    );
    return locationName[0].valueName;
  }

  enableContinue(): boolean {
    let flag = false;
    if (this.files.length === 4) {
      flag = true;
    } else if (this.files.length === 3 && this.registrationService.getSameAs() !== '') {
      flag = true;
    } else {
      flag = false;
    }
    return flag;
  }

  navigateDashboard() {
    // const routeParams = this.router.url.split('/');
    // this.router.navigate(['dashboard', routeParams[2]]);
    // sessionStorage.setItem('newApplicant', 'true');
    // this.router.navigate(['../demographic'], { relativeTo: this.route });
    localStorage.setItem('newApplicant', 'false');
    this.registrationService.changeMessage({ modifyUserFromPreview: 'false' });
    const url = Utils.getURL(this.router.url, 'demographic', 2);
    this.router.navigateByUrl(url);
  }

  navigateBack() {
    // const arr = this.router.url.split('/');
    // arr.pop();
    // arr.push('file-upload');
    // const url = arr.join('/');
    // this.router.navigateByUrl(url);

    const url = Utils.getURL(this.router.url, 'file-upload', 2);
    this.router.navigateByUrl(url);

    // this.router.navigate(['../file-upload'], { relativeTo: this.route });
  }

  navigateNext() {
    const url = Utils.getURL(this.router.url, 'booking/pick-center', 2);
    this.router.navigateByUrl(url);
  }
}
