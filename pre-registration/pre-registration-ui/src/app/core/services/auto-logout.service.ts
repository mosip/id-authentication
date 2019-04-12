import { Injectable } from "@angular/core";
import { UserIdleService, UserIdleConfig } from "angular-user-idle";
import { AuthService } from "src/app/auth/auth.service";
import { MatDialog } from "@angular/material";
import { DialougComponent } from "src/app/shared/dialoug/dialoug.component";
import { BehaviorSubject, merge, fromEvent, timer } from "rxjs";
import { ConfigService } from 'src/app/core/services/config.service';
import * as appConstants from 'src/app/app.constants';

/**
 * @description This class is responsible for auto logging out user when he is inactive for a
 *  specified period of time.
 * @author Deepak Choudhary
 * @export
 * @class AutoLogoutService
 */

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

  /**
   * @description This method gets value of idle,timeout and ping parameter from config file.
   *
   * @returns void
   * @memberof AutoLogoutService
   */
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
   /**
   * @description This method sets value of idle,timeout and ping parameter from config file.
   *
   * @returns void
   * @memberof AutoLogoutService
   */
  setValues() {
    this.timer.idle = this.idle
    this.timer.ping = this.ping
    this.timer.timeout = this.timeout
    this.userIdle.setConfigValues(this.timer);
   //console.log("get config ", this.userIdle.getConfigValue());
  }


  /**
   * @description This method is fired when dashboard gets loaded and starts the timer to watch for
   * user idle. onTimerStart() is fired when user idle has been detected for specified time.
   * After that onTimeout() is fired.
   *
   * @returns void
   * @memberof AutoLogoutService
   */


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
     /**
   * @description This methoed is used to logged out the user.
   *
   * @returns void
   * @memberof AutoLogoutService
   */

  onLogOut() {
    this.userIdle.stopWatching();
    this.dialog.closeAll();
    this.authService.onLogout();
    alert("you have been logged out due to inactivity");
    window.location.reload();
 }

      /**
   * @description This methoed opens a pop up when user idle has been detected for given time...
   *
   * @returns void
   * @memberof AutoLogoutService
   */


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
