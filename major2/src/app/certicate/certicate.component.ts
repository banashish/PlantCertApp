import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import * as jspdf from 'jspdf';
import html2canvas from 'html2canvas';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-certicate',
  templateUrl: './certicate.component.html',
  styleUrls: ['./certicate.component.css']
})
export class CerticateComponent implements OnInit {

  constructor(private activatedRoute: ActivatedRoute,private authService : AuthService,public  router:  Router) {
    this.authService.getData().subscribe(data => {
      if((!data) || (data.uid != localStorage.getItem('uid'))){
        this.router.navigate(['']);
      }
    })
   }
  info : any
  value : any
  routeurl : any
  ngOnInit() {
    this.activatedRoute.data.subscribe(data => {
      this.info = data.data;
      this.value = this.info.url
      this.routeurl = `https://authenticatorapp-2f46b.web.app/user/${this.value}`
    });
  }

public convetToPDF()
{
var data = document.getElementById('page-container');
html2canvas(data).then(canvas => {
// Few necessary setting options
var imgWidth = 300;
var pageHeight = 295;
var imgHeight = canvas.height * imgWidth / canvas.width;
var heightLeft = imgHeight;
 
const contentDataURL = canvas.toDataURL('image/png')
let pdf = new jspdf('l', 'mm', 'a4'); // A4 size page of PDF
var position = 5;
pdf.addImage(contentDataURL, 'PNG', 0, position, imgWidth, imgHeight)
pdf.save('new-file.pdf'); // Generated PDF
});
}

}
