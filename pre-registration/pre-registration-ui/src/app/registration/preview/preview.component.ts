import { Component, OnInit } from '@angular/core';
import { DataStorageService } from 'src/app/shared/data-storage.service';

@Component({
  selector: 'app-preview',
  templateUrl: './preview.component.html',
  styleUrls: ['./preview.component.css']
})
export class PreviewComponent implements OnInit {

  previewData = {};
  secondaryLanguagelabels = {};
  secondaryLanguage;

  constructor(private dataStorageService: DataStorageService ) { }

  ngOnInit() {

    this.dataStorageService.getPreviewData('83643927312961').subscribe(response => {
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
