import { Component, OnInit } from '@angular/core';

import * as appConstants from '../../app.constants';
import { Router } from '@angular/router';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent implements OnInit {
  display = false;
  selectedItem = 0;
  menuItems = [];

  constructor(private router: Router) {}

  ngOnInit() {
    this.menuItems = appConstants.menuItems;
  }
  showMenu() {
    this.display = true;
    console.log(this.display);
    document.getElementById('sidebar').style.display = 'block';
    document.getElementById('toggle-forward').style.display = 'none';
    document.getElementById('toggle-back').style.display = 'block';
  }

  hideMenu() {
    this.display = false;
    console.log(this.display);
    document.getElementById('sidebar').style.display = 'none';
    document.getElementById('toggle-back').style.display = 'none';
    document.getElementById('toggle-forward').style.display = 'block';
  }
  changeSelection(index: number) {
    this.selectedItem = index;
    if (this.selectedItem === 1) {
      this.router.navigateByUrl('admin/assetmgmt/assetmanagement');
    } else if (this.selectedItem === 0) {
      this.router.navigateByUrl('admin/dashboard');
    } else if (this.selectedItem === 2) {
      this.router.navigateByUrl('admin/usermgmt/userregistration');
    }

  }
  getDisplay() {
    if (window.innerWidth > 770) {
      return 'block';
    }
  }
}
