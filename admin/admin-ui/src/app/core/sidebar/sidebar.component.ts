import { Component, OnInit } from '@angular/core';

import * as appConstants from '../../app.constants';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent implements OnInit {

  display = true;
  selectedItem = 0;
  menuItems = [];

  constructor() { }

  ngOnInit() {
    this.menuItems = appConstants.menuItems;
  }

  showMenu() {
    this.display = true;
  }

  hideMenu() {
    this.display = false;
  }

  changeSelection(index: number) {
    this.selectedItem = index;
  }

}
