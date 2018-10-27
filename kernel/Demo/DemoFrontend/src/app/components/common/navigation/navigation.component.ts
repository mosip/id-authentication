import {Component} from '@angular/core';
import {Router} from '@angular/router';

@Component({selector: 'navigation', templateUrl: 'navigation.template.html'})

export class NavigationComponent  {

  constructor(private router : Router) {}

  activeRoute(routename : string) : boolean {
    return this
      .router
      .url
      .indexOf(routename) > -1;
  }

}
