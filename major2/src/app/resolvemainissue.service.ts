import { Injectable } from '@angular/core';
import { Resolve } from '@angular/router';
import { IssuedCertificatesService} from './issued-certificates.service'
import { AuthService } from './auth.service'
import { AngularFireStorage, AngularFireUploadTask } from '@angular/fire/storage';
import { Policy } from './policy.model';

@Injectable({
  providedIn: 'root'
})
export class ResolvemainissueService implements Resolve<any>{

  issuedPlants :any = []

  constructor(private issueService: IssuedCertificatesService,private authService : AuthService,private afStorage : AngularFireStorage) {
    this.issueService.getIssuedplants().snapshotChanges().subscribe(data => {
      if(this.issuedPlants.length == 0){
        data.forEach(e => {
          let item = e.payload.doc.data() as Policy
          item.id = e.payload.doc.id
          const task = this.afStorage.ref('pictures/' + item.Image).getDownloadURL()
          task.subscribe(url => {
            if (url) {
              item.address = url
            }
          })
          this.issuedPlants.push(item)
        })
      }
      else{
        this.issuedPlants = []
        data.forEach(e => {
          let item = e.payload.doc.data() as Policy
          item.id = e.payload.doc.id
          const task = this.afStorage.ref('pictures/' + item.Image).getDownloadURL()
          task.subscribe(url => {
            if (url) {
              item.address = url
            }
          })
          this.issuedPlants.push(item)
        })
      }
    })
      
   }

  resolve() {
    return this.issuedPlants
  }
}
