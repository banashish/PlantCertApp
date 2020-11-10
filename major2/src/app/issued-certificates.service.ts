import { Injectable } from '@angular/core';
import { AngularFirestore } from '@angular/fire/firestore';
import { AngularFireStorage } from '@angular/fire/storage';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class IssuedCertificatesService {

  constructor(private firestore: AngularFirestore, private afStorage: AngularFireStorage) { }

  getparticularDoc(id){
    var item :any
    return this.firestore.collection('issuedPlants').doc(id).ref.get().then(doc=>{
        return doc.data()
        item = doc.data()
        const task = this.afStorage.ref('pictures/' + item.Image).getDownloadURL()
        return task.subscribe(url => {
          if (url) {
            item.address = url;
            return item
          }
        })
        
      }).catch(function (error) {
        console.log("There was an error getting your document:", error);
      });
  }

  getIssuedplants() {
    return this.firestore.collection('issuedPlants')
  }

  createCertificate(data) {
    return this.firestore.collection('issuedPlants').doc(data.id).set(data);
  }

  deleteCertificate(id) {
    this.firestore.doc('issuedPlants/' + id).delete();
  }
}
