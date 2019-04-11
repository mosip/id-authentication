import { Injectable } from "@angular/core";
import { UserIdleService } from "angular-user-idle";
import { AuthService } from "src/app/auth/auth.service";
import { MatDialog } from "@angular/material";
import { DialougComponent } from "src/app/shared/dialoug/dialoug.component";
import { BehaviorSubject, merge, fromEvent, timer } from "rxjs";
import { ConfigService } from 'src/app/core/services/config.service';
import * as appConstants from 'src/app/app.constants';
import { timeout } from "q";

@Injectable({
  providedIn: "root"
})
export class AutoLogoutService {
  private messageAutoLogout = new BehaviorSubject({});
  currentMessageAutoLogout = this.messageAutoLogout.asObservable();
  isActive = false;

  idle : number;
  timeout : number;
  ping :number;
  timer = {
    idle: this.idle,
    timeout : this .timeout,
    ping: this.ping
  };

  constructor(
    private userIdle: UserIdleService,
    private authService: AuthService,
    private dialog: MatDialog,
    private configservice: ConfigService
  ) {}

  setisActive(value: boolean){
    this.isActive=value;

  }
  getisActive(){
  return this.isActive;
  }

  changeMessage(message: object) {
    this.messageAutoLogout.next(message);
  }


  public keepWatching() {
    this.idle = this.configservice.getConfigByKey(appConstants.CONFIG_KEYS.mosip_preregistration_auto_logout_idle);
    this.timeout = this.configservice.getConfigByKey(appConstants.CONFIG_KEYS.mosip_preregistration_auto_logout_timeout);
    this.ping = this.configservice.getConfigByKey(appConstants.CONFIG_KEYS.mosip_preregistration_auto_logout_ping);
    this.userIdle.setConfigValues(this.timer);

    this.userIdle.startWatching();
    this.changeMessage({ timerFired: true });

    this.userIdle.onTimerStart().subscribe(
      res => {
         console.log("hi");
        if (res == 1) {
          this.setisActive(false);
          this.openPopUp();
          console.log(res);
        }
      },
      err => {},
      () => {}
    );

    this.userIdle.onTimeout().subscribe(() => {

      if (!(this.isActive)) {
        this.onLogOut();
      }
       else {
        console.log("else condition");
        this.userIdle.resetTimer();
      }
    });
  }

  onLogOut() {
    // console.log("first logout function");
    this.userIdle.stopWatching();
    this.dialog.closeAll();
    this.authService.onLogout();
    alert("you have been logged out due to inactivity");
    window.location.reload();

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
}
