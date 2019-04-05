import { Component, OnInit } from "@angular/core";
import { UserIdleService } from "angular-user-idle";
import { Location } from "@angular/common";
import { HostListener } from "@angular/core";
import { AuthService } from "./auth/auth.service";
import { DialougComponent } from "src/app/shared/dialoug/dialoug.component";
import { MatDialog } from "@angular/material";
import { AutoLogoutService } from "src/app/core/services/auto-logout.service";

@Component({
  selector: "app-root",
  templateUrl: "./app.component.html",
  styleUrls: ["./app.component.css"]
})
export class AppComponent implements OnInit {
  title = "pre-registration";
  message: object;

  constructor(
    private userIdle: UserIdleService,
    private location: Location,
    private authService: AuthService,
    private dialog: MatDialog,
    private autoLogout: AutoLogoutService
  ) {}

  ngOnInit() {
    this.autoLogout.currentMessageAutoLogout.subscribe(res => {
      console.log(res);
    });
    this.autoLogout.changeMessage({ timerFired: false });
  }

  @HostListener("mouseover") onMouseOver() {
    this.autoLogout.setisActive(true);
  }
  @HostListener("click") onMouseClick() {
    this.autoLogout.setisActive(true);
  }
  @HostListener("keypress") onKeyPress() {
    this.autoLogout.setisActive(true);
  }
  @HostListener("document:mousemove", ["$event"])
  onMouseMove(e) {
    this.autoLogout.setisActive(true);
  }
}
