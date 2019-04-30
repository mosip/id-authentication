import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material';

@Component({
  selector: 'app-error',
  templateUrl: './error.component.html',
  styleUrls: ['./error.component.css']
})
export class ErrorComponent implements OnInit {
  constructor(public dialog: MatDialog) {}

  ngOnInit() {
    // if (this.authService.isAuthenticated()) this.authService.onLogout();
  }
}
