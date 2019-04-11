import { Injectable } from "@angular/core";
import { UserIdleService, UserIdleConfig } from "angular-user-idle";
import { AuthService } from "src/app/auth/auth.service";
import { MatDialog } from "@angular/material";
import { DialougComponent } from "src/app/shared/dialoug/dialoug.component";
import { BehaviorSubject, merge, fromEvent, timer } from "rxjs";
import { ConfigService } from 'src/app/core/services/config.service';
import * as appConstants from 'src/app/app.constants';

@Injectable({
  providedIn: "root"
})
export class AutoLogoutService {
  private messageAutoLogout = new BehaviorSubject({});
  currentMessageAutoLogout = this.messageAutoLogout.asObservable();
  isActive = false;

  timer = new UserIdleConfig();

  idle: number;
  timeout: number;
  ping: number;


  constructor(
    private userIdle: UserIdleService,
    private authService: AuthService,
    private dialog: MatDialog,
    private configservice: ConfigService
  ) {

  }

  getValues() {

    /*
          ******Documentation
          This method is used to get values for idle , timeout and ping which comes from confi data...
    */
   // console.log(this.configservice.getConfigByKey(appConstants.CONFIG_KEYS.mosip_preregistration_auto_logout_idle));
    this.idle = Number(this.configservice.getConfigByKey(appConstants.CONFIG_KEYS.mosip_preregistration_auto_logout_idle)),
    this.timeout = Number(this.configservice.getConfigByKey(appConstants.CONFIG_KEYS.mosip_preregistration_auto_logout_timeout)),
    this.ping = Number(this.configservice.getConfigByKey(appConstants.CONFIG_KEYS.mosip_preregistration_auto_logout_ping))
  }


  setisActive(value: boolean) {
    this.isActive = value;

  }
  getisActive() {
    return this.isActive;
  }

  changeMessage(message: object) {
    this.messageAutoLogout.next(message);
  }
  setValues() {

   /*
          ******Documentation
          This method is used to set values for idle , timeout and ping which comes from confi data...
    */

    this.timer.idle = this.idle
    this.timer.ping = this.ping
    this.timer.timeout = this.timeout
    this.userIdle.setConfigValues(this.timer);
   //console.log("get config ", this.userIdle.getConfigValue());
  }





  public keepWatching() {
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
      err => { },
      () => { }
    );

    this.userIdle.onTimeout().subscribe(() => {

     if (!(this.isActive)) {
        this.onLogOut();
      }
      else {
        //console.log("else condition");
        this.userIdle.resetTimer();
      }
    });
  }

  onLogOut() {
    this.userIdle.stopWatching();
    this.dialog.closeAll();
    this.authService.onLogout();
    alert("you have been logged out due to inactivity");
    window.location.reload();
 }

  openPopUp() {
   // console.log("keepspoping up");
    const data = {
      case: "POPUP"
    };
    this.dialog.open(DialougComponent, {
      width: "550px",
      data: data
    });
  }
}
