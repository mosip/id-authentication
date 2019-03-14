import { Component, OnInit, OnChanges, Input } from '@angular/core';

@Component({
  selector: 'app-stepper',
  templateUrl: './stepper.component.html',
  styleUrls: ['./stepper.component.css']
})
export class StepperComponent implements OnInit, OnChanges {

  @Input() componentName: string;

  classes = {
    step1: {
      p: [],
      icon: [],
      line: []
    },
    step2: {
      p: [],
      icon: [],
      line: []
    },
    step3: {
      p: [],
      icon: [],
      line: []
    },
    step4: {
      p: [],
      icon: []
    }
  };

  constructor() { }

  ngOnInit() {
  }

  ngOnChanges() {
    console.log(this.componentName);
    if (this.componentName === 'DemographicComponent') {
      this.classes.step1.p = ['active'];
      this.classes.step1.icon = ['inline-icon', 'inline-icon-background-active'];
      this.classes.step1.line = ['progress', 'progress-active'];
      this.classes.step2.p = ['incomplete'];
      this.classes.step2.icon = ['inline-icon', 'inline-icon-background-incomplete'];
      this.classes.step2.line = ['progress', 'progress-incomplete'];
      this.classes.step3.p = ['incomplete'];
      this.classes.step3.icon = ['inline-icon', 'inline-icon-background-incomplete'];
      this.classes.step3.line = ['progress', 'progress-incomplete'];
      this.classes.step4.p = ['incomplete'];
      this.classes.step4.icon = ['inline-icon', 'inline-icon-background-incomplete'];
    } else if (this.componentName === 'FileUploadComponent' || this.componentName === 'PreviewComponent') {
      this.classes.step1.p = ['complete'];
      this.classes.step1.icon = ['inline-icon', 'inline-icon-background-complete'];
      this.classes.step1.line = ['progress', 'progress-complete'];
      this.classes.step2.p = ['active'];
      this.classes.step2.icon = ['inline-icon', 'inline-icon-background-active'];
      this.classes.step2.line = ['progress', 'progress-active'];
      this.classes.step3.p = ['incomplete'];
      this.classes.step3.icon = ['inline-icon', 'inline-icon-background-incomplete'];
      this.classes.step3.line = ['progress', 'progress-incomplete'];
      this.classes.step4.p = ['incomplete'];
      this.classes.step4.icon = ['inline-icon', 'inline-icon-background-incomplete'];
    } else if (this.componentName === 'CenterSelectionComponent' || this.componentName === 'TimeSelectionComponent') {
      this.classes.step1.p = ['complete'];
      this.classes.step1.icon = ['inline-icon', 'inline-icon-background-complete'];
      this.classes.step1.line = ['progress', 'progress-complete'];
      this.classes.step2.p = ['complete'];
      this.classes.step2.icon = ['inline-icon', 'inline-icon-background-complete'];
      this.classes.step2.line = ['progress', 'progress-complete'];
      this.classes.step3.p = ['active'];
      this.classes.step3.icon = ['inline-icon', 'inline-icon-background-active'];
      this.classes.step3.line = ['progress', 'progress-active'];
      this.classes.step4.p = ['incomplete'];
      this.classes.step4.icon = ['inline-icon', 'inline-icon-background-incomplete'];
    } else if (this.componentName === 'AcknowledgementComponent') {
      this.classes.step1.p = ['complete'];
      this.classes.step1.icon = ['inline-icon', 'inline-icon-background-complete'];
      this.classes.step1.line = ['progress', 'progress-complete'];
      this.classes.step2.p = ['complete'];
      this.classes.step2.icon = ['inline-icon', 'inline-icon-background-complete'];
      this.classes.step2.line = ['progress', 'progress-complete'];
      this.classes.step3.p = ['complete'];
      this.classes.step3.icon = ['inline-icon', 'inline-icon-background-complete'];
      this.classes.step3.line = ['progress', 'progress-complete'];
      this.classes.step4.p = ['complete'];
      this.classes.step4.icon = ['inline-icon', 'inline-icon-background-complete'];
    }
  }

}
