import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'pre-registration';
  // userActive: boolean = false;

  constructor() // private userIdle: UserIdleService,
  // private location: Location
  {}
  ngOnInit() {
    // if(localStorage.getItem('loggedIn') == "true")
    // {
    // this.userIdle.startWatching();
    // this.userIdle.onTimerStart().subscribe(() => console.log('hi'));
    // this.userIdle.onTimeout().subscribe(
    //   res =>{
    //     this.doLogOut();
    //   },
    //   err =>{},
    //   () => console.log('Time is up!'));
    // }
  }
  // doLogOut(){
  //   alert('you have been logged out due to inactivity');
  //   // if(localStorage.getItem('loggedIn') == "true")
  //   // {
  //     this.userIdle.resetTimer();
  //     this.userIdle.startWatching();
  //     this.userIdle.onTimerStart().subscribe(() => console.log('hi'));
  //     this.userIdle.onTimeout().subscribe(
  //       res =>{
  //         this.doLogOut();
  //       },
  //       err =>{},
  //       () => console.log('Time is up!'));
  //   // }

  // }

  // checkUserActivity(){
  //   @HostListener('Keydown')  {
  //     this.userActive = false;
  //   }
  // }
}
