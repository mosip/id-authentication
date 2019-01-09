import { Component, OnInit } from '@angular/core';
import { DataStorageService } from 'src/app/shared/data-storage.service';
import { RegistrationService } from '../registration.service';

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

  constructor(private dataStorageService: DataStorageService, private registrationService: RegistrationService ) { }

  ngOnInit() {

    this.preRegId = this.registrationService.getUsers()[this.registrationService.getUsers().length - 1].preRegId;
    console.log(this.preRegId)
    this.dataStorageService.getPreviewData(this.preRegId).subscribe(response => {
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



  }

}
