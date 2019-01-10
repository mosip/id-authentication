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
  previewData: any;
  secondaryLanguagelabels: any;
  secondaryLanguage;
  preRegId: string;

  constructor(
    private dataStorageService: DataStorageService,
    private route: ActivatedRoute,
    private router: Router,
    private registrationService: RegistrationService
  ) {}

  ngOnInit() {
    this.preRegId = this.registrationService.getUsers()[this.registrationService.getUsers().length - 1].preRegId;
    console.log(this.preRegId);
    this.dataStorageService.getPreviewData(this.preRegId).subscribe(response => {
      console.log(response);
      this.previewData = response['response'][0].demographicDetails.identity;
      console.log(this.previewData);
      if (this.previewData['fullName'][1].language === 'arb') {
        this.secondaryLanguage = 'ar';
      }
      this.dataStorageService
        .getSecondaryLanguageLabels(this.secondaryLanguage || this.previewData['fullName'][1].language)
        .subscribe(response => {
          this.secondaryLanguagelabels = response['preview'];
          console.log(this.secondaryLanguagelabels);
        });
    });
  }

  navigateDashboard() {
    const routeParams = this.router.url.split('/');
    // this.router.navigate(['dashboard', routeParams[2]]);
    this.router.navigate(['../demographic'], { relativeTo: this.route });
    sessionStorage.setItem('newApplicant', 'true');
    sessionStorage.setItem('modifyUser', 'false');
  }

  navigateBack() {
    this.router.navigate(['../file-upload'], { relativeTo: this.route });
  }

  navigateNext() {
    this.router.navigate(['../pick-center'], { relativeTo: this.route });
  }
}
