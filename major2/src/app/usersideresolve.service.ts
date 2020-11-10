import { Injectable } from '@angular/core';
import { Resolve, ActivatedRoute, ActivatedRouteSnapshot} from '@angular/router';
import { IssuedCertificatesService} from './issued-certificates.service';
import { AngularFireStorage, AngularFireUploadTask } from '@angular/fire/storage';
import { Policy } from './policy.model';
import { AngularFirestore } from '@angular/fire/firestore';

@Injectable({
  providedIn: 'root'
})
export class UsersideresolveService implements Resolve<any>{
  issuedPlants: any = [];

  constructor(private issueService: IssuedCertificatesService,private afStorage : AngularFireStorage,private activated : ActivatedRoute,private firestore: AngularFirestore) {
   
   }

  resolve(route: ActivatedRouteSnapshot) {
    const id = route.params.id
    var item : any
    return this.issueService.getparticularDoc(id)
  }
}
