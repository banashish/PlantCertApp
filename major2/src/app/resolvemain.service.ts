import { Injectable } from '@angular/core';
import { Resolve } from '@angular/router';
import { ConnectorService } from './connector.service';
import { AuthService } from './auth.service'
import { AngularFireStorage, AngularFireUploadTask } from '@angular/fire/storage';
import { Policy } from './policy.model';
import { mergeMap, switchMap } from 'rxjs/operators';
import { pipe } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ResolvemainService implements Resolve<any> {

  policies: Policy[] = []
  downloadURL = [];
  item$;
  
  constructor(private policyService: ConnectorService,private authService : AuthService,private afStorage : AngularFireStorage) {


    this.policyService.getPolicies().snapshotChanges().subscribe(data => {
      if(this.policies.length == 0){
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
      }
      else{
        this.policies = []
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
      }
      
    })
    // else{
    //   this.policyService.getPolicies().snapshotChanges().subscribe(data => {
    //     this.policies = []
    //     data.forEach(e => {
    //       let item = e.payload.doc.data() as Policy
    //       item.id = e.payload.doc.id
    //       const task = this.afStorage.ref('pictures/' + item.Image).getDownloadURL()
    //       task.subscribe(url => {
    //         if (url) {
    //           item.address = url
    //         }
    //       })
    //       this.policies.push(item)
    //     })
    //   })
    // }
   }


  resolve() {
    return this.policies
  }
}
