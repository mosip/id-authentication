import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-language-selection',
  templateUrl: './language-selection.component.html',
  styleUrls: ['./language-selection.component.css']
})

export class LanguageSelectionComponent implements OnInit {

  constructor() { }

  languages:  Language[]  =  [
    { value:  'none',  viewValue:  '--select--' },
    { value:  'English',  viewValue:  'English' },
    { value:  'French',  viewValue:  'French' },
    { value:  'Arabic',  viewValue:  'Arabic' }];


  selected: string =  this.languages[0].value;
  ngOnInit() {
    if (sessionStorage.getItem('lan') != null) {
      this.selected = sessionStorage.getItem('lan');
    } else {
     // console.log(sessionStorage.getItem('lan'));
    }
  }

  languageChange(event) {
    sessionStorage.setItem('lan', event.value);
  }
}

export  interface  Language  {
  value:  string;
  viewValue:  string;
} 