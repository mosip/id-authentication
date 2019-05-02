import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-authentication',
  templateUrl: './authentication.component.html',
  styleUrls: ['./authentication.component.css']
})
export class AuthenticationComponent implements OnInit {

  minutes: any;
  seconds: any;
  timer: any;
  minuteString: any;
  secondString: any;
  constructor(private router: Router) {
    this.startCountdown(10);
   }

  ngOnInit() {
  }
   startCountdown(timeLeft): void {
    let counter = 0;
    const interval = setInterval(() => {
      console.log(counter);
      document.getElementById('timer').innerHTML = this.convertSeconds(timeLeft - counter);
      counter++;
      if (counter < 0 ) {
        clearInterval(interval);
      }
    }, 1000);
  }
    convertSeconds(timeLeft) {
    this.minutes = Math.floor(timeLeft / 60);
    this.seconds = Math.floor(timeLeft % 60);
    // tslint:disable-next-line:max-line-length
    return Number(this.minutes).toLocaleString('en-US', {minimumIntegerDigits: 2}) + ':' + Number(this.seconds).toLocaleString('en-US', {minimumIntegerDigits: 2});
   }
   onSubmit(){
   this.router.navigateByUrl('admin/dashboard');
   }
}
