import { Component, OnInit } from "@angular/core";
import { UserIdleService } from "angular-user-idle";
import { Location } from "@angular/common";
import { HostListener } from "@angular/core";
import { AuthService } from "./auth/auth.service";
import { DialougComponent } from "src/app/shared/dialoug/dialoug.component";
import { MatDialog } from "@angular/material";
import {ConfigService} from "src/app/core/services/config.service";

@Component({
  selector: "app-root",
  templateUrl: "./app.component.html",
  styleUrls: ["./app.component.css"]
})
export class AppComponent implements OnInit {
  title = "pre-registration-TEST";
  userActive: boolean = false;
  timerstart: boolean = false;
  message :object

  constructor(
    private userIdle: UserIdleService,
    private location: Location,
    private authService: AuthService,
    private dialog: MatDialog,
    private configService : ConfigService
  ) {}

  ngOnInit() {
    // this.keepWatching();
    this.configService.currentMessageAutoLogout.subscribe(res => {
        console.log(res);
    });
    this.configService.changeMessage({'timerFired':false});

  }

  keepWatching() {
    //console.log("timer reset");
    this.configService.changeMessage({'timerFired':true});
    this.userIdle.startWatching();
    this.userIdle.onTimerStart().subscribe(
      res => {
        console.log("hi");
        if (res == 1) {
          this.userActive = false;
          this.openPopUp();
          console.log(res);
        }
      },
      err => {},
      () => {}
    );

    this.userIdle.onTimeout().subscribe(() => {
      //console.log("timeout");
      console.log(this.userActive);
      if (!this.userActive) {
        //console.log("logging out");
        this.autoLogOut();
      } else {
        console.log("else condition");
        this.userIdle.resetTimer();
        //console.log("stop watching");
        //console.log("calling keep watching");
      }
    });
  }

  autoLogOut() {
    console.log("first logout function");
    this.userIdle.stopWatching();
    this.dialog.closeAll();
    this.authService.onLogout();
    alert("you have been logged out due to inactivity");
    window.location.reload();
    //console.log("stop watching");
  }

  openPopUp() {
    console.log("keepspoping up");
    const data = {
      case: "POPUP"
    };
    this.dialog.open(DialougComponent, {
      width: "550px",
      data: data
    });
  }

  @HostListener("mouseover") onMouseOver() {
    this.userActive = true;
    console.log(this.userActive);
  }
  @HostListener("click") onMouseClick() {
    this.userActive = true;
    console.log(this.userActive);
  }
  @HostListener("keypress") onKeyPress() {
    this.userActive = true;
    console.log(this.userActive);
  }
  @HostListener('document:mousemove', ['$event'])
  onMouseMove(e) {
    this.userActive = true;
  }
}
