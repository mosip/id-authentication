import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-categories',
  templateUrl: './categories.component.html',
  styleUrls: ['./categories.component.css']
})
export class CategoriesComponent implements OnInit {

  data = [
    { displayValue: 'Registration Centers' },
    { displayValue: 'Assets' },
    { displayValue: 'Location Data' },
    { displayValue: 'Random Data' }
  ];

  constructor(private router: Router) { }

  ngOnInit() {
  }

  loadData(item: any) {
    console.log(item);
    this.router.navigateByUrl('admin/dashboard/masterdata');
  }

}
