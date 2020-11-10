import { Component, OnInit } from '@angular/core';
import { AuthService } from '../auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  constructor(private authService : AuthService,public  router:  Router) {
    this.authService.getData().subscribe(data => {
      if(data.uid == localStorage.getItem('uid')){
        this.router.navigate(['main']);
      }
    })
   }

  ngOnInit() {
  }

}
