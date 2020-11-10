import { Component, OnInit } from '@angular/core';
import { ConnectorService } from 'src/app/connector.service';
import { IssuedCertificatesService } from 'src/app/issued-certificates.service'
import { Policy } from 'src/app/policy.model';
import { AuthService } from '../auth.service'
import { AngularFireStorage, AngularFireUploadTask } from '@angular/fire/storage';
import { ActivatedRoute, Router } from '@angular/router';
import { Validators, FormBuilder, FormGroup } from '@angular/forms';
import {ViewChild,ElementRef } from '@angular/core';
import { from } from 'rxjs';

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  styleUrls: ['./main.component.css']
})
export class MainComponent implements OnInit {

  plantData : any
  policies: Policy[];
  IssueForm: FormGroup;
  @ViewChild('myclose', {static: false}) pRef: ElementRef;
  constructor(private policyService: ConnectorService,private authService : AuthService,private afStorage : AngularFireStorage,private activatedRoute: ActivatedRoute,private fb: FormBuilder,public  router:  Router,private issue : IssuedCertificatesService) { }
  
  ngOnInit() {
    this.activatedRoute.data.subscribe(data => {
      this.policies = []
      this.policies = data.data;
    });
    this.IssueForm = this.fb.group({
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required]],
      date: ['', [Validators.required]]
    });
  }

  get f() {
    return this.IssueForm.controls;
  }

  onIssue(plant){
    this.plantData = plant;
  }

  onSubmit(){
    if (this.IssueForm.invalid) {
      return;
    }
    else{
      const info = {"Name" : this.IssueForm.controls.firstName.value + " " + this.IssueForm.controls.lastName.value,"Date" : this.IssueForm.controls.date.value,"url" : this.plantData.id}
      this.plantData.issuedTo = this.IssueForm.controls.firstName.value + " " + this.IssueForm.controls.lastName.value;
      this.plantData.issueDate = this.IssueForm.controls.date.value
      this.pRef.nativeElement.click()
      this.generateCertificate();
      this.policyService.setInfo(info);
      this.router.navigate(['certificate']);
    }
  }

  generateCertificate(){
    this.policyService.deletePlant(this.plantData.id);
    this.issue.createCertificate(this.plantData)    
  }

  getPlants(){
    this.policies = [];
    this.policyService.getPolicies().snapshotChanges().subscribe(data => {
      data.forEach(e => {
        let item = e.payload.doc.data() as Policy
        item.id = e.payload.doc.id
        const task = this.afStorage.ref('pictures/' + item.Image).getDownloadURL()
        task.subscribe(url => {
          if (url) {
            item.address = url
          }
        })
        this.policies.push(item)
      })
    })

  }
}
