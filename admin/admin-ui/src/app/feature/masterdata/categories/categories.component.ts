import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { DataStorageService } from '../../../shared/services/data-storage.service';

@Component({
  selector: 'app-categories',
  templateUrl: './categories.component.html',
  styleUrls: ['./categories.component.css']
})
export class CategoriesComponent implements OnInit {

  data = [];

  constructor(private router: Router, private dataStorageService: DataStorageService) { }

  ngOnInit() {
    this.getCards();
  }

  getCards() {
    this.dataStorageService.getMasterDataCards().subscribe(response => {
      this.data = response['response']['masterdata'];
    });
  }

  loadData(item: any) {
    console.log(item);
    this.router.navigateByUrl(`admin/dashboard/masterdata/${item.dataCode}`);
  }

}
