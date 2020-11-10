import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ConnectorService } from '../connector.service';
import { IssuedCertificatesService  } from "../issued-certificates.service";
import { Policy } from "../policy.model";
import { AngularFireStorage } from '@angular/fire/storage';

@Component({
  selector: 'app-issued-main',
  templateUrl: './issued-main.component.html',
  styleUrls: ['./issued-main.component.css']
})
export class IssuedMainComponent implements OnInit {

  constructor(private activatedRoute: ActivatedRoute,private connect : ConnectorService,private issuedCetificate : IssuedCertificatesService,private afStorage : AngularFireStorage,public  router:  Router) { }

  issuedData : any = []

  ngOnInit() {
    this.activatedRoute.data.subscribe(data => {
      this.issuedData = data.data;
    });
  }

  moveBack(data){
    const info = {
      "Name" : data.Name,
      "Location" : data.Location,
      "Latitude" : data.Latitude,
      "Longitude" : data.Longitude,
      "Image" : data.Image,
      "City" : data.City
    }
    this.connect.createPlant(info,data.id);
    this.issuedCetificate.deleteCertificate(data.id);
    this.getData();
  }

  getData(){
    this.issuedData = []
    this.issuedCetificate.getIssuedplants().snapshotChanges().subscribe(data => {
      data.forEach(e => {
        let item = e.payload.doc.data() as Policy
        item.id = e.payload.doc.id
        const task = this.afStorage.ref('pictures/' + item.Image).getDownloadURL()
        task.subscribe(url => {
          if (url) {
            item.address = url
          }
        })
        this.issuedData.push(item)
      })
    })
  }

  openCertificate(data){
    const info = {"Name" : data.issuedTo,"Date" : data.issueDate.toDate(),"url" : data.id}
    this.connect.setInfo(info);
    this.router.navigate(['certificate']);
  }

}
