import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material';
import { DataStorageService } from 'src/app/core/services/data-storage.service';
import { RegistrationCentre } from './registration-center-details.model';
import { Router, ActivatedRoute } from '@angular/router';

import { UserModel } from 'src/app/shared/models/demographic-model/user.modal';
import { SharedService } from '../booking.service';
import { RegistrationService } from 'src/app/core/services/registration.service';
import { TranslateService } from '@ngx-translate/core';
import Utils from 'src/app/app.util';
import { ConfigService } from 'src/app/core/services/config.service';
import * as appConstants from './../../../app.constants';

@Component({
  selector: 'app-center-selection',
  templateUrl: './center-selection.component.html',
  styleUrls: ['./center-selection.component.css']
})
export class CenterSelectionComponent implements OnInit {
  // @ViewChild(TimeSelectionComponent)
  // timeSelectionComponent: TimeSelectionComponent;

  REGISTRATION_CENTRES: RegistrationCentre[] = [];
  // displayedColumns: string[] = ['select', 'name', 'addressLine1', 'contactPerson', 'centerTypeCode', 'contactPhone'];
  // dataSource = new MatTableDataSource<RegistrationCentre>(REGISTRATION_CENTRES);
  // selection = new SelectionModel<RegistrationCentre>(true, []);
  searchClick: boolean = true;

  locationTypes = [];

  locationType = null;
  searchText = null;
  showTable = false;
  selectedCentre = null;
  showMap = false;
  showMessage = false;
  enableNextButton = false;
  bookingDataList = [];
  step = 0;
  showDescription = false;
  mapProvider = 'OSM';
  searchTextFlag = false;
  displayMessage = 'Showing nearby registration centers';
  users: UserModel[];

  constructor(
    private dialog: MatDialog,
    private service: SharedService,
    private dataService: DataStorageService,
    private router: Router,
    private route: ActivatedRoute,
    private registrationService: RegistrationService,
    private translate: TranslateService,
    private configService: ConfigService
  ) {
    this.translate.use(localStorage.getItem('langCode'));
  }

  ngOnInit() {
    this.REGISTRATION_CENTRES = [];
    // this.dataSource.data = REGISTRATION_CENTRES;
    this.selectedCentre = null;
    //  this.getLocation();
    this.dataService.getLocationTypeData().subscribe(response => {
      this.locationTypes = response['response']['locations'];
      console.log(this.locationTypes);
    });
    this.users = this.service.getNameList();
    this.getRecommendedCenters();
  }

  getRecommendedCenters() {
    const pincodes = [];
    this.users.forEach(user => {
      pincodes.push(user['postalCode']);
    });
    this.dataService
      .recommendedCenters(
        localStorage.getItem('langCode'),
        this.configService.getConfigByKey(appConstants.CONFIG_KEYS.preregistration_recommended_centers_locCode),
        pincodes
      )
      .subscribe(response => {
        console.log(response);
        if (!response['errors']) this.displayResults(response['response']);
      });
  }

  setSearchClick(flag: boolean) {
    this.searchClick = flag;
  }
  onSubmit() {
    this.searchTextFlag = true;
    if (this.searchText.length !== 0 || this.searchText !== null) {
      this.displayMessage = `Searching results for ${this.searchText} ....`;
    } else {
      this.displayMessage = '';
    }
    // if(REGISTRATION_CENTRES.length === 0){
    //   this.displayMessage = `No results found`;
    // }
  }
  setStep(index: number) {
    this.step = index;
  }

  nextStep() {
    this.step++;
    this.showDescription = true;
  }

  prevStep() {
    this.step--;
  }

  showResults() {
    console.log(this.locationType, this.searchText);
    this.REGISTRATION_CENTRES = [];
    if (this.locationType !== null && this.searchText !== null) {
      this.showMap = false;
      this.dataService
        .getRegistrationCentersByName(this.locationType.locationHierarchylevel, this.searchText)
        .subscribe(
          response => {
            console.log(response);
            if (!response['errors']) {
              this.displayResults(response['response']);
            } else {
              this.showMessage = true;
            }
          },
          error => {
            this.showMessage = true;
          }
        );
    }
  }

  plotOnMap() {
    this.showMap = true;
    this.service.changeCoordinates([Number(this.selectedCentre.longitude), Number(this.selectedCentre.latitude)]);
  }

  selectedRow(row) {
    this.selectedCentre = row;
    this.enableNextButton = true;
    console.log('row', row);
    if (Object.keys(this.selectedCentre).length !== 0) {
      this.plotOnMap();
    }
  }

  getLocation() {
    //  this.dataSource.data = [];
    this.REGISTRATION_CENTRES = [];
    if (navigator.geolocation) {
      this.showMap = false;
      navigator.geolocation.getCurrentPosition(position => {
        console.log(position);
        this.dataService.getNearbyRegistrationCenters(position.coords).subscribe(
          response => {
            console.log(response);
            if (response['errors'].length === 0 && response['response']['registrationCenters'].length !== 0) {
              this.displayResults(response['response']);
            } else {
              this.showMessage = true;
            }
          },
          error => {
            this.showMessage = true;
          }
        );
      });
    } else {
      alert('Location not suppored in this browser');
    }
  }

  changeTimeFormat(time: string): string | Number {
    let inputTime = time.split(':');
    let formattedTime: any;
    if (Number(inputTime[0]) < 12) {
      formattedTime = inputTime[0];
      formattedTime += ':' + inputTime[1] + ' am';
    } else {
      formattedTime = Number(inputTime[0]) - 12;
      formattedTime += ':' + inputTime[1] + ' pm';
    }

    return formattedTime;
  }

  dispatchCenterCoordinatesList() {
    const coords = [];
    this.REGISTRATION_CENTRES.forEach(centre => {
      const data = {
        id: centre.id,
        latitude: Number(centre.latitude),
        longitude: Number(centre.longitude)
      };
      coords.push(data);
    });
    this.service.listOfCenters(coords);
  }

  routeNext() {
    this.registrationService.setRegCenterId(this.selectedCentre.id);
    this.users.forEach(user => {
      this.service.updateRegistrationCenterData(user.preRegId, this.selectedCentre);
    });
    console.log(this.users);
    this.router.navigate(['../pick-time'], { relativeTo: this.route });
  }

  routeDashboard() {
    // const routeParams = this.router.url.split('/');
    const url = Utils.getURL(this.router.url, 'dashboard', 3);
    this.router.navigateByUrl(url);
  }

  routeBack() {
    let url = '';
    if (this.registrationService.getUsers().length === 0) {
      // const routeParams = this.router.url.split('/');
      // console.log('route params', routeParams);
      url = Utils.getURL(this.router.url, 'dashboard', 3);

      // this.router.navigateByUrl(`dashboard`);
    } else {
      // const routeParams = this.router.url.split('/');
      url = Utils.getURL(this.router.url, 'summary/preview', 2);
      // this.router.navigate([routeParams[1], 'summary', 'preview']);
    }
    this.router.navigateByUrl(url);
  }

  displayResults(response: any) {
    this.REGISTRATION_CENTRES = response['registrationCenters'];
    // this.dataSource.data = REGISTRATION_CENTRES;
    // console.log(this.dataSource.data);
    this.showTable = true;
    if (this.REGISTRATION_CENTRES) {
      this.selectedRow(this.REGISTRATION_CENTRES[0]);
      this.dispatchCenterCoordinatesList();
    }
  }
}
