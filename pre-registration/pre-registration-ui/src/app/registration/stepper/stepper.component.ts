import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-stepper',
  templateUrl: './stepper.component.html',
  styleUrls: ['./stepper.component.css']
})
export class StepperComponent implements OnInit {

  @Input() componentName: string;

  constructor() { }

  ngOnInit() {
    console.log(this.componentName);
  }

}
