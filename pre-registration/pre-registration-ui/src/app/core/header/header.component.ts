import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../auth/auth.service';

export interface Language {
  value: string;
  viewValue: string;
}

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {
  languages: Language[] = [
    { value: 'English', viewValue: 'English' },
    { value: 'French', viewValue: 'French' },
    { value: 'Arabic', viewValue: 'Arabic' }
  ];

  constructor(public authService: AuthService) {}

  ngOnInit() {}

  onLogout() {
    console.log('Logging out');
  }
}
