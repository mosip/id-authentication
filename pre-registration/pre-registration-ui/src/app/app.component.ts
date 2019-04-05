import { Component, OnInit } from '@angular/core';
import { HostListener } from '@angular/core';
import { AutoLogoutService } from 'src/app/core/services/auto-logout.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = "pre-registration";
  message: object;

  constructor(private autoLogout: AutoLogoutService) {}

  ngOnInit() {
    this.autoLogout.currentMessageAutoLogout.subscribe(res => {
      console.log(res);
    });
    this.autoLogout.changeMessage({ timerFired: false });
  }

  @HostListener('mouseover') onMouseOver() {
    this.autoLogout.setisActive(true);
  }
  @HostListener('click') onMouseClick() {
    this.autoLogout.setisActive(true);
  }
  @HostListener('keypress') onKeyPress() {
    this.autoLogout.setisActive(true);
  }
  @HostListener('document:mousemove', ['$event'])
  onMouseMove() {
    this.autoLogout.setisActive(true);
  }
}
