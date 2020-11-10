import { Component, OnInit, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
import { AuthService} from '../auth.service'
import { Router, ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit,AfterViewInit {

  constructor(private authService : AuthService,public  router:  Router,public route:ActivatedRoute) {
    this.authService.getData().subscribe(data => {
      if((!data) || (data.uid != localStorage.getItem('uid'))){
        this.router.navigate(['']);
      }
      else{
        this.username = data.email;
      }
    })
   }
  username : string
  @ViewChild('mynav', {static: false}) myRef: ElementRef;
  ngOnInit() {
       
  }
  ngAfterViewInit(){
    if(this.route.snapshot['_routerState'].url == "/main"){
      this.myRef.nativeElement.children[0].children[0].classList.add("active")
    }
    if(this.route.snapshot['_routerState'].url == "/main/issuedCerificate"){
      this.myRef.nativeElement.children[1].children[0].classList.add("active")
    }
  }
  onlogout(){
    this.router.navigate(['']);
    this.authService.logout();
  }

}
