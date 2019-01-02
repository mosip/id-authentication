import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-parent',
  templateUrl: './parent.component.html',
  styleUrls: ['./parent.component.css']
})
export class ParentComponent implements OnInit {

  componentName: string;

  constructor(private route: ActivatedRoute) {
    console.log(route);
  }

  ngOnInit() {
  }

  onActivate($event) {
    console.log($event);
    this.componentName = $event.route === undefined ? 'AcknowledgementComponent' : $event.route.component.name;
  }

}
