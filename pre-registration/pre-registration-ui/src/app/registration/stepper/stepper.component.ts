import { Component, OnInit, OnChanges, Input } from '@angular/core';

@Component({
  selector: 'app-stepper',
  templateUrl: './stepper.component.html',
  styleUrls: ['./stepper.component.css']
})
export class StepperComponent implements OnInit, OnChanges {

  @Input() componentName: string;

  constructor() { }

  ngOnInit() {
  }

  ngOnChanges() {
    console.log(this.componentName);
  }

}
