import { Component, OnInit } from '@angular/core';
import { DataStorageService } from 'src/app/shared/data-storage.service';
import { RegistrationService } from '../registration.service';
import { Router, ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-preview',
  templateUrl: './preview.component.html',
  styleUrls: ['./preview.component.css']
})
export class PreviewComponent implements OnInit {

  previewData : any;
  secondaryLanguagelabels : any ;
  secondaryLanguage;
  preRegId: string;
  files = [];

  constructor(private dataStorageService: DataStorageService, private route: ActivatedRoute, private router: Router, private registrationService: RegistrationService ) { }

  ngOnInit() {

    // this.preRegId = this.registrationService.getUsers()[this.registrationService.getUsers().length - 1].preRegId;
    // console.log(this.preRegId)
    this.dataStorageService.getPreviewData('25368956035901').subscribe(response => {
      console.log(response);
      this.previewData = response['response'][0].demographicDetails.identity;
      console.log(this.previewData);
      if (this.previewData['fullName'][1].language === 'arb') {
        this.secondaryLanguage = 'ar';
      }
    this.dataStorageService.getSecondaryLanguageLabels(this.secondaryLanguage || this.previewData['fullName'][1].language).subscribe(response => {
      this.secondaryLanguagelabels = response['preview'];
      console.log(this.secondaryLanguagelabels);
    })
    });
    this.files = this.registrationService.getUsers()[this.registrationService.getUsers().length - 1].files[0];
  }

  modifyDemographic() {
    const routeParams = this.router.url.split('/');
    this.router.navigate([routeParams[1], routeParams[2], 'demographic']);
  }

  modifyDocument() {
    this.navigateBack();
  }

  navigateDashboard() {
    const routeParams = this.router.url.split('/');
    sessionStorage.setItem('newApplicant', 'true');
    this.router.navigate([routeParams[1], routeParams[2], 'demographic']);
  }

  navigateBack() {
    const routeParams = this.router.url.split('/');
    this.router.navigate([routeParams[1], routeParams[2], 'file-upload']);
  }

  navigateNext() {
    this.router.navigate(['../pick-center'], {relativeTo: this.route});
  }

}
