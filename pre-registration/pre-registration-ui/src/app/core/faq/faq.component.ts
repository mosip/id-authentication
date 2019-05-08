import { Component, OnInit } from '@angular/core';
import { DataStorageService } from '../services/data-storage.service';

@Component({
  selector: 'app-faq',
  templateUrl: './faq.component.html',
  styleUrls: ['./faq.component.css']
})
export class FaqComponent implements OnInit {

  langCode = '';
  data = [];
  answerTranslation = '';

  constructor(private dataStorageService: DataStorageService) { }

  ngOnInit() {
    this.langCode = localStorage.getItem('langCode');
    this.dataStorageService.getSecondaryLanguageLabels(this.langCode).subscribe(response => {
      this.data = response['faq']['questions'];
      this.answerTranslation = response['faq']['answer'];
    })
  }

}
