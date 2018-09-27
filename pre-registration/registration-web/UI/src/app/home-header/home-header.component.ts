import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import swal from 'sweetalert2';

@Component({
  selector: 'app-home-header',
  templateUrl: './home-header.component.html',
  styleUrls: ['./home-header.component.css']
})
export class HomeHeaderComponent implements OnInit {
  username: string;
  constructor(private router: Router) { }

  ngOnInit() {
    this.username = sessionStorage.getItem('loginuser');
    // console.log(this.username);
  }

  logout() {
    swal({
      type: 'success',
      title: 'Logout',
      text: 'Successful logout',
      footer: ''
    })
    // sessionStorage.removeItem('loginuser');
    sessionStorage.clear();
    this.router.navigate(['/login']);
  }

}
