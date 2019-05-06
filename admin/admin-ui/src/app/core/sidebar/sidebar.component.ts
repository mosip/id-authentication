import { Component, OnInit } from '@angular/core';

import * as appConstants from '../../app.constants';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent implements OnInit {

  display = false;
  selectedItem = 0;
  menuItems = [];

  constructor() { }

  ngOnInit() {
    this.menuItems = appConstants.menuItems;
  }

  showMenu() {
    this.display = true;
    console.log(this.display);
    document.getElementById('sidebar').style.display = 'block';
  }

  hideMenu() {
    this.display = false;
    console.log(this.display);
    document.getElementById('sidebar').style.display = 'none';
  }

  changeSelection(index: number) {
    this.selectedItem = index;
  }

}
