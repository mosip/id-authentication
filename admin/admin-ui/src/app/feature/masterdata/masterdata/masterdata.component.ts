import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import * as appConstants from '../../../app.constants';
import { DataStorageService } from '../../../shared/services/data-storage.service';

@Component({
  selector: 'app-masterdata',
  templateUrl: './masterdata.component.html',
  styleUrls: ['./masterdata.component.css']
})
export class MasterdataComponent implements OnInit {

  tableData = [];

  constructor(private activatedRoute: ActivatedRoute, private dataStorageService: DataStorageService) {}

  ngOnInit() {
    this.activatedRoute.params.subscribe(params => {
      console.log(params);
      this.getData(params['id']);
    });
  }

  getData(id: string) {
    console.log(appConstants.code_url_mapping[id]);
    this.dataStorageService.getMasterData(appConstants.code_url_mapping[id].appendURL).subscribe(response => {
      console.log(response);
      this.tableData = response['response'][appConstants.code_url_mapping[id].parameterName];
    });
  }

}
